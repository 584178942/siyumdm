package com.siyu.mdm.custom.device.util;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;

import com.google.gson.Gson;
import com.siyu.mdm.custom.device.SGTApplication;
import com.vivo.customized.support.DriverImpl;
import com.vivo.customized.support.inter.VivoApplicationControl;
import com.vivo.customized.support.inter.VivoDeviceInfoControl;
import com.vivo.customized.support.inter.VivoOperationControl;
import com.vivo.customized.support.inter.VivoTelecomControl;
import com.vivo.customized.support.utils.CustPackageDeleteObserver;
import com.vivo.customized.support.utils.CustPackageInstallObserver;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.siyu.mdm.custom.device.util.AppConstants.UNINSTALL_PATTERN;

/**
 * @author ZT
 * @date 20201027
 */
public class MdmUtil {
    private static String[] columns = {CallLog.Calls.CACHED_NAME// 通话记录的联系人
            , CallLog.Calls.NUMBER// 通话记录的电话号码
            , CallLog.Calls.DATE// 通话记录的日期
            , CallLog.Calls.DURATION// 通话时长
            , CallLog.Calls.TYPE};// 通话类型}
    private static final String TAG = "MdmUtil";
    private static DriverImpl driverImpi = new DriverImpl();
    private static VivoOperationControl vivoOperationControl = driverImpi.getOperationManager();
    private static VivoDeviceInfoControl vivoDeviceInfoControl = driverImpi.getDeviceInfoManager();
    private static VivoApplicationControl vivoApplicationControl = driverImpi.getApplicationManager();
    private static VivoTelecomControl vivoTelecomControl = driverImpi.getTelecomManager();

    /**
     * 恢复出厂设置
     */
    public static void clearData() {
        try {
            Class vcs = Class.forName("com.vivo.services.cust.VivoCustomManager");
            Method clearData = vcs.getDeclaredMethod("clearData");
            clearData.invoke(vcs.newInstance());}
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取通话时长
     */
    public static void getCallLog() {
        Cursor cursor = SGTApplication.getContextApp().getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                "call_time"
        }, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor.moveToFirst()) {
            String call_time =cursor.getString(2);
            LogUtils.info("callTime", call_time);
        }
    }
    /**
     * 获取通话时长
     */
    public static String getCallLog2() {
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map;
        Cursor cursor = SGTApplication.getContextApp().getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                columns, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));  //姓名
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));  //号码
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)); //获取通话日期
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong));
            String time = new SimpleDateFormat("HH:mm").format(new Date(dateLong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));//获取通话时长，值为多少秒
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)); //获取通话类型：1.呼入2.呼出3.未接
            String dayCurrent = new SimpleDateFormat("dd").format(new Date());
            String dayRecord = new SimpleDateFormat("dd").format(new Date(dateLong));
            map.put("number",number);
            map.put("duration",duration);
            map.put("date",date);
            map.put("type",type);
            list.add(map);
            LogUtils.info(TAG,"Call log: name: " + name +"phone number: " + number  + "duration " +duration + "date:" + date + "type:" + type);
        }
        LogUtils.info(TAG,new Gson().toJson(list,List.class));
        return new Gson().toJson(list,List.class);
    }

    /**
     * 机卡绑定
     */
    public static void bindPhone(){
        int i = 0;
        vivoTelecomControl.setTelephonyPhoneState(i,i,i);
        vivoTelecomControl.setTelephonySmsState(i,i,i);
         TaskUtil.startBindActivity();
    }

    /**
     * 解除机卡绑定
     */
    public static void unBindPhone(){
        int i = 0;
        vivoTelecomControl.setTelephonyPhoneState(i,1,1);
        vivoTelecomControl.setTelephonySmsState(i,1,1);
        // vivoTelecomControl.setTelephonySlotState(0);
         TaskUtil.closeBindActivity();
         // TaskUtil.startLockActivity();
    }


    /**
     * 锁机
     */
    public static void lockPhone(){
        int i = 0;
        vivoTelecomControl.setTelephonyPhoneState(i,1,i);
        vivoTelecomControl.setTelephonySmsState(i,i,i);
        vivoOperationControl.setBackKeyEventState(i);
        vivoOperationControl.setMenuKeyEventState(i);
        vivoOperationControl.setHomeKeyEventState(i);
        vivoOperationControl.setStatusBarState(i);

        TaskUtil.startLockActivity();
    }
    /**
     * 锁机
     */
    /**
     * 机卡绑定
     */
    public static void unLockPhone(){
        int i = 1;
        vivoTelecomControl.setTelephonyPhoneState(0,i,i);
        vivoTelecomControl.setTelephonySmsState(0,i,i);
        // vivoTelecomControl.setTelephonySlotState(0);
        vivoOperationControl.setBackKeyEventState(i);
        vivoOperationControl.setMenuKeyEventState(i);
        vivoOperationControl.setHomeKeyEventState(i);
        vivoOperationControl.setStatusBarState(i);

        TaskUtil.closeLockActivity();
    }

    /**
     * 添加安装应用白名单
     */
    public static void addInstallWhiteList(List<String> pkgList){
        vivoApplicationControl.setInstallPattern(2);
        List<String> whiteList = new ArrayList<>();
        whiteList.add(SGTApplication.getContextApp().getPackageName());
        if (pkgList.size() >= 1) {
            for (String pkgName : pkgList) {
                LogUtils.info("getPkgName",pkgName);
                whiteList.add(pkgName);
            }
        } else {
            whiteList.add("com.baidu.searchbox.lite");
            whiteList.add("com.ss.android.article.lite");
            whiteList.add("com.ss.android.article.news");
            whiteList.add("com.ss.android.ugc.aweme");
            whiteList.add("us.zoom.videomeetings");
            whiteList.add("com.tencent.wework");
        }
        vivoApplicationControl.addInstallWhiteList(whiteList);
        LogUtils.info("com.siyu.mdm.custom.device",vivoApplicationControl.getInstallWhiteList().toString());
    }

    /**
     * 移除安装应用白名单
     */
    public static void clearInstallWhiteList(){
        vivoApplicationControl.setInstallPattern(0);
        vivoApplicationControl.clearInstallWhiteList();
        LogUtils.info("com.siyu.mdm.custom.device",vivoApplicationControl.getInstallWhiteList().toString());
    }
    //判断文件是否存在
    public static boolean fileIsExists(String filePath) {
        try {
            File f = new File(filePath);
            if(!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取IccId
     */
    public static List<String> getPhoneIccids(){
        return vivoDeviceInfoControl.getPhoneIccids();
    }
    /**
     * 获取imei
     */
    public static String getPhoneImeis(){
        return vivoDeviceInfoControl.getPhoneImeis().get(0);
    }
    /**
     * 静默安装
     */
    public static void installPackage(String path,String pkg){
        vivoApplicationControl.installPackageWithObserver(path,2,pkg,new CustPackageInstallObserver(){
            @Override
            public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) {
                super.onPackageInstalled(basePackageName, returnCode, msg, extras);
                LogUtils.info("",basePackageName + returnCode +msg);
            }
        });
    }

    /**
     * 禁止卸载
     */
    public static void setUninstall(){
        if (vivoApplicationControl.getUninstallPattern() != UNINSTALL_PATTERN){
            vivoApplicationControl.setUninstallPattern(UNINSTALL_PATTERN);
        }
        LogUtils.info("setUninstall",vivoApplicationControl.getUninstallPattern() + "");
        List<String> pakList = new ArrayList<>();
        pakList.add(SGTApplication.getContextApp().getPackageName());
        vivoApplicationControl.addUninstallBlackList(pakList);
        LogUtils.info(TAG,vivoApplicationControl.getUninstallPattern() + "addUninstallBlackList" + vivoApplicationControl.getUninstallBlackList());
    }
    /**
     * 静默卸载
     */
    public static void deletePackage(String pkgName){
        LogUtils.info("deletePackage",pkgName);
        vivoApplicationControl.deletePackageWithObserver(pkgName,2,new CustPackageDeleteObserver(){
            @Override
            public void onPackageDeleted(String basePackageName, int returnCode, String msg) {
                super.onPackageDeleted(basePackageName, returnCode, msg);
                LogUtils.info("onPackageDeleted",basePackageName+returnCode+msg);
            }
        });
    }
}

