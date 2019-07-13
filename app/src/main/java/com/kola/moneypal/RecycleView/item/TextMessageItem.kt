package com.kola.moneypal.RecycleView.item

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.R
import com.kola.moneypal.entities.TextMessage
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_text_message.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.wrapContent
import java.text.SimpleDateFormat

class TextMessageItem(
    val language: String,
    val message: TextMessage,
    val context: Context
) : MessageItem(message) {

    private val colorSrcText = Color.BLACK
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView_message_text.text = message.text
        viewHolder.textView_message_text.setTextColor(colorSrcText)
        viewHolder.textView_message_text.setTypeface(null, Typeface.NORMAL)
        viewHolder.button_no_translate_item.visibility = View.GONE
        viewHolder.button_no_translate_item.visibility = View.GONE
        super.bind(viewHolder, position)


    }


    private fun setTimetext(viewHolder: ViewHolder) {
        val dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT)
        viewHolder.textView_message_time.text = dateFormat.format(message.time)
    }

    private fun setMessageRootGravity(viewHolder: ViewHolder) {
        if (message.senderNumber == FirebaseAuth.getInstance().currentUser?.phoneNumber) {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_primary_color
                val Iparams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.END)
                this.layoutParams = Iparams as ViewGroup.LayoutParams?
            }
        } else {
            viewHolder.message_root.apply {
                backgroundResource = R.drawable.rect_round_white
                val Iparams = FrameLayout.LayoutParams(wrapContent, wrapContent, Gravity.START)
                this.layoutParams = Iparams
            }
        }
    }

    override fun getLayout() = R.layout.item_text_message

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