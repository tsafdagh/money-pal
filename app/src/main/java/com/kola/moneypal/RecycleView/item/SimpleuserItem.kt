package com.kola.moneypal.RecycleView.item

import android.content.Context
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kola.moneypal.R
import com.kola.moneypal.entities.User
import com.kola.moneypal.glide.GlideApp
import com.kola.moneypal.utils.GobalConfig
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_item_simple_user.*
import org.jetbrains.anko.toast

class SimpleuserItem(
    val person: User,
    private val context: Context
) : Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.id_simple_user_username.text = person.userName
        viewHolder.id_simple_user_item_phone_number.text = person.phoneNumber

        if (person.profilePicturePath != null) {
            GlideApp.with(context)
                .load(person.profilePicturePath)
                .transform(CircleCrop())
                .placeholder(R.drawable.nom_user)
                .into(viewHolder.id_silple_user_image_view)
        }

        viewHolder.id_chekbox.setOnCheckedChangeListener{buttonView, ischecked->
           if(ischecked){
                //context.toast("utilisateur ajouter: "+person.phoneNumber)
               GobalConfig.listIdUserForDynimicLinks.add(person)

            }else{
               // context.toast("utilisateur retirer: "+person.phoneNumber)
               GobalConfig.listIdUserForDynimicLinks.remove(person)

           }
        }

    }

    override fun getLayout() = R.layout.row_item_simple_user
}