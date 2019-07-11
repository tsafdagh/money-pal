package com.kola.moneypal.RecycleView.item

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.R
import com.kola.moneypal.entities.UserGroupeEntitie
import com.kola.moneypal.glide.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_item_user_group_transac.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.textColor
import org.jetbrains.anko.toast

class UserGroupeitem(
    val usercontribution: UserGroupeEntitie,
    val context: Context
) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.apply {
            id_text_view_name.text = usercontribution.username
            id_textview_date.text = usercontribution.contributionDate
            val montantFcfA = usercontribution.contributionMontant.toString() + " FCFA"
            id_tv_member_montant.text = montantFcfA
            if (usercontribution.imageSrc != "") {
                GlideApp.with(context)
                    .load(usercontribution.imageSrc)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.noun_user_group_)
                    .into(viewHolder.imageView_profile_picture)
            }

/*            cardView_item_person_group.setOnClickListener {
                if(usercontribution.phoneNumber != FirebaseAuth.getInstance().currentUser?.phoneNumber){
                    context.toast("evenement de clique")
                    withItems(usercontribution)
                }
            }*/
        }



        chekColor(viewHolder)

        if (usercontribution.phoneNumber == FirebaseAuth.getInstance().currentUser?.phoneNumber) {
            viewHolder.apply {
                contributed_user_cardView.setCardBackgroundColor(Color.parseColor("#ff4901"))
                id_tv_member_montant.textColor = Color.WHITE
            }
        } else {
            viewHolder.apply {
                contributed_user_cardView.setCardBackgroundColor(Color.TRANSPARENT)
                id_tv_member_montant.textColor = Color.WHITE
            }
            chekColor(viewHolder)

        }

    }

    private fun chekColor(viewHolder: ViewHolder) {
        if (usercontribution.contributionMontant == 0.0) {
            viewHolder.id_tv_member_montant.textColor = Color.RED
        } else
            viewHolder.id_tv_member_montant.textColor = Color.GREEN
    }


    /*fun withItems(usercontribution: UserGroupeEntitie) {

        val items = arrayOf(context.getString(R.string.item_alert_dialog_lis_appelr)+" "+usercontribution.username,
            context.getString(R.string.item_alert_dialog_transfert_argen)+" "+usercontribution.username,
            "Contribuer pour "+usercontribution.username)
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            setTitle("Choisir une action")
            setItems(items) { dialog, which ->
                when(items[which]){
                    items[0] ->{
                        appeler(usercontribution.phoneNumber)
                    }
                    items[1] ->{
                        context.toast("Transferer de l'argent")
                    }
                    items[2] ->{
                        context.toast("Contribuer")
                    }
                    else ->{
                        context.toast("Action inconnue")

                    }
                }
            }

            //setPositiveButton("OK", positiveButtonClick)
            setNegativeButton("ANULLER", negativeButton)
            show()
        }
    }*/


    /*val MY_PERMISSIONS_REQUEST_PONE_CALL =2
    private fun appeler(phoneNumber: String) {
        //context.makeCall(phoneNumber)


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(context,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_PONE_CALL)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            context.makeCall(phoneNumber)
        }

    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(context,
            android.R.string.no, Toast.LENGTH_SHORT).show()
    }
    val negativeButton = { dialog: DialogInterface, which: Int ->

    }*/

    override fun getLayout() = R.layout.row_item_user_group_transac
}