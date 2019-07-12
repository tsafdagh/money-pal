package com.kola.moneypal

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.firestore.ListenerRegistration
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.UserGroupeEntitie
import com.kola.moneypal.fragments.SelectUserListDialogFragment
import com.kola.moneypal.utils.*
import com.kola.moneypal.utils.AnotherUtil.getdateNow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_details_objective_group.*
import kotlinx.android.synthetic.main.row_item_user_group_transac.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.toast
import kotlin.collections.ArrayList

class DetailsObjectiveGroup : AppCompatActivity() {

    //TODO ajout du menu pour la description du group
    private var shouldInitrecycleView = true
    val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1
    private lateinit var usergroupSection: Section
    private lateinit var objGroupe: ObjectiveGroup
    private lateinit var groupId: String

    private lateinit var specificgroupeListenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_objective_group)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setUpRemoteConfig()
        defaultInitView()
    }

    private fun setUpRemoteConfig() {

        val isRemoteConfigActivateColor = RemoteConfigutils.isRemoteConfigColorsAcived()
        if (isRemoteConfigActivateColor) {
            //context!!.toast("Nouveau theme !!!")
            val headerColor = RemoteConfigutils.getHeaderColor()
            id_detail_obj_group_header.setBackgroundColor(Color.parseColor(headerColor))
            progressBar_p_objectif.setBackgroundColor(Color.WHITE)
        }
    }

    private fun defaultInitView() {
        objGroupe = (intent.getSerializableExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING) as ObjectiveGroup?)!!
        groupId = intent.getStringExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING)!!

        val progressdialog = indeterminateProgressDialog("Chargement des membres...")

        specificgroupeListenerRegistration = FireStoreUtil.addFindSpecificGroupListener(groupId, onListen = {
            progressBar_p_objectif.apply {
                max = it.objectiveamount.toInt()
                progress = it.courentAmount.toInt()

                objGroupe = it
                id_text_objectif.text = it.groupeName

                id_val_solde_total.text = objGroupe.objectiveamount.toString()
                val soldeCompteDate = getString(R.string.text_view_date) + " " + getdateNow()
                id_text_solde_date.text = soldeCompteDate
                val curenSold =
                    (it.courentAmount).toString() + getString(R.string.text_fcfa)
                id_text_montnt.text = curenSold

                // on met à jours la liste des membres du groupe
                FireStoreUtil.createObjectiveGroupMembersList(
                    it.members as ArrayList<String>,
                    applicationContext,
                    groupId,
                    onListen = { item ->
                        updateRecycleViewUserobjectiveGroupe(item as ArrayList<Item>)
                    }
                )

            }
            progressdialog.dismiss()
        })

        // val date = getCurrentDateTime()
        //val dateInString = date.toString("yyyy/MM/dd")

        id_add_member.setOnClickListener {

            GobalConfig.listIdUserForDynimicLinks.clear()
            val myModal = SelectUserListDialogFragment(groupId)
            myModal.show(supportFragmentManager, "Selectionnez les personnes à inviter")

            //buildShortDynamicLink()

        }
        id_pay_member.setOnClickListener {
            val intent = Intent(this, PayementActivity::class.java)
            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING, objGroupe)
            intent.putExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING, groupId)
            //intent.putExtra(GobalConfig.COUREN_AMOUNT_OF_CURENT_CONTRIBUTED_AMOUNT, contributedCurentAmount)
            startActivity(intent)
        }



        loadData(objGroupe, groupId)
    }


    private fun loadData(objGroupe: ObjectiveGroup, groupeId: String?) {

        FireStoreUtil.createObjectiveGroupMembersList(
            objGroupe.members as ArrayList<String>,
            this,
            groupeId!!,
            onListen = {
                updateRecycleViewUserobjectiveGroupe(it as ArrayList<Item>)
            })

/*        for(phone in objGroupe.members!!){
            FireStoreUtil.addCreateObjectiveGroupMembersList(phone, this,groupeId!!,onListen = {
                updateRecycleViewUserobjectiveGroupe(it as ArrayList<Item>)
            })
        }*/
    }

    private fun getContactList() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            ) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS
                )

            }
        } else {
            val myModal = SelectUserListDialogFragment(groupId)
            myModal.show(supportFragmentManager, "Selectionnez les personnes à inviter")

            //for (item in LocalPhoneUtil.getAllContacts(this))
            //toast(item.toString())
        }

    }

    private fun updateRecycleViewUserobjectiveGroupe(listOjectivegroupeuser: ArrayList<Item>) {
        fun init() {
            id_recycleview_groupe_item.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    usergroupSection = Section(listOjectivegroupeuser)
                    add(usergroupSection)
                    setOnItemClickListener { item: com.xwray.groupie.Item<*>, view: View ->
                        val curentItem = item as UserGroupeitem
                        view.contributed_user_cardView.setOnClickListener {
                            if (curentItem.usercontribution.phoneNumber != FirebaseAuth.getInstance().currentUser?.phoneNumber) {
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

        val items = arrayOf(
            getString(R.string.item_alert_dialog_lis_appelr) + " " + usercontribution.username,
            getString(R.string.item_alert_dialog_transfert_argen) + " " + usercontribution.username,
            "Contribuer pour " + usercontribution.username
        )
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Choisir une action")
            setItems(items) { dialog, which ->
                when (items[which]) {
                    items[0] -> {
                        appeler(usercontribution.phoneNumber)
                    }
                    items[1] -> {
                        toast("Transferer de l'argent")
                    }
                    items[2] -> {
                        toast("Contribuer pour...")
                    }
                    else -> {
                        toast("Action inconnue")

                    }
                }
            }

            //setPositiveButton("OK", positiveButtonClick)
            setNegativeButton("ANULLER", negativeButton)
            show()
        }
    }

    val MY_PERMISSIONS_REQUEST_PONE_CALL = 2
    private fun appeler(phoneNumber: String) {
        //context.makeCall(phoneNumber)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this as Activity,
                    Manifest.permission.CALL_PHONE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_PONE_CALL
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            makeCall(phoneNumber)
        }

    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(
            this,
            android.R.string.no, Toast.LENGTH_SHORT
        ).show()
    }
    val negativeButton = { dialog: DialogInterface, which: Int ->

    }

    override fun onDestroy() {
        super.onDestroy()

        try {
            FireStoreUtil.removeListener(specificgroupeListenerRegistration)
            shouldInitrecycleView = true
        } catch (e: Exception) {
            Log.e("DetailsObjectiveGroup", e.toString())
        }

    }

    /* fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
         val formatter = SimpleDateFormat(format, locale)
         return formatter.format(this)


     }*/


    /* fun getCurrentDateTime(): Date {
         return Calendar.getInstance().time
     }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    for (item in LocalPhoneUtil.getAllContacts(this))
                        toast(item.toString())
                } else {
                    toast("Vous devez accepter la permission")

                }
                return
            }
            else -> {

            }
        }
    }

    fun buildShortDynamicLink() {

        val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(DynamicLinkUtil.generateLongLink(groupId))
            .buildShortDynamicLink()
            .addOnSuccessListener { result ->
                // Short link created
                val shortLink = result.shortLink
                val flowchartLink = result.previewLink
                shareLink(shortLink.toString())
            }
            .addOnFailureListener {
                toast("Erreur de generation de lien court")
            }.addOnCompleteListener {
                toast("Complete lister de la generation de lien court")

            }
        //shareLink(DynamicLinkUtil.generateLongLink(groupId).toString())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.objective_group_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_info_group -> {
                AlertDialog.Builder(this)
                    .setTitle("Informations du groupe ${objGroupe.groupeName}")
                    .setMessage("Description: ${objGroupe.groupeDescription}\n" +
                            "Montant à atteindre: ${objGroupe.objectiveamount} FCFA\n" +
                            "Montant courant: ${objGroupe.courentAmount} FCFA\n" +
                            "Date d'échéance: ${objGroupe.DateEcheance}\n" +
                            "Nombre de membre: ${objGroupe.members!!.size}\n")
                    .setIcon(R.drawable.ic_action_form)
                    .setNeutralButton("OK"
                    ) { dialog, id ->
                        dialog.cancel()
                    }.show()
            }

            R.id.id_chat_menu_item -> {
                toast("chat du groupe, en cours de developpement...")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun shareLink(link: String) {
        val intent = Intent()
        val message = "Veuillez vous joindre à notre objectif via le lien suivant: $link"
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        startActivity(intent)

    }

}
