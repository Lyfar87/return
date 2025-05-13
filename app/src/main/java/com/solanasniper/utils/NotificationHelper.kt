package com.solanasniper.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.solanasniper.R

class NotificationHelper(private val context: Context) {
    private val channelId = "sniper_alerts"
    private val importance = NotificationManager.IMPORTANCE_HIGH

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.notification_channel_name),
                importance
            ).apply {
                description = context.getString(R.string.notification_channel_description)
            }

            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    fun showSnipeAlert(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
    }

    fun showPriceAlert(token: String, price: Double) {
        val message = context.getString(R.string.price_alert_message, token, price)
        showSnipeAlert(context.getString(R.string.price_alert_title), message)
    }
}