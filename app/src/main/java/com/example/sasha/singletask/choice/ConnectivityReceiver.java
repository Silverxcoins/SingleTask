package com.example.sasha.singletask.choice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.Http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ConnectivityReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {

        logger.debug("Connectivity changed");

        if (Http.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.internet_is_available, Toast.LENGTH_SHORT).show();
        }
    }
}
