package com.kola.moneypal.entities

import java.util.*

class ObjectiveGroup(val AdminPhoneNumber: String,
                     val groupeName: String,
                     val groupeDescription:String,
                     val objectiveamount:Number,
                     val createdAt: Date,
                     val groupIcon: String,
                     val members: MutableList<String>?
) {
    constructor(): this("","","", 0,Date(0),"",null)
}