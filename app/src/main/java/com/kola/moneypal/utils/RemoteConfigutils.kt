package com.kola.moneypal.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kola.moneypal.BuildConfig
import com.kola.moneypal.R
import org.jetbrains.anko.toast

object RemoteConfigutils {

    object RemoteProperties {

        private const val TAG = "TestremoteConfig"

        // Remote Config keys
        const val LOADING_PHRASE_CONFIG_KEY = "loading_phrase"
        const val WELCOME_MESSAGE_KEY = "welcome_message"
        const val MESSAGE_SPLASH_SCREEN1 = "message_splash_sreen1"
        const val MESSAGE_SPLASH_SCREEN2 = "message_splash_sreen2"
        const val MESSAGE_SPLASH_SCREEN3 = "message_splash_sreen3"
        const val DEMARER_TOUTES_LES8_APPLIS_VIA_DEMO = "demarage_de_toutes_les_applis_via_demo"

        const val IS_ACTIVE_REMOTE_COLOR_PRIMARY = "active_remote_colors"
        const val COULEUR_MILIEUR_OBJECTIVE_GROUP_FRAMENT = "fragment_objective_group_color_middle"
        const val HEADER_COLOR_ALL = "header_color_all"
        const val MEDDLE_COLOR_ALL = "middle_color_all"
    }

    private lateinit var remoteConfig: FirebaseRemoteConfig

    val TAG = "RemoteConfigutils"

    fun fetchRemoteConfigFromServer(context: Context, onComplete: () -> Unit) {

        remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .setMinimumFetchIntervalInSeconds(4200)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaults(R.xml.remote_config_defaults)


        // [START fetch_config_with_callback]
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val updated = it.result
                    Log.d(TAG, "Fetch and activate succeeded")
                } else {
                    Log.d(TAG, "Fetch failed")
                }
                onComplete()
            }
    }

    fun isRemoteConfigColorsAcived(): Boolean {
        return remoteConfig.getBoolean(RemoteProperties.IS_ACTIVE_REMOTE_COLOR_PRIMARY)
    }

    fun getHeaderColor(): String {
        return remoteConfig.getString(RemoteProperties.HEADER_COLOR_ALL)
    }

    fun getMiddleColor(): String {
        return remoteConfig.getString(RemoteProperties.MEDDLE_COLOR_ALL)
    }
}