package com.kingsley.floatbardemo;

import android.app.Application;

import com.kingsley.floatbardemo.db.DBManager;
import com.kingsley.floatbardemo.task.TaskScheduler;

/**
 * class name : FloatBarDemo
 * author : Kingsley
 * created date : on 2017/9/19 13:08
 * file change date : on 2017/9/19 13:08
 * version: 1.0
 */

public class App extends Application {
    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        TaskScheduler.init();
        DBManager.getInstance().copyCitysToDB();
    }
}
