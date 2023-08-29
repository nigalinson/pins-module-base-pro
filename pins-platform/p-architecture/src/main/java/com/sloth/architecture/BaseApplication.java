package com.sloth.architecture;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.multidex.MultiDexApplication;
import com.sloth.utils.Utils;

public class BaseApplication extends MultiDexApplication {

    private static final String TAG = BaseApplication.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    protected static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        Utils.init(this);

    }

    public static Context getContext() {
        return sContext;
    }

    public static void attach(Context context){
        sContext = context;
    }

}
