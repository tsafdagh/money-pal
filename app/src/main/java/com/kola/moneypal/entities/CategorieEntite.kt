package com.kola.moneypal.entities

object CategorieNature {
    const val NATURE_TRANSFERT_ARGENT = 1
    const val NATURE_ACHAT_CREDIT = 2
    const val NATURE_ACHAT_CONNEXION = 3
    const val NATURE_FACTURE_ENEO = 4
    const val NATURE_DEPOS_ARGENT = 5
    const val NATURE_LAST_TRANSACTION = 6
    const val NATURE_RETRAIT_ARGENT= 7
    const val NATURE_ALL = 8
}

data class CategorieEntite(
    val libele: String?,
    val imageurl: String,
    val natureCategorie: Int
)