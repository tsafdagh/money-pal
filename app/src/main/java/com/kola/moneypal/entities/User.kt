package com.kola.moneypal.entities

data class User(
    val phoneNumber: String,
    val userName: String,
    val email: String?,
    val profilePicturePath: String?
) {
    constructor() : this("", "", null, null)
}