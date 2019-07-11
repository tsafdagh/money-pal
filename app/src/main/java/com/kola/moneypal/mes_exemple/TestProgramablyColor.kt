package com.kola.moneypal.mes_exemple

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kola.moneypal.R
import com.kola.moneypal.mes_exemple.ThemeColors.Companion.setNewThemeColor
import kotlinx.android.synthetic.main.activity_test_programably_color.*
import java.util.*


class TestProgramablyColor : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeColors(this)

        setContentView(R.layout.activity_test_programably_color)

        btn_colot.setOnClickListener {
            val red =Color.RED
            val green = Color.GREEN
            val blue =Color.BLUE
            setNewThemeColor(this@TestProgramablyColor, red, green, blue)
        }

    }
}
