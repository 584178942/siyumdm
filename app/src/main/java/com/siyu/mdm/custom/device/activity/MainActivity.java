package com.siyu.mdm.custom.device.activity;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.siyu.mdm.custom.device.R;
import com.siyu.mdm.custom.device.SGTApplication;
import com.siyu.mdm.custom.device.util.LogUtils;
import com.siyu.mdm.custom.device.util.MdmUtil;
import com.siyu.mdm.custom.device.util.TaskUtil;
import com.vivo.customized.support.DriverImpl;
import com.vivo.customized.support.inter.VivoApplicationControl;
import com.vivo.customized.support.inter.VivoDeviceInfoControl;
import com.vivo.customized.support.inter.VivoOperationControl;
import com.vivo.customized.support.inter.VivoTelecomControl;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.siyu.mdm.custom.device.util.MdmUtil.setUninstall;
import static com.siyu.mdm.custom.device.util.TaskUtil.isTopActivity;

/**
 * @author Z T
 */
public class MainActivity extends AppCompatActivity {
    
    DriverImpl driverimpi;
    VivoOperationControl vivoOperationControl;
    VivoDeviceInfoControl vivoDeviceInfoControl;
    VivoApplicationControl vivoApplicationControl;
    VivoTelecomControl vivoTelecomControl;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions();
        setUninstall();

        isTopActivity();
        driverimpi = new DriverImpl();
        vivoOperationControl = driverimpi.getOperationManager();
        vivoDeviceInfoControl = driverimpi.getDeviceInfoManager();
        vivoApplicationControl = driverimpi.getApplicationManager();
        vivoTelecomControl = driverimpi.getTelecomManager();

        setContentView(R.layout.activity_main);
        List<String> whiteList = new ArrayList<>();
        whiteList.add("com.siyu.mdm.custom.device");
        vivoApplicationControl.addPersistApps(whiteList);
        //  PollAlarmReceiver.getAlarmManager(this);
        // 移除白名单
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdmUtil.clearInstallWhiteList();
            }
        });
        // 添加安装白名单
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MdmUtil.addInstallWhiteList();
            }
        });
        // 擦除数据
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdmUtil.bindPhone();
            }
        });
        // 机卡绑定
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdmUtil.unBindPhone();
            }
        });

        // 添加白名单
        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MdmUtil.clearData();
            }
        });
        // 远程安装应用
        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MdmUtil.installPackage();
                String pkg = SGTApplication.getContextApp().getPackageName();
                String path = "/sdcard/com.siyu.mdm.custom.device-1-1.0-20201106.apk";
                MdmUtil.installPackage(path,pkg);

               /* String path = "/sdcard/com.siyu.mdm.custom.device.test2-1-1.0-20201030.apk";
                String url = "http://download.eoemarket.com/app?id=914867&channel_id=426";
                UpdateUtils.processInstall(path,pkg);
                MdmUtil.installPackage(path,pkg);*/
            }
        });

        // 解除机卡绑定
        findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    WallpaperManager.getInstance(SGTApplication.contextApp).setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.lock, null));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //MdmUtil.deletePackage();
            }
        });
        // 下载图片
        findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String imgUrl = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2534506313,1688529724&fm=26&gp=0.jpg";
                        try {
                            TaskUtil.changeDownloadImage(imgUrl);
                        } catch (IOException e) {
                            LogUtils.info("",e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                }).start();
                //MdmUtil.deletePackage();
            }
        });

        // 获取通话时长
        findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPermissions();
            }
        });

        // 获取ccid  和  emei
        TextView text1 =  (TextView)findViewById(R.id.text1);
        text1.setText("IMEI : " + MdmUtil.getPhoneImeis() + "\nICCID :" + MdmUtil.getPhoneIccids());

        findViewById(R.id.btn10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskUtil.startLockActivity();
            }
        });
    }

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            //1. 检测是否添加权限   PERMISSION_GRANTED  表示已经授权并可以使用
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                //手机为Android6.0的版本,权限未授权去i请授权
                //2. 申请请求授权权限
                //1. Activity
                // 2. 申请的权限名称
                // 3. 申请权限的 请求码
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.READ_CALL_LOG//通话记录
                        }, 1005);
                MdmUtil. getCallLog2();
            } else {//手机为Android6.0的版本,权限已授权可以使用
                // 执行下一步
                MdmUtil. getCallLog2();
            }
        } else {//手机为Android6.0以前的版本，可以使用
            MdmUtil. getCallLog2();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.info(TAG,isTopActivity() + "");
        //   /startLockReceiver();
    }
}