package com.siyu.mdm.custom.device.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.siyu.mdm.custom.device.activity.BindActivity;
import com.siyu.mdm.custom.device.activity.LockActivity;
import com.siyu.mdm.custom.device.activity.MainActivity;
import com.siyu.mdm.custom.device.receiver.HeartBeatReceiver;
import com.siyu.mdm.custom.device.receiver.PollAlarmReceiver;
import com.siyu.mdm.custom.device.SGTApplication;
import com.siyu.mdm.custom.device.receiver.StartLockReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.content.ContentValues.TAG;
import static android.content.Context.ACTIVITY_SERVICE;
import static com.siyu.mdm.custom.device.activity.BindActivity.getBindActivity;
import static com.siyu.mdm.custom.device.SGTApplication.contextApp;
import static com.siyu.mdm.custom.device.activity.LockActivity.getLockActivity;
import static com.siyu.mdm.custom.device.util.AppConstants.FIFTH_SECOND;
import static com.siyu.mdm.custom.device.util.AppConstants.SPACE_SECOND;
import static com.siyu.mdm.custom.device.util.AppConstants.TWO_SECOND;

/**
 * @author ZT
 * @data 20200924
 */
public class TaskUtil {
    private static ExecutorService singleTaskExecutor = Executors.newSingleThreadExecutor();

    /**
     * 启动绑定页面
     */
    public static void startBindActivity() {
        try {
            Intent intent = new Intent(contextApp.getApplicationContext(), BindActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contextApp.getApplicationContext().startActivity(intent);
        } catch (SecurityException e) {
            LogUtils.info(TAG, e.getMessage());
        } catch (ActivityNotFoundException unused) {
            LogUtils.info(TAG, "startBindActivity ActivityNotFoundException Error");
        }
    }

    /**
     * 启动锁机页面
     */
    public static void startLockActivity() {
        try {
            Intent intent = new Intent(contextApp.getApplicationContext(), LockActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contextApp.getApplicationContext().startActivity(intent);
        } catch (SecurityException e) {
            LogUtils.info(TAG, e.getLocalizedMessage());
        } catch (ActivityNotFoundException unused) {
            LogUtils.info(TAG, "startLockActivity ActivityNotFoundException Error");
        }
    }
    /**
     *  关闭机卡绑定页面
      */
    public static void closeBindActivity() {
        if (getBindActivity() != null) {
            getBindActivity().finish();
        }
    }

    /**
     * 关闭Lock页面
     */
    public static void closeLockActivity(){
        if (getLockActivity() != null){
            getLockActivity().finish();
        }
    }
    /**
     * 下载图片
     * @param url
     * @return byte[]
     * @throws IOException
     */
    public static void changeDownloadImage(String url) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        // 更换桌面背景
        WallpaperManager.getInstance(SGTApplication.contextApp).setBitmap(Bytes2Bimap(bytes));
    }

    /**
     * byte[] 转 Bitmap
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 保存位图到本地
     * @param bitmap
     * @param path 本地路径
     * @return void
     */
    public void SavaImage(Bitmap bitmap, String path){
        File file=new File(path);
        FileOutputStream fileOutputStream=null;
        //文件夹不存在，则创建它
        if(!file.exists()){
            file.mkdir();
        }
        try {
            fileOutputStream=new FileOutputStream(path+"/"+System.currentTimeMillis()+".png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public static void startAlarm() {
        Intent intent = new Intent();
        Context applicationContext = contextApp.getApplicationContext();
        intent.setComponent(new ComponentName(applicationContext.getPackageName(), PollAlarmReceiver.class.getName()));
        PendingIntent broadcast = PendingIntent.getBroadcast(applicationContext, 1, intent, 268435456);
        long countAlarmMillis = SPACE_SECOND;
        LogUtils.info(TAG, "startAlarm intervalMillis = " + countAlarmMillis);
        @SuppressLint("WrongConstant") AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(2, SystemClock.elapsedRealtime() + countAlarmMillis, broadcast);
        }
    }

    public static void startHeartBeatAlarm() {
        Intent intent = new Intent();
        Context applicationContext = contextApp.getApplicationContext();
        intent.setComponent(new ComponentName(applicationContext.getPackageName(), HeartBeatReceiver.class.getName()));
        PendingIntent broadcast = PendingIntent.getBroadcast(applicationContext, 999, intent, FLAG_CANCEL_CURRENT);
        long countAlarmMillis = TWO_SECOND;
        LogUtils.info(TAG, "startAlarm intervalMillis = " + countAlarmMillis);
         @SuppressLint("WrongConstant") AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + countAlarmMillis, broadcast);
        }
    }

    /**
     * 启动LockActivity
     */
    public static void startLockReceiver(){
        Intent intent = new Intent();
        Context applicationContext = contextApp.getApplicationContext();
        intent.setComponent(new ComponentName(applicationContext.getPackageName(), StartLockReceiver.class.getName()));
        @SuppressLint("WrongConstant") PendingIntent broadcast = PendingIntent.getBroadcast(applicationContext, 888, intent, 268435456);
        long countAlarmMillis = FIFTH_SECOND;
        LogUtils.info(TAG, "startAlarm intervalMillis = " + countAlarmMillis);
        @SuppressLint("WrongConstant") AlarmManager alarmManager = (AlarmManager) applicationContext.getSystemService(NotificationCompat.CATEGORY_ALARM);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + countAlarmMillis, broadcast);
        }
    }

    /**
     * 当前页面是否在最上层
     * @return
     */
    public static boolean isTopActivity() {
        boolean isTop = false;
        ActivityManager am = (ActivityManager)SGTApplication.getContextApp().getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        LogUtils.info(TAG, "isTopActivity = " + cn.getClassName());
        if (cn.getClassName().contains("com.siyu.mdm.custom.device.activity.LockActivity")) {
            isTop = true;
        }
        LogUtils.info(TAG, "isTop = " + isTop);
        return isTop;
    }

   /* public static void checkTopActivity() {
        if (((Boolean) SpUtil.get(AppConstants.FILE_NAME, AppConstants.IS_LOCK, false)).booleanValue()) {
            LogUtils.info(TAG, "LockActivity onPause called, execute checkTask");
            singleTaskExecutor.execute($$Lambda$TaskUtil$_ga1PijKhPfHW6D6o_SAu_dgrcw.INSTANCE);
        }
    }*/

}
