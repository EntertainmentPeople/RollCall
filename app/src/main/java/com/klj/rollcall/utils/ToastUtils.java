package com.klj.rollcall.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.klj.rollcall.base.BaseActivity;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;

/**
 * Created by 娱乐人物 on 2017/5/18.
 */

public class ToastUtils {
    public static void showToastAtCenterOfScreen(Context appCxt, int resId) {
        Toast toast = Toast.makeText(appCxt, resId, Toast.LENGTH_SHORT);
        toast.setGravity(CENTER, 0, 0);
        toast.show();
    }

    public static void showToastAtBottomOfScreen(Context appCxt, int resId) {
        Toast toast = Toast.makeText(appCxt, resId, Toast.LENGTH_SHORT);
        toast.setGravity(BOTTOM, 0, 0);
        toast.show();
    }

    public static void showToastAtCenterOfScreen(Context appCxt, String msg) {
        Toast toast = Toast.makeText(appCxt, msg, Toast.LENGTH_SHORT);
        toast.setGravity(CENTER, 0, 0);
        toast.show();
    }

    public static void showToastAtBottomOfScreen(Context appCxt, String msg) {
        Toast toast = Toast.makeText(appCxt, msg, Toast.LENGTH_SHORT);
        toast.setGravity(BOTTOM, 0, 0);
        toast.show();
    }

    public static void showToastAndFinish(final Context appCxt, String msg) {
        showToastAtCenterOfScreen(appCxt, msg);
        Handler jumpHandler = new Handler();
        final int JUMP_DELAY = 3000;
        Runnable jumpThread = new Runnable() {
            @Override
            public void run() {
                ((BaseActivity) appCxt).finish();
            }
        };
        jumpHandler.postDelayed(jumpThread, JUMP_DELAY);
    }
}
