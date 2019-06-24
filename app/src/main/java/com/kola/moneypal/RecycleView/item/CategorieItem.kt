package com.kola.moneypal.RecycleView.item

import android.content.Context
import com.kola.moneypal.R
import com.kola.moneypal.entities.CategorieEntite
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_item_categories.*

class CategorieItem(val categorieItm: CategorieEntite,
                    val context: Context):Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.id_label_cartegorie.text =categorieItm.libele
    }

    override fun getLayout()= R.layout.row_item_categories
}