package com.kola.moneypal.RecycleView.item

import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat
import com.kola.moneypal.R
import com.kola.moneypal.entities.CategorieNature
import com.kola.moneypal.entities.TransactionEntitie
import com.kola.moneypal.utils.AnotherUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_item_transaction.*
import org.jetbrains.anko.textColor



class TransactionItem(
    val transaction: TransactionEntitie,
    val context: Context
) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val convertedDate = AnotherUtil.convertDate(transaction.dateTransaction,"dd/MM/yyyy hh:mm:ss")
        viewHolder.id_textview_date.text = convertedDate
        viewHolder.id_tv_montant.text = transaction.montanttransaction.toString()

        when(transaction.categorie){
            CategorieNature.NATURE_ACHAT_CREDIT->{
                viewHolder.id_tv_montant.textColor = Color.RED
                viewHolder.id_text_view_name.text = transaction.libeleTransaction+" à "+transaction.destinataire
            }
            CategorieNature.NATURE_TRANSFERT_ARGENT->{
                viewHolder.id_tv_montant.textColor = Color.GREEN
                viewHolder.id_text_view_name.text = transaction.libeleTransaction+" à "+transaction.destinataire
            }
            CategorieNature.NATURE_FACTURE_ENEO->{
                viewHolder.id_tv_montant.textColor = Color.RED
                viewHolder.id_text_view_name.text = transaction.libeleTransaction
            }

            CategorieNature.NATURE_ACHAT_CONNEXION->{
                viewHolder.id_tv_montant.textColor = Color.RED
                viewHolder.id_text_view_name.text = transaction.libeleTransaction
            }

            CategorieNature.NATURE_DEPOS_ARGENT->{
                viewHolder.id_tv_montant.textColor = Color.GREEN
                viewHolder.id_text_view_name.text = transaction.libeleTransaction+context.getString(R.string.string_transaction_item_par)+" "+transaction.destinataire
            }
        }

    }

    override fun getLayout() = R.layout.row_item_transaction


}