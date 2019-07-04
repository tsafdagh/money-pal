package com.kola.moneypal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_details_transacton.*

class DetailsTransactonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_transacton)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var detai11 = intent.getStringExtra("PARAM1")
        var detai12 = intent.getStringExtra("PARAM2")
        var detai13 = intent.getStringExtra("PARAM3")
        var detai14 = intent.getStringExtra("PARAM4")
        var detai15 = intent.getStringExtra("PARAM5")

        id_detail1.text = detai11
        id_detail2.text = detai12
        id_detail3.text = detai13
        id_detail4.text = detai14
        id_detail5.text = detai15

    }
}
