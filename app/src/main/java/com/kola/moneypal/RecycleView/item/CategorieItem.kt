package com.kola.moneypal.RecycleView.item

import android.content.Context
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kola.moneypal.R
import com.kola.moneypal.entities.CategorieEntite
import com.kola.moneypal.entities.CategorieNature
import com.kola.moneypal.fragments.HomeFragment
import com.kola.moneypal.glide.GlideApp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_item_categories.*
import kotlinx.android.synthetic.main.row_item_transaction.*

class CategorieItem(val categorieItm: CategorieEntite,
                    val context: Context):Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.id_label_cartegorie.text =categorieItm.libele

        when (categorieItm.natureCategorie){
            CategorieNature.NATURE_ACHAT_CREDIT->{
                GlideApp.with(context)
                    .load(R.drawable.icon_phone_credi)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.id_imgview_categorie_item)
            }
            CategorieNature.NATURE_RETRAIT_ARGENT->{
                GlideApp.with(context)
                    .load(R.drawable.icon_move_money)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.id_imgview_categorie_item)
            }
            CategorieNature.NATURE_TRANSFERT_ARGENT->{
                GlideApp.with(context)
                    .load(R.drawable.icon_send_money_phone)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.id_imgview_categorie_item)
            }
            CategorieNature.NATURE_DEPOS_ARGENT->{
                GlideApp.with(context)
                    .load(R.drawable.icon_depos)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.id_imgview_categorie_item)
            }
            CategorieNature.NATURE_FACTURE_ENEO->{

                GlideApp.with(context)
                    .load(R.drawable.icon_phone_electricity)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.id_imgview_categorie_item)
            }
            CategorieNature.NATURE_ACHAT_CONNEXION->{
                GlideApp.with(context)
                    .load(R.drawable.icon_internet_mobile)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.id_imgview_categorie_item)
            }
        }
    }

    override fun getLayout()= R.layout.row_item_categories
}