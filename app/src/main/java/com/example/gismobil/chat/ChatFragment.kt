package com.example.gismobil.chat

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gismobil.R
import com.example.gismobil.utils.PerformanceUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var backgroundOverlay: View  // Arka plan overlay'i
    private val messageList = ArrayList<Message>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View'ları başlat
        recyclerView = view.findViewById(R.id.recyclerView)
        etMessage = view.findViewById(R.id.et_message)
        btnSend = view.findViewById(R.id.btn_send)
        btnBack = view.findViewById(R.id.btn_back)
        backgroundOverlay = view.findViewById(R.id.transparentBackground)

        // RecyclerView ayarları
        recyclerView.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true // En son mesajdan başla
        }
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter

        // Arka plan efektini ayarla
        setupBackgroundEffect()

        // Örnek mesajları ekle
        addExampleMessages()

        // Geri butonunu ayarla
        btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        // Gönder butonunu ayarla
        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                etMessage.setText("")
            }
        }
    }

    // Arka plan efektini cihaz performansına göre ayarla
    private fun setupBackgroundEffect() {
        val isLowPerformance = context?.let { PerformanceUtils.isLowPerformanceMode(it) } ?: true

        if (isLowPerformance) {
            // Düşük performans modu: Sadece yarı-saydam arka plan (daha az saydam)
            backgroundOverlay.setBackgroundColor(0xDDFFFFFF.toInt()) // Daha opak beyaz
        } else {
            // Yüksek performans modu: Android sürümüne göre farklı efekt
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ için RenderEffect API kullan
                applyBlurEffectForAndroid12Plus()
            } else {
                // Düşük Android sürümleri için daha şeffaf arka plan
                backgroundOverlay.setBackgroundColor(0xAAFFFFFF.toInt()) // Yarı saydam beyaz
            }
        }
    }

    // Android 12+ için bulanıklık efekti uygula
    private fun applyBlurEffectForAndroid12Plus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val blurEffect = android.graphics.RenderEffect.createBlurEffect(
                    20f, 20f, android.graphics.Shader.TileMode.CLAMP
                )
                backgroundOverlay.setRenderEffect(blurEffect)
                // Arka planın kendisi biraz daha şeffaf olsun
                backgroundOverlay.setBackgroundColor(0x88FFFFFF.toInt()) // Hafif şeffaf beyaz
            } catch (e: Exception) {
                // RenderEffect başarısız olursa normal arka plana geri dön
                backgroundOverlay.setBackgroundColor(0xAAFFFFFF.toInt()) // Yarı saydam beyaz
            }
        }
    }

    // Mesaj gönderme fonksiyonu
    private fun sendMessage(messageText: String) {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val message = Message(
            text = messageText,
            timestamp = currentTime,
            isSent = true
        )

        messageList.add(message)
        messageAdapter.notifyItemInserted(messageList.size - 1)
        recyclerView.scrollToPosition(messageList.size - 1)

        // Örnek olarak otomatik cevap
        simulateReply()
    }

    // Örnek cevap simülasyonu
    private fun simulateReply() {
        // 1 saniye sonra cevap geldiğini simüle et
        recyclerView.postDelayed({
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val replyMessage = Message(
                text = "Mesajınız alındı. Size nasıl yardımcı olabiliriz?",
                timestamp = currentTime,
                isSent = false
            )

            messageList.add(replyMessage)
            messageAdapter.notifyItemInserted(messageList.size - 1)
            recyclerView.scrollToPosition(messageList.size - 1)
        }, 1000)
    }

    // Örnek mesajlar ekle
    private fun addExampleMessages() {
        val messages = listOf(
            Message(
                text = "Merhaba, Edremit Belediyesi'ne hoş geldiniz. Size nasıl yardımcı olabiliriz?",
                timestamp = "09:30",
                isSent = false
            ),
            Message(
                text = "Merhaba, adres değişikliği yapmak istiyorum.",
                timestamp = "09:31",
                isSent = true
            ),
            Message(
                text = "Elbette, adres değişikliği için lütfen TC kimlik numaranızı ve yeni adresinizi paylaşır mısınız?",
                timestamp = "09:32",
                isSent = false
            )
        )

        messageList.addAll(messages)
        messageAdapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(messageList.size - 1)
    }
}