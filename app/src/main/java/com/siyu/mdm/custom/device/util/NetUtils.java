package com.siyu.mdm.custom.device.util;

import com.google.gson.Gson;
import com.siyu.mdm.custom.device.R;
import com.siyu.mdm.custom.device.SGTApplication;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.siyu.mdm.custom.device.util.AppConstants.IS_TEST;

/**
 * @author Z T
 * @date 20200925
 */
public class NetUtils {

    private static final String TAG = "NetUtils";
    public static String appUrl = "";
    private static final byte[] LOCKER = new byte[0];
    private static NetUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private NetUtils() {
        if (IS_TEST){
            appUrl = SGTApplication.getContextApp().getString(R.string.api_urlTest);
        } else {
            appUrl = SGTApplication.getContextApp().getString(R.string.api_url);
        }
        OkHttpClient.Builder ClientBuilder = new OkHttpClient.Builder();
        ClientBuilder.readTimeout(20, TimeUnit.SECONDS);
        ClientBuilder.connectTimeout(6, TimeUnit.SECONDS);
        ClientBuilder.writeTimeout(60, TimeUnit.SECONDS);
        ClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mOkHttpClient = ClientBuilder.build();
    }

    /**
     * 单例模式获取NetUtils
     *
     * @return
     */
    public static NetUtils getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new NetUtils();
                }
            }
        }
        return mInstance;
    }
    /**
     * 自定义网络回调接口
     */
    public interface MyNetCall {
        void success(Call call, Response response) throws IOException;

        void failed(Call call, IOException e);
    }
    /**
     * post请求，异步方式，提交数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url
     * @param bodyParams
     * @param myNetCall
     */
    public void postDataAsynToNet(String url, Map<String, String> bodyParams, final MyNetCall myNetCall) {
        try {
            //1构造RequestBody
//            RequestBody body = setRequestBody(bodyParams);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(bodyParams));
            //为加密修改传入字符
            LogUtils.info(TAG,url + "?" + new Gson().toJson(bodyParams));
            //2 构造Request
            Request.Builder requestBuilder = new Request.Builder();
            Request request = requestBuilder.url(url).post(requestBody).build();
            Call call = mOkHttpClient.newCall(request);
            //4 执行Call
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    myNetCall.failed(call, e);
                    LogUtils.info(TAG,e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    myNetCall.success(call, response);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * post的请求参数，构造RequestBody
     *
     * @param BodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, String> BodyParams) {
        RequestBody body = null;
        okhttp3.FormBody.Builder formEncodingBuilder = new okhttp3.FormBody.Builder();
        if (BodyParams != null) {
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next();
                formEncodingBuilder.add(key, BodyParams.get(key));
                LogUtils.info("post http", "post_Params===" + key + "====" + BodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();

        return body;
    }
    public void downloadFile(String str, final String str2, final MyNetCall myNetCall) {
        this.mOkHttpClient.newCall(new Request.Builder().url(str).build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException iOException) {
                myNetCall.failed(call, iOException);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                myNetCall.success(call, response);
            }
        });
    }
}