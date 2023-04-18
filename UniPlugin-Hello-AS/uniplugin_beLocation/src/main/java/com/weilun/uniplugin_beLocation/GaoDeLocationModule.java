package com.weilun.uniplugin_beLocation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.weilun.uniplugin_beLocation.utils.AmapLocationUtils;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.weilun.uniplugin_beLocation.utils.PermissionsUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.common.UniModule;

/**
 * uniapp 组件
 *
 * @author 李浩
 * @version 1.0
 * @description:
 * 高德定位
 * @date 2023/4/12 9:44
 */
public class GaoDeLocationModule extends UniModule {

    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACKGROUND_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            BACKGROUND_LOCATION_PERMISSION
    };
    private JSONObject initOptions = null;
    private JSCallback initJsCallback = null;
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    private AmapLocationUtils amapLocationUtils = null;
    private AmapLocationUtils.onCallBackListener onCallBackListener;
    private PermissionsUtils.IPermissionsResult permissionsResult;

    /**
     * 判断是否开启通知权限
     *
     * @param options
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void isNotificationEnabled(JSONObject options, JSCallback jsCallback) throws ParseException {
        boolean isEnabled = false;
        if (mUniSDKInstance.getContext() instanceof Activity) {
            isEnabled = NotificationManagerCompat.from(mUniSDKInstance.getContext()).areNotificationsEnabled();
        }
        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("isEnabled", isEnabled);
        jsCallback.invoke(result);
    }

    /**
     * 跳转到设置界面打开通知
     *
     * @param options
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void invokeNotification(JSONObject options, JSCallback jsCallback) throws ParseException {
        boolean isEnabled = false;
        if (mUniSDKInstance.getContext() instanceof Activity) {
            isEnabled = NotificationManagerCompat.from(mUniSDKInstance.getContext()).areNotificationsEnabled();
        }
        if (!isEnabled) {
            AlertDialog alertDialog = new AlertDialog.Builder(mUniSDKInstance.getContext())
                    .setTitle("提示")
                    .setMessage("请在“设置”中打开通知权限")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                            Intent intent = new Intent();
                            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                            intent.putExtra("app_package", mUniSDKInstance.getContext().getPackageName());
                            intent.putExtra("app_uid", mUniSDKInstance.getContext().getApplicationInfo().uid);
                            // for Android 8 and above
                            intent.putExtra("android.provider.extra.APP_PACKAGE", mUniSDKInstance.getContext().getPackageName());
                            mUniSDKInstance.getContext().startActivity(intent);
                        }
                    }).create();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            alertDialog.show();
        }

    }

    /**
     * 初始化
     */
    public void initGaodeLocation(){
       amapLocationUtils = AmapLocationUtils.getInstance(mUniSDKInstance.getContext());
       amapLocationUtils.setOnCallBackListener(onCallBackListener);
       if (amapLocationUtils != null) {
           Long interval = null;
           if (initOptions != null) {
               interval = initOptions.getLong("interval");
           }
           amapLocationUtils.initLocation(interval);
           JSONObject result = new JSONObject();
           result.put("code", 200);
           result.put("msg", "定位初始化成功");
           initJsCallback.invoke(result);
       } else {
           JSONObject result = new JSONObject();
           result.put("code", 500);
           result.put("msg", "未获取定位权限");
       }
    }
    /**
     * 初始化高德定位
     *
     * @param options    {
     *                   "interval":2000
     *                   }
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void initLocation(JSONObject options, JSCallback jsCallback) throws ParseException {
        this.initOptions = options;
        this.initJsCallback = jsCallback;
        onCallBackListener = new AmapLocationUtils.onCallBackListener() {
            @Override
            public void onCallBack(double longitude, double latitude, AMapLocation location, boolean isSucdess, String address) {
                Map<String, Object> params = new HashMap<>();
                params.put("isSuccess", isSucdess);
                params.put("location", location);
                if (isSucdess) {
                    params.put("lngLat", new double[]{longitude, latitude});
                } else {
                    params.put("lngLat", null);
                }
                mUniSDKInstance.fireGlobalEventCallback("gaodeLocation", params);
            }
        };
        //创建监听权限的接口对象
        permissionsResult = new PermissionsUtils.IPermissionsResult() {
            @Override
            public void passPermissons() {
                initGaodeLocation();
            }

            @Override
            public void forbitPermissons() {
                showMissingPermissionDialog();
            }
        };
        if (Build.VERSION.SDK_INT > 28
                && mUniSDKInstance.getContext().getApplicationInfo().targetSdkVersion > 28) {
            needPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    BACKGROUND_LOCATION_PERMISSION
            };
        }
        if (Build.VERSION.SDK_INT >= 23
                && mWXSDKInstance.getContext().getApplicationInfo().targetSdkVersion >= 23) {
            PermissionsUtils.getInstance().chekPermissions((Activity) mUniSDKInstance.getContext(), needPermissions, permissionsResult);
        } else {
            initGaodeLocation();
        }

    }

    /**
     * 开启定位
     *
     * @param options
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void startLocation(JSONObject options, JSCallback jsCallback) throws ParseException {
        if (amapLocationUtils != null) {
            amapLocationUtils.startLocation();
            JSONObject result = new JSONObject();
            result.put("code", 200);
            result.put("msg", "开始定位");
            jsCallback.invoke(result);
        }
    }

    /**
     * 结束定位
     *
     * @param options
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void stopLocation(JSONObject options, JSCallback jsCallback) throws ParseException {
        if (amapLocationUtils != null) {
            amapLocationUtils.stopLocation();
            JSONObject result = new JSONObject();
            result.put("code", 200);
            result.put("msg", "结束定位");
            jsCallback.invoke(result);
        }
    }

    /**
     * 开启后台定位
     *
     * @param options
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void startBackgroundLocation(JSONObject options, JSCallback jsCallback) throws ParseException {
        if (amapLocationUtils != null) {
            amapLocationUtils.startBackgroundLocation();
        }
    }


    /**
     * 结束后台定位
     *
     * @param options
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void stopBackgroundLocation(JSONObject options, JSCallback jsCallback) throws ParseException {
        if (amapLocationUtils != null) {
            amapLocationUtils.stopBackgroundLocation();
        }
    }

    /**
     * 销毁高德定位
     *
     * @param options
     * @param jsCallback
     * @throws ParseException
     */
    @JSMethod(uiThread = false)
    public void destroyLocation(JSONObject options, JSCallback jsCallback) throws ParseException {
        if (amapLocationUtils != null) {
            amapLocationUtils.destroyLocation();
        }
    }


    @Override
    public void onActivityStart() {
        super.onActivityStart();
    }

    @Override
    public void onActivityCreate() {
        super.onActivityCreate();
    }

    @Override
    public void onActivityPause() {
        if (amapLocationUtils != null) {
            amapLocationUtils.startBackgroundLocation();
        }
        super.onActivityPause();
    }

    @Override
    public void onActivityResume() {
        if (Build.VERSION.SDK_INT > 28
                && mUniSDKInstance.getContext().getApplicationInfo().targetSdkVersion > 28) {
            needPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    BACKGROUND_LOCATION_PERMISSION
            };
        }
        if (Build.VERSION.SDK_INT >= 23
                && mWXSDKInstance.getContext().getApplicationInfo().targetSdkVersion >= 23) {
            PermissionsUtils.getInstance().chekPermissions((Activity) mUniSDKInstance.getContext(), needPermissions, permissionsResult);
        } else {
            amapLocationUtils = AmapLocationUtils.getInstance(mUniSDKInstance.getContext());
            amapLocationUtils.setOnCallBackListener(onCallBackListener);
        }
        if (amapLocationUtils != null) {
            amapLocationUtils.stopBackgroundLocation();
        }
        super.onActivityResume();
    }

    @Override
    public void onActivityDestroy() {
        if (amapLocationUtils != null) {
            amapLocationUtils.destroyLocation();
        }
        super.onActivityDestroy();
    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        PermissionsUtils.getInstance().onRequestPermissionsResult((Activity) mUniSDKInstance.getContext(), requestCode, permissions, paramArrayOfInt);
    }




    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mUniSDKInstance.getContext());
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + mUniSDKInstance.getContext().getPackageName()));
        mUniSDKInstance.getContext().startActivity(intent);
    }
}
