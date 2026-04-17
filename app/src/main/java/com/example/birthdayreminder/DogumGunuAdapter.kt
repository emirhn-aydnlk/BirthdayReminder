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
        holder.tvTarih.text = kisi.gun.toString()

        // RENK MANTIĞI BURADA ÇALIŞIYOR
        val bugun = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        if (kisi.gun < bugun) {
            // GEÇTİ - KIRMIZI TONU
            holder.tvDurum.text = "GEÇTİ"
            holder.arkaplan.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2")) // Hafif kırmızı
        } else if (kisi.gun == bugun) {
            // BUGÜN! - MAVİ TONU
            holder.tvDurum.text = "BUGÜN!"
            holder.arkaplan.setBackgroundColor(android.graphics.Color.parseColor("#BBDEFB")) // Hafif mavi
        } else {
            // YAKLAŞIYOR - YEŞİL TONU
            holder.tvDurum.text = "YAKLAŞIYOR"
            holder.arkaplan.setBackgroundColor(android.graphics.Color.parseColor("#C8E6C9")) // Hafif yeşil
        }
    }

    override fun getItemCount(): Int = liste.size
}