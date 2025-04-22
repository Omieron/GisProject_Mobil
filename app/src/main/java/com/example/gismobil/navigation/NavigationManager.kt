package com.example.gismobil.navigation

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.gismobil.R
import com.example.gismobil.chat.ChatFragment
import com.example.gismobil.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.maps.MapView

/**
 * Navigasyon işlemlerini yöneten yardımcı sınıf
 */
class NavigationManager(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val navView: BottomNavigationView,
    private val mapView: MapView
) {
    // Hoş geldiniz ekranı aktifken diğer sekmelere erişimi kontrol etmek için flag
    private var welcomeActive = true

    init {
        setupNavigation()
    }

    /**
     * Navigation bar'ı ayarlar ve tıklama olaylarını yönetir
     */
    private fun setupNavigation() {
        // Bottom Navigation için listener ekle
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_map -> {
                    // Sadece haritayı göster
                    showMap()
                    true
                }
                R.id.navigation_chat -> {
                    // Chat arayüzünü göster
                    showChatInterface()
                    true
                }
                R.id.navigation_settings -> {
                    // Ayarlar arayüzünü göster
                    showSettingsInterface()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Hoş geldiniz ekranını devre dışı bırakır
     */
    fun deactivateWelcomeMode() {
        welcomeActive = false
    }

    /**
     * Belirli bir navigation öğesini seçer
     */
    fun navigateTo(itemId: Int) {
        navView.selectedItemId = itemId
    }

    /**
     * Haritayı göster ve fragment'ları kaldır
     */
    fun showMap() {
        fragmentManager.fragments.forEach { fragment ->
            if (fragment.isVisible) {
                fragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
        mapView.visibility = View.VISIBLE
    }

    /**
     * Sohbet arayüzünü gösterme
     */
    private fun showChatInterface() {
        val chatFragment = ChatFragment()

        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, chatFragment)
            .commit()

        // Haritayı göstermeye devam et ama arkada dursun
        mapView.visibility = View.VISIBLE
    }

    /**
     * Ayarlar arayüzünü gösterme
     */
    private fun showSettingsInterface() {
        val settingsFragment = SettingsFragment()

        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, settingsFragment)
            .commit()

        // Haritayı göstermeye devam et ama arkada dursun
        mapView.visibility = View.VISIBLE
    }
}