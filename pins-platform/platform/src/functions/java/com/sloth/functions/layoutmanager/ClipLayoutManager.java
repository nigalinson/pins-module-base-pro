package com.sloth.functions.layoutmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.rongyi.common.widget.recyclerview.layoutmanager.base.AbstractBannerLayoutManager;
import com.rongyi.common.widget.recyclerview.layoutmanager.clippath.ClipAnimatorLayoutManager;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/7 16:28
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/7         Carl            1.0                    1.0
 * Why & What is modified:
 *
 * 跟手剪切动画，与 {@link ClipAnimatorLayoutManager} 的区别：
 * 此动画为跟手动画，直接对目标View进行剪裁处理，因此支持的动效类型较少
 * ClipAnimatorLayoutManager使用单独的AnimatorView进行动画处理
 * 因此支持更多动效
 */
@SuppressLint("NewApi")
public class ClipLayoutManager extends AbstractBannerLayoutManager {

    public enum Mode{
        /**
         * 单边剪切（横向正向滚动时|右→左，横向反向滚动时|左→右）
         */
        RIGHT_2_LEFT(10),
        LEFT_2_RIGHT(11),
        TOP_2_BOTTOM(12),
        BOTTOM_2_TOP(13),
        LEFT_TOP_2_RIGHT_BOTTOM(14),
        RIGHT_TOP_2_LEFT_BOTTOM(15),
        LEFT_BOTTOM_2_RIGHT_TOP(16),
        RIGHT_BOTTOM_2_LEFT_TOP(17),
        ;

        Mode(int code) {
            this.code = code;
        }

        public int code;
    }

    private int mode = Mode.RIGHT_2_LEFT.code;

    public ClipLayoutManager(Context context) {
        super(context);
    }

    public ClipLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    protected void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {

        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;
        layoutDecoratedWithMargins(v, anchorLeft, anchorTop, anchorLeft + getItemWidth(), anchorTop + getItemHeight());

        Rect rect = new Rect();
        rect.set(0,0, v.getWidth(), v.getHeight());
        if(isStackTop){
            handlerClip(v, rect, percent);
        }else{
            v.setClipBounds(rect);
        }
    }

    private void handlerClip(View v, Rect rect, float percent) {
        if(mode == Mode.RIGHT_2_LEFT.code){
            rect.right = v.getWidth() - (int) (v.getWidth() * percent);
        }else if(mode == Mode.LEFT_2_RIGHT.code){
            rect.left = (int) (v.getWidth() * percent);
        }else if(mode == Mode.TOP_2_BOTTOM.code){
            rect.top = (int) (v.getHeight() * percent);
        }else if(mode == Mode.BOTTOM_2_TOP.code){
            rect.bottom = v.getHeight() - (int)(v.getHeight() * percent);
        }else if(mode == Mode.LEFT_TOP_2_RIGHT_BOTTOM.code){
            rect.left = (int) (v.getWidth() * percent);
            rect.top = (int) (v.getHeight() * percent);
        }else if(mode == Mode.RIGHT_TOP_2_LEFT_BOTTOM.code){
            rect.right = v.getWidth() - (int) (v.getWidth() * percent);
            rect.top = (int) (v.getHeight() * percent);
        }else if(mode == Mode.LEFT_BOTTOM_2_RIGHT_TOP.code){
            rect.left = (int) (v.getWidth() * percent);
            rect.bottom = v.getHeight() - (int)(v.getHeight() * percent);
        }else if(mode == Mode.RIGHT_BOTTOM_2_LEFT_TOP.code){
            rect.right = v.getWidth() - (int) (v.getWidth() * percent);
            rect.bottom = v.getHeight() - (int)(v.getHeight() * percent);
        } else { }

        v.setClipBounds(rect);
    }

}
