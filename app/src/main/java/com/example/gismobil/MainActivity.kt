package com.example.gismobil

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
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var welcomeButton: Button
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
            // 5 saniye sonra (animasyon süresi kadar)
            handler.postDelayed({
                showChatInterface()
            }, 5000) // 5 saniye (CameraHelper.kt'deki animasyon süresi ile aynı)
        }
    }

    // Sohbet arayüzünü gösterme
    private fun showChatInterface() {
        val chatFragment = ChatFragment()

        supportFragmentManager.beginTransaction()
            .add(android.R.id.content, chatFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        GlobeRotator.stop()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}