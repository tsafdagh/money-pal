package com.kola.moneypal.utils

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.platforminfo.GlobalLibraryVersionRegistrar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.User
import java.util.*


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
        members: MutableList<String>? = null,
        groupeName: String,
        groupeDescription: String,
        objectiveAmount: Double,
        onComplete: (groupeId: String) -> Unit
    ) {

        val newgroupe =
            ObjectiveGroup(
                FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                groupeName,
                groupeDescription,
                objectiveAmount,
                Date(0),
                "",
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
}