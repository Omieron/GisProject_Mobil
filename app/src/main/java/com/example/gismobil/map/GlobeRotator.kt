package com.example.gismobil.map

import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object GlobeRotator {
    private var rotationJob: Job? = null

    // Dünya döndürme işlemi
    fun start(mapView: MapView) {
        rotationJob?.cancel()  // Önceki döndürme işini iptal et
        var angle = 0.0

        rotationJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(16) // ~60fps
                angle = (angle + 0.1) % 360
                val currentLng = 35.0 - angle
                mapView.getMapboxMap().setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(currentLng, 10.0))  // Sabit enlem
                        .zoom(0.1)  // Uzaklaştırılmış
                        .bearing(0.0)  // Döndürme açısı
                        .pitch(0.0)  // Yükseklik
                        .build()
                )
            }
        }
    }

    // Döndürmeyi durdurma işlemi
    fun stop() {
        rotationJob?.cancel()
        rotationJob = null
    }
}
