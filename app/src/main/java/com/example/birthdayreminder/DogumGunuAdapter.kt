import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.birthdayreminder.R
import java.util.*

class DogumGunuAdapter(private val liste: List<DogumGunu>) : RecyclerView.Adapter<DogumGunuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIsim: TextView = view.findViewById(R.id.tvKartIsim)
        val tvTarih: TextView = view.findViewById(R.id.tvKartTarih)
        val tvDurum: TextView = view.findViewById(R.id.tvKartDurum)
        val arkaplan: View = view.findViewById(R.id.arkaplanKutu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dogum_gunu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kisi = liste[position]
        holder.tvIsim.text = kisi.isim
        val aylarKisa = arrayOf("Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara")

        // Kartta "8 Nis" veya "22 May" şeklinde yazdırıyoruz
        holder.tvTarih.text = "${kisi.gun} ${aylarKisa[kisi.ay - 1]}"

        // --- RENK MANTIĞI BAŞLANGICI ---
        val takvim = java.util.Calendar.getInstance()
        val suankiAy = takvim.get(java.util.Calendar.MONTH) + 1 // Şu anki ayı bul (1-12)
        val bugunGun = takvim.get(java.util.Calendar.DAY_OF_MONTH) // Bugün ayın kaçı?

        if (kisi.ay == suankiAy && kisi.gun < bugunGun) {
            // 1. DURUM: Sadece "Şu anki ayın" içinde olup günü geçenler
            holder.tvDurum.text = "GEÇTİ"
            holder.arkaplan.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"))

        } else if (kisi.ay == suankiAy && kisi.gun == bugunGun) {
            // 2. DURUM: Tam olarak bugün
            holder.tvDurum.text = "BUGÜN!"
            holder.arkaplan.setBackgroundColor(android.graphics.Color.parseColor("#BBDEFB"))

        } else {
            // 3. DURUM: Geriye kalan her şey (Bu ayın gelecek günleri VEYA sonraki aylar)
            holder.tvDurum.text = "YAKLAŞIYOR"
            holder.arkaplan.setBackgroundColor(android.graphics.Color.parseColor("#C8E6C9"))
        }
// --- RENK MANTIĞI BİTİŞİ ---
    }

    override fun getItemCount(): Int = liste.size
}