package com.kola.moneypal.mes_exemple

import android.content.Context
import androidx.annotation.ColorInt
import android.graphics.Color.parseColor
import android.R.color
import android.content.SharedPreferences
import android.graphics.Color
import com.kola.moneypal.R
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.os.Build
import android.R.id.edit
import android.app.Activity





class ThemeColors (context: Context){

    private val NAME = "ThemeColors"
    private val KEY = "color"

    @ColorInt
    var  color:Int = 0

  init {
      val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
      val stringColor = sharedPreferences.getString(KEY, "004bff")
      color = Color.parseColor("#" + stringColor!!)

      if (isLightActionBar()) context.setTheme(R.style.AppTheme)
      context.setTheme(context.resources.getIdentifier("T_$stringColor", "style", context.packageName))
  }


   companion object {
       private val NAME = "ThemeColors"
       private val KEY = "color"
       fun setNewThemeColor(activity: Activity, red: Int, green: Int, blue: Int) {
        var red = red
        var green = green
        var blue = blue
        val colorStep = 15
        red = Math.round((red / colorStep).toFloat()) * colorStep
        green = Math.round((green / colorStep).toFloat()) * colorStep
        blue = Math.round((blue / colorStep).toFloat()) * colorStep

        val stringColor = Integer.toHexString(Color.rgb(red, green, blue)).substring(2)
        val editor = activity.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
        editor.putString(KEY, stringColor)
        editor.apply()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            activity.recreate()
        else {
            val i = activity.packageManager.getLaunchIntentForPackage(activity.packageName)
            i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(i)
        }
    }}

    private fun isLightActionBar(): Boolean {// Checking if title text color will be black
        val rgb = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
        return rgb > 210
    }

}