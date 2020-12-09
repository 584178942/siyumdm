package com.siyu.mdm.custom.device;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import com.siyu.mdm.custom.device.receiver.ScreenStatusReceiver;
import com.siyu.mdm.custom.device.util.BdLocationUtil;
import com.siyu.mdm.custom.device.bean.BdLocationVo;
import static com.siyu.mdm.custom.device.util.MdmUtil.setUninstall;
import static com.siyu.mdm.custom.device.util.TaskUtil.startHeartBeatAlarm;

/**
 * @author Z T
 */
public class SGTApplication extends Application {
    private static final String TAG = "SGTApplication";
    private static Context ourInstance;
    public static Application contextApp;
    private static BdLocationUtil bdLocationUtil;
    private ScreenStatusReceiver mScreenStatusReceiver;
    public static Context getContextApp() {
        return ourInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        setContextApp(this);
        ourInstance = getApplicationContext();
        setUninstall();
        startHeartBeatAlarm();
        //初始化监听广播
        getFilter();
        // getBdLocationUtil();

    }
    private void getFilter(){
        //注册监听函数
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.SCREEN_ON");
        intentFilter3.addAction("android.intent.action.SCREEN_OFF");
        mScreenStatusReceiver = new ScreenStatusReceiver();
        registerReceiver(mScreenStatusReceiver, intentFilter3);
    }
    private void setContextApp(Application application) {
        contextApp = application;
    }
    public static BdLocationVo getBdLocationUtil() {
        bdLocationUtil = BdLocationUtil.getInstance(getContextApp());
        return bdLocationUtil.getLocation();
    }
}
