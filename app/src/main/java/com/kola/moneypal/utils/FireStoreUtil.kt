package com.kola.moneypal.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.platforminfo.GlobalLibraryVersionRegistrar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kola.moneypal.RecycleView.item.ObjectivegroupItem
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.User
import com.xwray.groupie.kotlinandroidextensions.Item
import java.util.*
import kotlin.collections.ArrayList


object FireStoreUtil {
    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().currentUser?.phoneNumber
                ?: throw NullPointerException("Phone number is null..")}"
        )

    private val userCollection = firestoreInstance.collection(GobalConfig.REFFERENCE_USERS)
    private val groupeChatCollectionRef =
        firestoreInstance.collection(GobalConfig.REFERENCE_OBJECTIVE_GROUPE_COLLECTION)


    /**Fait une initialisation par défaut de l'utilisateur apres la creation de son compte*/
    fun initCurrentUserIfFirstTime(onComplete: (isOk: Boolean) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->

            // on initialise l'utilisateur la première fois que s'il n'existe pas encore
            if (!documentSnapshot.exists()) {
                val newUser = User(
                    FirebaseAuth.getInstance().currentUser?.phoneNumber!!,
                    FirebaseAuth.getInstance().currentUser?.displayName ?: "", "", ""
                )
                currentUserDocRef.set(newUser).addOnSuccessListener { onComplete(true) }
            } else {
                // s'il existe déja on ne l'initialise plus
                onComplete(false)
            }
        }
    }

    /** Fait une mise à jours de l'uttilisateur pour completter oubien
     * pour modifier ses informations dans fireStaore**/
    fun updateCurrentUser(
        name: String = "",
        email: String = "",
        imageProfil: String? = null,
        imageProfilsUri: Uri?,
        onComplete: (User: User, status: Boolean) -> Unit
    ) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["userName"] = name
        if (email.isNotBlank()) userFieldMap["email"] = email
        if (imageProfil != null)
            userFieldMap["profilePicturePath"] = imageProfil
        currentUserDocRef.update(userFieldMap).addOnSuccessListener {
            //lorsqu'on fait la mise à jours dans fireStore, on stoque le même utilisateur
            // dans son firebasAuth
            upDateFireAuthProfile(name, imageProfilsUri, onComplete = {
                val user =
                    FirebaseAuth.getInstance().currentUser!!.phoneNumber?.let { it1 ->
                        User(
                            it1,
                            name,
                            email,
                            imageProfil
                        )
                    }
                onComplete(user!!, it)
            })
        }
    }


    /** Met à jours le profils fireStoreAuth de l'utilisateur couran**/
    fun upDateFireAuthProfile(
        name: String,
        selectedImageURi: Uri?,
        onComplete: (isSuccess: Boolean) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name).setPhotoUri(selectedImageURi)
            .build()

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FIreStoreUtil:", "User name is set!")
                    onComplete(true)

                } else {
                    onComplete(false)
                }
            }

    }

    /** Recupère l'utilisateur couran couran de fireStore**/
    fun getCurrentUserFromFireStore(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }


    fun createObjectivegroupe(
        members: ArrayList<String>,
        groupeName: String,
        groupeDescription: String,
        objectiveAmount: Double,
        dateEcheance: String,
        onComplete: (groupeId: String) -> Unit
    ) {

        val newgroupe =
            ObjectiveGroup(
                FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                groupeName,
                groupeDescription,
                objectiveAmount,
                0.0,
                Date(0),
                dateEcheance, "",
                members
            )
        val newObjectiveGroup = groupeChatCollectionRef.document()
        newObjectiveGroup.set(newgroupe).addOnSuccessListener {
            //ajoutons l'id du groupe à l'utilisateur courant
            currentUserDocRef.collection(GobalConfig.REFERENCE_GROUPE_OF_ONE_USER)
                .add(mapOf(GobalConfig.REFERENCE_ID_GROUPE_OF_ONE to newObjectiveGroup.id))

            /*         // ajout de l'id du groupe à tous les autres membres sélectionnés pour ke groupe
                     for (itemuserId in members) {
                         val refCurentuser = firestoreInstance.document(
                             "users/${itemuserId}"
                         )
                         refCurentuser.collection("groupes").add(mapOf("groupeId" to newObjectiveGroup.id))
                     }*/
            onComplete(newObjectiveGroup.id)
        }
    }

    fun updateurlImageGroup(profilPicturePath: String? = null, refGroupe: String, onComplete: () -> Unit) {
        val refCurentGroupe =
            firestoreInstance.collection(GobalConfig.REFERENCE_OBJECTIVE_GROUPE_COLLECTION).document(refGroupe)
        refCurentGroupe.update("groupIcon", profilPicturePath)
            .addOnSuccessListener { onComplete() }
            .addOnFailureListener {
                Log.e(
                    "FireStore",
                    "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Error adding document",
                    it.cause
                )
            }
    }


    /** cette méthode permet de recupérer la liste des Groupe en temps réel
     * et aussi selon un critere de recherche bien defini **/
    fun addSearchGroupeListener(
        searchingcriterion: String,
        context: Context,
        onListen: (List<Item>) -> Unit
    ): ListenerRegistration {
        return firestoreInstance.collection(GobalConfig.REFERENCE_OBJECTIVE_GROUPE_COLLECTION)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Groupes listener error?", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    // on convertie a chaque fois le groupe courant en objet
                    val curentGroup = it.toObject(ObjectiveGroup::class.java)!!

                    if (!searchingcriterion.isEmpty()) {
                        if (it["groupeName"].toString().toUpperCase().contains(searchingcriterion.toUpperCase())
                            || it["groupeDescription"].toString().toUpperCase().contains(searchingcriterion.toUpperCase())
                            || it["objectiveamount"].toString().toUpperCase().contains(searchingcriterion.toUpperCase())
                        ) {
                            // on parcour les membres du groupe un par un
                            for (itm in curentGroup.members!!) {

                                /* si l'utilisateur fait partir des membres du groupe oubien s'il est l'administrateur
                                 du groupe on affiche le groupe dans son telephone
                                 */
                                if (itm == FirebaseAuth.getInstance().currentUser?.phoneNumber
                                    || curentGroup.AdminPhoneNumber == FirebaseAuth.getInstance().currentUser?.phoneNumber
                                ) {


                                    items.add(
                                        ObjectivegroupItem(
                                            it.toObject(ObjectiveGroup::class.java)!!,
                                            it.id,
                                            context
                                        )
                                    )
                                    break
                                }
                            }
                        }
                    } else {
                        // on parcour les membres du groupe un par un
                        for (itm in curentGroup.members!!) {
                            /* si l'utilisateur fait partir des membres du groupe oubien s'il est l'administrateur
                             du groupe on affiche le groupe dans son telephone */

                            if (itm == FirebaseAuth.getInstance().currentUser?.phoneNumber
                                || curentGroup.AdminPhoneNumber == FirebaseAuth.getInstance().currentUser?.phoneNumber
                            ) {
                                items.add(ObjectivegroupItem(it.toObject(ObjectiveGroup::class.java)!!, it.id, context))
                                break
                            }
                        }

                    }

                }

                onListen(items)
            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

}