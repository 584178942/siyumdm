package com.siyu.mdm.custom.device.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.siyu.mdm.custom.device.R;
import com.siyu.mdm.custom.device.util.LogUtils;

/**
 * @author Z T
 * @data 20201015
 */
public class BindActivity extends Activity {
    private static final String TAG = "BindActivity";
    public static BindActivity bindActivity;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
    private String lockMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_lock);
        lockMsg = "终端强制机卡绑定，如需解锁请与管理员联系";// StorageUtil.get(LOCK_MSG,DEFAULT_LOCK_MSG).toString();
        TextView lockTV = findViewById(R.id.lock_TV);
        LogUtils.info(TAG,lockMsg);
        lockTV.setText(lockMsg);
        bindActivity = this;
    }
    public static BindActivity getBindActivity() {
        return bindActivity;
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
    public void onAttachedToWindow() {
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        super.onAttachedToWindow();
    }

}