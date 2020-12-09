package com.siyu.mdm.custom.device.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.siyu.mdm.custom.device.SGTApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Response;
import static com.siyu.mdm.custom.device.SGTApplication.contextApp;

public class UpdateUtils {
    private final static String TAG = "UpdateUtils";
    public static String getLocalFile(String pkg) {
        File file;
        // LogUtils.info(TAG, "build.version.release " + Build.VERSION.RELEASE);
       /* if (Integer.parseInt(Build.VERSION.RELEASE) < 21) {
            file = SGTApplication.getContextApp().getApplicationContext().getExternalFilesDir("download");
        } else {
            file = SGTApplication.getContextApp().getApplicationContext().getCacheDir();
        }*/
       // LogUtils.info("file：",file.getPath());
        file = new File("/sdcard/Android/data/sy");// SGTApplication.getContextApp().getApplicationContext().getCacheDir();//new File("/sdcard/Android/data/sy");
        if (file == null || file.exists() || file.mkdirs()) {
            File file2 = new File(file, pkg + ".apk");
            try {
                String canonicalPath = file2.getCanonicalPath();
                if (file2.exists()) {
                    boolean delete = file2.delete();
                    LogUtils.info(TAG, "downLoadFile getLocalFile old file exists, delete it " + delete);
                }
                return canonicalPath;
            } catch (IOException unused) {
                LogUtils.info(TAG, "downLoadFile getCanonicalPath failed IOException");
                return "";
            }
        } else {
            // AppConstants.setBootInstallFlag(true);
            LogUtils.info(TAG, "downLoadFile mkdirs caught permission");
            return "";
        }
    }
    /**
     * 下载apk
     * updateBean 服务器更新数据
     * */
    public static void processInstall(final String httpUrl,final String pkg) {
        final String localFile = getLocalFile(pkg);

        if (TextUtils.isEmpty(localFile)) {
            LogUtils.info(TAG, "processInstall create updateFilePath failed");
        } else {
            NetUtils.getInstance().downloadFile(httpUrl, localFile, new NetUtils.MyNetCall() {
                @Override
                public void success(Call call, Response response) {
                    File file = new File(localFile);

                    InputStream inputStream = null;
                    FileOutputStream outputStream = null;
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(httpUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(10 * 1000);
                        connection.setReadTimeout(10 * 1000);
                        connection.connect();
                        inputStream = connection.getInputStream();
                        outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (outputStream != null) {
                                outputStream.close();
                            }
                            if (connection != null) {
                                connection.disconnect();
                            }
                        } catch (IOException e) {
                            inputStream = null;
                            outputStream = null;
                        }
                    }
                    MdmUtil.installPackage(file.getPath(),pkg);
                }

                @Override
                public void failed(Call call, IOException iOException) {
                    LogUtils.info(UpdateUtils.TAG, "downloadFile  IOException");
                }
            });
        }
    }

    private static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length);
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString.toUpperCase(Locale.ENGLISH));
        }
        return sb.toString();
    }

    /**
     * 下载至 Environment.getExternalStorageDirectory().getPath() + "/update.apk"
     *
     * @param httpUrl
     * @return
     */
    private static File downLoadFile(String httpUrl, String filePath) {

        File file = new File(filePath);
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();
            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                inputStream = null;
                outputStream = null;
            }
        }
        return file;
    }


    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @return
     */
    public static String getVerName() {
        String verName = "";
        try {
            verName = contextApp.getPackageManager().
                    getPackageInfo(contextApp.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public static boolean shouldUpdate(String str) {
        LogUtils.info(TAG, "version code from server = " + str);
        String[] split = str.split("\\.");
        String[] split2 = getVerName().split("\\.");
        if (split.length == 3 && split2.length == 3) {
            try {
                if (Integer.parseInt(split[0]) != Integer.parseInt(split2[0])) {
                    LogUtils.info(TAG, "version code first index not match, should not cross upgrade");
                    return false;
                } else if (Integer.parseInt(split[1]) != Integer.parseInt(split2[1])) {
                    LogUtils.info(TAG, "version code second index not match, should not cross upgrade");
                    return false;
                } else {
                    if (Integer.parseInt(split[2]) > Integer.parseInt(split2[2])) {
                        LogUtils.info(TAG, "server version is larger than local, should upgrade");
                        return true;
                    }
                    return false;
                }
            } catch (NumberFormatException unused) {
                LogUtils.info(TAG, "version code NumberFormatException");
            }
        } else {
            LogUtils.info(TAG, "version code length not right");
            return false;
        }
        return false;
    }
}



