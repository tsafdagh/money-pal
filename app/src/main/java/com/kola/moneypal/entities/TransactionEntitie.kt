package com.kola.moneypal.entities

data class TransactionEntitie(
    val libeleTransaction: String?,
    val dateTransaction: String,
    val montanttransaction: Double,
    val imageUrl: String,

    val categorie:String,
    val destinataire:String,
    val nouveauSolde:Number
){
    constructor( libeleTransaction: String?,
                 dateTransaction: String,
                 montanttransaction: Double,
                 imageUrl: String): this(libeleTransaction,dateTransaction,montanttransaction,imageUrl,"","",0.0)
}