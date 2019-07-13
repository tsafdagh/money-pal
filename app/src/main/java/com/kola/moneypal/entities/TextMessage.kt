package com.kola.moneypal.entities

import java.util.*

class TextMessage(val text: String,
                  override val time: Date,
                  override val senderNumber: String,
                  override val type: String = MessageType.TEXT)
    :Message {
    constructor(): this("", Date(0), "")
}