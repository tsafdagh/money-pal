package com.kola.moneypal.authentification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.kola.moneypal.MainActivity
import com.kola.moneypal.R
import kotlinx.android.synthetic.main.activity_userprofile_activitu.*
import org.jetbrains.anko.*


class UserprofileActivitu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_userprofile_activitu)

        id_edit_telephone.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
        id_editText_name.setText(FirebaseAuth.getInstance().currentUser!!.displayName)
        id_btn_sign_out.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(intentFor<SplashActivity>().newTask().clearTask())
            finish()
        }

        id_btn_save.setOnClickListener {

            if (id_editText_name.text.trim().toString() != "") {
                val progressdialog = indeterminateProgressDialog("Mise à jour de votre profils...")
                val myName = id_editText_name.text.toString()
                val user = FirebaseAuth.getInstance().currentUser

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(myName)
                    .build()

                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        progressdialog.dismiss()
                        if (task.isSuccessful) {
                            Log.d("UserProfileActivity:", "User name is set!")
                            val fireuseur = FirebaseAuth.getInstance().getCurrentUser()
                            toast(fireuseur?.phoneNumber!!)
                            toast(fireuseur.displayName!!)
                            startActivity(intentFor<MainActivity>().newTask().clearTask())

                        } else {
                            Snackbar.make(
                                id_user_profil,
                                getString(R.string.snackbar_erreur_connexion),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }

            } else {
                id_editText_name.error = getString(R.string.user_profil_nom_requis)
            }
        }
    }
}
