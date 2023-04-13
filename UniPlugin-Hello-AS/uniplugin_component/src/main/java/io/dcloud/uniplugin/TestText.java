package io.dcloud.uniplugin;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.MapView;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.UniSDKInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.ui.action.AbsComponentData;
import io.dcloud.feature.uniapp.ui.component.AbsVContainer;
import io.dcloud.feature.uniapp.ui.component.UniComponent;
import io.dcloud.feature.uniapp.ui.component.UniComponentProp;
import io.dcloud.feature.uniapp.ui.component.UniVContainer;
import uni.dcloud.io.uniplugin_component.R;

public class TestText extends UniVContainer<RelativeLayout> {

    public TestText(UniSDKInstance instance, AbsVContainer parent, AbsComponentData basicComponentData) {
        super(instance, parent, basicComponentData);
    }

    @Override
    protected RelativeLayout initComponentHostView(Context context) {
                RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT));
        MapView mapView = new MapView(context);
        mapView.onCreate(null);
        mapView.setBackgroundResource(R.drawable.ic_launcher);
        mapView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT));
        RelativeLayout.LayoutParams ivKnown_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT);
        ivKnown_lp.setMargins(0, 0, 0, 0);
        ivKnown_lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ivKnown_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ivKnown_lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ivKnown_lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeLayout.addView(mapView,ivKnown_lp);
        return relativeLayout;
    }
    @UniComponentProp(name = "tel")
    public void setTel(String telNumber) {
        MapView childAt = (MapView) getHostView().getChildAt(0);
        childAt.getMap();
//        childAt.setText("tel: " + telNumber);
//        Map<String, Object> params = new HashMap<>();
//        Map<String, Object> number = new HashMap<>();
//        number.put("tel", telNumber);
//        //目前uni限制 参数需要放入到"detail"中 否则会被清理
//        params.put("detail", number);
//        fireEvent("onTel", params);
    }

    @UniJSMethod
    public void clearTel() {
        MapView childAt = (MapView) getHostView().getChildAt(0);
        childAt.onDestroy();
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
    }

    @Override
    public void onActivityPause() {
        super.onActivityPause();
    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
    }
}
