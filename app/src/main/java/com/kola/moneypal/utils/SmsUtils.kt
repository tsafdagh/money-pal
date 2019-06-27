package com.kola.moneypal.utils

import android.content.Context
import android.net.Uri
import com.kola.moneypal.entities.CategorieEntite
import com.kola.moneypal.entities.CategorieNature
import com.kola.moneypal.entities.TransactionEntitie
import com.kola.moneypal.mes_exemple.smsObjet
import org.jetbrains.anko.toast

object SmsUtils {
    fun getMessagebyCriterion(
        context: Context,
        uriString: String,
        selection: String?
    )
            : ArrayList<smsObjet> {

        val lstSms = ArrayList<smsObjet>()
        var objSms: smsObjet
        val message = Uri.parse("content://sms/$uriString")
        val cr = context.contentResolver

        // on va faire le trie par ordre de date décroissant
        //val SORT_ORDER = "date DESC"
        val c = cr.query(message, null, selection, null, null)
        //context.startManagingCursor(c)
        val totalSMS = c!!.count

        if (c!!.moveToFirst()) {
            for (i in 0 until totalSMS) {


                val id = c!!.getString(c!!.getColumnIndexOrThrow("_id"))
                val address = c!!.getString(
                    c!!.getColumnIndexOrThrow("address")
                )

                val message = c!!.getString(c!!.getColumnIndexOrThrow("body"))
                val readState = c!!.getString(c!!.getColumnIndex("read"))
                val time = c!!.getString(c!!.getColumnIndexOrThrow("date"))
                var foldername = ""
                if (c!!.getString(c!!.getColumnIndexOrThrow("type")).contains("1")) {
                    foldername = "inbox"
                } else {
                    foldername = "sent"
                }

                objSms = smsObjet(id, address, message, readState, time, foldername)
                //context.toast("new sms $objSms")

                lstSms.add(objSms)
                c!!.moveToNext()
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c!!.close()

        return lstSms

    }


    /**Ces modèles representent les contenus courant des trasaction Mtn money et orange Money
    ils son utilisé dans le code pour détecter les types de messages**/

    object modelTransfertArgentOrange {
        val motClefEntree = "Transfert de "
        val motClefMilieu = " vers "
        val motClefFin = " Montant Net:"
    }

    object modelDepostArgentOrange {
        val motClefEntree = "Depot effectue par "
        val motClefMilieu = "to"
        val motClefMilieu2 = "Informations detaillees: "
        val motClefFin = " ID transaction :"
    }

    object modelAchatDeCreditOrange {
        val motClefEntre = "Rechargement reussi. Montant de la transaction"
        val motClefMilieu = "Nouveau Solde :"
        val motClefFin = "Other msisdn"
    }

    object modelAchatDeConnexionOrange {
        val motClefEntree = "Paiement de INTERNET MOBILE reussi par"
        val motClefMilieu = "Montant:"
        val motClefFin = "Solde:"
    }

    object modelFactureMtnEneo {
        val motClefEntree = "Votre paiement de"
        val motClefMilieu1 = " pour ENEO a ete effectue le"
        val motClefMilieu2 = "Votre nouveau solde: "
        val motClefFin = "Frais:"
    }

    object modelFactureMtnCredit {
        val motClefEntree = "Votre paiement de "
        val motClefMilieu1 = " pour MTNC AIRTIME"
        val motClefMilieu2 = "a ete effectue le"
        val motClefMilieu3 = "Votre nouveau solde:"
        val motClefFin = "Frais:"
    }


    /**ette fonction a pour but d'identifier le type d'un message et de le formater pour le retourner**/
    fun findSpecificSpending(lisMySms: ArrayList<smsObjet>, natureRecherche: Int): ArrayList<TransactionEntitie> {

        fun isTransfertArgentOrange(smsMessage: String): Boolean {
            return smsMessage.contains(modelTransfertArgentOrange.motClefEntree, false) &&
                    smsMessage.contains(modelTransfertArgentOrange.motClefMilieu, false) &&
                    smsMessage.contains(modelTransfertArgentOrange.motClefFin, false)

        }

        fun isAchatDeCreditOrange(smsMessage: String): Boolean {
            return smsMessage.contains(modelAchatDeCreditOrange.motClefEntre, false) &&
                    smsMessage.contains(modelAchatDeCreditOrange.motClefMilieu, false) &&
                    smsMessage.contains(modelAchatDeCreditOrange.motClefFin, false)
        }

        fun isAchatDeConnexionOrange(smsMessage: String): Boolean {
            return smsMessage.contains(modelAchatDeConnexionOrange.motClefEntree, false) &&
                    smsMessage.contains(modelAchatDeConnexionOrange.motClefMilieu, false) &&
                    smsMessage.contains(modelAchatDeConnexionOrange.motClefFin, false)

        }

        fun isFacturecreditmtn(smsMessage: String): Boolean {
            return smsMessage.contains(modelFactureMtnCredit.motClefEntree, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefMilieu1, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefMilieu2, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefMilieu3, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefFin, true)
        }

        fun isAchatFactureMtnEneo(smsMessage: String): Boolean {
            return smsMessage.contains(modelFactureMtnEneo.motClefEntree, false) &&
                    smsMessage.contains(modelFactureMtnEneo.motClefMilieu1, false) &&
                    smsMessage.contains(modelFactureMtnEneo.motClefFin, false) &&
                    smsMessage.contains(modelFactureMtnEneo.motClefMilieu2, false)
        }

        fun isModelDeposArgentOrange(smsMessage: String): Boolean {
            return smsMessage.contains(modelDepostArgentOrange.motClefEntree, false) &&
                    smsMessage.contains(modelDepostArgentOrange.motClefMilieu, false) &&
                    smsMessage.contains(modelDepostArgentOrange.motClefMilieu2, false) &&
                    smsMessage.contains(modelDepostArgentOrange.motClefFin, false)
        }

        val listeRetour = ArrayList<TransactionEntitie>()

        when (natureRecherche) {
            CategorieNature.NATURE_TRANSFERT_ARGENT -> {

                for (mySms in lisMySms) {
                    // traitement des sms du type transfert d'argent orange
                    if (isTransfertArgentOrange(mySms.msg)) {
                        // on recupere le nom de celui qui envoie :
                        var newStrinForSender = mySms.msg.split(Regex("vers |reussi"))
                        val destinataire = newStrinForSender[1] // le nom du destinataire est à l'indexe numéro 1

                        // on reecupere le montant net envoyé
                        var newStrinForMontant = mySms.msg.split(Regex("Montant Net: | FCFA,"))


                        val montantNentEnvoye =
                            newStrinForMontant[newStrinForMontant.size - 2] // le montant net se trouve à la position numéro 3

                        // on recupère le nouveau solde
                        var newStrinForNewSolde = mySms.msg.split(Regex("Nouveau Solde: | FCFA."))
                        val nouveauSolde = newStrinForNewSolde[newStrinForNewSolde.size - 2]

                        // on retourne un nouvel orjet de type transaction
                        listeRetour.add(
                            TransactionEntitie(
                                "Transfert",
                                mySms.time,
                                montantNentEnvoye.toDouble(),
                                "",
                                CategorieNature.NATURE_TRANSFERT_ARGENT,
                                destinataire,
                                nouveauSolde.toDouble()
                            )
                        )
                    }
                }
                return listeRetour
            }

            CategorieNature.NATURE_DEPOS_ARGENT -> {

                for (mySms in lisMySms) {
                    // traitement des sms du type transfert d'argent orange
                    if (isModelDeposArgentOrange(mySms.msg)) {

                        val newListForMontantDepos = mySms.msg.split(Regex("Montant de transaction : | FCFA,"))
                        var montant = newListForMontantDepos[1]

                        val newListForSender = mySms.msg.split(Regex("Depot effectue par | to "))
                        var sender = newListForSender[1]


                        val newListForNouveauSolde = mySms.msg.split(Regex("Nouveau Solde : | FCFA."))
                        var newSolde = newListForNouveauSolde[newListForNouveauSolde.size - 2]

                        // on recupère le nouveau solde
                        var newStrinForNewSolde = mySms.msg.split(Regex("Nouveau Solde: | FCFA."))

                        // on retourne un nouvel orjet de type transaction
                        listeRetour.add(
                            TransactionEntitie(
                                "Dépos ",
                                mySms.time,
                                montant.toDouble(),
                                "",
                                CategorieNature.NATURE_DEPOS_ARGENT,
                                sender,
                                newSolde.toDouble()
                            )
                        )
                    }
                }
                return listeRetour
            }

            CategorieNature.NATURE_ACHAT_CREDIT -> {
                for (mySms in lisMySms) {
                    // traitement des sms du type achat de credit orange
                    if (isAchatDeCreditOrange(mySms.msg)) {
                        val newListForMontantCredit = mySms.msg.split(Regex("Montant de la transaction : |FCFA, ID"))
                        var valMontantCrdit = newListForMontantCredit[1]  // on recupere le montant du credit acheté
                        val newListForDestinateur = mySms.msg.split(Regex("Other msisdn"))
                        var destinataire = newListForDestinateur[1] // on recupère le numero du destinataire du credit
                        val newListForNewSolde = mySms.msg.split(Regex("Nouveau Solde :|FCFA Other"))
                        var nouveauSolde = newListForNewSolde[1]

                        // on retourne un nouvel orjet de type transaction
                        listeRetour.add(
                            TransactionEntitie(
                                "Achat crédit",
                                mySms.time,
                                valMontantCrdit.toDouble(),
                                "",
                                CategorieNature.NATURE_ACHAT_CREDIT,
                                destinataire,
                                nouveauSolde.toDouble()
                            )
                        )

                    }

                }
                return listeRetour
            }

            CategorieNature.NATURE_FACTURE_ENEO -> {
                for (mySms in lisMySms) {
                    // traitement des sms du type facture eneo MTN
                    if (isAchatFactureMtnEneo(mySms.msg)) {
                        val newListForMontantFacture = mySms.msg.split(Regex("paiement de | FCFA pour"))
                        var valMontantFacture = newListForMontantFacture[1]  // on recupere le montant de la facture

                        val newListForMontantNewSolde = mySms.msg.split(Regex("Votre nouveau solde: |FCFA. Frais"))
                        var nouveauSolde = newListForMontantNewSolde[1]

                        // on retourne un nouvel orjet de type transaction
                        listeRetour.add(
                            TransactionEntitie(
                                "Facture Eneo",
                                mySms.time,
                                valMontantFacture.toDouble(),
                                "",
                                CategorieNature.NATURE_FACTURE_ENEO,
                                "Eneo",
                                nouveauSolde.toDouble()
                            )
                        )

                    }

                }
                return listeRetour
            }

            CategorieNature.NATURE_ACHAT_CONNEXION -> {
                for (mySms in lisMySms) {
                    // traitement des sms du type achat de connexion orange
                    if (isAchatDeConnexionOrange(mySms.msg)) {
                        val newListForMontantPaiementInternet = mySms.msg.split(Regex(", Montant:| FCFA."))
                        var valMontantPaiement =
                            newListForMontantPaiementInternet[1]  // on recupere le montant du paiement

                        val newListForMontantNewSolde = mySms.msg.split(Regex("Solde: | FCFA."))
                        var nouveauSolde = newListForMontantNewSolde[2]

                        // on retourne un nouvel orjet de type transaction
                        listeRetour.add(
                            TransactionEntitie(
                                "Paiement Internet",
                                mySms.time,
                                valMontantPaiement.toDouble(),
                                "",
                                CategorieNature.NATURE_ACHAT_CONNEXION,
                                "",
                                nouveauSolde.toDouble()
                            )
                        )

                    }

                }
                return listeRetour
            }


            // On détecte automatiquement le type du dernier message et on rappelle la fonction courante avec le type détecté
            CategorieNature.NATURE_LAST_TRANSACTION -> {

                if (lisMySms.size > 0) {
                    var lastSMS = lisMySms.get(0)
                    lisMySms.clear()

                    // traitement des sms du type achat de connexion orange
                    if (isAchatDeConnexionOrange(lastSMS.msg)) {
                        lisMySms.add(lastSMS)
                        findSpecificSpending(lisMySms, CategorieNature.NATURE_ACHAT_CONNEXION)
                    }
                    if (isAchatDeCreditOrange(lastSMS.msg)) {
                        lisMySms.add(lastSMS)
                        findSpecificSpending(lisMySms, CategorieNature.NATURE_ACHAT_CREDIT)
                    }
                    if (isAchatFactureMtnEneo(lastSMS.msg)) {
                        lisMySms.add(lastSMS)
                        findSpecificSpending(lisMySms, CategorieNature.NATURE_FACTURE_ENEO)
                    }
                    if (isModelDeposArgentOrange(lastSMS.msg)) {
                        lisMySms.add(lastSMS)
                        findSpecificSpending(lisMySms, CategorieNature.NATURE_DEPOS_ARGENT)
                    }
                    if (isTransfertArgentOrange(lastSMS.msg)) {
                        lisMySms.add(lastSMS)
                        findSpecificSpending(lisMySms, CategorieNature.NATURE_TRANSFERT_ARGENT)
                    }

                }
            }

        }
        return listeRetour
    }

}