package com.kola.moneypal.RecycleView.item

import android.content.Context
import android.content.Intent
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kola.moneypal.DetailsObjectiveGroup
import com.kola.moneypal.R
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.glide.GlideApp
import com.kola.moneypal.utils.GobalConfig
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_objective_group.*
import org.jetbrains.anko.toast


class ObjectivegroupItem(
    val obgroup: ObjectiveGroup,
    val obgroupId: String,
    val context: Context
) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.id_text_view_name_groupe_item.text = obgroup.groupeName
        viewHolder.id_textview_description_group_item.text = obgroup.groupeDescription
        viewHolder.id_tv_montant_cotiser.text = obgroup.courentAmount.toString() + " F."
        viewHolder.id_tv_objective_amount.text = obgroup.objectiveamount.toString() + " F."

        if (obgroup.groupIcon != "") {
            GlideApp.with(context)
                .load(obgroup.groupIcon)
                .transform(CircleCrop())
                .placeholder(R.drawable.noun_user_group_)
                .into(viewHolder.id_imag_group_item)
        }

        viewHolder.cardView_item_objectiveGroupe.setOnClickListener {

            val intent = Intent(context, DetailsObjectiveGroup::class.java)
            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING, obgroup)
            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING, obgroupId)
            context.startActivity(intent)
        }
    }

    override fun getLayout() = R.layout.item_objective_group

}