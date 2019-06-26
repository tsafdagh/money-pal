package com.kola.moneypal

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kola.moneypal.fragments.HomeFragment
import com.kola.moneypal.fragments.ObjectifGroupFragment
import com.kola.moneypal.mes_exemple.ReadMessageActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_groupe -> {
                replaceFragment(ObjectifGroupFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.navigation)

        navView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_groupe -> {
                    replaceFragment(ObjectifGroupFragment())
                    true
                }

                else -> {
                    false
                }

            }
        }
    }

    override fun onStart() {
        super.onStart()
        replaceFragment(HomeFragment())

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_layout, fragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.id_menu_objectifs -> {
                toast("objectifs")
            }

            R.id.id_menu_stat -> {
                toast("statistiques")
            }

            R.id.id_menu_mon_compte -> {
                toast("mon compte")
                startActivity<ReadMessageActivity>()
            }
            else -> {
                toast(getString(R.string.selection_inconu))
                Log.d("MainActivity", getString(R.string.selection_inconu))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
