package com.kola.moneypal.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ObjectiveGroup(val AdminPhoneNumber: String,
                          val groupeName: String,
                          val groupeDescription:String,
                          val objectiveamount:Double = 0.0,
                          var courentAmount: Double =0.0,
                          val createdAt: Date,
                          val DateEcheance: String,
                          val groupIcon: String,
                          val members: MutableList<String>?
): Parcelable {
    constructor(): this("","","", 0.0,0.0,Date(0),"","",null)
}