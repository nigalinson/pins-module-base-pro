package com.sloth.functions.layoutmanager.blinds;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.animator.blinds.BlindsView;
import com.rongyi.common.functions.log.LogUtils;
import com.rongyi.common.utils.RYViewUtils;
import com.rongyi.common.widget.recyclerview.layoutmanager.base.AbsSimpleComingAnimatorLayoutManager;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/17 15:46
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class BlindsGridAnimatorLayoutManager extends AbsSimpleComingAnimatorLayoutManager {

    private static final String TAG = BlindsGridAnimatorLayoutManager.class.getSimpleName();

    private BlindsView playingView;

    private int rowCount = 5, columnCount = 3, itemSpace = 15;

    private long duration = 1500;

    public BlindsGridAnimatorLayoutManager(Context context, RecyclerView rv) {
        super(context, rv);
    }

    public BlindsGridAnimatorLayoutManager(Context context, int orientation, boolean reverseLayout, RecyclerView rv) {
        super(context, orientation, reverseLayout, rv);
    }

    @Override
    protected void initThings(Context context) {
        super.initThings(context);
        playingView = new BlindsView(context);
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setItemSpace(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    protected View complexExitView() {
        return playingView;
    }

    @Override
    protected void beforeAttachComplexExitAnimation(View complexExitView, View view, Bitmap bitmap) {
        playingView.setBitmap((bitmap != null ? bitmap : RYViewUtils.snapshot(view)), RYViewUtils.snapshot(view));
    }

    @Override
    protected void startMixedExitAnimationPlaying(View complexExitView, View view, Bitmap bitmap, Animator.AnimatorListener listener) {
        LogUtils.d(TAG, "开始播放退场动画");
        boolean isVertical = canScrollVertically();
        Rect rect = new Rect();
        rect.set(mRecyclerView.getLeft(), mRecyclerView.getTop(), mRecyclerView.getLeft() + view.getWidth(), mRecyclerView.getRight() + view.getHeight());

        playingView.setAnimatorStateListener(listener);
        playingView.setSpace(itemSpace);
        playingView.setRowsAndColumns(rowCount, columnCount);
        playingView.setDuration(duration);

        MotionEvent event = MotionEvent.obtain(0, 0, 2, 1080, 0, 1);
        playingView.setAnimationPercent(0, event, isVertical);
        playingView.startAnimation(isVertical, event, -1.0f);
    }

}
