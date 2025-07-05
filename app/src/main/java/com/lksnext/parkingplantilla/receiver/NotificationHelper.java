package com.lksnext.parkingplantilla.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lksnext.parkingplantilla.domain.Reserva;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationHelper {

    public static void programarNotificaciones(Context context, Reserva reserva) {
        long startMillis = calcularInicioMillis(reserva);
        long endMillis = calcularFinMillis(reserva);

        long notificacionInicio = startMillis - 15 * 60 * 1000;
        if (notificacionInicio > System.currentTimeMillis()) {
            programarAlarma(context, reserva, notificacionInicio, "Tu reserva empieza en 15 minutos", "START");
        }

        long notificacionFin30 = endMillis - 30 * 60 * 1000;
        if (notificacionFin30 > System.currentTimeMillis()) {
            programarAlarma(context, reserva, notificacionFin30, "Tu reserva termina en 30 minutos", "END30");
        }

        long notificacionFin15 = endMillis - 15 * 60 * 1000;
        if (notificacionFin15 > System.currentTimeMillis()) {
            programarAlarma(context, reserva, notificacionFin15, "Tu reserva termina en 15 minutos", "END15");
        }
    }

    private static void programarAlarma(Context context, Reserva reserva, long triggerAtMillis, String mensaje, String tipoNotificacion) {
        Intent intent = new Intent(context, NotificacionReceiver.class);
        intent.putExtra("title", "ParkingLKS");
        intent.putExtra("message", mensaje);

        int requestCode = generarRequestCode(reserva, tipoNotificacion);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            pendingIntent
                    );
                } else {
                    alarmManager.setWindow(
                            AlarmManager.RTC_WAKEUP,
                            triggerAtMillis,
                            5 * 60 * 1000,
                            pendingIntent
                    );

                    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(settingsIntent);
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                );
            }
        }
    }

    public static void cancelarNotificaciones(Context context, Reserva reserva) {
        cancelarAlarma(context, reserva, "START");
        cancelarAlarma(context, reserva, "END30");
        cancelarAlarma(context, reserva, "END15");
    }

    private static void cancelarAlarma(Context context, Reserva reserva, String tipoNotificacion) {
        Intent intent = new Intent(context, NotificacionReceiver.class);
        intent.putExtra("title", "ParkingLKS");

        int requestCode = generarRequestCode(reserva, tipoNotificacion);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private static int generarRequestCode(Reserva reserva, String tipoNotificacion) {
        return reserva.getIdReserva().hashCode() + tipoNotificacion.hashCode();
    }

    private static long calcularInicioMillis(Reserva reserva) {
        try {
            String horaInicioStr = reserva.getHora().getHoraInicio();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(reserva.getFecha().toDate());
            return sdf.parse(fechaStr + " " + horaInicioStr).getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    private static long calcularFinMillis(Reserva reserva) {
        try {
            String horaFinStr = reserva.getHora().getHoraFin();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(reserva.getFecha().toDate());
            return sdf.parse(fechaStr + " " + horaFinStr).getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    public static void programarNotificacionesReserva(Context context, String fecha, List<String> horas) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            long startMillis = sdf.parse(fecha + " " + horas.get(0)).getTime();
            long endMillis = sdf.parse(fecha + " " + horas.get(horas.size() - 1)).getTime();

            long notificacionInicio = startMillis - 15 * 60 * 1000;
            if (notificacionInicio > System.currentTimeMillis()) {

                // Se recomienda usar programarNotificaciones(context, reserva) mejor.
            }

            long notificacionFin30 = endMillis - 30 * 60 * 1000;
            if (notificacionFin30 > System.currentTimeMillis()) {
                // Igual aquí, solo si reservas sin objeto Reserva.
            }

            long notificacionFin15 = endMillis - 15 * 60 * 1000;
            if (notificacionFin15 > System.currentTimeMillis()) {
                // Igual aquí.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
