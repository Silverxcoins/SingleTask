package com.example.sasha.singletask.choice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.sasha.singletask.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeLeftService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(TimeLeftService.class);
    public static final String ACTION_TIME_LEFT_CHANGED = "action.TIME_LEFT_CHANGED";
    private static final String TIME_KEY = "time";
    private static final String NAME_KEY = "name";
    private static final String FROM_SERVICE_KEY = "isIntentFromService";

    private static Thread thread;

    @Override
    public void onCreate() {

        logger.debug("onCreate()");

        super.onCreate();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        logger.debug("onStartCommand()");

        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int time = intent.getIntExtra(TIME_KEY, 0);
                    String name = intent.getStringExtra(NAME_KEY);

                    while (!Thread.interrupted() && time != 0) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            logger.warn("Sleep failed");
                            Thread.currentThread().interrupt();
                            TimeLeftService.super.stopSelf();
                        }
                        time--;
                        Intent timeIntent = new Intent(ACTION_TIME_LEFT_CHANGED);
                        timeIntent.putExtra(TIME_KEY, time);
                        sendBroadcast(timeIntent);
                        if (time == 0) {
                            sendNotification(name);
                        }
                    }
                    Thread.currentThread().interrupt();
                    TimeLeftService.super.stopSelf();
                }
            });
            thread.start();
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        logger.debug("onDestroy()");

        super.onDestroy();
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    void sendNotification(String name) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.common_ic_googleplayservices)
                        .setContentTitle("Время задания истекло")
                        .setContentText(name);
        mBuilder.setAutoCancel(true);


        Intent resultIntent = new Intent(this, ChoiceActivity.class);
        resultIntent.putExtra(FROM_SERVICE_KEY, true);
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
