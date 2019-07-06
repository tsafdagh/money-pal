package com.kola.moneypal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.glide.GlideApp
import com.kola.moneypal.utils.GobalConfig
import kotlinx.android.synthetic.main.activity_payement.*
import org.jetbrains.anko.toast

class PayementActivity : AppCompatActivity() {

    private lateinit var objGroupe: ObjectiveGroup
    private lateinit var groupId: String

    private var montantAPayer: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payement)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)

        objGroupe = intent.getParcelableExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING)!!
        groupId = intent.getStringExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING)!!

        id_edit_nom_groupe_payement.text = objGroupe.groupeName
        id_description_group_payement.text = objGroupe.groupeDescription
        val montant = getString(R.string.text_payement_montant_a_cotiser) + objGroupe.objectiveamount + " FCFA"
        id_montant_a_cotiser_group_payement.text = montant

        id_btn_proceder_payement.setOnClickListener {
            montantAPayer = id_editText_montant_payement.text.toString().toDouble()
            toast("Declenchement du payement")
            // TODO implementation du payement
        }

        btn_annuler_payement.setOnClickListener {
            onBackPressed()
        }

        GlideApp.with(this)
            .load(objGroupe.groupIcon)
            .placeholder(R.drawable.noun_user_group_)
            .into(imageView_profile_groupe_picture)
    }

    override fun onBackPressed() {
        /*val intent = Intent(this, DetailsObjectiveGroup::class.java)
        intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING, objGroupe)
        intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING, groupId)
        startActivity(intent)*/
        super.onBackPressed()

    }
}
