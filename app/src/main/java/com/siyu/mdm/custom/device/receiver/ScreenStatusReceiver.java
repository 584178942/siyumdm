package com.siyu.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.siyu.mdm.custom.device.util.LogUtils;
import static android.content.ContentValues.TAG;
import static com.siyu.mdm.custom.device.util.TaskUtil.startAlarm;
import static com.siyu.mdm.custom.device.util.TaskUtil.startHeartBeatAlarm;

/**
 * 灭屏/亮屏广播
 * @author Z T
 */
public class ScreenStatusReceiver extends BroadcastReceiver {
    /**
     * 亮屏
     */
    public static final String ANDROID_INTENT_ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    /**
     * 灭屏
     */
    public static final String ANDROID_INTENT_ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ANDROID_INTENT_ACTION_SCREEN_ON.equals(intent.getAction())) {
            LogUtils.info(TAG, "Detect screen on and set mScreenPowerStatus false");
            //startAlarm();
            startHeartBeatAlarm();
        } else if (ANDROID_INTENT_ACTION_SCREEN_OFF.equals(intent.getAction())) {
            LogUtils.info(TAG, "Detect screen off and set mScreenPowerStatus ture");
        }
    }
}
