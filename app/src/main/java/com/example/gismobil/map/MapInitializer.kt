package com.example.gismobil.map

import android.content.Context
import com.example.gismobil.utils.PerformanceUtils
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

    fun setupMap(context: Context, mapView: MapView, performanceLevel: Int = PerformanceUtils.PERFORMANCE_MEDIUM) {
        val mapboxMap = mapView.getMapboxMap()

        // Performans için gereksiz UI öğelerini devre dışı bırak
        setupMapUIElements(mapView, performanceLevel)

        // Kamerayı başlangıç konumuna ayarlama
        val startPosition = CameraOptions.Builder()
            .center(Point.fromLngLat(35.0, 10.0))
            .zoom(0.1)
            .bearing(0.0)
            .pitch(0.0)
            .build()

        mapboxMap.setCamera(startPosition)

        // Performans seviyesine göre stil seçme
        val styleUri = getMapStyleForPerformanceLevel(performanceLevel)

        mapboxMap.loadStyleUri(styleUri) { style ->
            // Düşük veya orta performans için gereksiz katmanları gizleme
            if (performanceLevel != PerformanceUtils.PERFORMANCE_HIGH) {
                hideUnneededLayers(style, performanceLevel)
            }

            // Harita yüklendikten sonra dünya döndürme işlemini başlatma
            startGlobeRotation(mapView, performanceLevel)
        }
    }

    // Harita UI elemanlarını performans seviyesine göre ayarla
    private fun setupMapUIElements(mapView: MapView, performanceLevel: Int) {
        // Ölçek çubuğu ve pusula her zaman kapalı olsun
        mapView.scalebar.enabled = false
        mapView.compass.enabled = false

        // Kullanıcı etkileşimi ayarları
        mapView.gestures.apply {
            // Tüm performans seviyelerinde temel etkileşimler açık
            pinchToZoomEnabled = true
            doubleTapToZoomInEnabled = true
            quickZoomEnabled = true
            scrollEnabled = true

            // Orta ve yüksek performans modlarında ek özellikler açık
            doubleTouchToZoomOutEnabled = performanceLevel >= PerformanceUtils.PERFORMANCE_MEDIUM
            pitchEnabled = performanceLevel >= PerformanceUtils.PERFORMANCE_MEDIUM
            rotateEnabled = performanceLevel >= PerformanceUtils.PERFORMANCE_MEDIUM
        }
    }

    // Performans seviyesine göre harita stili belirle
    private fun getMapStyleForPerformanceLevel(performanceLevel: Int): String {
        return when (performanceLevel) {
            PerformanceUtils.PERFORMANCE_LOW -> "mapbox://styles/mapbox/light-v11"  // En hafif stil
            PerformanceUtils.PERFORMANCE_MEDIUM -> "mapbox://styles/mapbox/streets-v12" // Normal stil
            PerformanceUtils.PERFORMANCE_HIGH -> "mapbox://styles/mapbox/streets-v12"   // Tam detaylı stil (3D binalar ile)
            else -> "mapbox://styles/mapbox/streets-v12"
        }
    }

    // Globe döndürme işlemini başlat
    private fun startGlobeRotation(mapView: MapView, performanceLevel: Int) {
        // Performans seviyesine göre FPS ve animasyon kalitesi ayarla
        val fpsRate = when (performanceLevel) {
            PerformanceUtils.PERFORMANCE_LOW -> 15L    // Düşük FPS
            PerformanceUtils.PERFORMANCE_MEDIUM -> 30L // Normal FPS
            PerformanceUtils.PERFORMANCE_HIGH -> 60L   // Yüksek FPS
            else -> 30L
        }

        // Sadece orta ve yüksek performans modlarında yumuşak animasyon
        val smoothAnimation = performanceLevel >= PerformanceUtils.PERFORMANCE_MEDIUM

        // Globe döndürme işlemini başlat
        GlobeRotator.start(mapView, fpsRate, smoothAnimation)
    }

    // Düşük performans modu için gereksiz katmanları gizleme
    private fun hideUnneededLayers(style: Style, performanceLevel: Int) {
        // Düşük performans modunda tamamen gizlenmesi gereken katmanlar
        val lowPerformanceHiddenLayers = listOf(
            "poi-label",           // İlgi çekici noktalar
            "transit-label",       // Toplu taşıma etiketleri
            "road-label",          // Yol etiketleri
            "natural-point-label", // Doğal nokta etiketleri
            "water-point-label",   // Su noktası etiketleri
            "building",            // Bina katmanı (3D olmayan)
            "road-secondary-tertiary", // İkincil ve üçüncül yollar
            "road-street",         // Sokaklar
            "waterway"             // Su yolları
        )

        // Orta performans modunda gizlenmesi gereken katmanlar
        val mediumPerformanceHiddenLayers = listOf(
            "natural-point-label", // Sadece doğal nokta etiketleri
            "water-point-label"    // Su noktası etiketleri
        )

        // Performans seviyesine göre gizlenecek katmanları belirle
        val layersToHide = when (performanceLevel) {
            PerformanceUtils.PERFORMANCE_LOW -> lowPerformanceHiddenLayers
            PerformanceUtils.PERFORMANCE_MEDIUM -> mediumPerformanceHiddenLayers
            else -> emptyList()
        }

        // Katmanları gizle
        layersToHide.forEach { layerId ->
            style.getLayer(layerId)?.visibility(Visibility.NONE)
        }
    }
}