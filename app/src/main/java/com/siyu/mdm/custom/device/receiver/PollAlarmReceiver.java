package com.siyu.mdm.custom.device.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.siyu.mdm.custom.device.bean.Bean;
import com.siyu.mdm.custom.device.util.LogUtils;
import com.siyu.mdm.custom.device.util.MdmUtil;
import com.siyu.mdm.custom.device.util.NetUtils;
import com.siyu.mdm.custom.device.util.TaskUtil;
import com.siyu.mdm.custom.device.util.UpdateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Response;

import static com.siyu.mdm.custom.device.util.AppConstants.IS_BIND;

/**
 * Created by 心跳广播
 * @author Z T
 * @date 20200924
 */
public class PollAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "PollAlarmReceiver";
    /**
     * 闹钟时间间隔
     */
    @Override
    public void onReceive(Context context, Intent intent) {
             //startAlarm();
            if (TextUtils.isEmpty(MdmUtil.getPhoneIccids().get(0))&& IS_BIND){
                LogUtils.info(TAG,"ICCID =  NULL");
                MdmUtil.bindPhone();
                return;
            }
            Map paramMap = new HashMap();
            paramMap.put("ImeiCode",MdmUtil.getPhoneImeis());
            paramMap.put("IccId",MdmUtil.getPhoneIccids().get(0));
            paramMap.put("phoneLog",MdmUtil.getCallLog());

            passiveReceiveBus(paramMap, "getData");
        }

    public void passiveReceiveBus(Map params, String url) {
        NetUtils netUtils = NetUtils.getInstance();
        netUtils.postDataAsynToNet(NetUtils.appUrl + url, params, new NetUtils.MyNetCall() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void success(Call call, Response response) throws IOException {
                String result = response.body().string();
                LogUtils.info(TAG,result);
                if (TextUtils.isEmpty(result)){
                    return;
                }
                try {
                    Bean bean = new Gson().fromJson(result,Bean.class);
                    switch (bean.getType()) {
                        case 1:
                            MdmUtil.clearInstallWhiteList();
                            break;
                        case 2:
                            MdmUtil.addInstallWhiteList(new ArrayList<String>());
                            break;
                        case 3:
                            MdmUtil.bindPhone();
                            break;
                        case 4:
                            MdmUtil.unBindPhone();
                            break;
                        case 5:
                            MdmUtil.clearData();
                            break;
                        case 6:
                            if (!TextUtils.isEmpty(bean.getApkUrl())|| !TextUtils.isEmpty(bean.getPkgName())) {
                                String pkg2 = bean.getPkgName();
                                String url2 = bean.getApkUrl();
                                LogUtils.info(TAG,"getApkUrl"+bean.getApkUrl() + "getPkgName" + bean.getPkgName());
                                UpdateUtils.processInstall(url2,pkg2);
                            }
                            break;
                        case 7:
                            LogUtils.info(TAG,"getApkUrl"+TextUtils.isEmpty(bean.getApkUrl()) + "getPkgName" + !TextUtils.isEmpty(bean.getPkgName()));
                            if (!TextUtils.isEmpty(bean.getPkgName())) {
                                String pkgName = bean.getPkgName();
                                MdmUtil.deletePackageWithObserver(pkgName);
                            }
                            break;
                        case 8:
                            if (!TextUtils.isEmpty(bean.getImageUrl())){
                                String url = bean.getImageUrl();
                                TaskUtil.changeDownloadImage(url);
                            }
                            LogUtils.info(TAG,"bean.getImageUrl() == null");
                            break;
                        case 9:
                            LogUtils.info(TAG,bean.getApkUrl() + ":" + bean.getPkgName() );
                            if (!TextUtils.isEmpty(bean.getApkUrl())|| !TextUtils.isEmpty(bean.getPkgName())){
                                String pkg2 = bean.getPkgName();
                                String url2 = bean.getApkUrl();
                                UpdateUtils.processInstall(url2,pkg2);
                            }
                            LogUtils.info(TAG,"bean.getApkUrl() || bean.getPkgName() == null");
                            break;
                        default:
                            LogUtils.info(TAG," " + bean.getType());
                            break;
                    }
                } catch (Exception e) {
                    LogUtils.info(TAG,e.getLocalizedMessage());
                }
            }
            @Override
            public void failed(Call call, IOException e) {
                LogUtils.info(TAG, "failed" + e.getLocalizedMessage());
            }
        });
    }
}

