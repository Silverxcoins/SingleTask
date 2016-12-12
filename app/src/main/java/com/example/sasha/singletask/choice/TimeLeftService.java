package com.example.sasha.singletask.choice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.user.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeLeftService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(TimeLeftService.class);
    public static final String ACTION_TIME_LEFT_CHANGED = "action.TIME_LEFT_CHANGED";
    private static final String TIME_KEY = "time";
    private static final String NAME_KEY = "name";

    private Thread thread;
    private NotificationManager nm;

    @Override
    public void onCreate() {

        logger.debug("onCreate()");

        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {

        logger.debug("onStartCommand()");

        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int time = intent.getIntExtra(TIME_KEY, 0);
                    String name = intent.getStringExtra(NAME_KEY);

                    while (time != 0) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            logger.warn("Sleep failed");
                        }
                        time--;
                        Intent timeIntent = new Intent(ACTION_TIME_LEFT_CHANGED);
                        timeIntent.putExtra(TIME_KEY, time);
                        sendBroadcast(timeIntent);
                    }
                    sendNotification(name);
                    TimeLeftService.super.stopSelf();
                }
            });
            thread.start();
        }

        return START_REDELIVER_INTENT;
    }

    void sendNotification(String name) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_ic_googleplayservices)
                        .setContentTitle("Время задания истекло")
                        .setContentText(name);


        Intent resultIntent = new Intent(this, ChoiceActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
