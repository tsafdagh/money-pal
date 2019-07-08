package com.kola.moneypal.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ListenerRegistration
import com.kola.moneypal.R;
import com.kola.moneypal.RecycleView.item.SimpleuserItem
import com.kola.moneypal.utils.DynamicLinkUtil
import com.kola.moneypal.utils.FireStoreUtil
import com.kola.moneypal.utils.GobalConfig
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import io.sentry.event.User
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.row_item_simple_user.*
import kotlinx.android.synthetic.main.row_item_simple_user.view.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast

class SelectUserListDialogFragment(val groupId: String) : BottomSheetDialogFragment() {
    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitrecycleView = true
    private lateinit var poepleSection: Section

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

    override fun onStart() {
        super.onStart()

        id_btn_modal_share_link.setOnClickListener {
            buildShortDynamicLink()
        }

        id_btn_modal_add_member.setOnClickListener {

            if (!GobalConfig.listIdUserForDynimicLinks.isEmpty()){
                val progressdialog = context!!.indeterminateProgressDialog("Ajout des membres...")

                FireStoreUtil.addMemberToObjectiveGroup(groupId, GobalConfig.listIdUserForDynimicLinks, onComplete = {
                    if(it){
                        context!!.toast("Liste des membres mis à jours avec succes")
                        progressdialog.dismiss()
                        this.dismiss()
                    }else{
                        context!!.toast("Erreur d'ajout des membres")
                        progressdialog.dismiss()
                        this.dismiss()
                    }
                })
            }else{
                context!!.toast("Vous devez cocher un ou plusieurs membres")
            }
        }
    }


    fun buildShortDynamicLink() {

        /*val shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
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

            }*/
        shareLink(DynamicLinkUtil.generateLongLink(groupId).toString())

    }


    fun shareLink(link: String) {
        val intent = Intent()
        val message = "Veuillez vous joindre à notre objectif via le lien suivant: $link"
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        startActivity(intent)
        this.dismiss()
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
                    setOnItemClickListener(onItemClick)
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

    private val onItemClick = OnItemClickListener { item, view ->
        if (item is SimpleuserItem) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FireStoreUtil.removeListener(userListenerRegistration)
        shouldInitrecycleView = true
    }
}
