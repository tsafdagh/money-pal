package com.kola.moneypal

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.hover.sdk.actions.HoverAction
import com.hover.sdk.api.Hover
import com.hover.sdk.api.HoverParameters
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.glide.GlideApp
import com.kola.moneypal.utils.GobalConfig
import com.kola.moneypal.utils.PayementConfiguration
import kotlinx.android.synthetic.main.activity_payement.*
import org.jetbrains.anko.toast
import com.hover.sdk.permissions.PermissionActivity
import com.kola.moneypal.utils.FireStoreUtil
import org.jetbrains.anko.indeterminateProgressDialog
import java.util.ArrayList


class PayementActivity : AppCompatActivity(), Hover.DownloadListener {
    var montantRestant = 0.0
    override fun onError(p0: String?) {
        // toast("erreur de télécharger de l'action " + p0)

    }

    override fun onSuccess(p0: ArrayList<HoverAction>?) {
        // toast("Action télécharger avec succes ${p0.toString()}")
    }

    private lateinit var objGroupe: ObjectiveGroup
    private lateinit var groupId: String

    private var montantAPayer: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payement)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)


        objGroupe = (intent.getSerializableExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING) as ObjectiveGroup?)!!
        groupId = intent.getStringExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING)!!

        FireStoreUtil.evaluateCurrentAmountOfObjectiveGroup(this, groupId, onListen = {
            val currentAmount = GobalConfig.contributedAmountForGroup
            montantRestant = objGroupe.objectiveamount - currentAmount
            val montantRestantString =
                getString(R.string.montant_restant_text) + (montantRestant).toString() + " FCFA"
            id_montant_restant_group_payement.text = montantRestantString

        })

        id_edit_nom_groupe_payement.text = objGroupe.groupeName
        id_description_group_payement.text = objGroupe.groupeDescription
        val montant = getString(R.string.text_payement_montant_a_cotiser) + objGroupe.objectiveamount + " FCFA"
        id_montant_a_cotiser_group_payement.text = montant

        id_btn_proceder_payement.setOnClickListener {
            montantAPayer = id_editText_montant_payement.text.toString()
            if (montantAPayer == "" || montantAPayer.toDouble() == 0.0) {
                id_editText_montant_payement.error = getString(R.string.ms_entre_mnt_correct)
            } else {
                if (montantAPayer.toDouble() <= montantRestant) {

                    payementProcess(montantAPayer)

                } else id_editText_montant_payement.error = getString(R.string.ms_montant_tres_grand)
            }
        }

        btn_annuler_payement.setOnClickListener {
            onBackPressed()
        }

        GlideApp.with(this)
            .load(objGroupe.groupIcon)
            .placeholder(R.drawable.noun_user_group_)
            .into(imageView_profile_groupe_picture)

        // initialisation de l'api de payement
        Hover.initialize(applicationContext, this)
        //on demande d'abord toutes les permissions
        //startActivityForResult(Intent(this, PermissionActivity::class.java), 0)
    }

    private fun payementProcess(montant: String) {

        // on vérifie si l'utilisateur a u=dans son telephone une carte SIM permettant d'éffectuer
        // le payement pour cette operateur
        //if(Hover.isActionSimPresent(PayementConfiguration.ID_ACTION_PAYEMENT_ORANGE_MONEY, this)){
        // 690935868

        val payementIntent = HoverParameters.Builder(this)
            .request(PayementConfiguration.ID_ACTION_PAYEMENT_ORANGE_MONEY)
            .extra(
                PayementConfiguration.ETAPE_1_PAYEMENT_ORANGE_MONEY,
                objGroupe.adminPhoneNumber.substring(4)// on enlève le +237
            )
            .extra(PayementConfiguration.ETAPE_2_PAYEMENT_ORANGE_MONEY, montant)
            .buildIntent()
        // pour les payement de orange money vers MTN money
        /*val payementIntent  = HoverParameters.Builder(this)
            .request(PayementConfiguration.ID_ACTION_PAYEMENT_ORANGE_MONEY_VERS_MTN)
            .extra(PayementConfiguration.ETAPE_1_PAYEMENT_ORANGE_MONEY_VERS_MTN,objGroupe.AdminPhoneNumber)
            .extra(PayementConfiguration.ETAPE_2_PAYEMENT_ORANGE_MONEY_VERS_MTN, montant.toInt().toString())
            .buildIntent()*/
        startActivityForResult(payementIntent, PayementConfiguration.REQUEST_CODE_FOR_PAYEMENY_ORANGE_MONEY)
        /* }else{
             Snackbar.make(
                 id_payement_activity,
                 getString(R.string.snack_bar_erreur_action_payement_sim),
                 Snackbar.LENGTH_LONG
             ).show()
         }*/

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // on controle le resulatat de la transaction
        if (requestCode == PayementConfiguration.REQUEST_CODE_FOR_PAYEMENY_ORANGE_MONEY && resultCode == Activity.RESULT_OK) {

            //quand la transaction a bien été initialisée
            //val sessionTextArr = data?.getStringArrayExtra("ussd_messages")
            //val uuid = data?.getStringExtra("uuid")
            val allMessage = data?.getStringArrayExtra("ussd_messages")
            val message = allMessage!!.last()
            toast("Message= $message")
            Snackbar.make(
                id_payement_activity,
                message,
                Snackbar.LENGTH_LONG
            ).show()


            //quand tout est ok on met ç jours le payement dans firebase
            val progressdialog = indeterminateProgressDialog("Mise à jours du solde dans le groupe...")
            FireStoreUtil.updateContributedAmountOnobjectiveGroupForCurentuser(groupId, montantAPayer.toDouble(), onComplete = {
                if (it){
                    toast("Payement mis à jours avec succes")
                    progressdialog.dismiss()
                    this.onBackPressed()
                }else{
                    toast("Erreur de mis à jour du payement")
                    progressdialog.dismiss()
                    Snackbar.make(
                        id_payement_activity,
                        "Erreur de mis à jour du payement",
                        Snackbar.LENGTH_LONG
                    ).show()
                    this.onBackPressed()
                }
            })

        } else {
            // en cas d'érreur de l'initialisation de la transaction
            val erroMessage = data?.getStringExtra("error")
            toast(erroMessage.toString())
            Snackbar.make(
                id_payement_activity,
                erroMessage.toString(),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onBackPressed() {
        /*val intent = Intent(this, DetailsObjectiveGroup::class.java)
        intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING, objGroupe)
        intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING, groupId)
        startActivity(intent)*/
        super.onBackPressed()

    }
}
