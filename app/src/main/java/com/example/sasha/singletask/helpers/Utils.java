package com.example.sasha.singletask.helpers;

import android.app.Activity;
import android.app.FragmentManager;

public class Utils {
    private static int userId;

    // ASK: что тут происходит?
    public static void clearBackStack(Activity activity) {
        FragmentManager manager = activity.getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public static void setUserId(int userId) {
        Utils.userId = userId;
    }

    public static int getUserId() {
        return userId;
    }
}
