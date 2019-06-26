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

        loadData()
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
        if (item is CategorieItem) {
            if (item.categorieItm.natureCategorie == CategorieNature.NATURE_TRANSFERT_ARGENT) {

                Toast.makeText(this@HomeFragment.context, "Categorie transfert d'argent cliquer",Toast.LENGTH_LONG).show()

                val listorangeTransactions =
                    SmsUtils.getMessagebyCriterion(this@HomeFragment.context!!, "inbox", "address LiKE 'OrangeMoney'")
                //SmsUtils.getMessagebyCriterion(this, "inbox", "address LiKE 'MobileMoney'")
                for (smstransaction in listorangeTransactions) {
                    val transactionType = SmsUtils.findSpecificSpending(smstransaction)
                    transactionType?.let {
                        listTransactions.add(
                            TransactionItem(
                                transactionType,
                                this@HomeFragment.context!!
                            )
                        )
                    }
                }

                updateRecycleViewTransactions(listTransactions)
            }
            if (item.categorieItm.natureCategorie == CategorieNature.NATURE_ACHAT_CONNEXION) {
                //TODO code de lecture des transactions du type achat de connexion
            }
            if (item.categorieItm.natureCategorie == CategorieNature.NATURE_FACTURE_ENEO) {
                //TODO code de lecture des transactions du type facture eneo

            }
            if (item.categorieItm.natureCategorie == CategorieNature.NATURE_ACHAT_CREDIT) {
                //TODO code de lecture des transactions de nature achat de credit
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

        fun updateItems() = transactioneSection.update(listeCategorie)

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
