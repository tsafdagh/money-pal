package com.kola.moneypal

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Half
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.kola.moneypal.authentification.UserprofileActivitu
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.fragments.HomeFragment
import com.kola.moneypal.fragments.ObjectifGroupFragment
import com.kola.moneypal.fragments.StatistiquesFragment
import com.kola.moneypal.glide.GlideApp
import com.kola.moneypal.mes_exemple.PieadCardActivity
import com.kola.moneypal.mes_exemple.ReadMessageActivity
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.GobalConfig
import com.kola.moneypal.utils.StorageUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_group_dialog.*
import kotlinx.android.synthetic.main.create_group_dialog.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    companion object DEFAULT_FRAGMENT_CONFIG {
        var isWillHomeFragment = true
    }

    val listIdUserForGroup = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.navigation)

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    isWillHomeFragment = true
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_groupe -> {
                    isWillHomeFragment = true
                    replaceFragment(ObjectifGroupFragment())
                    true
                }
                /*         R.id.navigation_statistiques -> {
                             isWillHomeFragment = true
                             replaceFragment(StatistiquesFragment())
                             true
                         }*/

                else -> {
                    false
                }

            }
        }

        // Si l'utilisateur a au préalable recus un lien dynamique venant d'un groupe, il est ajouter dans le groupe
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
            .addOnSuccessListener {
                if (it != null) {
                    var deepLink = it.link.toString()

                    val idGroup = deepLink.substring(34)
                    toast(idGroup).duration = Toast.LENGTH_LONG

                    val progress = indeterminateProgressDialog("Ajout dans le groupe en cours")
                    FireStoreUtil.addUserOnGroupViaDynamicLink(idGroup, onComplete = {

                        if (it) {
                            progress.dismiss()
                            Snackbar.make(
                                container,
                                getString(R.string.snackbar_ajout_group_succes),
                                Snackbar.LENGTH_LONG
                            ).show()
                            isWillHomeFragment = true
                            replaceFragment(ObjectifGroupFragment())
                        } else progress.dismiss()

                    })
                }
            }
            .addOnFailureListener {
                // initialisation par défaut de la vue
            }.addOnCompleteListener {
            }
    }

    override fun onStart() {
        super.onStart()
        if (isWillHomeFragment)
            replaceFragment(HomeFragment())
        else {
            replaceFragment(ObjectifGroupFragment())
        }

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
                startActivity<PieadCardActivity>()

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
                listIdUserForGroup.add(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                // on démare la création du groupe
                val progressdialog = indeterminateProgressDialog(getString(R.string.pdialog_progression_en_cour))
                FireStoreUtil.createObjectivegroupe(listIdUserForGroup,
                    mDialogView!!.id_edit_nom_groupe.text.toString(),
                    mDialogView!!.id_editText_description_objectif.text.toString(),
                    mDialogView!!.id_editText_objectif_amount.text.toString().toDouble(),
                    mDialogView!!.id_editText_dateEcheance.text.toString(),
                    onComplete = { groupeId ->

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
                                        val intent = Intent(this, DetailsObjectiveGroup::class.java)
                                        val objGroup = ObjectiveGroup(
                                            FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                                            mDialogView!!.id_edit_nom_groupe.text.toString(),
                                            mDialogView!!.id_editText_description_objectif.text.toString(),
                                            mDialogView!!.id_editText_objectif_amount.text.toString().toDouble(),
                                            0.0,
                                            Date(0),
                                            mDialogView!!.id_editText_dateEcheance.text.toString(),
                                            urlImage,
                                            listIdUserForGroup
                                        )
                                        intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING, objGroup)
                                        intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING, groupeId)
                                        startActivity(intent)
                                    })
                                })
                        } else {
                            progressdialog.dismiss()
                            toast("Groupe créer avec success")
                            val objGroup = ObjectiveGroup(
                                FirebaseAuth.getInstance().currentUser!!.phoneNumber!!,
                                mDialogView!!.id_edit_nom_groupe.text.toString(),
                                mDialogView!!.id_editText_description_objectif.text.toString(),
                                mDialogView!!.id_editText_objectif_amount.text.toString().toDouble(),
                                0.0,
                                Date(0),
                                mDialogView!!.id_editText_dateEcheance.text.toString(),
                                "",
                                listIdUserForGroup
                            )
                            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING, objGroup)
                            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING, groupeId)
                            startActivity(intent)
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

            GlideApp.with(this)
                .load(selectedImagePathUri).circleCrop()
                .into(mDialogView!!.imageView_profile_groupe_picture)
        }
    }
}
