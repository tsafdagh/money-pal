package com.kola.moneypal.intro_slider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Half
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.MainActivity
import com.kola.moneypal.R
import com.kola.moneypal.datas.SharedPreference
import com.kola.moneypal.datas.SheredprefKeysObj
import com.kola.moneypal.utils.RemoteConfigutils
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.lang.Thread.sleep


class FirtsSplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firts_splash_screen)

        setUpRemoteConfig()

        /* val myThread = Thread {
             sleep(1000)

             //Crashlytics.getInstance().crash()

         }

         myThread.start()*/
    }


    private var preferenceManager: SharedPreference? = null
    private fun setUpRemoteConfig() {

        // on déclenche la recupération des configurations dans remoteConfig
        val pDialog = indeterminateProgressDialog("Chargement des configurations en cours ...")
        RemoteConfigutils.fetchRemoteConfigFromServer(this, onComplete = {

            // Si le démarage via l'intro est forcé, on autorise la première initialisation et on démarre via l'intro
            val isForcerdemarageViaDemo = RemoteConfigutils.isForcerdemarageViaDemo()
            if (isForcerdemarageViaDemo) {
                preferenceManager = SharedPreference(this)
                preferenceManager?.save(SheredprefKeysObj.FIRST_INITIALISATION_OF_APP, false)

                startActivity<MainScreen>()
            } else {
                //si non on sui la procedure normale
                startAnotherActivity()
            }

            pDialog.dismiss()
        })

    }

    fun startAnotherActivity() {
        if (FirebaseAuth.getInstance().currentUser == null)
            startActivity<MainScreen>()
        else
            startActivity<MainActivity>()
        finish()
    }
}
