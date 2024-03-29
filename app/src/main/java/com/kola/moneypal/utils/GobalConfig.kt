package com.kola.moneypal.utils

import com.kola.moneypal.entities.User

object GobalConfig {
    val COUREN_AMOUNT_OF_CURENT_CONTRIBUTED_AMOUNT = "COUREN_AMOUNT_OF_CURENT_CONTRIBUTED_AMOUNT"
    val REFERENCE_OBJECTIVE_GROUPE_STORAGE ="objectiveGroupes_images"
    val REFERENCE_USER_IMAGE_PROFIL = "user_profils_images/"
    val REFERENCE_OBJECTIVE_GROUPE_COLLECTION = "objectiveGroupes"
    val REFFERENCE_USERS = "users"
    val REFERENCE_GROUPE_OF_ONE_USER = "groupes"
    val REFERENCE_ID_GROUPE_OF_ONE = "objectiveGroupeId"

    val EXTRAT_REFERENCE_OBJ_GROUP_STRING = "objective_group"
    val EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING = "objective_group_id"
    val CONTRIBUTED_USER_AMOUNT = "montant_cotiser"
    val DATE_CONTRIBUTED_ON_GROUP = "contributed_date"

    val USER_OBJECTIVES_GROUPS = "user_objective_groups"



    var contributedAmountForGroup :Double = 0.0
    val listIdUserForDynimicLinks = arrayListOf<User>()
}