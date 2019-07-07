package com.kola.moneypal.intro_slider

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kola.moneypal.R
import androidx.viewpager.widget.ViewPager
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Button
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import android.content.Intent
import android.graphics.Color
import android.text.Html
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.kola.moneypal.authentification.SingInActivity
import com.kola.moneypal.datas.SharedPreference
import com.kola.moneypal.datas.SheredprefKeysObj
import com.kola.moneypal.glide.GlideApp
import kotlinx.android.synthetic.main.activity_firts_splash_screen.view.*
import kotlinx.android.synthetic.main.intro_screen2.*


class MainScreen : AppCompatActivity() {

    private var preferenceManager: SharedPreference? = null
    var Layout_bars: LinearLayout? = null
    var bottomBars: ArrayList<TextView>? = null
    var screens: IntArray? = null
    var Skip: Button? = null
    var Next: Button? = null
    var vp: ViewPager? = null
    var myvpAdapter: MyViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        supportActionBar?.hide()
        vp = findViewById<ViewPager>(R.id.view_pager)
        Layout_bars = findViewById<LinearLayout>(R.id.layoutBars)
        Skip = findViewById<Button>(R.id.skip)

        Next = findViewById<Button>(R.id.next)
        Next!!.setOnClickListener {
            next(it)
        }
        preferenceManager = SharedPreference(this)

        screens = intArrayOf(R.layout.intro_screen1, R.layout.intro_screen2, R.layout.intro_screen3)
        myvpAdapter = MyViewPagerAdapter(this.screens!!, this)
        vp!!.adapter = myvpAdapter
        vp!!.addOnPageChangeListener(viewPagerPageChangeListener) //on écoute l'évènement du changement d'écran

        if (preferenceManager!!.getValueBoolien(SheredprefKeysObj.FIRST_INITIALISATION_OF_APP, false)) {
            launchMain()
            finish()
        }
        ColoredBars(0)

        Skip!!.setOnClickListener {
            skip(it)
        }

    }

    fun next(v: View) {
        val i = getItem(+1)
        if (i < screens?.size!!) {
            vp?.currentItem = i
        } else {
            launchMain()
        }
    }

    fun skip(view: View) {
        launchMain()
    }

    private fun ColoredBars(thisScreen: Int) {
        val colorsInactive = resources.getIntArray(R.array.dot_on_page_not_active)
        val colorsActive = resources.getIntArray(R.array.dot_on_page_active)
        bottomBars = ArrayList<TextView>()

        Layout_bars!!.removeAllViews()
        for (i in 0 until bottomBars!!.size) {
            bottomBars!!.add(i, TextView(this))
            bottomBars!![i].textSize = 100.0f
            bottomBars!![i].text = Html.fromHtml("¯_---")
            Layout_bars!!.addView(bottomBars!![i])
            //bottomBars!![i].setTextColor(colorsInactive[thisScreen])
            bottomBars!![i].setTextColor(Color.GRAY)
        }
        if (bottomBars!!.size > 0) {
            //bottomBars!![thisScreen].setTextColor(colorsActive[thisScreen])
            bottomBars!![thisScreen].setTextColor(Color.WHITE)
        }
    }

    private fun getItem(i: Int): Int {
        return vp!!.currentItem + i
    }

    private fun launchMain() {
        preferenceManager?.save(SheredprefKeysObj.FIRST_INITIALISATION_OF_APP, true)
        startActivity(Intent(this@MainScreen, SingInActivity::class.java))
        finish()
    }

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            ColoredBars(position)
            if (position == screens!!.size - 1) {
                Next?.text = "start"
                Skip?.visibility = View.GONE
            } else {
                Next?.text = getString(R.string.next)
                Skip?.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }
    }

    class MyViewPagerAdapter(private var screens: IntArray, private val context: Context) : PagerAdapter() {


        private var inflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            inflater = LayoutInflater.from(context)
            val view = inflater!!.inflate(screens[position], container, false)

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return screens.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val v = `object` as View
            container.removeView(v)
        }

        override fun isViewFromObject(view: View, myobject: Any): Boolean {
            return view === myobject
        }
    }
}
