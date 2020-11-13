package com.siyu.mdm.custom.device.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siyu.mdm.custom.device.util.LogUtils;
import com.siyu.mdm.custom.device.util.MdmUtil;
import com.siyu.mdm.custom.device.bean.InstallBean;
import com.siyu.mdm.custom.device.bean.RemoveBean;
import com.siyu.mdm.custom.device.bean.SwichImg;
import com.siyu.mdm.custom.device.bean.TypeBean;
import com.siyu.mdm.custom.device.bean.White;
import com.siyu.mdm.custom.device.util.NetUtils;
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
import static com.siyu.mdm.custom.device.util.AppConstants.CLEAR;
import static com.siyu.mdm.custom.device.util.AppConstants.INSTALL;
import static com.siyu.mdm.custom.device.util.AppConstants.IS_LOCK;
import static com.siyu.mdm.custom.device.util.AppConstants.LOCK;
import static com.siyu.mdm.custom.device.util.AppConstants.REMOVE;
import static com.siyu.mdm.custom.device.util.AppConstants.REMOVE_WHITE;
import static com.siyu.mdm.custom.device.util.AppConstants.SWITCH_IMG;
import static com.siyu.mdm.custom.device.util.AppConstants.UN_BIND;
import static com.siyu.mdm.custom.device.util.AppConstants.UN_LOCK;
import static com.siyu.mdm.custom.device.util.AppConstants.UPDATE;
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
        startHeartBeatAlarm();
        if (TextUtils.isEmpty(MdmUtil.getPhoneIccids().get(0))&& IS_LOCK){
            LogUtils.info(TAG,"ICCID =  NULL");
            MdmUtil.bindPhone();
            return;
        }
        loadData();
    }

    public void loadData() {
        Map paramMap = new HashMap();
        paramMap.put("imeiCode",MdmUtil.getPhoneImeis());
        paramMap.put("iccId",MdmUtil.getPhoneIccids());
        paramMap.put("version",UpdateUtils.getVerName());
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(NetUtils.appUrl + "heartbeat", paramMap, new NetUtils.MyNetCall() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
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
                        MdmUtil.bindPhone();
                        break;
                    case UN_LOCK:
                        MdmUtil.unBindPhone();
                        break;
                    case CLEAR:
                        MdmUtil.clearData();
                        break;
                    case INSTALL:
                        InstallBean installBean = new Gson().fromJson(bean.getData(), InstallBean.class);
                        if (!TextUtils.isEmpty(installBean.getApkUrl())|| !TextUtils.isEmpty(installBean.getPkgName())) {
                            String pkg2 = installBean.getPkgName();
                            String url2 = installBean.getApkUrl();
                            LogUtils.info(TAG,"getApkUrl" + installBean.getApkUrl() + "getPkgName" + installBean.getPkgName());
                            UpdateUtils.processInstall(url2,pkg2);
                        }
                        break;
                    case REMOVE:
                        RemoveBean removeBean = new Gson().fromJson(bean.getData(), RemoveBean.class);
                        if (!TextUtils.isEmpty(removeBean.getPkgName())) {
                            LogUtils.info(REMOVE,bean.getData());
                            String pkgName = removeBean.getPkgName();
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
                        InstallBean updateBean = new Gson().fromJson(bean.getData(), InstallBean.class);
                        if (!TextUtils.isEmpty(updateBean.getApkUrl())|| !TextUtils.isEmpty(updateBean.getPkgName())) {
                            String pkg2 = updateBean.getPkgName();
                            String url2 = updateBean.getApkUrl();
                            LogUtils.info(TAG,"getApkUrl" + updateBean.getApkUrl() + "getPkgName" + updateBean.getPkgName());
                            UpdateUtils.processInstall(url2,pkg2);
                        }
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
}
