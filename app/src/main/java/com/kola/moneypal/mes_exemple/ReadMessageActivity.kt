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
import java.util.*
import kotlin.collections.ArrayList

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
            setSmsMessages("inbox", "address LiKE '${getString(R.string.phone_number)}'")
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
            setSmsMessages("inbox", "address LiKE '${getString(R.string.phone_number)}'")
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == requestReadSms)  setSmsMessages("", "address LiKE '${getString(R.string.phone_number)}'")
    }

    private fun setSmsMessages(uriString: String, selection: String?) {

        val smsList = ArrayList<SmsData>()

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
            } while (cursor.moveToFirst())
        }

        cursor.close()

        val adapter = ListAdapter(this, smsList)

        sms_list_view.adapter = adapter
    }
}
