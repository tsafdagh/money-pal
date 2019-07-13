package com.kola.moneypal.entities

import java.util.*


object MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    val time: Date
    val senderNumber: String
    val type: String
}