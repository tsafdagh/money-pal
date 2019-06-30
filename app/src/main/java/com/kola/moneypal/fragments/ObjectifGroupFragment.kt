package com.kola.moneypal.fragments


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.kola.moneypal.R
import com.kola.moneypal.RecycleView.item.UserGroupeitem
import com.kola.moneypal.entities.UserGroupeEntitie
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_details_objective_group.*
import kotlinx.android.synthetic.main.fragment_objectif_group.*

class ObjectifGroupFragment : Fragment() {


    private var shouldInitrecycleView = true

    private lateinit var usergroupSection: Section

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_objectif_group, container, false)
    }

    override fun onStart() {
        super.onStart()

    }

}
