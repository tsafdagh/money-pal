package com.kola.moneypal.RecycleView.item

import android.content.Context
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kola.moneypal.R
import com.kola.moneypal.entities.ObjectiveGroup
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_objective_group.*

class ObjectivegroupItem (val obgroup: ObjectiveGroup,
                          val obgroupId: String,
                          val context: Context
) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.id_text_view_name_groupe_item.text =obgroup.groupeName
        viewHolder.id_textview_description_group_item.text = obgroup.groupeDescription
       /* if(obgroup.groupIcon != ""){
            GlideApp.with(context)
                .load(obgroup.groupIcon)
                .transform(CircleCrop())
                .placeholder(R.drawable.noun_user_group_)
                .into(viewHolder.id_imag_group_item)
        }*/

    }

    override fun getLayout() = R.layout.item_objective_group

}