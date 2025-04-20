package com.example.gismobil.map

import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo

object CameraHelper {
    fun flyToEdremit(mapView: MapView, edremitLocation: Point) {
        GlobeRotator.stop()

        mapView.getMapboxMap().flyTo(
            CameraOptions.Builder()
                .center(edremitLocation)
                .zoom(15.0)
                .pitch(60.0)
                .bearing(-17.6)
                .build(),
            MapAnimationOptions.mapAnimationOptions {
                duration(5000)
            }
        )
    }
}
