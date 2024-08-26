package com.example.exergames_beta.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Random;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.exergames_beta.R;

public class DailyNotificationReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        // Crear el NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Generar un número aleatorio entre 1 y 3
        int randomNotificationId = new Random().nextInt(3) + 1;
        String channelId = "";

        // Configurar el canal y el contenido de la notificación según el ID aleatorio
        NotificationCompat.Builder builder;
        switch (randomNotificationId) {
            case 1:
                channelId = "rehab_reminder_channel";
                builder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.logouibblanco)
                        .setContentTitle("¡Es hora de hacer tus ejercicios de rehabilitación!")
                        .setContentText("Recuerda realizar tus ejercicios diarios para mejorar tu salud.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                break;

            case 2:
                channelId = "mid_morning_reminder_channel";
                builder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.logouibblanco)
                        .setContentTitle("¡Es hora de una pausa activa!")
                        .setContentText("Jugar a los exergames puede ayudarte a despejarte y relajarte.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                break;

            case 3:
                channelId = "evening_relaxation_channel";
                builder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.logouibblanco)
                        .setContentTitle("¿Has hecho tus ejercicios diarios?")
                        .setContentText("No te olvides de hacer tus ejercicios de rehabilitación.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + randomNotificationId);
        }

        // Verificar si el canal de notificaciones ya existe
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    getChannelName(channelId),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Enviar la notificación
        notificationManager.notify(randomNotificationId, builder.build());
    }

    private String getChannelName(String channelId) {
        switch (channelId) {
            case "rehab_reminder_channel":
                return "Recordatorio de Rehabilitación";
            case "mid_morning_reminder_channel":
                return "Pausa Activa a Media Mañana";
            case "evening_relaxation_channel":
                return "Recordatorio de Relajación por la Noche";
            default:
                throw new IllegalArgumentException("Unknown channel ID");
        }
    }

}

