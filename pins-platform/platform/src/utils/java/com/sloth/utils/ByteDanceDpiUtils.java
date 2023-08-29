package com.sloth.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import androidx.appcompat.app.AlertDialog;

import com.sloth.platform.Platform;

/**
 * 实际使用中偶然会出现density计算异常,导致图标大小异常
 */
public class ByteDanceDpiUtils {
    private static final String TAG = ByteDanceDpiUtils.class.getSimpleName();

    public static void setCustomDensity(Resources resource) {
        setCustomDensity(resource, 160, 640, 360);
    }

    public static void setCustomDensity(Resources resource, int sampleDpi, int landscapeWidth, int portraitWidth) {

        final DisplayMetrics appDisplayMetrics = resource.getDisplayMetrics();

        //1080表示这个项目设计图的宽度为
        final float ratio = 1f * appDisplayMetrics.widthPixels / screenWidth(resource, landscapeWidth, portraitWidth);

        final int targetDensity = (int) (sampleDpi * ratio);//重新计算设备的dpi

        appDisplayMetrics.density = appDisplayMetrics.scaledDensity = ratio;

        appDisplayMetrics.densityDpi = targetDensity;

        final DisplayMetrics activityDisplayMetrics = resource.getDisplayMetrics();

        activityDisplayMetrics.density = activityDisplayMetrics.scaledDensity = ratio;

        activityDisplayMetrics.densityDpi = targetDensity;
    }

    private static int screenWidth(Resources resource, int land, int port) {
        Configuration mConfiguration = resource.getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            return land;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            return port;
        }
        return port;
    }

    public static void adjustDialogSize(Activity activity, AlertDialog mAlertDialog){
        if (mAlertDialog.getWindow() != null) {
            Platform.log().d(TAG, "调整AlertDialog大小");
            WindowManager.LayoutParams lp = mAlertDialog.getWindow().getAttributes();
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            //宽度，可根据屏幕宽度进行计算
            lp.width = size.x / 10 * 8;
            lp.gravity = Gravity.CENTER;
            mAlertDialog.getWindow().setAttributes(lp);
        }
    }

}
