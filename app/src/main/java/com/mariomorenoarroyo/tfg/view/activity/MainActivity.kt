package com.mariomorenoarroyo.tfg.view.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mariomorenoarroyo.tfg.R
import com.mariomorenoarroyo.tfg.view.fragment.SaboresFragment
import com.mariomorenoarroyo.tfg.view.fragment.TiendaFragment
import com.mariomorenoarroyo.tfg.view.fragment.VapeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        val navigation:BottomNavigationView = findViewById(R.id.navigation_view)
        val btnShow: Button =findViewById(R.id.btnShowMenu)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_tienda -> {
                    replaceFragment(TiendaFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_sabor -> {
                    replaceFragment(SaboresFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_vape -> {
                    replaceFragment(VapeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }


        btnShow.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_tienda -> {
                  //  replaceFragment(TiendaFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_sabor -> {
                   // replaceFragment(SaboresFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.menu_vape -> {
                  //  replaceFragment(VapeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }


    }




    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_main, fragment)
            .addToBackStack(null)
            .commit()
    }




}