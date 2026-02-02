package com.example.birthdayreminder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddBirthdayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_birthday)

        // 1. Tasarımdaki elemanları tanıtalım
        val tvTarih = findViewById<android.widget.TextView>(R.id.tvTarih)
        val btnTarihSec = findViewById<android.widget.Button>(R.id.btnTarihSec)

// 2. Takvim ayarlarını tutacak bir nesne oluşturalım
// (Bu nesne hem varsayılan tarihi gösterecek hem de seçileni tutacak)
        val takvim = java.util.Calendar.getInstance()

// 3. Butona tıklanma olayını yazalım
        btnTarihSec.setOnClickListener {

            // Şimdiki zamanı alalım ki takvim açılınca bugünü göstersin
            val yil = takvim.get(java.util.Calendar.YEAR)
            val ay = takvim.get(java.util.Calendar.MONTH)
            val gun = takvim.get(java.util.Calendar.DAY_OF_MONTH)

            // 4. DatePickerDialog (Tarih Seçici Pencere) oluşturuyoruz
            val datePicker = android.app.DatePickerDialog(
                this,
                { _, secilenYil, secilenAy, secilenGun ->
                    // KULLANICI "TAMAM"A BASINCA BURASI ÇALIŞIR

                    // ÖNEMLİ NOT: Android'de aylar 0'dan başlar (Ocak=0, Şubat=1).
                    // O yüzden ekranda düzgün gözüksün diye aya +1 ekliyoruz.
                    val gercekAy = secilenAy + 1

                    // Seçilen tarihi TextView'a yazdıralım
                    tvTarih.text = "$secilenGun/$gercekAy/$secilenYil"

                    // Seçilen tarihi hafızadaki takvim nesnesine de kaydedelim (Kaydet butonu için lazım olacak)
                    takvim.set(secilenYil, secilenAy, secilenGun)

                }, yil, ay, gun
            ) // Takvim açıldığında varsayılan olarak hangi gün seçili gelsin?

            // 5. Pencereyi göster!
            datePicker.show()

            enableEdgeToEdge()

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnTarihSec)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}