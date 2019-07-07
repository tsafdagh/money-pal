package com.kola.moneypal.utils

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.kola.moneypal.entities.User

object LocalPhoneUtil {
    fun getAllContacts(context: Context): ArrayList<User> {

        val contactModelArrayList: ArrayList<User>? = ArrayList()
        val phones = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (phones!!.moveToNext()) {
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val photoUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
            val email =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.SEND_TO_VOICEMAIL))
            val contactModel = User(phoneNumber, name, email, photoUri)

            contactModelArrayList!!.add(contactModel)
            Log.d("LocalPhoneUtil ", "$name  $phoneNumber  $photoUri  $email")
        }
        phones.close()

        return contactModelArrayList!!
    }
}