package com.siyu.mdm.custom.device.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siyu.mdm.custom.device.SGTApplication;
import com.siyu.mdm.custom.device.bean.UpdateBean;
import com.siyu.mdm.custom.device.util.LogUtils;
import com.siyu.mdm.custom.device.util.MdmUtil;
import com.siyu.mdm.custom.device.bean.InstallBean;
import com.siyu.mdm.custom.device.bean.RemoveBean;
import com.siyu.mdm.custom.device.bean.SwichImg;
import com.siyu.mdm.custom.device.bean.TypeBean;
import com.siyu.mdm.custom.device.bean.White;
import com.siyu.mdm.custom.device.util.NetUtils;
import com.siyu.mdm.custom.device.util.StorageUtil;
import com.siyu.mdm.custom.device.util.TaskUtil;
import com.siyu.mdm.custom.device.util.UpdateUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Response;

import static com.siyu.mdm.custom.device.util.AppConstants.ADD_WHITE;
import static com.siyu.mdm.custom.device.util.AppConstants.BIND;
import static com.siyu.mdm.custom.device.util.AppConstants.CALL_RECORDS;
import static com.siyu.mdm.custom.device.util.AppConstants.CLEAR;
import static com.siyu.mdm.custom.device.util.AppConstants.INSTALL;
import static com.siyu.mdm.custom.device.util.AppConstants.IS_BIND;
import static com.siyu.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.siyu.mdm.custom.device.util.AppConstants.LOCK;
import static com.siyu.mdm.custom.device.util.AppConstants.REMOVE;
import static com.siyu.mdm.custom.device.util.AppConstants.REMOVE_WHITE;
import static com.siyu.mdm.custom.device.util.AppConstants.SWITCH_IMG;
import static com.siyu.mdm.custom.device.util.AppConstants.UN_BIND;
import static com.siyu.mdm.custom.device.util.AppConstants.UN_LOCK;
import static com.siyu.mdm.custom.device.util.AppConstants.UPDATE;
import static com.siyu.mdm.custom.device.util.AppConstants.HEART_BEAT;
import static com.siyu.mdm.custom.device.util.TaskUtil.cancelPollAlarmReceiver;
import static com.siyu.mdm.custom.device.util.TaskUtil.startHeartBeatAlarm;

/**
 * @author ZT
 * @date 20201105
 */
public class HeartBeatReceiver extends BootBroadcastReceiver{
    private static final String TAG = "HeartBeatReceiver";

    /**
     * 闹钟时间间隔
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            startHeartBeatAlarm();
            if (TextUtils.isEmpty(MdmUtil.getPhoneIccids().get(0)) && IS_BIND){
                LogUtils.info(TAG,"ICCID =  NULL");
                MdmUtil.bindPhone();
                return;
            }
            loadData();
        } catch (Exception e) {
            LogUtils.info("e", e.getLocalizedMessage());
        }

    }

    public void loadData() {
        Map paramMap = new HashMap();
        paramMap.put("imeiCode",MdmUtil.getPhoneImeis());
        paramMap.put("iccId",MdmUtil.getPhoneIccids());
        paramMap.put("version",UpdateUtils.getVerName());
//      paramMap.put("callRecords",MdmUtil.getCallLog());

        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(NetUtils.appUrl + HEART_BEAT, paramMap, new NetUtils.MyNetCall() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
//      paramMap.put("latitude", SGTApplication.getBdLocationUtil().getLatitude());
//      paramMap.put("lontitude", SGTApplication.getBdLocationUtil().getLontitude());
            public void success(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (TextUtils.isEmpty(result)){
                    return;
                }
                processingData(result);
            }
            @Override
            public void failed(Call call, IOException e) {
                LogUtils.info("failed", e.getLocalizedMessage());
            }
        });
    }

    public void processingData(String result){
        // cancelPollAlarmReceiver();
        LogUtils.info("result", result);
        try {
            List<TypeBean> typeBean = new Gson().fromJson(result, new TypeToken<List<TypeBean>>() {}.getType());
            for (TypeBean bean : typeBean) {
                switch (bean.getType()) {
                    case BIND:
                        MdmUtil.bindPhone();
                        break;
                    case UN_BIND:
                        MdmUtil.unBindPhone();
                        break;
                    case LOCK:
                        StorageUtil.put(IS_LOCK,bean.getType());
                        MdmUtil.lockPhone();
                        break;
                    case UN_LOCK:
                        // track();
                        StorageUtil.put(IS_LOCK,bean.getType());
                        MdmUtil.unLockPhone();
                        break;
                    case CLEAR:
                        MdmUtil.clearData();
                        break;
                    case INSTALL:
                        installApp(bean.getData());
                        break;
                    case REMOVE:
                        RemoveBean removeBean = new Gson().fromJson(bean.getData(), RemoveBean.class);
                        if (!TextUtils.isEmpty(removeBean.getPkgName())) {
                            LogUtils.info(REMOVE,bean.getData());
                            String pkgName = removeBean.getPkgName();
                            // MdmUtil.deletePackageWithObserver(pkgName);

                            MdmUtil.deletePackage(pkgName);
                        }
                        break;
                    case SWITCH_IMG:
                        SwichImg swichImg = new Gson().fromJson(bean.getData(), SwichImg.class);
                        if (!TextUtils.isEmpty(swichImg.getImageUrl())){
                            String url = swichImg.getImageUrl();
                            TaskUtil.changeDownloadImage(url);
                        }
                        break;
                    case ADD_WHITE:
                        if (!"".equals(bean.getData())){
                            White white = new Gson().fromJson(bean.getData(), White.class);
                            List<String> whiteList = Arrays.asList(white.getPkgNames().split(","));
                            MdmUtil.addInstallWhiteList(whiteList);
                        }
                        break;
                    case REMOVE_WHITE:
                        MdmUtil.clearInstallWhiteList();
                        break;
                    case UPDATE:
                       updateApp(bean.getData());
                        break;
                    case CALL_RECORDS:
                        callRecords();
                        break;
                    default:
                        LogUtils.info("default",result);
                        break;
                }
            }
        } catch (Exception e) {
            LogUtils.info("IllegalStateException",e.getLocalizedMessage());
        }
    }

    /**
     * 安装apk
     * @param installStr 下载String
     */
    private void installApp(String installStr){
        InstallBean installBean = new Gson().fromJson(installStr, InstallBean.class);
        if (!TextUtils.isEmpty(installBean.getApkUrl())|| !TextUtils.isEmpty(installBean.getPkgName())) {
            String pkg2 = installBean.getPkgName();
            String url2 = installBean.getApkUrl();
            LogUtils.info(TAG,"getApkUrl" + installBean.getApkUrl() + "getPkgName" + installBean.getPkgName());
            UpdateUtils.processInstall(url2,pkg2);
        }
    }

    /**
     * 更新apk
     * @param updateStr 更新String
     */
    private void updateApp(String updateStr){
        UpdateBean updateBean = new Gson().fromJson(updateStr, UpdateBean.class);
        if (!TextUtils.isEmpty(updateBean.getApkUrl())|| !TextUtils.isEmpty(updateBean.getPkgName())) {
            String pkg2 = updateBean.getPkgName();
            String url2 = updateBean.getApkUrl();
            String version = updateBean.getVersion();
            LogUtils.info(TAG,"getApkUrl" + updateBean.getApkUrl() + "getPkgName" + updateBean.getPkgName());
            if (UpdateUtils.shouldUpdate(version)){
                UpdateUtils.processInstall(url2,pkg2);
            }
        }
    }
    /**
     * 通话记录
     */
    private void callRecords(){
        Map paramMap = new HashMap();
        paramMap.put("imeiCode",MdmUtil.getPhoneImeis());
        paramMap.put("iccId",MdmUtil.getPhoneIccids());
        paramMap.put("version",UpdateUtils.getVerName());
        paramMap.put("callRecords",MdmUtil.getCallLog());

        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(NetUtils.appUrl + CALL_RECORDS, paramMap, new NetUtils.MyNetCall() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void success(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogUtils.info("CALL_RECORDS",result);
            }
            @Override
            public void failed(Call call, IOException e) {
                LogUtils.info("failed", e.getLocalizedMessage());
            }
        });
    }

    /**
     * 通话记录
     */
    private void track(){
        Map paramMap = new HashMap();
        paramMap.put("imeiCode",MdmUtil.getPhoneImeis());
        paramMap.put("iccId",MdmUtil.getPhoneIccids().get(1));
        paramMap.put("version",UpdateUtils.getVerName());
        paramMap.put("latitude", SGTApplication.getBdLocationUtil().getLatitude());
        paramMap.put("longitude", SGTApplication.getBdLocationUtil().getLontitude());

        NetUtils netUtils = NetUtils.getInstance();
        String appUrl = "http://192.168.2.144:9999/api/track";
        netUtils.postDataAsynToNet(appUrl, paramMap, new NetUtils.MyNetCall() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void success(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogUtils.info("CALL_RECORDS",result);
            }
            @Override
            public void failed(Call call, IOException e) {
                LogUtils.info("failed", e.getLocalizedMessage());
            }
        });
    }
}
