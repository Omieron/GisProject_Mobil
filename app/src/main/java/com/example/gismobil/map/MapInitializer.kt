package com.example.gismobil.map

import android.content.Context
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.plugin.compass.compass

object MapInitializer {

    fun setupMap(context: Context, mapView: MapView) {
        mapView.getMapboxMap().loadStyleUri("mapbox://styles/mapbox/streets-v12") {
            val startPosition = CameraOptions.Builder()
                .center(Point.fromLngLat(35.0, 10.0))  // Başlangıç koordinatı
                .zoom(0.1)  // Dünya'nın tamamı görünsün
                .bearing(0.0)  // Dönüş
                .pitch(0.0)  // Yükseklik
                .build()

            mapView.getMapboxMap().setCamera(startPosition)

            // Ölçek ve pusula ayarları
            mapView.scalebar.enabled = false
            mapView.compass.enabled = false

            // Dünya döndürme başlatma
            GlobeRotator.start(mapView)
        }
    }
}
