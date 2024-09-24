package com.animee.forecast.base;

import android.app.Application;

import com.animee.forecast.db.DBManager;

import org.xutils.x;

public class UniteApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        DBManager.initDB(this);
    }
}
