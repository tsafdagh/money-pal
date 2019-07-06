package com.kola.moneypal

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.UserGroupeEntitie
import com.kola.moneypal.utils.AnotherUtil.getdateNow
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.GobalConfig
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_details_objective_group.*
import kotlinx.android.synthetic.main.row_item_user_group_transac.*
import kotlinx.android.synthetic.main.row_item_user_group_transac.view.*
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.toast
import kotlin.collections.ArrayList

class DetailsObjectiveGroup : AppCompatActivity() {

    private var shouldInitrecycleView = true

    private lateinit var usergroupSection: Section
    private lateinit var objGroupe: ObjectiveGroup
    private lateinit var groupId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_objective_group)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        objGroupe = intent.getParcelableExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING)
        groupId = intent.getStringExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING)
        progressBar_p_objectif.apply {
            max = objGroupe!!.objectiveamount.toInt()
            progress = objGroupe.courentAmount.toInt()
        }

        id_text_objectif.text = objGroupe!!.groupeName
        id_val_solde_total.text = objGroupe.objectiveamount.toString()

        // val date = getCurrentDateTime()
        //val dateInString = date.toString("yyyy/MM/dd")

        val soldeCompteDate = getString(R.string.text_view_date) + " " + getdateNow()
        id_text_solde_date.text = soldeCompteDate
        val curenSold = objGroupe.courentAmount.toString() + getString(R.string.text_fcfa)
        id_text_montnt.text = curenSold

        id_add_member.setOnClickListener {
            toast("Add member")
        }
        id_pay_member.setOnClickListener {
            toast("Payer votre contribution")
            val intent = Intent(this, PayementActivity::class.java)
            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING, objGroupe)
            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING, groupId)
            startActivity(intent)
        }

        loadData(objGroupe, groupId)
    }

    private fun loadData(objGroupe: ObjectiveGroup, groupeId: String?) {
        /*val listOjectivegroupeuser = arrayListOf<Item>()
        //initialisation des transactions
        val transaction1 = UserGroupeEntitie("Martine", "Samedi 9", (35400).toDouble(), "imageUrl")
        val transaction2 = UserGroupeEntitie("Maximilien", "Samedi 9", (400).toDouble(), "imageUrl")
        val transaction3 = UserGroupeEntitie("Ange", "Samedi 9", (500).toDouble(), "imageUrl")
        val transaction4 = UserGroupeEntitie("Patrick", "samedi 9", (500).toDouble(), "imageUrl")
        listOjectivegroupeuser.add(UserGroupeitem(transaction1, this))
        listOjectivegroupeuser.add(UserGroupeitem(transaction2, this))
        listOjectivegroupeuser.add(UserGroupeitem(transaction3, this))
        listOjectivegroupeuser.add(UserGroupeitem(transaction4, this))*/

        FireStoreUtil.createObjectiveGroupMembersList(objGroupe.members as ArrayList<String>, this,groupeId!!,onListen = {
            updateRecycleViewUserobjectiveGroupe(it as ArrayList<Item>)
        })

/*        for(phone in objGroupe.members!!){
            FireStoreUtil.addCreateObjectiveGroupMembersList(phone, this,groupeId!!,onListen = {
                updateRecycleViewUserobjectiveGroupe(it as ArrayList<Item>)
            })
        }*/
    }

    private fun updateRecycleViewUserobjectiveGroupe(listOjectivegroupeuser: ArrayList<Item>) {
        fun init() {
            id_recycleview_groupe_item.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    usergroupSection = Section(listOjectivegroupeuser)
                    add(usergroupSection)
                    setOnItemClickListener{item: com.xwray.groupie.Item<*>, view: View ->
                        val curentItem = item as UserGroupeitem
                        view.cardView_item_person_group.setOnClickListener {
                            if(curentItem.usercontribution.phoneNumber != FirebaseAuth.getInstance().currentUser?.phoneNumber){
                                withItems(curentItem.usercontribution)
                            }
                        }
                    }
                }
            }
            shouldInitrecycleView = false
        }

        fun updateItems() = usergroupSection.update(listOjectivegroupeuser)

        if (shouldInitrecycleView) {
            try {
                init()
            } catch (e: Exception) {
                Log.e("Hommefragent", "Erreur Null: " + e.message)
            }
        } else
            updateItems()
    }

    fun withItems(usercontribution: UserGroupeEntitie) {

        val items = arrayOf(getString(R.string.item_alert_dialog_lis_appelr)+" "+usercontribution.username,
            getString(R.string.item_alert_dialog_transfert_argen)+" "+usercontribution.username,
            "Contribuer pour "+usercontribution.username)
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Choisir une action")
            setItems(items) { dialog, which ->
                when(items[which]){
                    items[0] ->{
                        appeler(usercontribution.phoneNumber)
                    }
                    items[1] ->{
                        context.toast("Transferer de l'argent")
                    }
                    items[2] ->{
                        context.toast("Contribuer pour...")
                    }
                    else ->{
                        context.toast("Action inconnue")

                    }
                }
            }

            //setPositiveButton("OK", positiveButtonClick)
            setNegativeButton("ANULLER", negativeButton)
            show()
        }
    }

    val MY_PERMISSIONS_REQUEST_PONE_CALL =2
    private fun appeler(phoneNumber: String) {
        //context.makeCall(phoneNumber)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_PONE_CALL)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            makeCall(phoneNumber)
        }

    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(this,
            android.R.string.no, Toast.LENGTH_SHORT).show()
    }
    val negativeButton = { dialog: DialogInterface, which: Int ->

    }


    /* fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
         val formatter = SimpleDateFormat(format, locale)
         return formatter.format(this)


     }*/


    /* fun getCurrentDateTime(): Date {
         return Calendar.getInstance().time
     }*/
}
