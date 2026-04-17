package com.example.birthdayreminder

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

        val butonEkle = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabEkle)

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
        val rvDogumGunleri = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvDogumGunleri)
        val tvAyBaslik = findViewById<android.widget.TextView>(R.id.tvAyBaslik)

        // Listenin nasıl görüneceğini söylüyoruz (Alt alta düz bir liste)
        rvDogumGunleri.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        // Bulunduğumuz ayı bulup başlığa yazma
        val takvim = java.util.Calendar.getInstance()
        val aylar = arrayOf("OCAK", "ŞUBAT", "MART", "NİSAN", "MAYIS", "HAZİRAN", "TEMMUZ", "AĞUSTOS", "EYLÜL", "EKİM", "KASIM", "ARALIK")
        val suankiAyIndex = takvim.get(java.util.Calendar.MONTH) // 0 (Ocak) ile 11 (Aralık) arası döner
        tvAyBaslik.text = aylar[suankiAyIndex]


        //firebase'den o ayki doğum günlerini getirme
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        // "DogumGunleri" klasöründe, "ay" değeri şimdiki aya eşit olanları bul
        // Not: Firestore'a kaydederken +1 eklemiştik (1-12 arası), o yüzden burada da +1 ekliyoruz.
        db.collection("DogumGunleri")
            .whereEqualTo("ay", suankiAyIndex + 1)
            .addSnapshotListener { veriler, hata ->
                if (hata != null) {
                    android.util.Log.e("HATA", "Veri çekilemedi: ${hata.message}")
                    return@addSnapshotListener
                }

                if (veriler != null) {
                    // Gelen ham verileri "DogumGunu" listesine çeviriyoruz
                    val liste = veriler.toObjects(DogumGunu::class.java)

                    // Listeyi gün sırasına göre dizelim (1 Nisan, 5 Nisan... şeklinde)
                    val siraliListe = liste.sortedBy { it.gun }

                    // Hazırladığımız Adapter'ı listeye bağlıyoruz
                    rvDogumGunleri.adapter = DogumGunuAdapter(siraliListe)
                }
            }
    }
}