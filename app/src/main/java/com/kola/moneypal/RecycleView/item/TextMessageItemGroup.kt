package com.kola.moneypal.RecycleView.item

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.R
import com.kola.moneypal.entities.TextMessage
import com.kola.moneypal.utils.FireStoreUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message_groupe.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import java.text.SimpleDateFormat


class TextMessageItemGroup(
    val language: String,
    val message: TextMessage,
    val context: Context
) : Item() {
    var isSelectet = false
    private val colorTranslateText = Color.GRAY
    private val colorSrcText = Color.BLACK

    override fun bind(viewHolder: ViewHolder, position: Int) {

        var senderName: String = ""
        val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time_groupe.text = dateFormat.format(message.time)

        var origineLanguageCode = "?"
        // on recupère zt on affiche le nom de de celui qui a envoyer le message
        FireStoreUtil.getUserByPhoneNumber(message.senderNumber, onComplete = {
            senderName = "me"
            // on controle si le message ne provient pas del'utiliisateur couran
            if (FirebaseAuth.getInstance().currentUser?.phoneNumber != message.senderNumber)
                senderName = it.userName

            viewHolder.textView_sender_name_txt.text = senderName
        })

        viewHolder.textView_message_text_group.text = message.text
        viewHolder.textView_message_text_group.setTextColor(colorSrcText)
        viewHolder.textView_message_text_group.setTypeface(null, Typeface.NORMAL)
        viewHolder.button_no_translate_item_group.visibility = View.GONE

        viewHolder.button_translate_item_groupe.text = origineLanguageCode

        setMessageRootGravity(viewHolder)
    }

    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderNumber == FirebaseAuth.getInstance().currentUser?.phoneNumber) {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_primary_color
                val Iparams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = Iparams
            }
        } else {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_white
                val Iparams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = Iparams
            }
        }
    }


    override fun getLayout() = R.layout.item_text_message_groupe

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TextMessageItem)
            return false
        if (this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }
}