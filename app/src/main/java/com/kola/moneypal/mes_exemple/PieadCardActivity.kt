package com.kola.moneypal.mes_exemple

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kola.moneypal.R
import kotlinx.android.synthetic.main.activity_piead_card.*
import kotlinx.android.synthetic.main.fragment_statistiques.*

class PieadCardActivity : AppCompatActivity() {


    private val yData = mutableListOf<Float>(25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f)
    private val xData = mutableListOf<String>("Mich", "Jessica", "Mohammad", "KElsey", "Sam", "Robert", "Ashley")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_piead_card)

        idPieChart_activity.apply {
            contentDescription = "Sales by employer(in Thousands $"
            isRotationEnabled = true
            //setUsePercentValues(true)
            //setHoleColor(Color.BLUE)
            //setCenterTextColor(Color.BLACK)
            holeRadius = 25f
            centerText = "Syper cool chart"
            setTransparentCircleAlpha(0)
            setCenterTextSize(10.0f)
            //setDrawEntryLabels(true)
            //setEntryLabelTextSize(20.0f)
        }

        addataSet()
        //idPieChart. setOnChartValueSelectedListener(set)
    }

    private fun addataSet() {
        val yEntrys: ArrayList<PieEntry> = arrayListOf()
        val xEntrys: ArrayList<String> = arrayListOf()

        var count = 0 until yData.size-1
        for (i in count) {
            yEntrys.add(PieEntry(yData[i], i))
        }
        count = 0 until xData.size-1
        for (i in count) {
            xEntrys.add(xData[i])
        }

        // creation de la dataSet
        val pieDataSet = PieDataSet(yEntrys, "Employer Sales")

        pieDataSet.apply {
            sliceSpace = 2.0f
            valueTextSize = 12.0f
        }

        //creation des couleurs
        val colorsArray: ArrayList<Int> = arrayListOf()
        colorsArray.add(Color.GRAY)
        colorsArray.add(Color.BLUE)
        colorsArray.add(Color.RED)
        colorsArray.add(Color.GREEN)
        colorsArray.add(Color.CYAN)
        colorsArray.add(Color.YELLOW)
        colorsArray.add(Color.MAGENTA)

        pieDataSet.colors = colorsArray

        //legende
        val legend = idPieChart_activity.legend
        legend.form = Legend.LegendForm.CIRCLE
        //legend.setPosition(LegendPosition.BELOW_CHART_LEFT)

        //create pie data object
        val  pieData = PieData(pieDataSet)
        idPieChart_activity.data = pieData
        idPieChart_activity.invalidate()

    }

}
