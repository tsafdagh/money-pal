package com.kola.moneypal.RecycleView.item

import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat
import com.kola.moneypal.R
import com.kola.moneypal.entities.TransactionEntitie
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_item_transaction.*
import org.jetbrains.anko.textColor



class TransactionItem(
    val transaction: TransactionEntitie,
    val context: Context
) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.id_text_view_name.text = transaction.libeleTransaction+" à "+transaction.destinataire
        val convertedDate = convertDate(transaction.dateTransaction,"dd/MM/yyyy hh:mm:ss")
        viewHolder.id_textview_date.text = convertedDate
        viewHolder.id_tv_montant.text = transaction.montanttransaction.toString()
        if(transaction.montanttransaction < 0){
            viewHolder.id_tv_montant.textColor = Color.RED
        }else
            viewHolder.id_tv_montant.textColor = Color.GREEN

    }

    override fun getLayout() = R.layout.row_item_transaction

    fun convertDate(dateInMilliseconds: String, dateFormat: String): String {
        return DateFormat.format(dateFormat, java.lang.Long.parseLong(dateInMilliseconds)).toString()
    }
}