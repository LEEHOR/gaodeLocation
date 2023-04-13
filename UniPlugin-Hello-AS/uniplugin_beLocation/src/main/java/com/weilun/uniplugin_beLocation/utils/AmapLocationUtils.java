package com.weilun.uniplugin_beLocation.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.weilun.uniplugin_beLocation.R;

import java.text.ParseException;

/**
 * @author 李浩
 * @version 1.0
 * @description: TODO
 * @date 2023/4/12 13:10
 */
public class AmapLocationUtils {
    /**
     * AmapLocationUtil
     */
    private Context mContext;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    public static double longitude = 0;
    public static double latitude = 0;
    private onCallBackListener mOnCallBackListener = null;
    private static AmapLocationUtils instance;

    public static AmapLocationUtils getInstance(Context context) {

        if (instance == null) {
            synchronized (AmapLocationUtils.class) {
                if (instance == null) {
                    instance = new AmapLocationUtils(context);
                }
            }
        }

        return instance;
    }

    public AmapLocationUtils(Context context) {
        this.mContext = context;

    }

    public AmapLocationUtils() {
    }

    /**
     * 初始化定位
     */
    public void initLocation(Long interval) {
        //初始化client
        if (null != instance && locationClient==null) {
            AMapLocationClient.updatePrivacyShow(mContext, true, true);
            AMapLocationClient.updatePrivacyAgree(mContext, true);
            try {
                locationClient = new AMapLocationClient(mContext);
                locationOption = getDefaultOption(interval);
                //设置定位参数
                locationClient.setLocationOption(locationOption);
                // 设置定位监听
                locationClient.setLocationListener(locationListener);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("initLocation", e.toString());
            }
        }
    }


    private AMapLocationClientOption getDefaultOption(Long interval) {
        Log.e("getDefaultOption", interval + "");
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //如果网络可用就选择高精度
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(interval == null ? 2000 : interval);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            StringBuilder sb = new StringBuilder();
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    String district = location.getDistrict();
                    locationSuccess(longitude, latitude, true, location, district);
                    //定位成功，停止定位：如果实时定位，就把stopLocation()关闭
                    Log.e("---> 定位成功", location.toString());
//                    stopLocation();
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                    Log.e("---> 定位失败", sb.toString());
                    LocationFarile(false, location);

                }
            } else {
                LocationFarile(false, location);
            }
        }
    };

    private void LocationFarile(boolean isSucdess, AMapLocation location) {
        if (mOnCallBackListener != null) {
            mOnCallBackListener.onCallBack(0, 0, location, false, "");
        }
    }

    public void locationSuccess(double longitude, double latitude, boolean isSucdess, AMapLocation location, String address) {
        if (mOnCallBackListener != null) {
            mOnCallBackListener.onCallBack(longitude, latitude, location, true, address);
        }
    }

    public void setOnCallBackListener(onCallBackListener listener) {
        this.mOnCallBackListener = listener;
    }

    public interface onCallBackListener {
        void onCallBack(double longitude, double latitude, AMapLocation location, boolean isSucdess, String address);
    }

    /**
     * 开始定位
     */
    public void startLocation() {
        if (locationClient != null) {
            if (locationClient.isStarted()) {
                locationClient.stopLocation();
            }
            locationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    public void stopLocation() {
        if (locationClient != null) {
            if (locationClient.isStarted()) {
                locationClient.stopLocation();
            }
        }
    }

    /**
     * 开启后台定位
     */
    public void startBackgroundLocation() {
        if (null == locationClient) {
            try {
                locationClient = new AMapLocationClient(mContext);
                locationClient.disableBackgroundLocation(true);
                //启动后台定位
                locationClient.enableBackgroundLocation(1011, buildNotification());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            locationClient.disableBackgroundLocation(true);
            locationClient.enableBackgroundLocation(1011, buildNotification());
        }
    }

    /**
     * 结束后台定位
     */
    public void stopBackgroundLocation() {
        if (null == locationClient) {
            try {
                locationClient = new AMapLocationClient(mContext);
                locationClient.disableBackgroundLocation(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            locationClient.disableBackgroundLocation(true);
        }
    }

    /**
     * 销毁定位
     */
    public void destroyLocation() {
        if (null != locationClient) {
            locationClient.unRegisterLocationListener(locationListener);
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    private static final String NOTIFICATION_CHANNEL_NAME = "quanqiLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;

    @SuppressLint("NewApi")
    private Notification buildNotification() {
        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            String channelId = mContext.getPackageName();
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(mContext.getApplicationContext(), channelId);
        } else {
            builder = new Notification.Builder(mContext.getApplicationContext());
        }
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(NoticeUtils.getAppName(mContext))
                .setContentText("正在后台运行")
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }
}
