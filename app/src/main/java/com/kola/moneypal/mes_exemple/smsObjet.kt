package com.kola.moneypal.mes_exemple

data class smsObjet (val id:String,
                     val address:String,
                     val msg:String,
                     val readState:String,
                     val time:String,
                     val folderName:String){
    constructor():this("","","","","","")
}