package com.kola.moneypal.entities


/** Cette classe a pour but de formater les données de groupe de contribution pour un utilisateur donner*/
data class UserInfoForGroup(
    val contributed_date: String,
    val montant_cotiser: Double,
    val objectiveGroupeId: String
){
    constructor() : this("",0.0,"")
}