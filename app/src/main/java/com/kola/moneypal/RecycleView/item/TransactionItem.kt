package com.kola.moneypal.RecycleView.item

import android.content.Context
import android.graphics.Color
import android.text.format.DateFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kola.moneypal.R
import com.kola.moneypal.entities.CategorieNature
import com.kola.moneypal.entities.TransactionEntitie
import com.kola.moneypal.glide.GlideApp
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

        when(transaction.categorie){
            CategorieNature.NATURE_ACHAT_CREDIT->{
                viewHolder.id_tv_montant.textColor = Color.RED
                viewHolder.id_text_view_name.text = transaction.libeleTransaction+" à "+transaction.destinataire
                viewHolder.id_tv_montant.text = "-"+transaction.montanttransaction.toString()
                GlideApp.with(context)
                    .load(R.drawable.icon_phone_credi)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.imageView_profile_picture)

            }
            CategorieNature.NATURE_TRANSFERT_ARGENT->{
                viewHolder.id_tv_montant.textColor = Color.GREEN
                viewHolder.id_text_view_name.text = transaction.libeleTransaction+" à "+transaction.destinataire
                viewHolder.id_tv_montant.text = "+/-"+transaction.montanttransaction.toString()


                GlideApp.with(context)
                    .load(R.drawable.icon_send_money_phone)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.imageView_profile_picture)

            }
            CategorieNature.NATURE_FACTURE_ENEO->{
                viewHolder.id_tv_montant.textColor = Color.RED
                viewHolder.id_text_view_name.text = transaction.libeleTransaction
                viewHolder.id_tv_montant.text = "-"+transaction.montanttransaction.toString()

                GlideApp.with(context)
                    .load(R.drawable.icon_phone_electricity)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.imageView_profile_picture)

            }

            CategorieNature.NATURE_ACHAT_CONNEXION->{
                viewHolder.id_tv_montant.textColor = Color.RED
                viewHolder.id_text_view_name.text = transaction.libeleTransaction
                viewHolder.id_tv_montant.text = "-"+transaction.montanttransaction.toString()

                GlideApp.with(context)
                    .load(R.drawable.icon_internet_mobile)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.imageView_profile_picture)

            }

            CategorieNature.NATURE_DEPOS_ARGENT->{
                viewHolder.id_tv_montant.textColor = Color.GREEN
                viewHolder.id_text_view_name.text = transaction.libeleTransaction+context.getString(R.string.string_transaction_item_par)+" "+transaction.destinataire
                viewHolder.id_tv_montant.text = "+"+transaction.montanttransaction.toString()
                GlideApp.with(context)
                    .load(R.drawable.icon_depos)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.imageView_profile_picture)

            }
            CategorieNature.NATURE_RETRAIT_ARGENT->{
                viewHolder.id_tv_montant.textColor = Color.RED
                viewHolder.id_text_view_name.text = transaction.libeleTransaction+" "+context.getString(R.string.transaction_item_par)+" "+transaction.destinataire
                viewHolder.id_tv_montant.text = "-"+transaction.montanttransaction.toString()
                GlideApp.with(context)
                    .load(R.drawable.icon_move_money)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.icon_circle_grey)
                    .into(viewHolder.imageView_profile_picture)

            }
            else->{
                viewHolder.id_text_view_name.text = transaction.libeleTransaction
                viewHolder.id_tv_montant.text = transaction.montanttransaction.toString()
            }
        }

    }

    override fun getLayout() = R.layout.row_item_transaction


}