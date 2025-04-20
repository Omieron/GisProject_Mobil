package com.example.gismobil

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gismobil.map.CameraHelper
import com.example.gismobil.map.MapInitializer
import com.example.gismobil.map.MapUtils
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //basligi kapat
        supportActionBar?.hide()

        // UI bileşenlerini başlat
        mapView = findViewById(R.id.mapView)
        val welcomeButton = findViewById<Button>(R.id.welcomeButton) // Buton ID'sini düzelttim

        // Haritayı başlat
        MapInitializer.setupMap(this, mapView)

        // 3D binaları haritaya ekle
        MapUtils.add3DBuildings(mapView)

        // Welcome button tıklama olayı
        welcomeButton.setOnClickListener {
            // Edremit'in koordinatları
            val edremitLocation = Point.fromLngLat(27.01944, 39.59611) // Edremit koordinatları
            CameraHelper.flyToEdremit(mapView, edremitLocation)
            // Butonu gizle
            welcomeButton.visibility = Button.GONE
            Toast.makeText(this, "Edremit'e uçuluyor...", Toast.LENGTH_SHORT).show()
        }

        // Hoşgeldiniz mesajı
        //Toast.makeText(this, "3D binalar haritaya eklendi", Toast.LENGTH_LONG).show()
    }
}