package com.example.birthdayreminder

import DogumGunu
import DogumGunuAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val butonEkle =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabEkle)

        butonEkle.setOnClickListener {
            val gecis = android.content.Intent(this, AddBirthdayActivity::class.java)

            startActivity(gecis)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.btnTarihSec)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ana ekranda o ay içerisindeki doğum günlerini görüntüleme
        val rvDogumGunleri =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvDogumGunleri)
        val tvAyBaslik = findViewById<android.widget.TextView>(R.id.tvAyBaslik)

        // Listenin nasıl görüneceğini söylüyoruz (Alt alta düz bir liste)
        rvDogumGunleri.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val takvim = java.util.Calendar.getInstance()

        // 1. Şimdiki ayı ve Sonraki ayı bulalım
        val suankiAy = takvim.get(java.util.Calendar.MONTH) + 1
        // Eğer 12. ayadaysak (Aralık), sonraki ay 1 (Ocak) olur. Değilse normal bir artır.
        val sonrakiAy = if (suankiAy == 12) 1 else suankiAy + 1

        // Başlığı daha genel bir şey yapalım (İsteğe bağlı)
        tvAyBaslik.text = "YAKLAŞAN DOĞUM GÜNLERİ"

        // 2. Firebase'den İKİ AYI birden çekelim (whereIn komutu ile)
        db.collection("DogumGunleri")
            .whereIn(
                "ay",
                listOf(suankiAy, sonrakiAy)
            ) // "Ay değeri suankiAy YADA sonrakiAy olanları getir"
            .addSnapshotListener { veriler, hata ->
                if (hata != null) return@addSnapshotListener

                if (veriler != null) {
                    val liste = veriler.toObjects(DogumGunu::class.java)

                    // 3. AKILLI SIRALAMA (Aralık - Ocak problemini çözer)
                    val siraliListe = liste.sortedWith(compareBy<DogumGunu> {
                        // Eğer ay şimdiki aydan küçükse (Biz Aralıkta, hedef Ocak'taysa) onu seneye at ki listenin sonuna gitsin
                        if (it.ay < suankiAy) it.ay + 12 else it.ay
                    }.thenBy {
                        it.gun // Ayları dizdikten sonra günleri kendi içinde diz
                    })

                    rvDogumGunleri.adapter = DogumGunuAdapter(siraliListe)
                }
            }
    }
}