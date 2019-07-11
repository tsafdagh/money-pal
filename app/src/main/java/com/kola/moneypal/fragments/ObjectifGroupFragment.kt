package com.kola.moneypal.fragments


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.kola.moneypal.MainActivity

import com.kola.moneypal.R
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.UserGroupeEntitie
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.RemoteConfigutils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_details_objective_group.*
import kotlinx.android.synthetic.main.fragment_objectif_group.*
import org.jetbrains.anko.support.v4.startActivity

class ObjectifGroupFragment : Fragment() {


    private lateinit var groupeListenerRegistration: ListenerRegistration
    private var shouldInitrecycleView = true
    private lateinit var poepleSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        MainActivity.isWillHomeFragment = false

        groupeListenerRegistration = FireStoreUtil.addSearchGroupeListener("",
            this@ObjectifGroupFragment.context!!
            ,
            onListen = {
                updateRecycleView(it)
            }
        )
        //val red = Color.parseColor()
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_objectif_group, container, false)
    }


    @SuppressLint("MissingSuperCall")
    override fun onDestroyView() {
        super.onDestroy()
        FireStoreUtil.removeListener(groupeListenerRegistration)
        shouldInitrecycleView = true
    }

    private fun updateRecycleView(items: List<Item>) {

        fun init() {
            recycle_view_objective_groupe.apply {
                layoutManager = LinearLayoutManager(this@ObjectifGroupFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    poepleSection = Section(items)
                    add(poepleSection)
                    //setOnItemClickListener(onItemClick)
                }
            }
            shouldInitrecycleView = false
        }

        fun updateItems() = poepleSection.update(items)

        if (shouldInitrecycleView) {
            try {
                init()
            }catch (e: Exception){
                Log.e("Groupefragent", "Erreur Null: "+e.message)
            }
        } else
            updateItems()

    }

/*    private val onItemClick = OnItemClickListener { item, view ->
        if (item is GroupeItem) {
            startActivity<ChatGroupActivity>(
                AppConstants.ID_GROUPE to item.chatGroupeId,
                AppConstants.NOM_GROUPE to item.chatGroupe.groupeName,
                AppConstants.NOMBRE_MEMBRE_GROUPE to item.chatGroupe.members?.size.toString()
            )

        }
    }*/

    override fun onStart() {
        super.onStart()
         }

}
