package com.lksnext.parkingplantilla.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationHelper {
    public static void programarNotificacionesReserva(Context context, String fecha, List<String> horas) {
        if (horas == null || horas.isEmpty()) return;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            // Ordena las horas por orden ascendente
            horas.sort(String::compareTo);

            // Toma la primera hora para la notificación de inicio
            String horaInicio = horas.get(0);
            String horaFin = horas.get(horas.size() - 1);

            Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(sdf.parse(fecha + " " + horaInicio));

            Calendar calFin = Calendar.getInstance();
            calFin.setTime(sdf.parse(fecha + " " + horaFin));

            // Resta 30 min al inicio
            calInicio.add(Calendar.MINUTE, -30);
            // Resta 15 min al fin
            calFin.add(Calendar.MINUTE, -15);

            programarAlarma(context, calInicio.getTimeInMillis(),
                    "Recordatorio de reserva",
                    "Tu reserva empieza en 30 minutos");

            programarAlarma(context, calFin.getTimeInMillis(),
                    "Fin de reserva",
                    "Tu reserva termina en 15 minutos");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void programarAlarma(Context context, long triggerAtMillis, String title, String message) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificacionReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        int requestCode = (int) System.currentTimeMillis(); // único
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
}
