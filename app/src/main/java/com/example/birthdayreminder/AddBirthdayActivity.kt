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
        }
    }
}