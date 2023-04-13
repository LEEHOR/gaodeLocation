package com.weilun.uniplugin_beLocation;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.uniapp.UniSDKInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.ui.action.AbsComponentData;
import io.dcloud.feature.uniapp.ui.component.AbsVContainer;
import io.dcloud.feature.uniapp.ui.component.UniComponent;

/**
 * @author 李浩
 * @version 1.0
 * @description: 高德地图
 * @date 2023/4/12 16:45
 */
public class AmapViewModule extends UniComponent<MapView> {

    private MapView mapView;
    private List<Marker> markerList = new ArrayList<>();
    private AMap map;
    private Context mContext;

    public AmapViewModule(UniSDKInstance instance, AbsVContainer parent, AbsComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    @Override
    protected MapView initComponentHostView(@NonNull Context context) {
        this.mContext = context;
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mapView = new MapView(context);
        mapView.onCreate(null);
        mapView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        if (mapView != null) {
            if (map == null) {
                map = mapView.getMap();
            }
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        //map.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        map.clear();
        return mapView;
    }

    @UniJSMethod
    public void initMap() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.clear();
        for (Marker item : markerList
        ) {
            if (item != null) {
                item.destroy();
            }
        }
    }

    //    {
//        "driverLngLats": [
//        {
//            "name": "name",
//                "lngLat": [
//            117,
//                    31
//            ]
//        },
//        {
//            "name": "name",
//                "lngLat": [
//            117,
//                    31
//            ]
//        }
//    ],
//        "customerLngLat": {
//        "name":"name",
    //         "address":"address",
    //      "lngLat":[
    //        117,
//                31
//    ]
//        }
//   }
    @UniJSMethod
    public void drawMarker(String jsonLngLat) {
        if (mapView == null && map == null) {
            return;
        }
        if (jsonLngLat != null && !jsonLngLat.equals("")) {
            JSONObject jsonObject = JSONObject.parseObject(jsonLngLat);
            //绘制客户位置
            JSONObject customerLngLat = jsonObject.getJSONObject("customerLngLat");
            if (customerLngLat != null) {
                String name = customerLngLat.getString("name");
                String address = customerLngLat.getString("address");
                JSONArray lngLat = customerLngLat.getJSONArray("lngLat");
                if (lngLat != null && lngLat.size() > 1) {
                    LatLng latLng = new LatLng(lngLat.getDouble(1), lngLat.getDouble(0));
                    Marker marker = map.addMarker(new MarkerOptions().position(latLng).title(name).snippet("地址:" + address));
                    markerList.add(marker);
                    map.setMyLocationEnabled(false);
                    marker.showInfoWindow();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                }
            }
            //绘制司机
            JSONArray driverLngLats = jsonObject.getJSONArray("driverLngLats");
            if (driverLngLats != null && driverLngLats.size() > 0) {
                for (int i = 0; i < driverLngLats.size(); i++) {
                    JSONObject jsonObjectd = driverLngLats.getJSONObject(i);
                    String name = jsonObjectd.getString("name");
                    JSONArray lngLat = jsonObjectd.getJSONArray("lngLat");
                    if (lngLat != null && lngLat.size() > 1) {
                        LatLng latLng = new LatLng(lngLat.getDouble(1), lngLat.getDouble(0));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(name);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeResource(mContext.getResources(), R.drawable.cheliangweizhi)));
                        Marker marker = map.addMarker(markerOptions);
                        markerList.add(marker);
                    }
                }
            }
        }
    }
    @UniJSMethod
    public void onResume() {
        if (mapView != null) {
            //在activity执行onPause时执行mMapView.onResume ()，重新绘制加载地图
            mapView.onResume();
        }
    }
    @UniJSMethod
    public void onPause() {
        if (mapView != null) {
            //在activity执行onPause时执行mMapView.onResume ()，重新绘制加载地图
            mapView.onPause();
        }
    }
    @UniJSMethod
    public void onDestroy() {
        if (mapView != null) {
            //在activity执行onPause时执行mMapView.onResume ()，重新绘制加载地图
            mapView.onDestroy();
        }
    }
    @Override
    public void onActivityResume() {
        if (mapView != null) {
            //在activity执行onPause时执行mMapView.onResume ()，重新绘制加载地图
            mapView.onResume();
        }

    }

    @Override
    public void onActivityPause() {
        if (mapView != null) {
            //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
            mapView.onPause();
        }
    }

    @Override
    public void onActivityDestroy() {
        if (mapView != null) {
            //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
            mapView.onDestroy();
        }
    }
}
