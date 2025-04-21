package com.example.gismobil

import com.example.gismobil.settings.SettingsFragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gismobil.chat.ChatFragment
import com.example.gismobil.map.CameraHelper
import com.example.gismobil.map.GlobeRotator
import com.example.gismobil.map.MapInitializer
import com.example.gismobil.map.MapUtils
import com.example.gismobil.utils.PerformanceUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var welcomeButton: Button
    private lateinit var navView: BottomNavigationView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Performans durumunu kontrol et ve kaydet
        PerformanceUtils.checkAndSavePerformanceMode(this)
        val isLowPerformanceMode = PerformanceUtils.isLowPerformanceMode(this)

        setContentView(R.layout.activity_main)

        // Başlığı kapat
        supportActionBar?.hide()

        // UI bileşenlerini başlat
        mapView = findViewById(R.id.mapView)
        welcomeButton = findViewById(R.id.welcomeButton)
        navView = findViewById(R.id.nav_view)

        // Haritayı başlat (cihaz performansına göre)
        MapInitializer.setupMap(this, mapView, isLowPerformanceMode)

        // 3D binaları haritaya ekle (sadece yüksek performanslı cihazlarda)
        if (!isLowPerformanceMode) {
            MapUtils.add3DBuildings(mapView)
        }

        // Welcome button tıklama olayı
        welcomeButton.setOnClickListener {
            // Butonu gizle
            welcomeButton.visibility = View.GONE

            // Edremit'in koordinatları
            val edremitLocation = Point.fromLngLat(27.01944, 39.59611)

            // Animasyon mesajı
            Toast.makeText(this, "Edremit'e uçuluyor...", Toast.LENGTH_SHORT).show()

            // Kamera animasyonu
            CameraHelper.flyToEdremit(mapView, edremitLocation)

            // Animasyon tamamlandıktan sonra chat arayüzünü göster
            handler.postDelayed({
                // Sohbet sekmesine geç
                navView.selectedItemId = R.id.navigation_chat
            }, 5000)
        }

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

    // Haritayı göster ve fragment'ları kaldır
    private fun showMap() {
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment.isVisible) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
        mapView.visibility = View.VISIBLE
    }

    // Sohbet arayüzünü gösterme
    private fun showChatInterface() {
        val chatFragment = ChatFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, chatFragment)
            .commit()

        // Haritayı göstermeye devam et ama arkada dursun
        mapView.visibility = View.VISIBLE
    }

    // Ayarlar arayüzünü gösterme
    private fun showSettingsInterface() {
        val settingsFragment = SettingsFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, settingsFragment)
            .commit()

        // Haritayı göstermeye devam et ama arkada dursun
        mapView.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        GlobeRotator.stop()
    }
}