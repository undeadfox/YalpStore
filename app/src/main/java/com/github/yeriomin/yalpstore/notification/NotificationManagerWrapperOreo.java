package com.github.yeriomin.yalpstore.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.github.yeriomin.yalpstore.BuildConfig;
import com.github.yeriomin.yalpstore.R;

@TargetApi(Build.VERSION_CODES.O)
public class NotificationManagerWrapperOreo extends NotificationManagerWrapperJellybean {

    public NotificationManagerWrapperOreo(Context context) {
        super(context);
        NotificationChannel channel = manager.getNotificationChannel(BuildConfig.APPLICATION_ID);
        if (null == channel) {
            manager.createNotificationChannel(new NotificationChannel(
                BuildConfig.APPLICATION_ID,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ));
        }
    }

    @Override
    protected Notification.Builder getBuilder(Intent intent, String title, String message) {
        return super.getBuilder(intent, title, message).setChannel(BuildConfig.APPLICATION_ID);
    }
}
