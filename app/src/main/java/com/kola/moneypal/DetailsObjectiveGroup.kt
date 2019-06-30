package com.kola.moneypal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.UserGroupeEntitie
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_details_objective_group.*

class DetailsObjectiveGroup : AppCompatActivity() {

    private var shouldInitrecycleView = true

    private lateinit var usergroupSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_objective_group)

        progressBar_p_objectif.apply {
            max = 1000
            progress = 600
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
}
