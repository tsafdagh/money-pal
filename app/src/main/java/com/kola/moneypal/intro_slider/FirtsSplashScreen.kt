package com.kola.moneypal.intro_slider

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.MainActivity
import com.kola.moneypal.R
import org.jetbrains.anko.startActivity
import java.lang.Thread.sleep


class FirtsSplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firts_splash_screen)

        val myThread =  Thread {
            sleep(1000)

            //Crashlytics.getInstance().crash()
            if (FirebaseAuth.getInstance().currentUser == null)
                startActivity<MainScreen>()
            else
                startActivity<MainActivity>()
            finish()
        }

        myThread.start()
    }
}
