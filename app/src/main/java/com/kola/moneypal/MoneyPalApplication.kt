package com.kola.moneypal

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class MoneyPalApplication :Application() {

    override fun onCreate() {
        super.onCreate()
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        try {
            MultiDex.install(this)
        } catch (multiDexException: RuntimeException) {
            multiDexException.printStackTrace()
        }

    }
}