package com.example.ble;


import android.text.TextUtils;
import android.util.Log;


import java.util.List;

import pub.devrel.easypermissions.BuildConfig;

/**
 * @author mirrer.wangzhonglin
 * @Date 2019/8/27 17:54
 * @Description 日志打印类, 只在debug模式会打印
 */
public class LogUtils {


    //	 public static final boolean IsNeedLog = true;

    public static final boolean IsNeedLog = true;


    /**
     * 日志输出时的TAG
     */

    private static String mTag = "LogUtils";


    /**
     * 日志输出级别NONE
     */

    public static final int LEVEL_NONE = 0;

    /**
     * 日志输出级别V
     */

    public static final int LEVEL_VERBOSE = 1;

    /**
     * 日志输出级别D
     */

    public static final int LEVEL_DEBUG = 2;

    /**
     * 日志输出级别I
     */

    public static final int LEVEL_INFO = 3;

    /**
     * 日志输出级别W
     */

    public static final int LEVEL_WARN = 4;

    /**
     * 日志输出级别E
     */

    public static final int LEVEL_ERROR = 5;


    /**
     * 是否允许输出log
     */

    private static int mDebuggable = LEVEL_ERROR;


    /**
     * 用于记时的变量
     */

    private static long mTimestamp = 0;

    /** 写文件的锁对象 */


    /**
     * 以级别为 v 的形式输出LOG，输出颜色为黑色的，输出大于或等于VERBOSE日志级别的信息
     */

    public static void v(String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_VERBOSE) {

            Log.v(mTag, msg);

        }

    }

    public static void v(String tag, String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_VERBOSE) {

            Log.v(tag, msg);

        }

    }


    /**
     * 以级别为 d 的形式输出LOG，输出颜色是蓝色的，输出大于或等于DEBUG日志级别的信息
     */

    public static void d(String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_DEBUG) {

            Log.d(mTag, msg);

        }

    }

    public static void d(String tag, String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_DEBUG) {

            Log.d(tag, msg);

        }

    }

    /**
     * 以级别为 i 的形式输出LOG，输出为绿色，输出大于或等于INFO日志级别的信息
     */

    public static void i(String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_INFO) {

            Log.i(mTag, msg);

        }

    }

    public static void i(String tag, String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_INFO) {

            Log.i(tag, msg);

        }

    }

    /**
     * 以级别为 w 的形式输出LOG，输出为橙色, 输出大于或等于WARN日志级别的信息
     */

    public static void w(String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_WARN) {

            Log.w(mTag, msg);

        }

    }

    public static void w(String tag, String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_WARN) {

            Log.w(tag, msg);

        }

    }

    /**
     * 以级别为 w 的形式输出Throwable，输出为红色，仅输出ERROR日志级别的信息.
     */

    public static void w(Throwable tr) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_WARN) {

            Log.w(mTag, "", tr);

        }

    }


    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     */

    public static void w(String msg, Throwable tr) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_WARN && null != msg) {

            Log.w(mTag, msg, tr);

        }

    }


    /**
     * 以级别为 e 的形式输出LOG
     */

    public static void e(String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_ERROR) {

            Log.e(mTag, msg);

        }

    }

    public static void e(String tag, String msg) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_ERROR) {

            Log.e(tag, msg);

        }

    }

    /**
     * 以级别为 e 的形式输出Throwable
     */

    public static void e(Throwable tr) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_ERROR) {

            Log.e(mTag, "", tr);

        }

    }


    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */

    public static void e(String msg, Throwable tr) {

        if (!IsNeedLog) {

            return;

        }

        if (mDebuggable >= LEVEL_ERROR && null != msg) {

            Log.e(mTag, msg, tr);

        }

    }

    /**
     * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段起始点
     *
     * @param msg 需要输出的msg
     */

    public static void msgStartTime(String msg) {

        mTimestamp = System.currentTimeMillis();

        if (!TextUtils.isEmpty(msg)) {

            e("[Started：" + mTimestamp + "]" + msg);

        }

    }


    /**
     * 以级别为 e 的形式输出msg信息,附带时间戳，用于输出一个时间段结束点* @param msg 需要输出的msg
     */

    public static void elapsed(String msg) {

        long currentTime = System.currentTimeMillis();

        long elapsedTime = currentTime - mTimestamp;

        mTimestamp = currentTime;

        e("[Elapsed：" + elapsedTime + "]" + msg);

    }


    public static <T> void printList(List<T> list) {

        if (list == null || list.size() < 1) {

            return;

        }

        int size = list.size();

        i("---begin---");

        for (int i = 0; i < size; i++) {

            i(i + ":" + list.get(i).toString());

        }

        i("---end---");

    }


    public static <T> void printArray(T[] array) {

        if (array == null || array.length < 1) {

            return;

        }

        int length = array.length;

        i("---begin---");

        for (int i = 0; i < length; i++) {

            i(i + ":" + array[i].toString());

        }

        i("---end---");

    }

}
