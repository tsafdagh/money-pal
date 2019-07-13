package com.kola.moneypal.entities

import java.util.*

class ImageMessage(val imagePath: String,
                    override val time: Date,
                    override val senderNumber: String,
                    override val type: String = MessageType.IMAGE)
    :Message {
    constructor(): this("", Date(0), "")
}