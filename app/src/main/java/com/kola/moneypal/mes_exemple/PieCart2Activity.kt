package com.kola.moneypal.mes_exemple

import android.annotation.TargetApi
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.kola.moneypal.R
import com.github.mikephil.charting.utils.ColorTemplate
import android.text.style.ForegroundColorSpan
import android.graphics.Typeface
import android.text.style.StyleSpan
import android.text.style.RelativeSizeSpan
import android.text.SpannableString
import android.graphics.Color
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.kola.moneypal.entities.CategorieNature
import com.kola.moneypal.utils.SmsUtils
import kotlinx.android.synthetic.main.activity_pie_cart2.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast


class PieCart2Activity : AppCompatActivity(), OnChartValueSelectedListener {

    private var chart: PieChart? = null
    var tfRegular: Typeface? = null
    var tfLight: Typeface? = null


    var totalRetraitArgent = 0.0
    var totalDeposArgent = 0.0
    var totalAchatdecredit = 0.0
    var totalTransfertArgent = 0.0
    var totalFactureEneot = 0.0
    var totalAchatConnexion = 0.0


    private val categoriesTransactions = mutableListOf<String>(
        "Retrait d'argent",
        "Dépos d'argent",
        "Achat de credit",
        "Transfert d'argent",
        "Fcature énéo",
        "Achat de connexion"
    )
    val depenseOarCategories = mutableListOf(
        3000F,
        4000F,
        500F,
        0F,
        150F,
        2000F
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pie_cart2)

        title = "Statistiques des transactions"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        chart = findViewById(R.id.chart1)
        chart!!.setUsePercentValues(true)
        chart!!.description.isEnabled = false
        chart!!.setExtraOffsets(5F, 10F, 5F, 5F)

        chart!!.dragDecelerationFrictionCoef = 0.95f

        chart!!.setCenterTextTypeface(tfLight)
        chart!!.centerText = generateCenterSpannableText()

        chart!!.isDrawHoleEnabled = true
        chart!!.setHoleColor(Color.WHITE)

        chart!!.setTransparentCircleColor(Color.WHITE)
        chart!!.setTransparentCircleAlpha(110)

        chart!!.holeRadius = 58f
        chart!!.transparentCircleRadius = 61f

        chart!!.setDrawCenterText(true)

        chart!!.rotationAngle = 0F
        // enable rotation of the chart by touch
        chart!!.isRotationEnabled = true
        chart!!.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart!!.setOnChartValueSelectedListener(this)


        chart!!.animateY(2000, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        var l = chart!!.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        chart!!.setEntryLabelColor(Color.WHITE)
        chart!!.setEntryLabelTypeface(tfRegular)
        chart!!.setEntryLabelTextSize(12f)

        setData()

    }


    private fun setData() {


        sumTransactionsByCategorie()
        depenseOarCategories.clear()

        depenseOarCategories.add(0, totalRetraitArgent.toFloat())
        depenseOarCategories.add(1, totalDeposArgent.toFloat())
        depenseOarCategories.add(2, totalAchatdecredit.toFloat())
        depenseOarCategories.add(3, totalTransfertArgent.toFloat())
        depenseOarCategories.add(4, totalFactureEneot.toFloat())
        depenseOarCategories.add(5, totalAchatConnexion.toFloat())

        val depensesTotales = depenseOarCategories.sum()

        val depense = "Dépenses totales $depensesTotales FCFA"
        id_tv_depenses_totales.text = depense
        val entries = ArrayList<PieEntry>()

        for (i in 0 until categoriesTransactions.size) {
            entries.add(
                PieEntry(
                    depenseOarCategories[i],
                    categoriesTransactions[i]
                )
            )
        }

        val dataSet = PieDataSet(entries, "Catégories de transaction")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f


        //creation des couleurs
        val colorsArray: ArrayList<Int> = arrayListOf()
        colorsArray.add(Color.BLUE)
        colorsArray.add(Color.RED)
        colorsArray.add(Color.GREEN)
        colorsArray.add(Color.CYAN)
        colorsArray.add(Color.YELLOW)
        colorsArray.add(Color.MAGENTA)

        dataSet.colors = colorsArray
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        data.setValueTypeface(tfLight)
        chart!!.data = data

        // undo all highlights
        chart!!.highlightValues(null)

        chart!!.invalidate()
    }

    private fun generateCenterSpannableText(): SpannableString {

        val s = SpannableString("Recapitulatif des transactions\n toutes catégories confondues")
        s.setSpan(RelativeSizeSpan(1.7f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
        s.setSpan(RelativeSizeSpan(.8f), 30, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 43, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 30, s.length, 0)
        return s
    }


    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        //toast("Depensess")

        //var post1 = e.toString().indexOf("(sum): ")
        val amount = e.toString().substring(16)

        Log.i("Statistique", amount)
/*        for(i in 0 until depenseOarCategories.size -1){
            if(depenseOarCategories[i] == amount.toFloat()){
                post1 = i
                break
            }
        }

        toast(e.toString())
        toast(h.toString())*/

        // val categoRie  = categoriesTransactions[post1 +1]
        //toast("Categorie $categoRie \n Valeures: $amount")


        AlertDialog.Builder(this)
            .setTitle("Montant total de la transaction")
            .setMessage("Le montant total de vos dépenses pour cette catégorie est de: $amount FCFA")
            .setIcon(R.drawable.ic_action_form)
            .setNeutralButton("OK"
            ) { dialog, id ->
                dialog.cancel()
            }.show()

    }


    fun sumTransactionsByCategorie() {
        // on recupère les messages provenant d'orange money  et de MTN Money
        val listorangeTransactions =
            SmsUtils.getMessagebyCriterion(this, "inbox", "address LiKE 'OrangeMoney'")
        listorangeTransactions.addAll(
            SmsUtils.getMessagebyCriterion(
                this,
                "inbox",
                "address LiKE 'MobileMoney'"
            )
        )

        // les dépos d'agent dans le compte de l'utilisateur
        var transactionType =
            SmsUtils.findSpecificSpending(
                listorangeTransactions,
                CategorieNature.NATURE_DEPOS_ARGENT
            )
        for (element in transactionType) {
            totalDeposArgent += element.montanttransaction
        }


        // les achats de credit dans le compte de l'utilisateur
        transactionType =
            SmsUtils.findSpecificSpending(listorangeTransactions, CategorieNature.NATURE_ACHAT_CREDIT)
        for (element in transactionType) {
            totalAchatdecredit += element.montanttransaction
        }


        //les factures d'éneo
        transactionType =
            SmsUtils.findSpecificSpending(listorangeTransactions, CategorieNature.NATURE_FACTURE_ENEO)
        for (element in transactionType) {
            totalFactureEneot += element.montanttransaction
        }

        // les achats de connexion
        transactionType =
            SmsUtils.findSpecificSpending(
                listorangeTransactions,
                CategorieNature.NATURE_ACHAT_CONNEXION
            )
        for (element in transactionType) {
            totalAchatConnexion += element.montanttransaction
        }

        // les retraits d'argent
        transactionType =
            SmsUtils.findSpecificSpending(
                listorangeTransactions,
                CategorieNature.NATURE_RETRAIT_ARGENT
            )
        for (element in transactionType) {
            totalRetraitArgent += element.montanttransaction
        }
    }
}
