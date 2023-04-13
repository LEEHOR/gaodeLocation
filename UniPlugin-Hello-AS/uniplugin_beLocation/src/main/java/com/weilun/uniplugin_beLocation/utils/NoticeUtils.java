package com.weilun.uniplugin_beLocation.utils;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author 李浩
 * @version 1.0
 * @description: TODO
 * @date 2023/4/12 10:08
 */
public class NoticeUtils {
    /**
     * 获取app的名称
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            appName =  context.getResources().getString(labelRes);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return appName;
    }

}
