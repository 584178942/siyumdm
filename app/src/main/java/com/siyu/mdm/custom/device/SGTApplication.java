package com.siyu.mdm.custom.device;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.siyu.mdm.custom.device.receiver.PollAlarmReceiver;
import com.siyu.mdm.custom.device.receiver.ScreenStatusReceiver;

import java.util.List;

import static com.siyu.mdm.custom.device.util.MdmUtil.setUninstall;
import static com.siyu.mdm.custom.device.util.TaskUtil.startAlarm;
import static com.siyu.mdm.custom.device.util.TaskUtil.startHeartBeatAlarm;

/**
 * @author Z T
 */
public class SGTApplication extends Application {
    private static final String TAG = "SGTApplication";
    private static Context ourInstance;

    public static Application contextApp;
    private List<String> whiteList;
    private PollAlarmReceiver pollAlarmReceiver;
    private ScreenStatusReceiver mScreenStatusReceiver;
    public static Context getContextApp() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setContextApp(this);
        ourInstance = getApplicationContext();
        contractStatusCheck();
        setUninstall();
        startHeartBeatAlarm();

        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.SCREEN_ON");
        intentFilter3.addAction("android.intent.action.SCREEN_OFF");
        mScreenStatusReceiver = new ScreenStatusReceiver();
        registerReceiver(mScreenStatusReceiver, intentFilter3);



    }

    private void setContextApp(Application application) {
        contextApp = application;
    }

    private void contractStatusCheck() {

    }


}
