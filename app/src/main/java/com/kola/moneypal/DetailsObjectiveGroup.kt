package com.kola.moneypal

import android.annotation.SuppressLint
import java.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.ObjectiveGroup
import com.kola.moneypal.entities.UserGroupeEntitie
import com.kola.moneypal.utils.AnotherUtil.getdateNow
import com.kola.moneypal.utils.GobalConfig
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_details_objective_group.*
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

class DetailsObjectiveGroup : AppCompatActivity() {

    private var shouldInitrecycleView = true

    private lateinit var usergroupSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_objective_group)


        val objGroupe: ObjectiveGroup? = intent.getParcelableExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_STRING)
        val groupId: String? = intent.getStringExtra(GobalConfig.EXTRAT_REFERENCE_OBJ_GROUP_ID_STRING)

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
            toast("Pay member")
        }

        loadData()
    }

    private fun loadData() {
        val listOjectivegroupeuser = arrayListOf<Item>()
        //initialisation des transactions
        val transaction1 = UserGroupeEntitie("Martine", "Samedi 9", (35400).toDouble(), "imageUrl")
        val transaction2 = UserGroupeEntitie("Maximilien", "Samedi 9", (400).toDouble(), "imageUrl")
        val transaction3 = UserGroupeEntitie("Ange", "Samedi 9", (500).toDouble(), "imageUrl")
        val transaction4 = UserGroupeEntitie("Patrick", "samedi 9", (500).toDouble(), "imageUrl")
        listOjectivegroupeuser.add(UserGroupeitem(transaction1, this))
        listOjectivegroupeuser.add(UserGroupeitem(transaction2, this))
        listOjectivegroupeuser.add(UserGroupeitem(transaction3, this))
        listOjectivegroupeuser.add(UserGroupeitem(transaction4, this))
        updateRecycleViewUserobjectiveGroupe(listOjectivegroupeuser)

    }

    private fun updateRecycleViewUserobjectiveGroupe(listOjectivegroupeuser: ArrayList<Item>) {
        fun init() {
            id_recycleview_groupe_item.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    usergroupSection = Section(listOjectivegroupeuser)
                    add(usergroupSection)
                    //setOnItemClickListener(onItemClick)
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


    /* fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
         val formatter = SimpleDateFormat(format, locale)
         return formatter.format(this)


     }*/


    /* fun getCurrentDateTime(): Date {
         return Calendar.getInstance().time
     }*/
}
