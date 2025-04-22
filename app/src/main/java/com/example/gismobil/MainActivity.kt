package com.example.gismobil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gismobil.map.MapController
import com.example.gismobil.navigation.NavigationManager
import com.example.gismobil.utils.PerformanceUtils
import com.example.gismobil.welcome.WelcomeManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.maps.MapView

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var navView: BottomNavigationView

    // Modüler bileşenler
    private lateinit var mapController: MapController
    private lateinit var navigationManager: NavigationManager
    private lateinit var welcomeManager: WelcomeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Performans durumunu kontrol et ve kaydet (yeni sistem)
        PerformanceUtils.checkAndSavePerformanceMode(this)

        setContentView(R.layout.activity_main)

        // Başlığı kapat
        supportActionBar?.hide()

        // UI bileşenlerini başlat
        initializeViews()

        // Modüler bileşenleri oluştur
        initializeComponents()
    }

    /**
     * View elemanlarını başlatır
     */
    private fun initializeViews() {
        mapView = findViewById(R.id.mapView)
        navView = findViewById(R.id.nav_view)
    }

    /**
     * Modüler yapıları başlatır
     */
    private fun initializeComponents() {
        // Sıralama önemli: Önce MapController, sonra NavigationManager, en son WelcomeManager

        // Harita kontrolcüsünü başlat
        mapController = MapController(this, mapView)

        // Navigasyon yöneticisini başlat
        navigationManager = NavigationManager(this, supportFragmentManager, navView, mapView)

        // Karşılama ekranı yöneticisini başlat
        welcomeManager = WelcomeManager(
            this,
            mapView,
            findViewById(R.id.welcomeButton),
            findViewById(R.id.welcomeCard),
            navView,
            navigationManager
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        // Kaynakları temizle
        welcomeManager.cleanup()
        mapController.cleanup()
    }
}