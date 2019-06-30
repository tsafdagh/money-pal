package com.kola.moneypal.fragments

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kola.moneypal.DetailsTransactonActivity

import com.kola.moneypal.R
import com.kola.moneypal.RecycleView.item.CategorieItem
import com.kola.moneypal.RecycleView.item.TransactionItem
import com.kola.moneypal.authentification.UserprofileActivitu
import com.kola.moneypal.entities.CategorieEntite
import com.kola.moneypal.entities.CategorieNature
import com.kola.moneypal.entities.TransactionEntitie
import com.kola.moneypal.mes_exemple.smsObjet
import com.kola.moneypal.utils.AnotherUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import com.kola.moneypal.utils.SmsUtils
import kotlinx.android.synthetic.main.activity_userprofile_activitu.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast


class HomeFragment : Fragment() {

    private var shouldInitrecycleViewcategorie = true
    private var shouldInitrecycleViewTransaction = true
    private lateinit var categorieSection: Section
    private lateinit var transactioneSection: Section
    private val listTransactions = arrayListOf<Item>()
    private val requestReadSms: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onStart() {
        super.onStart()

        if (this@HomeFragment.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_SMS
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            this.activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.READ_SMS),
                    requestReadSms
                )
            }
        } else {
            id_btn_tout_voir.setOnClickListener {

                findSpecificOrAllTransaction(null, true)
            }
            id_image_user_account.setOnClickListener {

                startActivity(Intent(this@HomeFragment.context, UserprofileActivitu::class.java).apply {
                    /*        putExtra("extra_1", value1)
                            putExtra("extra_2", value2)
                            putExtra("extra_3", value3) */
                })

            }

            id_tw_last_transaction.setOnClickListener {
                val lastTransction = findLastTransaction("ORANGE")
                setLasTTransaction(lastTransction)
            }

            loadData()
            val lastTransction = findLastTransaction("ORANGE")
            setLasTTransaction(lastTransction)
            findSpecificOrAllTransaction(null, true)
        }

        Glide.with(this)
            .load(FirebaseAuth.getInstance().currentUser!!.photoUrl?:R.drawable.nom_user)
            .into(id_image_user_account)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == requestReadSms) {
            id_btn_tout_voir.setOnClickListener {

                findSpecificOrAllTransaction(null, true)
            }
            id_image_user_account.setOnClickListener {

                startActivity(Intent(this@HomeFragment.context, UserprofileActivitu::class.java).apply {
                    /*        putExtra("extra_1", value1)
                            putExtra("extra_2", value2)
                            putExtra("extra_3", value3) */
                })

            }

            id_tw_last_transaction.setOnClickListener {
                val lastTransction = findLastTransaction("ORANGE")
                setLasTTransaction(lastTransction)
            }

            loadData()
            val lastTransction = findLastTransaction("ORANGE")
            setLasTTransaction(lastTransction)

            //progressdialog = this@HomeFragment.context!!.indeterminateProgressDialog(getString(R.string.pdialog_recherche_transactions))

            findSpecificOrAllTransaction(null, true)
        }

    }

    private fun setLasTTransaction(lastTransction: TransactionEntitie) {
        if (lastTransction.montanttransaction > 0 && lastTransction.dateTransaction != "" && (lastTransction.libeleTransaction != "") && lastTransction.destinataire != "") {
            // on nétoie d'abord toutes les transactions
            listTransactions.clear()
            listTransactions.add(TransactionItem(lastTransction, this@HomeFragment.context!!))
            updateRecycleViewTransactions(listTransactions)
            val msgDate = getString(R.string.text_view_date) + " " + AnotherUtil.convertDate(
                lastTransction.dateTransaction,
                "dd/MM/yyyy"
            )
            id_text_solde_date.text = msgDate
            val msgSolde = lastTransction.nouveauSolde.toString() + " FCFA"
            id_text_montnt.text = msgSolde
        } else {
            Snackbar.make(id_home_fragment, getString(R.string.snack_bar_aucune_transaction), Snackbar.LENGTH_LONG)
                .show()
        }
    }

    private fun findLastTransaction(operator: String): TransactionEntitie {

        // on recupère les messages provenant d'orange money  ou de MTN Money
        var listTransactionsSMS = ArrayList<smsObjet>()

        if (operator == "ORANGE") {
            listTransactionsSMS =
                SmsUtils.getMessagebyCriterion(this@HomeFragment.context!!, "inbox", "address LiKE 'OrangeMoney'")
        } else {
            listTransactionsSMS = SmsUtils.getMessagebyCriterion(
                this@HomeFragment.context!!,
                "inbox",
                "address LiKE 'MobileMoney'"
            )
        }

        return SmsUtils.findLastTransaction(listTransactionsSMS)


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

            // les dépos d'agent dans le compte de l'utilisateur
            transactionType =
                SmsUtils.findSpecificSpending(
                    listorangeTransactions,
                    CategorieNature.NATURE_DEPOS_ARGENT
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

            // les retraits d'argent
            transactionType =
                SmsUtils.findSpecificSpending(
                    listorangeTransactions,
                    CategorieNature.NATURE_RETRAIT_ARGENT
                )
            for (element in transactionType)
                listTransactions.add(
                    TransactionItem(
                        element,
                        this@HomeFragment.context!!
                    )
                )

            listTransactions.shuffle()
            // on fait le mélange avant d'afficher
            updateRecycleViewTransactions(listTransactions)

        } else {
            //si la demande est de recuperer un type spécifique de message

            if (item is CategorieItem) {
                when (item.categorieItm.natureCategorie) {
                    CategorieNature.NATURE_TRANSFERT_ARGENT -> {

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

                    CategorieNature.NATURE_DEPOS_ARGENT -> {

                        val transactionType =
                            SmsUtils.findSpecificSpending(
                                listorangeTransactions,
                                CategorieNature.NATURE_DEPOS_ARGENT
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

                    CategorieNature.NATURE_RETRAIT_ARGENT -> {

                        val transactionType =
                            SmsUtils.findSpecificSpending(
                                listorangeTransactions,
                                CategorieNature.NATURE_RETRAIT_ARGENT
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
                    setOnItemClickListener { item: com.xwray.groupie.Item<*>, view: View ->

                        val transactionDetails = item as TransactionItem
                        startActivity(Intent(this@HomeFragment.context, DetailsTransactonActivity::class.java).apply {
                            putExtra("PARAM1", "NATURE: " + transactionDetails.transaction.libeleTransaction)
                            putExtra(
                                "PARAM2",
                                "DATE: " + AnotherUtil.convertDate(
                                    transactionDetails.transaction.dateTransaction,
                                    "dd/MM/yyyy hh:mm:ss"
                                )
                            )
                            putExtra("PARAM3", "RESPONSABLE: " + transactionDetails.transaction.destinataire)
                            putExtra("PARAM4", "MONTANT: " + transactionDetails.transaction.montanttransaction + "FCFA")
                            putExtra("PARAM5", "NOUVEAU SOLDE: " + transactionDetails.transaction.nouveauSolde + "FCFA")

                        })
                    }
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
        val cateorie5 = CategorieEntite("Dépos d'argent", "urlImg", CategorieNature.NATURE_DEPOS_ARGENT)
        val cateorie6 = CategorieEntite("Retrait d'argent", "urlImg", CategorieNature.NATURE_RETRAIT_ARGENT)


        listcategorieTransaction.add(CategorieItem(cateorie6, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie5, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie2, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie1, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie3, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie4, this@HomeFragment.context!!))
        listcategorieTransaction.add(CategorieItem(cateorie6, this@HomeFragment.context!!))

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
