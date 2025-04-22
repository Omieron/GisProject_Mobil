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
    private val performanceLevel: Int

    init {
        // Performans seviyesini kontrol et
        performanceLevel = PerformanceUtils.getPerformanceLevel(context)

        // Haritayı başlat
        setupMap()

        // Mapbox logosu ve atıf alanını özelleştir
        customizeMapboxLogoAndAttribution()
    }

    /**
     * Haritayı ve ilgili özellikleri başlatır
     */
    private fun setupMap() {
        // Haritayı performans seviyesine göre başlat
        MapInitializer.setupMap(context, mapView, performanceLevel)

        // Sadece yüksek performans modunda 3D binaları ekle
        if (performanceLevel == PerformanceUtils.PERFORMANCE_HIGH) {
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

            // Performans seviyesine göre logo ve attribution ayarları
            when (performanceLevel) {
                PerformanceUtils.PERFORMANCE_LOW -> {
                    // Düşük performans modunda minimum görünürlük
                    logoPlugin.enabled = true
                    attributionPlugin.enabled = true
                    // Daha sade görünüm için ayarlar yapılabilir
                }
                PerformanceUtils.PERFORMANCE_MEDIUM, PerformanceUtils.PERFORMANCE_HIGH -> {
                    // Orta ve yüksek performans modunda tam görünürlük
                    logoPlugin.enabled = true
                    attributionPlugin.enabled = true
                }
            }

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