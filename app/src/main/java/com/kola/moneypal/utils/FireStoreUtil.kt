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
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.User
import com.kola.moneypal.entities.UserGroupeEntitie
import com.kola.moneypal.entities.UserInfoForGroup
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
    private fun upDateFireAuthProfile(
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

            //val defaulftUserGroupConfig = mutableListOf<Map<String, String>>()

            //ajoutons l'id du groupe à l'utilisateur courant
            // on initialise la contribution de l'utilisateur qui creè le groupe à 0FCFA
            // on initie la date de contribution de l'utilisateur à la date actuelle
            //defaulftUserGroupConfig.add(mapOf(GobalConfig.REFERENCE_ID_GROUPE_OF_ONE to newObjectiveGroup.id))
            //defaulftUserGroupConfig.add(mapOf(GobalConfig.CONTRIBUTED_USER_AMOUNT to (0.0).toString()))
            //defaulftUserGroupConfig.add(mapOf(GobalConfig.DATE_CONTRIBUTED_ON_GROUP to AnotherUtil.getdateNow()))
            //currentUserDocRef.collection(GobalConfig.REFERENCE_GROUPE_OF_ONE_USER)
            //    .add(defaulftUserGroupConfig)

            //ajout de la reference du croupe céer armis les propriétés de l'utilisareur
            val defaulftUserGroupConfig = mutableMapOf<String, Any>()
            defaulftUserGroupConfig[GobalConfig.REFERENCE_ID_GROUPE_OF_ONE] = newObjectiveGroup.id
            defaulftUserGroupConfig[GobalConfig.CONTRIBUTED_USER_AMOUNT] = 0.0
            defaulftUserGroupConfig[GobalConfig.DATE_CONTRIBUTED_ON_GROUP] = AnotherUtil.getdateNow()

            currentUserDocRef.collection(newObjectiveGroup.id).add(defaulftUserGroupConfig)

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

    /** Cette fonction a pour but de creer et d'afficher la liste des membres d'un groupe d'objectif
     * dans la page DetailsobjectiveGroupe**/
    //TODO cette fonction est à optimiser car elle ne fonctionne que pour un seul
    fun createObjectiveGroupMembersList(
        listOfMember: ArrayList<String>,
        context: Context,
        groupeId: String,
        onListen: (List<Item>) -> Unit
    ) {
        val items = mutableListOf<Item>()

        var count = 1

        for (itemPhonenumber in listOfMember) {
            getUsersByPhoneNumber(itemPhonenumber, onComplete = { user ->
                // on recupère les informations de l'utilisateur pour le roupe courent
                firestoreInstance.collection("users").document(itemPhonenumber).collection(groupeId).get()
                    .addOnSuccessListener {
                        val tmpinfo = it.toObjects(UserInfoForGroup::class.java)
                        val curentUserGroupEntitie = UserGroupeEntitie(
                            user.userName,
                            tmpinfo[0].contributed_date,
                            tmpinfo[0].montant_cotiser,
                            user.profilePicturePath!!,
                            user.phoneNumber
                        )

                        items.add(
                            UserGroupeitem(curentUserGroupEntitie, context)
                        )
                        onListen(items)
                        count++
                    }.addOnFailureListener {
                        Log.e("FireStorutil", it.printStackTrace().toString())
                    }
            })
        }

        //onListen(items)
    }

    /**Joue le même rôle que la précedente mais uniquement pour un seul utilisateur du groupe**/
    fun addCreateObjectiveGroupMembersList(
        phoneNumber: String,
        context: Context,
        groupeId: String,
        onListen: (List<Item>) -> Unit
    ): ListenerRegistration {
        return firestoreInstance.collection("users").document(phoneNumber).collection(groupeId)
            .orderBy("contributed_date")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "ChatMessageslistener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                val items = mutableListOf<Item>()
                querySnapshot!!.documents.forEach {

                    getUsersByPhoneNumber(phoneNumber, onComplete = { user ->
                        val tmpinfo = it.toObject(UserInfoForGroup::class.java)
                        val curentUserGroupEntitie = UserGroupeEntitie(
                            user.userName,
                            tmpinfo!!.contributed_date,
                            tmpinfo.montant_cotiser,
                            user.profilePicturePath!!,
                            user.phoneNumber
                        )

                        items.add(
                            UserGroupeitem(curentUserGroupEntitie, context)
                        )
                        onListen(items)

                    })

                    return@forEach
                }
                //onListen(items)

            }

    }


    /** cette méthode permet de recupérer un utilisateur àpartir de son Numero de télephone**/
    private fun getUsersByPhoneNumber(userPhoneNumber: String, onComplete: (User) -> Unit) {
        userCollection.document(userPhoneNumber).get()
            .addOnSuccessListener { onComplete(it.toObject(User::class.java)!!) }
    }

    /** Cette fonction a pour but d'ajouter les informations de l'utilisateur dans
     * un groupe d'objectif après qu'il ai valider son addésion par un lient dynamique **/
    fun addUserToObjectiveGroup(objectivegroupId: String, onComplete: () -> Unit) {
        val defaulftUserGroupConfig = mutableMapOf<String, Any>()
        defaulftUserGroupConfig[GobalConfig.REFERENCE_ID_GROUPE_OF_ONE] = objectivegroupId
        defaulftUserGroupConfig[GobalConfig.CONTRIBUTED_USER_AMOUNT] = 0.0
        defaulftUserGroupConfig[GobalConfig.DATE_CONTRIBUTED_ON_GROUP] = AnotherUtil.getdateNow()
        currentUserDocRef.collection(objectivegroupId).add(defaulftUserGroupConfig)
            .addOnSuccessListener {
                onComplete()
            }
    }

    /**  lorsqu'un membre du groupe décide de faire un payement dans le groupe,
     * cette fonction est appélée dans le but d'accroite la contribution de l'utilisateur
     * pour le groupe d'objectif**/
    fun updateContributedAmountOnobjectiveGroup(
        objectivegroupId: String,
        newContributedAmount: Double,
        onComplete: (status: Boolean) -> Unit
    ) {
        // on recupère d'abord la dernière contribution de l'utilisateur
        currentUserDocRef.collection(objectivegroupId).document().get().addOnSuccessListener {
            var oldContributedAmount = it[GobalConfig.CONTRIBUTED_USER_AMOUNT] as Double
            oldContributedAmount += newContributedAmount
            //ensuite on met à jours le nouveau solde de contribution
            currentUserDocRef.collection(objectivegroupId).document()
                .update(GobalConfig.CONTRIBUTED_USER_AMOUNT, oldContributedAmount)
                .addOnSuccessListener {
                    Log.d("FireStoreUtil", "Solde mis à jours")
                    onComplete(true)
                }
                .addOnFailureListener {
                    Log.d("FireStoreUtil", "Solde non mis à jours")
                    onComplete(false)
                }
        }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

}