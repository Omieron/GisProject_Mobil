# Edremit Belediyesi Uygulaması - Sesli Mesaj Özelliği

## Genel Bakış

Bu özellik, kullanıcıların mikrofon düğmesine basarak sesli mesaj göndermelerini sağlar. Android'in SpeechRecognizer API'sini kullanarak konuşma metne dönüştürülür.

## Dosya Yapısı

Özellik aşağıdaki dosyalardan oluşur:

1. **SpeechRecognizerHelper.kt**: Ana ses tanıma yardımcı sınıfı, mikrofon butonunu yönetir ve dinleme işlevselliğini sağlar.

2. **SpeechRecognizerConfig.kt**: Ses tanıma ayarları için farklı intent yapılandırmaları içerir.

3. **ChatFragment.kt**: Konuşma tanıma özelliğini sohbet arayüzüne entegre eder.

4. **chat_fragment.xml**: Mikrofon butonu eklenen kullanıcı arayüzü düzeni.

5. **Drawable kaynakları**: Mikrofon ikonları (normal ve aktif durumlar için).

## Kullanım Şekli

### Normal Dinleme (5-10 saniye)
- Mikrofon düğmesine **kısa basın**
- Sistem sizi dinleyecek ve konuşmanızı algılayacak
- Konuşma tamamlandığında, algılanan metin otomatik olarak giriş kutusuna yerleştirilecek

### Uzun Dinleme (15-20 saniye)
- Mikrofon düğmesine **uzun basın** (1 saniye kadar)
- "Uzun dinleme modu aktif" mesajı görünecek
- Sistem daha uzun süre dinleyecek ve duraksamalara karşı daha toleranslı olacak
- Konuşma tamamlandığında metin giriş kutusuna yerleştirilecek

## Teknik Özellikleri

- Türkçe dil desteği (`Locale("tr", "TR")`)
- Gerçek zamanlı geri bildirim (konuşurken kısmi sonuçlar görünür)
- Esnek dinleme süreleri (kısa basış = normal süre, uzun basış = uzun süre)
- Otomatik mikrofon izni yönetimi
- Hata durumlarında anlaşılır bildirimler

## Kurulum

1. Tüm dosyaları projeye ekleyin
2. AndroidManifest.xml'e mikrofon iznini ekleyin:
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```
3. Gerekli drawable kaynaklarını ekleyin:
    - ic_mic.xml (normal durum)
    - ic_mic_active.xml (dinleme durumu)

## Uyarılar

- Google Play Servisleri gereklidir (cihazda yüklü olmalı)
- İnternet bağlantısı gereklidir (tanıma işlemi sunucu tarafında gerçekleşir)