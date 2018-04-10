package com.plugamap;

import android.content.Context;

import com.benmu.framework.manager.ManagerFactory;
import com.benmu.framework.manager.impl.dispatcher.DispatchEventManager;
import com.benmu.framework.model.WeexEventBean;
import com.benmu.framework.utils.JsPoster;
import com.benmu.wxbase.EventGate;
import com.plugamap.manager.GeoManager;
import com.plugamap.model.GeoResultBean;
import com.squareup.otto.Subscribe;
import com.taobao.weex.bridge.JSCallback;

/**
 * Created by Carry on 2017/11/17.
 */

public class EventGeo extends EventGate {
    private JSCallback mCallback;
    private GeoManager mGeoManager;

    @Override
    public void perform(Context context,WeexEventBean weexEventBean) {
        getLocation(context, weexEventBean.getJscallback());
    }

    public void getLocation(Context context, JSCallback jscallback) {
        this.mCallback = jscallback;
        mGeoManager = ManagerFactory.getManagerService(GeoManager.class);
        mGeoManager.startLocation(context);
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().register(this);
    }

    @Subscribe
    public void onLocation(GeoResultBean bean) {
        if (bean.resCode == 0) {
            //定位成功
            JsPoster.postSuccess(bean.getData(), mCallback);
        } else {
            //定位失败
            JsPoster.postFailed(mCallback);
        }
        mGeoManager.onPause();
        ManagerFactory.getManagerService(DispatchEventManager.class).getBus().unregister(this);
    }

}
