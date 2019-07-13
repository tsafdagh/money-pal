package com.kola.moneypal.RecycleView.item

import android.content.Context
import com.kola.moneypal.R
import com.kola.moneypal.entities.ImageMessage
import com.kola.moneypal.glide.GlideApp
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.StorageUtil

import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_image_message_groupe.*

class ImageMessageItemGroup(val message: ImageMessage,
                            val context:Context):MessageItem(message) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        GlideApp.with(context)
            .load(message.imagePath)
            .placeholder(R.drawable.ic_gallery)
            .into(viewHolder.imageView_message_image_groupe)

        FireStoreUtil.getUserByPhoneNumber(message.senderNumber, onComplete = {
            viewHolder.textView_sender_name_groupe.text = it.userName
        })
    }

    override fun getLayout()= R.layout.item_image_message_groupe

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is ImageMessageItemGroup)
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