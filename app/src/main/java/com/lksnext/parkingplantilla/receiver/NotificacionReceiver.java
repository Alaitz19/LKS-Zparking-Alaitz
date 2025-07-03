package com.lksnext.parkingplantilla.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.lksnext.parkingplantilla.R;

public class NotificacionReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "ParkingLKS_Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crea el canal si es necesario (Android 8+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "ParkingLKS Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de recordatorios de reserva");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.zparking) // Icono de tu app
                .setContentTitle(title != null ? title : "ParkingLKS")
                .setContentText(message != null ? message : "Tu reserva está por terminar.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
