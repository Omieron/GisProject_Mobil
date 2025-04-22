package com.example.gismobil.welcome

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.gismobil.R
import com.example.gismobil.map.CameraHelper
import com.example.gismobil.navigation.NavigationManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView

/**
 * Karşılama ekranını ve ilgili animasyonları yöneten sınıf
 */
class WelcomeManager(
    private val context: Context,
    private val mapView: MapView,
    private val welcomeButton: Button,
    private val welcomeCard: CardView,
    private val navView: BottomNavigationView,
    private val navigationManager: NavigationManager
) {
    private val handler = Handler(Looper.getMainLooper())

    init {
        // Başlangıçta bottom navigation'ı gizle
        navView.visibility = View.GONE
        navView.translationY = -200f  // Ekranın dışında bir pozisyonda başla

        setupWelcomeScreen()
    }

    /**
     * Karşılama ekranını ve buton olaylarını ayarlar
     */
    private fun setupWelcomeScreen() {
        // Welcome button tıklama olayı
        welcomeButton.setOnClickListener {
            // Animasyonu başlat
            startWelcomeAnimation()
        }
    }

    /**
     * Karşılama animasyonu ve akışını başlatır
     */
    private fun startWelcomeAnimation() {
        // Karşılama kartını tamamen gizle
        welcomeCard.visibility = View.GONE

        // Edremit'in koordinatları
        val edremitLocation = Point.fromLngLat(27.01944, 39.59611)

        // Animasyon mesajı
        Toast.makeText(context, "Edremit'e uçuluyor...", Toast.LENGTH_SHORT).show()

        // Kamera animasyonu
        CameraHelper.flyToEdremit(mapView, edremitLocation)

        // Navigation bar'ı hemen göster (animasyonla)
        showNavigationWithAnimation()

        // Uçuş tamamlandıktan sonra chat sekmesine geç
        handler.postDelayed({
            // Hoş geldiniz modunu devre dışı bırak
            navigationManager.deactivateWelcomeMode()

            // Sohbet sekmesine geç
            navigationManager.navigateTo(R.id.navigation_chat)
        }, 5000)
    }

    /**
     * Navigation bar'ı animasyonla gösterir
     */
    private fun showNavigationWithAnimation() {
        // Önce görünür yap ama hala ekranın dışında
        navView.visibility = View.VISIBLE

        // Yukarıdan aşağıya doğru kayarak gelsin
        val navAnim = ObjectAnimator.ofFloat(navView, "translationY", -200f, 0f)
        navAnim.duration = 800
        navAnim.interpolator = AccelerateDecelerateInterpolator()
        navAnim.start()
    }

    /**
     * Handler'ı temizler
     */
    fun cleanup() {
        handler.removeCallbacksAndMessages(null)
    }
}