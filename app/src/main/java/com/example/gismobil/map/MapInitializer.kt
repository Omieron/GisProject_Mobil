package com.example.gismobil.map

import android.content.Context
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility

object MapInitializer {

    fun setupMap(context: Context, mapView: MapView, lowPerformanceMode: Boolean = false) {
        val mapboxMap = mapView.getMapboxMap()

        // Performans için gereksiz UI öğelerini devre dışı bırak
        mapView.scalebar.enabled = false
        mapView.compass.enabled = false

        // Kullanıcı etkileşimini koruyan ayarlar
        mapView.gestures.apply {
            pinchToZoomEnabled = true    // Yakınlaştırma/uzaklaştırma
            doubleTapToZoomInEnabled = true  // Çift tıklama ile yakınlaştırma
            doubleTouchToZoomOutEnabled = true  // Çift dokunma ile uzaklaştırma
            quickZoomEnabled = true      // Hızlı yakınlaştırma
            pitchEnabled = true          // Eğim ayarı
            scrollEnabled = true         // Kaydırma etkin
        }

        // Kamerayı başlangıç konumuna ayarlama
        val startPosition = CameraOptions.Builder()
            .center(Point.fromLngLat(35.0, 10.0))
            .zoom(0.1)
            .bearing(0.0)
            .pitch(0.0)
            .build()

        mapboxMap.setCamera(startPosition)

        // Düşük performans modu için daha basit bir stil yükleme
        val styleUri = if (lowPerformanceMode) {
            "mapbox://styles/mapbox/light-v11" // Daha hafif stil
        } else {
            "mapbox://styles/mapbox/streets-v12"
        }

        mapboxMap.loadStyleUri(styleUri) { style ->
            // Performans için gereksiz katmanları gizleme
            if (lowPerformanceMode) {
                hideUnneededLayers(style)
            }

            // Harita yüklendikten sonra dünya döndürme işlemini başlatma
            val fpsRate = if (lowPerformanceMode) 20L else 30L // Düşük performans modunda daha düşük FPS
            GlobeRotator.start(mapView, fpsRate, !lowPerformanceMode)
        }
    }

    // Düşük performans modu için gereksiz katmanları gizleme
    private fun hideUnneededLayers(style: Style) {
        // Gereksiz katmanları listeleyin ve gizleyin
        val layersToHide = listOf(
            "poi-label", // İlgi çekici noktalar
            "transit-label", // Toplu taşıma etiketleri
            "road-label", // Yol etiketleri
            "natural-point-label", // Doğal nokta etiketleri
            "water-point-label" // Su noktası etiketleri
        )

        layersToHide.forEach { layerId ->
            style.getLayer(layerId)?.visibility(Visibility.NONE)
        }
    }
}