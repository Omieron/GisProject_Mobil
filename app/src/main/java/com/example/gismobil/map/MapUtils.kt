package com.example.gismobil.map

import android.widget.Toast
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.FillExtrusionLayer
import com.mapbox.maps.extension.style.layers.getLayer

object MapUtils {
    // 3D binalar ekleyen fonksiyon
    fun add3DBuildings(mapView: MapView) {
        val mapboxMap = mapView.getMapboxMap()

        mapboxMap.getStyle { style ->
            // Eğer 3D binalar katmanı haritaya eklenmemişse ekleyelim
            if (style.getLayer("3d-buildings") == null) {
                // Create fill extrusion layer
                val fillExtrusionLayer = FillExtrusionLayer(
                    layerId = "3d-buildings",
                    sourceId = "composite"
                )

                // Set layer source layer
                fillExtrusionLayer.sourceLayer("building")

                // Set the fill extrusion color
                fillExtrusionLayer.fillExtrusionColor(android.graphics.Color.parseColor("#AAAAAA"))

                // Set height using properties from the data
                fillExtrusionLayer.fillExtrusionHeight(Expression.get("height"))

                // Set base height using min_height from the data
                fillExtrusionLayer.fillExtrusionBase(Expression.get("min_height"))

                // Set opacity
                fillExtrusionLayer.fillExtrusionOpacity(0.6)

                // Add the layer to the style
                style.addLayer(fillExtrusionLayer)

                // Display success message
                // Toast.makeText(mapView.context, "3D Binalar Haritaya Eklendi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}