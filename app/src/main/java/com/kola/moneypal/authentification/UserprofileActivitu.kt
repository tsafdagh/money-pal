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


class UserprofileActivitu : AppCompatActivity() {

    private val RC_SELECT_IMAGE = 2
    private val REQUESTrEADiMAGE = 1
    private var isImageSelected = false
    private var pictureJustChanged = false
    private var selectedImagePathUri: Uri? = null
    private lateinit var selectedImageBytes: ByteArray


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
                isImageSelected = true
                startActivityForResult(Intent.createChooser(intent, "Selectionnez une image"), RC_SELECT_IMAGE)
            }
        }

        id_btn_save.setOnClickListener {

            if (id_editText_name.text.trim().toString() != "") {
                val progressdialog = indeterminateProgressDialog("Mise à jour de votre profils...")

                val myName = id_editText_name.text.toString()
                val emailAddresse = id_editText_email.text.toString()

                if(selectedImagePathUri != null ){
                    StorageUtil.uploadFromLocalFile(selectedImagePathUri!!,GobalConfig.REFERENCE_USER_IMAGE_PROFIL,onSuccess = { url->
                        FireStoreUtil.updateCurrentUser(
                            myName,
                            emailAddresse,
                            url,
                            selectedImagePathUri,
                            onComplete = { user, isOk ->
                                if (isOk) {
                                    Log.d("UserProfileActivity:", "User name is set!")
                                    progressdialog.dismiss()
                                    toast(user.phoneNumber)
                                    toast(user.userName)
                                    toast(user.email!!)
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
                }else{
                    FireStoreUtil.updateCurrentUser(
                        myName,
                        emailAddresse,
                        "",
                        selectedImagePathUri,
                        onComplete = { user, isOk ->
                            if (isOk) {
                                Log.d("UserProfileActivity:", "User name is set!")
                                progressdialog.dismiss()
                                toast(user.phoneNumber)
                                toast(user.userName)
                                toast(user.email!!)
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
            isImageSelected = true
            startActivityForResult(Intent.createChooser(intent, "Selectionnez une image"), RC_SELECT_IMAGE)
        }

    }

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
                .placeholder(R.drawable.nom_user)
                .transform(CircleCrop())
                .into(imageView_profile_picture)


            pictureJustChanged = true

        }
    }

    override fun onStart() {
        super.onStart()
        id_edit_telephone.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
        id_editText_name.setText(FirebaseAuth.getInstance().currentUser!!.displayName ?: "")

/*        if (!pictureJustChanged) {
            imageView_profile_picture.clearColorFilter()
            Glide.with(this)
                .load(FirebaseAuth.getInstance().currentUser!!.photoUrl)
                .into(imageView_profile_picture)
        }*/

        GlideApp.with(this)
            .load(FirebaseAuth.getInstance().currentUser!!.photoUrl)
            .placeholder(R.drawable.nom_user)
            .transform(CircleCrop())
            .into(imageView_profile_picture)
      /*  FireStoreUtil.getCurrentUserFromFireStore {

        }*/

    }
}
