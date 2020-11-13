package com.siyu.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.siyu.mdm.custom.device.util.LogUtils;

/**
 * 开机自启
 *
 * @author Z T
 * @date 20200924
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    /**
     * TAG
     */
    private static final String TAG = "BootBroadcastReceiver";
    /**
     * 开机广播
     */
    static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    /**
     * 开机广播
     */
    static final String LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BOOT_COMPLETED)) {
            LogUtils.info(TAG, BOOT_COMPLETED);
        } else if (intent.getAction().equals(LOCKED_BOOT_COMPLETED)) {
            LogUtils.info(TAG, LOCKED_BOOT_COMPLETED);
        }
    }
}
