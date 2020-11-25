package com.siyu.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.siyu.mdm.custom.device.util.LogUtils;
import com.siyu.mdm.custom.device.util.MdmUtil;
import com.siyu.mdm.custom.device.util.StorageUtil;
import com.siyu.mdm.custom.device.util.TaskUtil;

import static com.siyu.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.siyu.mdm.custom.device.util.TaskUtil.isTopActivity;

/**
 * @author ZT
 * @date 20201110
 */
public class StartLockReceiver extends BroadcastReceiver {
    private static final String TAG = "StartLockReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.info(TAG,isTopActivity() + "lock:" + StorageUtil.get(IS_LOCK,""));

        if (!isTopActivity() && "lock".equals(StorageUtil.get(IS_LOCK,""))){
            TaskUtil.startLockActivity();
        }
    }
}
