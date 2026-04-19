package com.example.birthdayreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Intent'ten bilgileri alalım
        val isim = intent.getStringExtra("ISIM") ?: "Biri"
        val mesaj = intent.getStringExtra("MESAJ") ?: "Bugün birinin doğum günü!"

        val kanalId = "dogumgunu_kanali"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android 8.0 ve üzeri için bildirim kanalı oluşturmak şarttır
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val kanal = NotificationChannel(kanalId, "Doğum Günü Hatırlatıcı", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(kanal)
        }

        // Bildirimi hazırlıyoruz
        val bildirim = NotificationCompat.Builder(context, kanalId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Bildirim ikonu
            .setContentTitle("Doğum Günü Hatırlatıcı! 🎉")
            .setContentText("$isim $mesaj")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Tıklayınca gitsin
            .build()

        // Bildirimi gönder!
        notificationManager.notify(isim.hashCode(), bildirim)
    }
}