package com.kola.moneypal.utils

import android.content.Context
import android.net.Uri
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

    object modelAchatDeConnexionOrange {
        val motClefEntree = "Paiement de INTERNET MOBILE"
        val motClefMilieu1 = " reussi par "
        val motClefMilieu2 = "Montant:"
        val motClefFin = "Solde:"
    }

    object modelFactureMtnEneo {
        val motClefEntree = "Votre paiement de "
        val motClefMilieu1 = " pour ENEO"
        val motClefMilieu2 = "a ete effectue le"
        val motClefMilieu3 = "Votre nouveau solde:"
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
    fun findSpecificSpending(mySms: smsObjet): TransactionEntitie? {

        fun isTransfertArgentOrange(smsMessage: String): Boolean {
            return smsMessage.contains(modelTransfertArgentOrange.motClefEntree, false) &&
                    smsMessage.contains(modelTransfertArgentOrange.motClefMilieu, false) &&
                    smsMessage.contains(modelTransfertArgentOrange.motClefFin, false)

        }

        fun isAchatDeConnexionOrange(smsMessage: String): Boolean {
            return smsMessage.contains(modelAchatDeConnexionOrange.motClefEntree, false) &&
                    smsMessage.contains(modelAchatDeConnexionOrange.motClefMilieu1, false) &&
                    smsMessage.contains(modelAchatDeConnexionOrange.motClefMilieu2, false) &&
                    smsMessage.contains(modelAchatDeConnexionOrange.motClefFin, false)

        }

        fun isFacturecreditmtn(smsMessage: String): Boolean {
            return smsMessage.contains(modelFactureMtnCredit.motClefEntree, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefMilieu1, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefMilieu2, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefMilieu3, true) &&
                    smsMessage.contains(modelFactureMtnCredit.motClefFin, true)
        }

        fun modelFactureMtnEneo(smsMessage: String): Boolean {
            return smsMessage.contains(modelFactureMtnEneo.motClefEntree, false) &&
                    smsMessage.contains(modelFactureMtnEneo.motClefMilieu1, false) &&
                    smsMessage.contains(modelFactureMtnEneo.motClefMilieu2, false) &&
                    smsMessage.contains(modelFactureMtnEneo.motClefFin, false) &&
                    smsMessage.contains(modelFactureMtnEneo.motClefMilieu3, false)
        }

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
            return TransactionEntitie(
                "Transfert",
                mySms.time,
                montantNentEnvoye.toDouble(),
                "",
                "Transfert",
                destinataire,
                nouveauSolde.toDouble()
            )
        }
        if (isAchatDeConnexionOrange(mySms.msg)) {
            //TODO code pour le traitement de message de type achat de connexion orange et retourn d'un objet
        }
        if (isFacturecreditmtn(mySms.msg)) {
            //TODO code pour le traitement de message de type achat de credit mtn et retourn d'un objet
        }
        if (modelFactureMtnEneo(mySms.msg)) {
            //TODO code pour le traitement de message de type facture eneo mtn et retourn d'un objet
        }

        return null
    }

}