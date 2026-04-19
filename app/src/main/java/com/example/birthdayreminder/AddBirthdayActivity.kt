package com.example.birthdayreminder

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddBirthdayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_birthday)

        // 1. TÜM ELEMANLARI EN ÜSTTE TANITIYORUZ
        val tvTarih = findViewById<android.widget.TextView>(R.id.tvTarih)
        val btnTarihSec = findViewById<android.widget.Button>(R.id.btnTarihSec)
        val btnKaydet = findViewById<android.widget.Button>(R.id.btnKaydet)
        val etIsim = findViewById<android.widget.EditText>(R.id.etIsim)
        val cbBugun = findViewById<android.widget.CheckBox>(R.id.cbBugun)
        val cbBirGunOnce = findViewById<android.widget.CheckBox>(R.id.cbBirGunOnce)
        val cbBirHaftaOnce = findViewById<android.widget.CheckBox>(R.id.cbBirHaftaOnce)
        val takvim = java.util.Calendar.getInstance()

        btnTarihSec.setOnClickListener {
            val yil = takvim.get(java.util.Calendar.YEAR)
            val ay = takvim.get(java.util.Calendar.MONTH)
            val gun = takvim.get(java.util.Calendar.DAY_OF_MONTH)

            val datePicker = android.app.DatePickerDialog(this,
                { _, secilenYil, secilenAy, secilenGun ->
                    val gercekAy = secilenAy + 1
                    tvTarih.text = "$secilenGun/$gercekAy/$secilenYil"

                    // Seçilen tarihi hafızaya (takvime) atıyoruz
                    takvim.set(secilenYil, secilenAy, secilenGun)
                }, yil, ay, gun)
            datePicker.show()
        }

        // 4. KAYDET BUTONU TIKLANMA KODLARI
        btnKaydet.setOnClickListener {
            val girilenIsim = etIsim.text.toString()

            if (girilenIsim.isEmpty()) {
                android.widget.Toast.makeText(this, "Lütfen bir isim girin!", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ... isim kontrolünden hemen sonra ...

            // 1. Herhangi bir kutucuk seçilmiş mi kontrol edelim
            val secenekSecildiMi = cbBugun.isChecked || cbBirGunOnce.isChecked || cbBirHaftaOnce.isChecked

            // 2. Eğer hiçbiri seçilmemişse (! işareti "değilse" demektir)
            if (!secenekSecildiMi) {
                android.widget.Toast.makeText(this, "Lütfen en az bir hatırlatma seçeneği seçin!", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener // İşlemi burada durdur, aşağıya (Firebase'e) geçme
            }

            // ARTIK BURASI KIRMIZI YANMAYACAK ÇÜNKÜ "takvim"i YUKARIDA (2. ADIMDA) GÖRÜYOR
            val yil = takvim.get(java.util.Calendar.YEAR)
            val ay = takvim.get(java.util.Calendar.MONTH) + 1
            val gun = takvim.get(java.util.Calendar.DAY_OF_MONTH)

            val dogumGunuVerisi = hashMapOf(
                "isim" to girilenIsim,
                "gun" to gun,
                "ay" to ay,
                "yil" to yil,
                "bugunHatirlat" to cbBugun.isChecked,
                "birGunOnceHatirlat" to cbBirGunOnce.isChecked,
                "birHaftaOnceHatirlat" to cbBirHaftaOnce.isChecked
            )

            val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
            db.collection("DogumGunleri")
                .add(dogumGunuVerisi)
                .addOnSuccessListener {
                    android.widget.Toast.makeText(this, "Başarıyla Kaydedildi! 🚀", android.widget.Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { hata ->
                    android.widget.Toast.makeText(this, "Hata oluştu: ${hata.message}", android.widget.Toast.LENGTH_LONG).show()
                }

            // ... Firebase addOnSuccessListener içinde ...
            if (cbBugun.isChecked) alarmKur(girilenIsim, yil, gun, ay, 0, "bugün doğum günü!")
            if (cbBirGunOnce.isChecked) alarmKur(girilenIsim, yil, gun, ay, 1, "doğum günü yarın!")
            if (cbBirHaftaOnce.isChecked) alarmKur(girilenIsim, yil, gun, ay, 7, "doğum gününe 1 hafta kaldı!")
        }
    }

    private fun alarmKur(isim: String, dogumYili: Int, gun: Int, ay: Int, offset: Int, mesaj: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = android.content.Intent(this, AlarmReceiver::class.java).apply {
            putExtra("ISIM", isim)
            putExtra("MESAJ", mesaj)
        }

        // Her alarmın farklı bir kimliği olmalı (çakışmasınlar diye)
        val alarmId = (isim + mesaj).hashCode()
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            this, alarmId, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val takvim = java.util.Calendar.getInstance()
        val suankiYil = takvim.get(java.util.Calendar.YEAR)

        // Alarm tarihini ayarlıyoruz
        val hedefTakvim = java.util.Calendar.getInstance()
        hedefTakvim.set(java.util.Calendar.YEAR, suankiYil)
        hedefTakvim.set(java.util.Calendar.MONTH, ay - 1)
        hedefTakvim.set(java.util.Calendar.DAY_OF_MONTH, gun)
        hedefTakvim.set(java.util.Calendar.HOUR_OF_DAY, 9) // Sabah 09:00'da çalsın
        hedefTakvim.set(java.util.Calendar.MINUTE, 0)

        // Seçilen seçeneğe göre (o gün, 1 gün önce, 1 hafta önce) tarihi kaydır
        hedefTakvim.add(java.util.Calendar.DATE, -offset)

        // Eğer tarih geçmişse bir sonraki yıla kur
        if (hedefTakvim.before(java.util.Calendar.getInstance())) {
            hedefTakvim.add(java.util.Calendar.YEAR, 1)
        }

        // Yaş hesaplama (Bildirim metni için)
        val yeniMesaj = if (offset == 0) "bugün ${hedefTakvim.get(java.util.Calendar.YEAR) - dogumYili} yaşına girdi!" else mesaj
        intent.putExtra("MESAJ", yeniMesaj)

        // Alarmı kur (Tam vaktinde çalması için)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Telefon Android 12 veya üzeriyse izin kontrolü yapıyoruz
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    android.app.AlarmManager.RTC_WAKEUP,
                    hedefTakvim.timeInMillis,
                    pendingIntent
                )
            } else {
                // Kullanıcıya izin vermediğini söyleyip ayarlar sayfasına yönlendiriyoruz
                android.widget.Toast.makeText(this, "Hatırlatıcı için 'Alarmlar' izni vermeniz gerekiyor!", android.widget.Toast.LENGTH_LONG).show()
                val ayarlarIntent = android.content.Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(ayarlarIntent)
            }
        } else {
            // Telefon eski (Android 11 ve altı) ise doğrudan kurabiliriz
            alarmManager.setExactAndAllowWhileIdle(
                android.app.AlarmManager.RTC_WAKEUP,
                hedefTakvim.timeInMillis,
                pendingIntent
            )
        }
    }
}