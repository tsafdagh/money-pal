package com.kola.moneypal.utils

import android.content.Context
import android.net.Uri
import android.nfc.Tag
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
import com.kola.moneypal.RecycleView.item.SimpleuserItem
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.User
import com.kola.moneypal.entities.UserGroupeEntitie
import com.kola.moneypal.entities.UserInfoForGroup
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.toast
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
    private val objectiveGroupeCollectionRef =
        firestoreInstance.collection(GobalConfig.REFERENCE_OBJECTIVE_GROUPE_COLLECTION)

    private val TAG = "FireStoreUtil"

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
        val newObjectiveGroup = objectiveGroupeCollectionRef.document()
        newObjectiveGroup.set(newgroupe).addOnSuccessListener {

            //ajout de la reference du croupe céer parmis les propriétés de l'utilisareur
            addObjectiveGroupInfoToUser(
                newObjectiveGroup.id,
                FirebaseAuth.getInstance().currentUser?.phoneNumber!!,
                onComplete = {
                    onComplete(newObjectiveGroup.id)
                })

/*            val defaulftUserGroupConfig = mutableMapOf<String, Any>()


            defaulftUserGroupConfig[GobalConfig.REFERENCE_ID_GROUPE_OF_ONE] = newObjectiveGroup.id
            defaulftUserGroupConfig[GobalConfig.CONTRIBUTED_USER_AMOUNT] = 0.0
            defaulftUserGroupConfig[GobalConfig.DATE_CONTRIBUTED_ON_GROUP] = AnotherUtil.getdateNow()

            currentUserDocRef.collection(newObjectiveGroup.id).add(defaulftUserGroupConfig) zjez

                    *//*         // ajout de l'id du groupe à tous les autres membres sélectionnés pour ke groupe
                             for (itemuserId in members) {
                                 val refCurentuser = firestoreInstance.document(
                                     "users/${itemuserId}"
                                 )
                                 refCurentuser.collection("groupes").add(mapOf("groupeId" to newObjectiveGroup.id))
                             }*//*
                    onComplete(newObjectiveGroup.id)*/
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
                                    || curentGroup.adminPhoneNumber == FirebaseAuth.getInstance().currentUser?.phoneNumber
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
                                || curentGroup.adminPhoneNumber == FirebaseAuth.getInstance().currentUser?.phoneNumber
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
    fun createObjectiveGroupMembersList(
        listOfMember: ArrayList<String>,
        context: Context,
        groupeId: String,
        onListen: (List<Item>) -> Unit
    ) {
        val items = mutableListOf<Item>()

        for (itemPhonenumber in listOfMember) {
            getUsersByPhoneNumber(itemPhonenumber, onComplete = { user ->
                // on recupère les informations de l'utilisateur pour le groupe courent
/*                firestoreInstance.collection("users").document(itemPhonenumber).collection(groupeId).get()
                    .addOnSuccessListener {
                        val tmpinfo = it.toObjects(UserInfoForGroup::class.java)
                        val curentUserGroupEntitie = UserGroupeEntitie(
                            user.userName,
                            tmpinfo[0].contributed_date,
                            tmpinfo[0].montant_cotiser,
                            user.profilePicturePath!!,
                            user.phoneNumber
                        )
                        // context.toast(curentUserGroupEntitie.toString())

                        items.add(
                            UserGroupeitem(curentUserGroupEntitie, context)
                        )
                        onListen(items)
                    }.addOnFailureListener {
                        Log.e(TAG, it.printStackTrace().toString())
                    }*/

                // on recupère les informations de l'utilisateur pour le groupe courent
                firestoreInstance.collection("users").document(itemPhonenumber).collection(GobalConfig.USER_OBJECTIVES_GROUPS).document(groupeId).get()
                    .addOnSuccessListener {
                        val tmpinfo = it.toObject(UserInfoForGroup::class.java)
                        val curentUserGroupEntitie = UserGroupeEntitie(
                            user.userName,
                            tmpinfo!!.contributed_date,
                            tmpinfo.montant_cotiser,
                            user.profilePicturePath!!,
                            user.phoneNumber
                        )
                        // context.toast(curentUserGroupEntitie.toString())

                        items.add(
                            UserGroupeitem(curentUserGroupEntitie, context)
                        )
                        onListen(items)
                    }.addOnFailureListener {
                        Log.e(TAG, it.printStackTrace().toString())
                    }
            })
        }

        //onListen(items)
    }

    /**Joue le même rôle que la fonction createObjectiveGroupMembersList() mais uniquement pour un seul utilisateur du groupe**/
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


    /**Cette fonction a pour but d'évaluer le montant courrent d'un group dobjectif donner*
     * NB: elle est simpilaire à fonction createObjectiveGroupMembersList() à la difference qu'elle ne prend pas en
     * entrée la liste des membres mais elle se charge de la créer toute seule
     * */
    fun evaluateCurrentAmountOfObjectiveGroup(
        context: Context,
        groupeId: String,
        onListen: (List<Item>) -> Unit
    ) {

        val items = mutableListOf<Item>()
        GobalConfig.contributedAmountForGroup = 0.0

        // on commence par recuperer le groupe à partir de son ID et on parcout les soldes conntribués de ses membres
        findSpecificGroupByID(groupeId, onComplete = { objectiveGroup ->
            for (itemPhonenumber in objectiveGroup.members!!) {
                getUsersByPhoneNumber(itemPhonenumber, onComplete = { user ->
                    // on recupère les informations de l'utilisateur pour le groupe courent
                    firestoreInstance.collection("users").document(itemPhonenumber).collection(GobalConfig.USER_OBJECTIVES_GROUPS).document(groupeId).get()
                        .addOnSuccessListener {
                            val tmpinfo = it.toObject(UserInfoForGroup::class.java)
                            val curentUserGroupEntitie = UserGroupeEntitie(
                                user.userName,
                                tmpinfo!!.contributed_date,
                                tmpinfo.montant_cotiser,
                                user.profilePicturePath!!,
                                user.phoneNumber
                            )
                            // context.toast(curentUserGroupEntitie.toString())
                            GobalConfig.contributedAmountForGroup =
                                GobalConfig.contributedAmountForGroup + curentUserGroupEntitie.contributionMontant
                            items.add(
                                UserGroupeitem(curentUserGroupEntitie, context)
                            )
                            onListen(items)
                        }.addOnFailureListener {
                            Log.e("FireStorutil", it.printStackTrace().toString())
                        }
                })
            }
        })
    }

    /**Cette fonction a pour but de retourner un groupe donnée à partir de son ID**/
    private fun findSpecificGroupByID(
        idGroup: String,
        onComplete: (ObjectiveGroup) -> Unit
    ) {
        firestoreInstance.collection(GobalConfig.REFERENCE_OBJECTIVE_GROUPE_COLLECTION).document(idGroup)
            .get().addOnSuccessListener {

                val newObjGroup = it?.toObject(ObjectiveGroup::class.java)!!
                onComplete(newObjGroup)
            }
    }

    /**cette fonction à pour but d'écouter en temps réel le évènement du groupe courent afin de
     * faire à chaque fois une mise à jours quand il y'a changement
     */
    fun addFindSpecificGroupListener(
        idGroup: String,
        onListen: (ObjectiveGroup) -> Unit
    ): ListenerRegistration {
        return firestoreInstance.collection(GobalConfig.REFERENCE_OBJECTIVE_GROUPE_COLLECTION).document(idGroup)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Group listener error?", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                // on met dans un try cach car il peut arriver que le groupe ai été supprimer entre-temps
                try {
                    val newObjGroup = querySnapshot?.toObject(ObjectiveGroup::class.java)!!
                    onListen(newObjGroup)
                } catch (ex: Exception) {
                    Log.e(TAG, "Erreur " + ex.stackTrace.toString())
                }

            }
    }

    /** cette méthode permet de recupérer un utilisateur àpartir de son Numero de télephone**/
    private fun getUsersByPhoneNumber(userPhoneNumber: String, onComplete: (User) -> Unit) {
        userCollection.document(userPhoneNumber).get()
            .addOnSuccessListener { onComplete(it.toObject(User::class.java)!!) }
    }

    /**cette méthode a pour but de mettre à jour la liste des membres
     * d'un groupe d'objectif donné**/
    fun addMemberToObjectiveGroup(
        groupeid: String,
        listOfUsers: ArrayList<User>,
        onComplete: (isOk: Boolean) -> Unit
    ) {
        // on commence par recuperer les membres du groupe couran
        findSpecificGroupByID(groupeid, onComplete = {
            val members = it.members
            // on ajoute l'utilisateur dans le groupe que s'il n'y était pas déja
            for (user in listOfUsers) {
                if (!(members?.contains(user.phoneNumber)!!)) {
                    members.add(user.phoneNumber)
                }
            }
            //on met à jour la  liste des membres dans le referenciel du groupe
            objectiveGroupeCollectionRef.document(groupeid).update("members", members)
                .addOnSuccessListener {

                    Log.i(TAG, "Ajout du groupe au profils de chaque utilisateur ")
                    // parcourir la réference de chaque utilisateur et y ajouter l'id du groupe
                    for (phoneuser in members!!) {
                        addObjectiveGroupInfoToUser(groupeid, phoneuser, onComplete = {
                        })
                    }

                    onComplete(true)
                }.addOnFailureListener {
                    Log.e(TAG, "Erreur de mis à jour des membres du groupe " + it.stackTrace.toString())

                    onComplete(false)
                }
        })
    }

    /** Cette fonction a pour but d'ajouter les informations de l'utilisateur dans
     * un groupe d'objectif après qu'il ai valider son adhésion par un lient dynamique
     * ou que le créateur du groupe l'ai sélectionner**/

    fun addObjectiveGroupInfoToUser(
        objectivegroupId: String,
        userPhoneNumber: String,
        onComplete: (isOk: Boolean) -> Unit
    ) {
        val defaulftUserGroupConfig = mutableMapOf<String, Any>()
        defaulftUserGroupConfig[GobalConfig.REFERENCE_ID_GROUPE_OF_ONE] = objectivegroupId
        defaulftUserGroupConfig[GobalConfig.CONTRIBUTED_USER_AMOUNT] = 0.0
        defaulftUserGroupConfig[GobalConfig.DATE_CONTRIBUTED_ON_GROUP] = AnotherUtil.getdateNow()

/*        userCollection.document(userPhoneNumber).collection(objectivegroupId).add(defaulftUserGroupConfig)
            .addOnSuccessListener {
                Log.i(TAG, "Ajout du groupe au profils de l'utilisateur effectuer avec success")
                onComplete(true)

            }
            .addOnFailureListener {
                Log.e(TAG, "Erreur d'ajout des infos du groupe au profil de l'utilisateur")
                onComplete(false)
            }*/
        userCollection.document(userPhoneNumber).collection(GobalConfig.USER_OBJECTIVES_GROUPS).document(objectivegroupId).set(defaulftUserGroupConfig)
            .addOnSuccessListener {
                Log.i(TAG, "Ajout du groupe au profils de l'utilisateur effectuer avec success")
                onComplete(true)

            }
            .addOnFailureListener {
                Log.e(TAG, "Erreur d'ajout des infos du groupe au profil de l'utilisateur")
                onComplete(false)
            }
    }

    /**  lorsqu'un membre du groupe décide de faire un payement dans le groupe,
     * cette fonction est appélée dans le but d'accroite la contribution de l'utilisateur
     * pour le groupe d'objectif**/
    fun updateContributedAmountOnobjectiveGroupForCurentuser(
        objectivegroupId: String,
        newContributedAmount: Double,
        onComplete: (status: Boolean) -> Unit
    ) {
        // on recupère d'abord la dernière contribution de l'utilisateur
/*        currentUserDocRef.collection(objectivegroupId).get()
            .addOnSuccessListener {
                var oldContributedAmount = it.documents[0][GobalConfig.CONTRIBUTED_USER_AMOUNT] as Number

                val newAmount = oldContributedAmount.toDouble() + newContributedAmount

                //ensuite on met à jours le nouveau solde de contribution
                val documentId = it.documents[0].id
                currentUserDocRef.collection(objectivegroupId).document(documentId)
                    .update(GobalConfig.CONTRIBUTED_USER_AMOUNT, newAmount).addOnSuccessListener {
                        Log.d("FireStoreUtil", "Solde mis à jours")
                        notifyGroupeAndUpdateCurrentContributedAmount(
                            objectivegroupId,
                            newContributedAmount,
                            onComplete = {
                                onComplete(it)
                            })
                    }
                    .addOnFailureListener {
                        Log.e("FireStoreUtil", "Solde non mis à jours" + it.stackTrace.toString())
                        onComplete(false)
                    }
            }*/

        // on recupère d'abord la dernière contribution de l'utilisateur
        currentUserDocRef.collection(GobalConfig.USER_OBJECTIVES_GROUPS).document(objectivegroupId).get()
            .addOnSuccessListener {
                var oldContributedAmount = it[GobalConfig.CONTRIBUTED_USER_AMOUNT] as Number

                val newAmount = oldContributedAmount.toDouble() + newContributedAmount

                //ensuite on met à jours le nouveau solde de contribution
                val documentId = it.id
                currentUserDocRef.collection(GobalConfig.USER_OBJECTIVES_GROUPS).document(documentId)
                    .update(GobalConfig.CONTRIBUTED_USER_AMOUNT, newAmount).addOnSuccessListener {
                        Log.d("FireStoreUtil", "Solde mis à jours")
                        notifyGroupeAndUpdateCurrentContributedAmount(
                            objectivegroupId,
                            newContributedAmount,
                            onComplete = {
                                onComplete(it)
                            })
                    }
                    .addOnFailureListener {
                        Log.e("FireStoreUtil", "Solde non mis à jours" + it.stackTrace.toString())
                        onComplete(false)
                    }
            }
    }

    fun notifyGroupeAndUpdateCurrentContributedAmount(
        objectivegroupId: String,
        newUserContributedAmount: Double,
        onComplete: (isOk: Boolean) -> Unit
    ) {

        // on recupère le montant courent du groupe, on additionne au nouveau de l'utilisateur
        objectiveGroupeCollectionRef.document(objectivegroupId).get().addOnSuccessListener {
            var curronContributedAmount = it["courentAmount"] as Double
            curronContributedAmount += newUserContributedAmount

            //Et on le met à jours
            objectiveGroupeCollectionRef.document(objectivegroupId).update("courentAmount", curronContributedAmount)
                .addOnSuccessListener {
                    Log.d(TAG, "Contant contribué du group mis à jours")
                    onComplete(true)

                }.addOnFailureListener {
                    Log.e(TAG, "Erreur de mise à jours du montant déja contribué du groupe")
                    onComplete(false)
                }
        }
    }

    /**
     * Cette fonction a pour nut de lister les utilisateur
     * qu'on poura sélection pour les ajouter à un groupe d'objectif
     * **/
    fun addSearchUserListenerForcreatingGroupe(
        valeurRecherche: String,
        context: Context,
        onListen: (List<Item>) -> Unit
    ): ListenerRegistration {
        return firestoreInstance.collection(GobalConfig.REFFERENCE_USERS)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error?", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                    if (it["phoneNumber"] != FirebaseAuth.getInstance().currentUser?.phoneNumber) {

                        if (!valeurRecherche.isEmpty()) {
                            if (it["userName"].toString().toUpperCase().contains(valeurRecherche.toUpperCase())
                                || it["phoneNumber"].toString().toUpperCase().contains(valeurRecherche.toUpperCase())
                                || it["email"].toString().toUpperCase().contains(valeurRecherche.toUpperCase())
                            ) {
                                items.add(SimpleuserItem(it.toObject(User::class.java)!!, context))
                                Log.d("FIRESTOREUTIL", "NOUVELLE VALEURE AJOUTTEE !!!")
                            }
                        } else {
                            items.add(SimpleuserItem(it.toObject(User::class.java)!!, context))
                        }
                    }
                }
                onListen(items)
            }
    }


    fun addUserOnGroupViaDynamicLink(groupeId: String, onComplete: (isOk: Boolean) -> Unit) {

        // on commence par recuperer les membres du groupe courant afin de vérifier que l'utilisateur n'en fait pas encore partir
        findSpecificGroupByID(groupeId, onComplete = {
            val members = it.members
            var userIsIngroup = false
            // on ajoute l'utilisateur dans le groupe que s'il n'y était pas déja
            for (phoneUser in members!!) {
                if(phoneUser ==FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                    userIsIngroup = true
            }

            // on ajoute l'utilisateur dans le groupe si il n'y était pas déja

            if (!userIsIngroup){
                //on ajoute le groupe dans la reference de l'utilisateur
                addObjectiveGroupInfoToUser(
                    groupeId,
                    FirebaseAuth.getInstance().currentUser?.phoneNumber!!,
                    onComplete = {
                        if (it) {
                            val curentUser = User(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,"","","")
                            val listSingleUser= ArrayList<User>()
                            listSingleUser.add(curentUser)
                            // on ajoute le numéro de téléphone de l'utilisateur dans la liste des numéro du groupe
                            addMemberToObjectiveGroup(groupeId,  listSingleUser, onComplete = {
                                onComplete(it)
                            } )
                        } else
                            onComplete(false)
                    })
            }else onComplete(false)

        })
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

}