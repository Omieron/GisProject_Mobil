package com.example.gismobil.map

import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object GlobeRotator {
    private var rotationJob: Job? = null
    private var angle = 0.0

    // Dünya döndürme işlemi - optimize edilmiş versiyon
    fun start(mapView: MapView, fpsRate: Long = 30, smoothAnimation: Boolean = true) {
        stop() // Önceki döndürme işini iptal et

        val mapboxMap = mapView.getMapboxMap()
        val rotationSpeed = 0.1 // Dönüş hızı
        val frameDelay = 1000L / fpsRate // FPS'e göre frame arası gecikme (33ms ~ 30fps, 16ms ~ 60fps)

        rotationJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(frameDelay)
                angle = (angle + rotationSpeed) % 360
                val currentLng = 35.0 - angle

                if (smoothAnimation) {
                    // Animasyonlu geçiş - daha yumuşak ama biraz daha kaynak kullanır
                    mapboxMap.easeTo(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(currentLng, 10.0))
                            .zoom(0.1)
                            .bearing(0.0)
                            .pitch(0.0)
                            .build(),
                        MapAnimationOptions.Builder()
                            .duration(frameDelay) // Bir sonraki frame'e kadar olan süre
                            .build()
                    )
                } else {
                    // Direkt kamera değişimi - daha performanslı ama daha az yumuşak
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(currentLng, 10.0))
                            .zoom(0.1)
                            .bearing(0.0)
                            .pitch(0.0)
                            .build()
                    )
                }
            }
        }
    }

    // Döndürmeyi durdurma işlemi
    fun stop() {
        rotationJob?.cancel()
        rotationJob = null
    }

    // Döndürme hızını ayarlama
    fun setRotationSpeed(speed: Double) {
        // Gerektiğinde dinamik olarak hızı değiştirmek için uygulanabilir
    }

    // Döndürme durumunu kontrol etme
    fun isRotating(): Boolean {
        return rotationJob != null && rotationJob?.isActive == true
    }
}