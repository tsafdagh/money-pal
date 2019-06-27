package com.kola.moneypal.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.kola.moneypal.R
import com.kola.moneypal.RecycleView.item.CategorieItem
import com.kola.moneypal.RecycleView.item.TransactionItem
import com.kola.moneypal.entities.CategorieEntite
import com.kola.moneypal.entities.CategorieNature
import com.kola.moneypal.entities.TransactionEntitie
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import com.kola.moneypal.utils.SmsUtils


class HomeFragment : Fragment() {

    private var shouldInitrecycleViewcategorie = true
    private var shouldInitrecycleViewTransaction = true
    private lateinit var categorieSection: Section
    private lateinit var transactioneSection: Section
    private val listTransactions = arrayListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onStart() {
        super.onStart()
        id_btn_tout_voir.setOnClickListener {

            Toast.makeText(this@HomeFragment.context, "Tout voir cliqué", Toast.LENGTH_SHORT)
                .show()
            findSpecificOrAllTransaction(null, true)
        }
        loadData()
        findSpecificOrAllTransaction(null, true)
    }

    private fun updateRecycleViewCategories(listCategorieTransaction: ArrayList<Item>) {
        fun init() {
            recycle_view_categorie_transactions.apply {
                layoutManager = LinearLayoutManager(this@HomeFragment.context).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
                adapter = GroupAdapter<ViewHolder>().apply {
                    categorieSection = Section(listCategorieTransaction)
                    add(categorieSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitrecycleViewcategorie = false
        }

        fun updateItems() = categorieSection.update(listCategorieTransaction)

        if (shouldInitrecycleViewcategorie) {
            try {
                init()
            } catch (e: Exception) {
                Log.e("Hommefragent", "Erreur Null: " + e.message)
            }
        } else
            updateItems()
    }

    private val onItemClick = OnItemClickListener { item, view ->

        findSpecificOrAllTransaction(item as Item, false)
    }

    private fun findSpecificOrAllTransaction(item: Item?, isAll: Boolean) {

        // on nétoie au préalable les élements de la liste précedemment affichés
        listTransactions.clear()

        // on recupère les messages provenant d'orange money  et de MTN Money
        var listorangeTransactions =
            SmsUtils.getMessagebyCriterion(this@HomeFragment.context!!, "inbox", "address LiKE 'OrangeMoney'")
        listorangeTransactions.addAll(
            SmsUtils.getMessagebyCriterion(
                this@HomeFragment.context!!,
                "inbox",
                "address LiKE 'MobileMoney'"
            )
        )

        // si la demande est de recupérer toutes les transactions confondus
        if (isAll) {
            // les transferts d'argents
            var transactionType =
                SmsUtils.findSpecificSpending(
                    listorangeTransactions,
                    CategorieNature.NATURE_TRANSFERT_ARGENT
                )
            for (element in transactionType)
                listTransactions.add(
                    TransactionItem(
                        element,
                        this@HomeFragment.context!!
                    )
                )
            //les achats de credit
            transactionType =
                SmsUtils.findSpecificSpending(listorangeTransactions, CategorieNature.NATURE_ACHAT_CREDIT)
            for (element in transactionType)
                listTransactions.add(
                    TransactionItem(
                        element,
                        this@HomeFragment.context!!
                    )
                )
            //les factures d'éneo
            transactionType =
                SmsUtils.findSpecificSpending(listorangeTransactions, CategorieNature.NATURE_FACTURE_ENEO)
            for (element in transactionType)
                listTransactions.add(
                    TransactionItem(
                        element,
                        this@HomeFragment.context!!
                    )
                )
            // les achats de connexion
            transactionType =
                SmsUtils.findSpecificSpending(
                    listorangeTransactions,
                    CategorieNature.NATURE_ACHAT_CONNEXION
                )
            for (element in transactionType)
                listTransactions.add(
                    TransactionItem(
                        element,
                        this@HomeFragment.context!!
                    )
                )
            listTransactions.shuffle() // on fait le mélange avant d'afficher
            updateRecycleViewTransactions(listTransactions)

        } else {
            //si la demande est de recuperer un type spécifique de message

            if (item is CategorieItem) {
                when (item.categorieItm.natureCategorie) {
                    CategorieNature.NATURE_TRANSFERT_ARGENT -> {

                        Toast.makeText(
                            this@HomeFragment.context,
                            "Categorie transfert d'argent cliquée",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        val transactionType =
                            SmsUtils.findSpecificSpending(
                                listorangeTransactions,
                                CategorieNature.NATURE_TRANSFERT_ARGENT
                            )
                        for (element in transactionType)
                            listTransactions.add(
                                TransactionItem(
                                    element,
                                    this@HomeFragment.context!!
                                )
                            )
                        updateRecycleViewTransactions(listTransactions)
                    }
                    CategorieNature.NATURE_ACHAT_CREDIT -> {
                        Toast.makeText(
                            this@HomeFragment.context,
                            "Categorie Achat de credit cliquée",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        val transactionType =
                            SmsUtils.findSpecificSpending(listorangeTransactions, CategorieNature.NATURE_ACHAT_CREDIT)
                        for (element in transactionType)
                            listTransactions.add(
                                TransactionItem(
                                    element,
                                    this@HomeFragment.context!!
                                )
                            )
                        updateRecycleViewTransactions(listTransactions)

                    }

                    CategorieNature.NATURE_FACTURE_ENEO -> {
                        Toast.makeText(this@HomeFragment.context, "Categorie Facture Eneo cliquée", Toast.LENGTH_SHORT)
                            .show()

                        val transactionType =
                            SmsUtils.findSpecificSpending(listorangeTransactions, CategorieNature.NATURE_FACTURE_ENEO)
                        for (element in transactionType)
                            listTransactions.add(
                                TransactionItem(
                                    element,
                                    this@HomeFragment.context!!
                                )
                            )
                        updateRecycleViewTransactions(listTransactions)

                    }
                    CategorieNature.NATURE_ACHAT_CONNEXION -> {
                        Toast.makeText(
                            this@HomeFragment.context,
                            "Categorie Achat de connexion cliquée",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        val transactionType =
                            SmsUtils.findSpecificSpending(
                                listorangeTransactions,
                                CategorieNature.NATURE_ACHAT_CONNEXION
                            )
                        for (element in transactionType)
                            listTransactions.add(
                                TransactionItem(
                                    element,
                                    this@HomeFragment.context!!
                                )
                            )
                        updateRecycleViewTransactions(listTransactions)

                    }
                }

            }
        }
    }

    private fun updateRecycleViewTransactions(listeCategorie: ArrayList<Item>) {
        fun init() {
            id_receycleView_transactions.apply {
                layoutManager = LinearLayoutManager(this@HomeFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    transactioneSection = Section(listeCategorie)
                    add(transactioneSection)
                    //setOnItemClickListener(onItemClick)
                }
            }
            shouldInitrecycleViewTransaction = false
        }

        fun updateItems() {
            transactioneSection.update(listeCategorie)
        }

        if (shouldInitrecycleViewTransaction) {
            try {
                init()
            } catch (e: Exception) {
                Log.e("Hommefragent", "Erreur Null: " + e.message)
            }
        } else
            updateItems()
    }

    private fun loadData() {
        val listcategorieTransaction = arrayListOf<Item>()
        val listTransaction = arrayListOf<Item>()
        // initialisation des catégories
        val cateorie1 = CategorieEntite("Transfert d'argent", "urlImg", CategorieNature.NATURE_TRANSFERT_ARGENT)
        val cateorie2 = CategorieEntite("Achat de credit", "urlImg", CategorieNature.NATURE_ACHAT_CREDIT)
        val cateorie3 = CategorieEntite("factures Eneo", "urlImg", CategorieNature.NATURE_FACTURE_ENEO)
        val cateorie4 = CategorieEntite("Achat de connexion", "urlImg", CategorieNature.NATURE_ACHAT_CONNEXION)

        listcategorieTransaction.add(CategorieItem(cateorie1, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie2, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie3, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie4, this@HomeFragment.context!!))

        //initialisation des transactions
        /* val transaction1 = TransactionEntitie("Transfert à", "Samedi 9", (35400).toDouble(), "imageUrl")
         val transaction2 = TransactionEntitie("Recu transfer", "Samedi 9", (-1400).toDouble(), "imageUrl")
         val transaction3 = TransactionEntitie("facture Eneo", "Samedi 9", (500).toDouble(), "imageUrl")
         val transaction4 = TransactionEntitie("Achat de connexion", "samedi 9", (-500).toDouble(), "imageUrl")
         listTransaction.add(TransactionItem(transaction1, this@HomeFragment.context!!))
         listTransaction.add(TransactionItem(transaction2, this@HomeFragment.context!!))
         listTransaction.add(TransactionItem(transaction3, this@HomeFragment.context!!))
         listTransaction.add(TransactionItem(transaction4, this@HomeFragment.context!!))
         */

        updateRecycleViewCategories(listcategorieTransaction)
        //updateRecycleViewTransactions(listTransaction)

    }

}
