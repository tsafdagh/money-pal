package com.kola.moneypal.mes_exemple

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kola.moneypal.BuildConfig
import com.kola.moneypal.R
import kotlinx.android.synthetic.main.activity_test_remote_config.*
import org.jetbrains.anko.backgroundColor

class TestRemoteConfig : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_remote_config)

        fetchButton.setOnClickListener { fetchWelcome() }

        //Crashlytics.getInstance().crash()
        // Get Remote Config instance.
        // [START get_remote_config_instance]
        remoteConfig = FirebaseRemoteConfig.getInstance()
        // [END get_remote_config_instance]

        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. Also use Remote Config
        // Setting to set the minimum fetch interval.
        // [START enable_dev_mode]
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .setMinimumFetchIntervalInSeconds(4200)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        // [END enable_dev_mode]

        // Set default Remote Config parameter values. An app uses the in-app default values, and
        // when you need to adjust those defaults, you set an updated value for only the values you
        // want to change in the Firebase console. See Best Practices in the README for more
        // information.
        // [START set_default_values]
        remoteConfig.setDefaults(R.xml.remote_config_defaults)
        // [END set_default_values]

        fetchWelcome()
    }

    override fun onStart() {
        super.onStart()

    }

    /**
     * Fetch a welcome message from the Remote Config service, and then activate it.
     */
    private fun fetchWelcome() {
        welcomeTextView.text = remoteConfig.getString(LOADING_PHRASE_CONFIG_KEY)

        // [START fetch_config_with_callback]
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(
                        this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                displayWelcomeMessage()
            }
        // [END fetch_config_with_callback]
    }

    /**
     * Display a welcome message in all caps if welcome_message_caps is set to true. Otherwise,
     * display a welcome message as fetched from welcome_message.
     */
    // [START display_welcome_message]
    private fun displayWelcomeMessage() {
        // [START get_config_values]
        val welcomeMessage = remoteConfig.getString(WELCOME_MESSAGE_KEY)
        // [END get_config_values]
        welcomeTextView.isAllCaps = remoteConfig.getBoolean(WELCOME_MESSAGE_CAPS_KEY)
        welcomeTextView.text = welcomeMessage

        val isDefaultConfigActivated =  remoteConfig.getBoolean(DEMARER_TOUTES_LES8_APPLIS_VIA_DEMO)
        if(isDefaultConfigActivated){
            welcomeTextView3.text = remoteConfig.getString(MESSAGE_SPLASH_SCREEN3)
            default_config2.text = remoteConfig.getString(MESSAGE_SPLASH_SCREEN2)
            default_config1.text = remoteConfig.getString(MESSAGE_SPLASH_SCREEN1)
        }else{
            Snackbar.make(test_remote_id, "mise à jours des configs désactivée",Snackbar.LENGTH_LONG).show()
        }

        val headerColor = remoteConfig.getString(COULEUR_ENTETE_OBJECTIVE_GROUP_FRAMENT)
        val conlordMiddle = remoteConfig.getString(COULEUR_MILIEUR_OBJECTIVE_GROUP_FRAMENT)
        test_remote_id.backgroundColor = Color.parseColor(headerColor!!)
        fetchButton.setBackgroundColor(Color.parseColor(conlordMiddle))

    }


    companion object {

        private const val TAG = "TestremoteConfig"

        // Remote Config keys
        private const val LOADING_PHRASE_CONFIG_KEY = "loading_phrase"
        private const val WELCOME_MESSAGE_KEY = "welcome_message"
        private const val WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps"
        private const val MESSAGE_SPLASH_SCREEN1 = "message_splash_sreen1"
        private const val MESSAGE_SPLASH_SCREEN2 = "message_splash_sreen2"
        private const val MESSAGE_SPLASH_SCREEN3 = "message_splash_sreen3"
        private const val DEMARER_TOUTES_LES8_APPLIS_VIA_DEMO = "demarage_de_toutes_les_applis_via_demo"

        private const val COULEUR_MILIEUR_OBJECTIVE_GROUP_FRAMENT = "fragment_objective_group_color_middle"
        private const val COULEUR_ENTETE_OBJECTIVE_GROUP_FRAMENT = "fragment_objective_group_color_header"
    }

}
