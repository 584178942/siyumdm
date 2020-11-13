package com.siyu.mdm.custom.device.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.siyu.mdm.custom.device.R;
import com.siyu.mdm.custom.device.util.LogUtils;

import static com.siyu.mdm.custom.device.util.TaskUtil.isTopActivity;
import static com.siyu.mdm.custom.device.util.TaskUtil.startLockReceiver;

/**
 * @author Z T
 * @data 20201015
 */
public class LockActivity extends Activity {
    private static final String TAG = "LockActivity";
    public static LockActivity lockActivity;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private String lockMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_lock);
        lockMsg = "锁机页面";// StorageUtil.get(LOCK_MSG,DEFAULT_LOCK_MSG).toString();
        TextView lockTV = findViewById(R.id.lock_TV);
        LogUtils.info(TAG,lockMsg);
        lockTV.setText(lockMsg);
        lockActivity = this;
    }
    public static LockActivity getLockActivity() {
        return lockActivity;
    }
    /**
     * 屏蔽返回键的代码:
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_CALL:
            case KeyEvent.KEYCODE_SYM:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_STAR:
                return true;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.info(TAG,isTopActivity() + "");
        startLockReceiver();
    }

    @Override
    public void onAttachedToWindow() {
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        super.onAttachedToWindow();
    }

}