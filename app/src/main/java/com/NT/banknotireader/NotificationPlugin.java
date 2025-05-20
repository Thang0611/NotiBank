package com.NT.banknotireader;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.util.Log;

public class NotificationPlugin {

    private static final String TAG = "NotificationPlugin";

    public static void startNotificationService(Context context) {
        Log.i(TAG, "Notification Listener Service Started");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30 trở lên
            Intent intent = new Intent("android.settings.NOTIFICATION_LISTENER_DETAIL_SETTINGS");
            ComponentName componentName = new ComponentName(
                    context.getPackageName(),
                    NotificationListenerService.class.getName()
            );
            intent.putExtra(
                    "android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME",
                    componentName.flattenToString()
            );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
