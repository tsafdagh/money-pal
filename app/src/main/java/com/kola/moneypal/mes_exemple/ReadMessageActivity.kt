package com.kola.moneypal.mes_exemple

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.kola.moneypal.R
import kotlinx.android.synthetic.main.activity_read_message.*
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList
import android.content.ContentResolver


class ReadMessageActivity : AppCompatActivity() {

    private val requestReadSms: Int = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_message)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), requestReadSms)
        } else {
            setSmsMessages("inbox", "address LiKE 'MobileMoney'")
        }

        all_sms.setOnClickListener {
            setSmsMessages("", null)
        }

        inbox_sms.setOnClickListener {
            setSmsMessages("inbox", null)
        }

        outbox_sms.setOnClickListener {
            setSmsMessages("outbox", null)
        }

        sent_sms.setOnClickListener {
            setSmsMessages("sent", null)
        }

        draft_sms.setOnClickListener {
            setSmsMessages("draft", null)
        }

        one_number.setOnClickListener {
            setSmsMessages("inbox", "address LiKE 'MobileMoney'")
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == requestReadSms) setSmsMessages("inbox", "address LiKE 'MobileMoney'")
    }

    private fun setSmsMessages(uriString: String, selection: String?) {

        /*val smsList = ArrayList<SmsData>()

        val cursor = contentResolver.query(
            Uri.parse("content://sms/$uriString"),
            null,
            selection,
            null,
            null
        )

        if (cursor?.moveToFirst()!!) {
            val nameId = cursor.getColumnIndex("address")
            val messageId = cursor.getColumnIndex("body")
            val dateID = cursor.getColumnIndex("date")

            do {
                val dateString = cursor.getString(dateID)
                smsList.add(
                    SmsData(
                        cursor.getString(nameId),
                        Date(dateString.toLong()).toString(),
                        cursor.getString(messageId)
                    )
                )
                toast("new sms"+cursor.getString(messageId))
            } while (cursor.moveToFirst())
        }

        cursor.close()

        val adapter = ListAdapter(this, smsList)

        sms_list_view.adapter = adapter*/

        val lstSms = ArrayList<smsObjet>()
        var objSms: smsObjet
        val message = Uri.parse("content://sms/$uriString")
        val cr = this.contentResolver

        val c = cr.query(message, null, selection, null, null)
        this.startManagingCursor(c)
        val totalSMS = c!!.getCount()

        if (c!!.moveToFirst()) {
            for (i in 0 until totalSMS) {


                val id = c!!.getString(c!!.getColumnIndexOrThrow("_id"))
                val address = c!!.getString(c!!.getColumnIndexOrThrow("address")
                    )

                val message = c!!.getString(c!!.getColumnIndexOrThrow("body"))
                val readState = c!!.getString(c!!.getColumnIndex("read"))
                val time = c!!.getString(c!!.getColumnIndexOrThrow("date"))
                var foldername =""
                if (c!!.getString(c!!.getColumnIndexOrThrow("type")).contains("1")) {
                    foldername = "inbox"
                } else {
                    foldername = "sent"
                }

                objSms = smsObjet(id,address,message,readState,time,foldername)
                toast("new sms $objSms")

                lstSms.add(objSms)
                c!!.moveToNext()
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c!!.close()
    }
}
