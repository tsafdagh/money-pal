package com.kola.moneypal.RecycleView.item

import android.content.Context
import android.graphics.Color
import com.kola.moneypal.R
import com.kola.moneypal.entities.UserGroupeEntitie
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_item_user_group_transac.*
import org.jetbrains.anko.textColor

class UserGroupeitem (val usercontribution: UserGroupeEntitie,
                      val context: Context
) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.id_text_view_name.text = usercontribution.username
        viewHolder.id_textview_date.text = usercontribution.contributionDate
        viewHolder.id_tv_member_montant.text = usercontribution.contributionMontant.toString()
        if(usercontribution.contributionMontant < 0){
            viewHolder.id_tv_member_montant.textColor = Color.RED
        }else
            viewHolder.id_tv_member_montant.textColor = Color.GREEN

    }

    override fun getLayout() = R.layout.row_item_user_group_transac
}