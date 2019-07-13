package com.kola.moneypal.authentification

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.facebook.drawee.backends.pipeline.Fresco
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.MainActivity
import com.kola.moneypal.R
import com.kola.moneypal.glide.GlideApp
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.GobalConfig
import com.kola.moneypal.utils.StorageUtil
import kotlinx.android.synthetic.main.activity_userprofile_activitu.*
import org.jetbrains.anko.*
import java.io.ByteArrayOutputStream
import java.lang.Exception


class UserprofileActivitu : AppCompatActivity() {

    private val RC_SELECT_IMAGE = 2
    private val REQUESTrEADiMAGE = 1
    private var selectedImagePathUri: Uri? = null
    private lateinit var selectedImageBytes: ByteArray

    //TODO corection du burg lié à la sélection de l'image de profil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_userprofile_activitu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        id_btn_sign_out.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener {
                    startActivity(intentFor<SplashActivity>().newTask().clearTask())
                    finish()
                }
        }
        imageView_profile_picture.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUESTrEADiMAGE
                )
            } else {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, "Selectionnez une image"), RC_SELECT_IMAGE)
            }
        }

        id_btn_save.setOnClickListener {

            if (id_editText_name.text.trim().toString() != "") {
                val progressdialog = indeterminateProgressDialog("Mise à jour de votre profils...")

                val myName = id_editText_name.text.toString()
                val emailAddresse = id_editText_email.text.toString()

                if (selectedImagePathUri != null) {
                    StorageUtil.uploadFromLocalFile(
                        selectedImagePathUri!!,
                        GobalConfig.REFERENCE_USER_IMAGE_PROFIL,
                        onSuccess = { url ->
                            FireStoreUtil.updateCurrentUser(
                                myName,
                                emailAddresse,
                                url,
                                selectedImagePathUri,
                                onComplete = { user, isOk ->
                                    if (isOk) {
                                        Log.d("UserProfileActivity:", "User name is set!")
                                        progressdialog.dismiss()
                                        startActivity(intentFor<MainActivity>().newTask().clearTask())
                                    } else {
                                        progressdialog.dismiss()
                                        Snackbar.make(
                                            id_user_profil,
                                            getString(R.string.snackbar_erreur_connexion),
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                                })
                        })
                } else {
                    FireStoreUtil.updateCurrentUser(
                        myName,
                        emailAddresse,
                        "",
                        selectedImagePathUri,
                        onComplete = { user, isOk ->
                            if (isOk) {
                                Log.d("UserProfileActivity:", "User name is set!")
                                progressdialog.dismiss()
                                startActivity(intentFor<MainActivity>().newTask().clearTask())
                            } else {
                                progressdialog.dismiss()
                                Snackbar.make(
                                    id_user_profil,
                                    getString(R.string.snackbar_erreur_connexion),
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        })
                }

            } else {
                id_editText_name.error = getString(R.string.user_profil_nom_requis)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUESTrEADiMAGE) {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(intent, "Selectionnez une image"), RC_SELECT_IMAGE)
        }

    }

    var isNewImageSelected = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            selectedImagePathUri = data.data!!
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePathUri)

            val outPutStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outPutStream)
            //selectedImageBytes = outPutStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImagePathUri)
                .transform(CircleCrop())
                .into(imageView_profile_picture)

            isNewImageSelected = true

        }
    }


    override fun onStart() {
        super.onStart()
        id_edit_telephone.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
        id_editText_name.setText(FirebaseAuth.getInstance().currentUser!!.displayName ?: "")

        val progressdialog = indeterminateProgressDialog("Chargement de vos informations...")
        FireStoreUtil.getCurrentUserFromFireStore {
            id_editText_email.setText(it.email)
            progressdialog.dismiss()

            // aprè avoir séléctionner l'image, on entre aussi dans la méthode onStart, ce booléen nous permet e savoir
            // si le déclencheur da la méthode onStart c'est la sélection de l'image courante ou pas
            if (isNewImageSelected) {
                try {
                    GlideApp.with(this)
                        .load(selectedImagePathUri)
                        .placeholder(R.drawable.nom_user)
                        .transform(CircleCrop())
                        .into(imageView_profile_picture)
                } catch (e: Exception) {
                    Log.e("UrerProfilActivity", e.message.toString())
                }
            } else {

                try {
                    GlideApp.with(this)
                        .load(it.profilePicturePath)
                        .placeholder(R.drawable.nom_user)
                        .transform(CircleCrop())
                        .into(imageView_profile_picture)
                } catch (e: Exception) {
                    Log.e("UrerProfilActivity", e.message.toString())
                }
            }
        }
    }
}
