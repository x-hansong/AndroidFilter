package com.hansong.filter.app;

import android.app.Application;
import com.hansong.filter.core.IBlocker;


/**
 * Created by xhans on 2016/7/3 0003.
 */
public class FilterApp extends Application{

    private IBlocker iBlocker;

    public IBlocker getiBlocker() {
        return iBlocker;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setiBlocker(IBlocker iBlocker) {
        this.iBlocker = iBlocker;
    }
}
