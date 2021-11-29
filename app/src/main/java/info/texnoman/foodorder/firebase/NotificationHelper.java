package info.texnoman.foodorder.firebase;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import info.texnoman.foodorder.HomeActivity;
import info.texnoman.foodorder.MainActivity;
import info.texnoman.foodorder.R;
import info.texnoman.foodorder.SplashActivity;

public class NotificationHelper {
    public static void displayNotification(Context context,String title, String text){


        Intent resultIntent = new Intent(context, SplashActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, HomeActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.delivery_boy2)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,mBuilder.build());

    }
}
