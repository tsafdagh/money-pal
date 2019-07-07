package com.kola.moneypal.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ListenerRegistration
import com.kola.moneypal.R;
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.GobalConfig
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import io.sentry.event.User
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*

class SelectUserListDialogFragment : BottomSheetDialogFragment() {
    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitrecycleView = true
    private lateinit var poepleSection: Section

    // cet objet va contenir la liste des mebres du groupe qui auront été sélectionnés à la création du groupe
    private val memberOfGroupe = arrayListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        userListenerRegistration = FireStoreUtil.addSearchUserListenerForcreatingGroupe("",
            this@SelectUserListDialogFragment.context!!
            ,
            onListen = {
                updateRecycleView(it)
            }
        )

        GobalConfig.listIdUserForDynimicLinks.clear()
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroyView() {
        super.onDestroy()
        FireStoreUtil.removeListener(userListenerRegistration)
        shouldInitrecycleView = true
    }

    private fun updateRecycleView(items: List<Item>) {

        fun init() {
            recycle_view_peaple.apply {
                layoutManager = LinearLayoutManager(this@SelectUserListDialogFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    poepleSection = Section(items)
                    add(poepleSection)
                    //setOnItemClickListener(onItemClick)
                }
            }
            shouldInitrecycleView = false
        }

        fun updateItems() = poepleSection.update(items)

        if (shouldInitrecycleView)
            init()
        else
            updateItems()

    }
    override fun onDestroy() {
        super.onDestroy()
        FireStoreUtil.removeListener(userListenerRegistration)
        shouldInitrecycleView = true
        //ParamModalFragment.listIdUserForGroup.clear()
        for (tmp_item in this.memberOfGroupe) {
           // GobalConfig.listIdUserForDynimicLinks.add(tmp_item)
        }
    }

    data class ModalSelectedMember(var uidSelectedMember: String, var view: View)
}
