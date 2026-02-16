package com.example.veterinariaapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.veterinariaapp.ui.fragments.ConsultasFragment
import com.example.veterinariaapp.ui.fragments.MascotasFragment
import com.example.veterinariaapp.ui.viewmodel.ListadoViewModel
import com.google.android.material.tabs.TabLayout

class ListadoActivityV4 : AppCompatActivity() {

    private val viewModel: ListadoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_v4)

        setupToolbar()
        setupTabs()

        // Cargamos fragment inicial
        if (savedInstanceState == null) {
            loadFragment(MascotasFragment())
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Listados"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupTabs() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayoutListado)
        tabLayout.addTab(tabLayout.newTab().setText("🐾 Mascotas"))
        tabLayout.addTab(tabLayout.newTab().setText("📋 Consultas"))

        // Accesibilidad
        tabLayout.getTabAt(0)?.contentDescription = "Pestaña de mascotas"
        tabLayout.getTabAt(1)?.contentDescription = "Pestaña de consultas"

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadFragment(MascotasFragment())
                    1 -> loadFragment(ConsultasFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerListado, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}