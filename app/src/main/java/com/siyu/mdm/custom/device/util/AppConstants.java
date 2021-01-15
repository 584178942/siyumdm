 package com.siyu.mdm.custom.device.util;

/**
 * @author ZT
 * @data 2020/09/24
 */
public class AppConstants {
    /**
     * true 测试环境  false 正式环境
     */
    public static final Boolean IS_TEST = true;

    public static final int	FLAG_DATABL = 6;
    public static final int	FLAG_DATAWL = 7;
    public static final int	FLAG_FORBID = 1;
    public static final int	FLAG_INSTALLBL = 4;
    public static final int	FLAG_INSTALLWL = 5;
    public static final int	FLAG_PERSIST = 0;
    public static final int	FLAG_UNINSTALLBL = 2;
    public static final int	FLAG_UNINSTALLWL = 3;
    public static final int	FLAG_WIFIBL	= 8;
    public static final int	FLAG_WIFIWL = 9;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    public static final String IS_LOCK = "is_lock";
    /**
     * 机卡绑定开关
     */
    public static final boolean IS_BIND = false;
    /**
     * 锁机
     */
    public static final String LOCK = "lock";

    /**
     * 解锁
     */
    public static final String UN_LOCK = "unlock";

    /**
     * 格式化
     */
    public static final String CLEAR = "clear";

    /**
     * 安装
     */
    public static final String INSTALL = "install";

    /**
     * 卸载
     */
    public static final String REMOVE = "remove";

    /**
     * 锁机
     */
    public static final String SWITCH_IMG = "switchImg";

    /**
     * 添加白名单
     */
    public static final String ADD_WHITE = "addWhite";

    /**
     * 添加白名单
     */
    public static final String REMOVE_WHITE = "removeWhite";

    /**
     * 添加白名单
     */
    public static final String UPDATE = "update";

    /**
     * 添加白名单
     */
    public static final String BIND = "bind";

    /**
     * 添加白名单
     */
    public static final String UN_BIND = "unbind";

    /**
     * 通话记录
     */
    public static final String CALL_RECORDS = "callRecords";

    /**
     * 接口名 heartbeat 心跳
     */
    public static final String HEART_BEAT = "heartbeat";

    /**
     * 添加白名单
     */
    public static final int UNINSTALL_PATTERN = 1;

    /**
     * 锁屏文字
     */
    public static final String LOCK_MSG = "LOCK_MSG";

    /**
     * 存储文件name
     */
    public static final String COM_SGT_SECURITY = "com_sgt_security";

    /**
     * 默认锁屏文字
     */
    public static final String DEFAULT_LOCK_MSG = "终端强制锁定，如需解锁请与管理员联系";



    /**
     * 5秒
     */
    public static final int FIVE_SECOND = 5000;

    /**
     * 15秒
     */
    public static final int FIFTH_SECOND = 15000;

    /**
     * 20秒
     */
    public static final int TWO_SECOND = 20000;

    /**
     * 灭屏5分钟
     */
    public static final int SPACE_SECOND = 300000;

    public static final String CONTRACT_COMPLETED_ACTION = "CONTRACT_COMPLETED_ACTION";

    public static final String IMSICODE = "imsiCode";

    public static final String IMSICODE2 = "imsiCode2";

    public static final int TYPE_NONE = -1;

    public static final int TYPE_MOBILE = 0;

    public static final int TYPE_WIFI = 1;

    public static final String ANDROID_INTENT_ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";

    public static final String ANDROID_INTENT_ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";

    /**
     * 亮屏默认最小时间间隔
     */
    public static int BRIGHT_SCREEN_MIN = 30;

    /**
     * 亮屏默认最大时间间隔
     */
    public static int BRIGHT_SCREEN_MAX = 60;

    /**
     * 暗屏默认最小时间间隔
     */
    public static int Dark_SCREEN_MIN = 5;

    /**
     * 暗屏默认最大时间间隔
     */
    public static int Dark_SCREEN_MAX = 10;

    /**
     * 默认间隔
     */
    public static int DEFAULT_INTERVAL = 60;

}
