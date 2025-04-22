package com.example.gismobil.map

import android.content.Context
import com.example.gismobil.utils.PerformanceUtils
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.logo.logo

/**
 * Harita işlemlerini yöneten controller sınıfı
 */
class MapController(
    private val context: Context,
    private val mapView: MapView
) {
    private val isLowPerformanceMode: Boolean

    init {
        // Performans durumunu kontrol et
        isLowPerformanceMode = PerformanceUtils.isLowPerformanceMode(context)

        // Haritayı başlat
        setupMap()

        // Mapbox logosu ve atıf alanını özelleştir
        customizeMapboxLogoAndAttribution()
    }

    /**
     * Haritayı ve ilgili özellikleri başlatır
     */
    private fun setupMap() {
        // Haritayı başlat (cihaz performansına göre)
        MapInitializer.setupMap(context, mapView, isLowPerformanceMode)

        // 3D binaları haritaya ekle (sadece yüksek performanslı cihazlarda)
        if (!isLowPerformanceMode) {
            MapUtils.add3DBuildings(mapView)
        }
    }

    /**
     * Mapbox logosu ve atıf alanını özelleştir - basit yöntem
     */
    private fun customizeMapboxLogoAndAttribution() {
        try {
            // Logo ve attribution için konum ayarlarını basit şekilde yapalım
            mapView.getMapboxMap().getStyle { style ->
                // Bu noktada daha basit ayarlamalar da yapılabilir
                // Şimdilik mevcut durumu koruyalım
            }

            // Logo ve attribution plugin'lerine doğrudan erişim
            val logoPlugin = mapView.logo
            val attributionPlugin = mapView.attribution

            // En basit ayarları yapalım
            logoPlugin.enabled = true  // Logoyu aktif tut
            attributionPlugin.enabled = true  // Attribution'ı aktif tut

        } catch (e: Exception) {
            // Herhangi bir hata durumunda logoyu ve attribution'ı varsayılan halde bırak
            // ve hatayı görmezden gel
        }
    }

    /**
     * Kaynakları temizler
     */
    fun cleanup() {
        GlobeRotator.stop()
    }
}