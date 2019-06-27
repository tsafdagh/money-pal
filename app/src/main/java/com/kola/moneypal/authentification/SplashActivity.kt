package com.kola.moneypal.authentification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.MainActivity
import com.kola.moneypal.R
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crashlytics.getInstance().crash()
        if (FirebaseAuth.getInstance().currentUser == null)
            startActivity<SingInActivity>()
        else
            startActivity<MainActivity>()
        finish()
    }
}
