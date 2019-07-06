package com.kola.moneypal.authentification

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.kola.moneypal.R
import kotlinx.android.synthetic.main.activity_sing_in.*
import org.jetbrains.anko.design.longSnackbar

import java.util.*
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.DialogInterface
import android.text.Editable
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.kola.moneypal.MainActivity
import com.kola.moneypal.datas.SharedPreference
import com.kola.moneypal.datas.SheredprefKeysObj
import com.kola.moneypal.intro_slider.FirtsSplashScreen
import com.kola.moneypal.intro_slider.MainScreen
import com.kola.moneypal.utils.FireStoreUtil
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar


class SingInActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    private var preferenceManager: SharedPreference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_in)
        preferenceManager = SharedPreference(this)
        account_sign_in.setOnClickListener {
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        Arrays.asList(
                            AuthUI.IdpConfig.PhoneBuilder().build()
                        )
                    )
                    .setLogo(R.drawable.icon_logo)
                    .build(),
                RC_SIGN_IN
            )
        }
        id_revoir_intro.setOnClickListener {
            preferenceManager?.save(SheredprefKeysObj.FIRST_INITIALISATION_OF_APP, false)
            startActivity(intentFor<FirtsSplashScreen>().newTask().clearTask())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val progressdialog = indeterminateProgressDialog("Configuration de votre compte")


                val objSheredPref = SharedPreference(this)

                // si l'utilisateur avait déja initialiser son compte
                if (objSheredPref.getValueBoolien(SheredprefKeysObj.CREATEDACCOUNTSTATE, false)) {
                    progressdialog.dismiss()
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                }else{

                    FireStoreUtil.initCurrentUserIfFirstTime(onComplete = { isOk ->
                        progressdialog.dismiss()


                        // on défini le compte de l'utilisateur comme étant déja initier
                        // afin de ne plus avoir à le refaire
                        objSheredPref.save(SheredprefKeysObj.CREATEDACCOUNTSTATE, true)
                        if (isOk) {
                            val alert2 = AlertDialog.Builder(this)
                            alert2.setTitle(getString(R.string.alert_configurer_votre_profils_utilisateur))
                            alert2.setPositiveButton("OUI", DialogInterface.OnClickListener { dialog, whichButton ->
                                startActivity(intentFor<UserprofileActivitu>().newTask().clearTask())

                            })
                            alert2.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, whichButton ->
                                startActivity(intentFor<MainActivity>().newTask().clearTask())

                            })
                            alert2.show()
                        } else {
                            Snackbar.make(
                                constraint_layout,
                                getString(R.string.snack_bar_config_init_profil_erreur),
                                Snackbar.LENGTH_LONG
                            ).show()
                            startActivity(intentFor<UserprofileActivitu>().newTask().clearTask())
                        }


                    })
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return

                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> {
                        Snackbar.make(
                            constraint_layout,
                            getString(R.string.snack_bar_erreur_reseau),
                            Snackbar.LENGTH_LONG
                        ).show()

                    }

                    ErrorCodes.UNKNOWN_ERROR -> {
                        Snackbar.make(
                            constraint_layout,
                            getString(R.string.snackbar_erreur_inkonu),
                            Snackbar.LENGTH_LONG
                        ).show()

                    }

                }
            }
        }
    }
}
