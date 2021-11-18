package com.sloth.functions.player;

import android.content.Context;

import com.rongyi.common.utils.RYDeviceUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/15 10:37
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/15         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class PlayerOnPreparedUtils {

    //
    private static final long DEFAULT_CLOSE_PREVIEW_DELAY = 0L;

    /**
     * 关闭封面的延迟时间
     * 显示封面以后surface实际显示画面还需要一定时间
     * 这个值根据主板、硬件状态等综合得出
     * @param context
     * @return
     */
    public static long closePreviewDelay(Context context){
        if(RYDeviceUtils.is3399Device()){
            return 0L;
        }else if(RYDeviceUtils.isRk3288Device()){
            return 0L;
        }else{
            //其他未知设备使用默认时间
            return DEFAULT_CLOSE_PREVIEW_DELAY;
        }
    }

}
