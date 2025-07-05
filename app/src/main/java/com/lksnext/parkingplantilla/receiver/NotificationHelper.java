package com.lksnext.parkingplantilla.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Reserva;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public final class NotificationHelper {

    private NotificationHelper() {
    }


    public static void programarNotificaciones(Context context, Reserva reserva) {
        long now = System.currentTimeMillis();
        long startMillis = calcularMillis(reserva, true);
        long endMillis = calcularMillis(reserva, false);

        programarNotificacionSiProcede(context, reserva, startMillis - minutos(15),
                context.getString(R.string.notification_start_message), "START", now);
        programarNotificacionSiProcede(context, reserva, endMillis - minutos(30),
                context.getString(R.string.notification_end30_message), "END30", now);
        programarNotificacionSiProcede(context, reserva, endMillis - minutos(15),
                context.getString(R.string.notification_end15_message), "END15", now);
    }


    public static void cancelarNotificaciones(Context context, Reserva reserva) {
        cancelarAlarma(context, reserva, "START");
        cancelarAlarma(context, reserva, "END30");
        cancelarAlarma(context, reserva, "END15");
    }


    private static void programarNotificacionSiProcede(
            Context context,
            Reserva reserva,
            long triggerAtMillis,
            String mensaje,
            String tipoNotificacion,
            long now
    ) {
        if (triggerAtMillis > now) {
            programarAlarma(context, reserva, triggerAtMillis, mensaje, tipoNotificacion);
        }
    }

    private static void programarAlarma(
            Context context,
            Reserva reserva,
            long triggerAtMillis,
            String mensaje,
            String tipoNotificacion
    ) {
        Intent intent = new Intent(context, NotificacionReceiver.class);
        intent.putExtra("title", context.getString(R.string.notification_title_default));
        intent.putExtra("message", mensaje);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                generarRequestCode(reserva, tipoNotificacion),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else {
                    alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerAtMillis, minutos(5), pendingIntent);
                    Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(settingsIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        }
    }

    private static void cancelarAlarma(Context context, Reserva reserva, String tipoNotificacion) {
        Intent intent = new Intent(context, NotificacionReceiver.class);
        intent.putExtra("title", context.getString(R.string.notification_title_default));

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                generarRequestCode(reserva, tipoNotificacion),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }


    private static long calcularMillis(Reserva reserva, boolean inicio) {
        try {
            String hora = inicio ? reserva.getHora().getHoraInicio() : reserva.getHora().getHoraFin();
            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(reserva.getFecha().toDate());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return Objects.requireNonNull(sdf.parse(fecha + " " + hora)).getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    private static int generarRequestCode(Reserva reserva, String tipoNotificacion) {
        return reserva.getIdReserva().hashCode() + tipoNotificacion.hashCode();
    }

    private static long minutos(int minutos) {
        return minutos * 60 * 1000L;
    }
}
