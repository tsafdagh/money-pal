package com.kola.moneypal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.kola.moneypal.authentification.UserprofileActivitu
import com.kola.moneypal.fragments.HomeFragment
import com.kola.moneypal.fragments.ObjectifGroupFragment
import com.kola.moneypal.mes_exemple.ReadMessageActivity
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.StorageUtil
import kotlinx.android.synthetic.main.create_group_dialog.*
import kotlinx.android.synthetic.main.create_group_dialog.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.navigation)

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_groupe -> {
                    replaceFragment(ObjectifGroupFragment())
                    true
                }

                else -> {
                    false
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        replaceFragment(HomeFragment())

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_layout, fragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }


    var mDialogView: View? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.id_menu_objectifs -> {
                toast("objectifs groupe")

                createobjectiveGroup()
            }

            R.id.id_menu_stat -> {
                toast("statistiques")
            }

            R.id.id_menu_mon_compte -> {
                startActivity<UserprofileActivitu>()
            }
            else -> {
                toast(getString(R.string.selection_inconu))
                Log.d("MainActivity", getString(R.string.selection_inconu))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val RC_SELECT_IMAGE = 2
    private val REQUESTrEADiMAGE = 1
    private fun createobjectiveGroup() {
        //Inflate the dialog with custom view
        mDialogView = LayoutInflater.from(this).inflate(R.layout.create_group_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Creation du groupe")
        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView!!.imageView_profile_groupe_picture.setOnClickListener {

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
        mDialogView!!.id_btn_creer_group.setOnClickListener {
            //dismiss dialog

            if (mDialogView!!.id_edit_nom_groupe.text.toString() != ""
                && mDialogView!!.id_editText_description_objectif.text.toString() != ""
                && mDialogView!!.id_editText_objectif_amount.text.toString() != ""
            ) {
                mAlertDialog.dismiss()

                // on démare la création du groupe
                val progressdialog = indeterminateProgressDialog(getString(R.string.pdialog_progression_en_cour))
                FireStoreUtil.createObjectivegroupe(null, mDialogView!!.id_edit_nom_groupe.text.toString(),
                    mDialogView!!.id_editText_description_objectif.text.toString(),
                    mDialogView!!.id_editText_objectif_amount.text.toString().toDouble(), onComplete = { groupeId ->

                        // si l'image est sélectionée on l'upload
                        if (selectedImagePathUri != null) {
                            StorageUtil.uploadImageGroupFromLocalFile(
                                selectedImagePathUri!!,
                                groupeId,
                                onSuccess = { urlImage ->
                                    //on met à jours l'url de l'image dans le groupe
                                    FireStoreUtil.updateurlImageGroup(urlImage, groupeId, onComplete = {
                                        toast("Groupe créer avec success")
                                        progressdialog.dismiss()
                                    })
                                })
                        } else {
                            progressdialog.dismiss()
                            toast("Groupe créer avec success")
                        }
                    })
            } else {
                mDialogView!!.id_edit_nom_groupe.error = getString(R.string.creation_group_champ_requis)
                mDialogView!!.id_editText_description_objectif.error = getString(R.string.creation_group_champ_requis)
                mDialogView!!.id_editText_objectif_amount.error = getString(R.string.creation_group_champ_requis)
            }
        }
        //cancel button click of custom layout
        mDialogView!!.btn_annuler_creation.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUESTrEADiMAGE) {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(intent, "Selectionnez une image de groupe"), RC_SELECT_IMAGE)
        }

    }

    var selectedImagePathUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            selectedImagePathUri = data.data!!
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePathUri)

            val outPutStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outPutStream)
            //selectedImageBytes = outPutStream.toByteArray()

            Glide.with(this)
                .load(selectedImagePathUri)
                .into(mDialogView!!.imageView_profile_groupe_picture)
        }
    }
}
