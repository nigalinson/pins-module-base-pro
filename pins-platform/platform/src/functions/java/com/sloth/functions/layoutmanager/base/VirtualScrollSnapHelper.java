package com.sloth.functions.layoutmanager.base;

import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.functions.log.LogUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/2 18:06
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/2         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class VirtualScrollSnapHelper extends RecyclerView.OnFlingListener{
    private static final String TAG = VirtualScrollSnapHelper.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private Scroller mGravityScroller;

    private int flippingDuration = RecyclerView.UNDEFINED_DURATION;

    public VirtualScrollSnapHelper() { }

    public VirtualScrollSnapHelper(int flippingDuration) {
        this.flippingDuration = flippingDuration;
    }

    // Handles the snap on scroll case.
    private final RecyclerView.OnScrollListener mScrollListener =
            new RecyclerView.OnScrollListener() {
                boolean mScrolled = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                        mScrolled = false;
                        snapToTargetExistingView();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dx != 0 || dy != 0) {
                        mScrolled = true;
                    }
                }
            };

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }
        int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
                && snapFromFling(layoutManager, velocityX, velocityY);
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            setupCallbacks();
            mGravityScroller = new Scroller(mRecyclerView.getContext(), new DecelerateInterpolator());
            snapToTargetExistingView();
        }
    }

    /**
     * Called when an instance of a {@link RecyclerView} is attached.
     */
    private void setupCallbacks() throws IllegalStateException {
        if (mRecyclerView.getOnFlingListener() != null) {
            throw new IllegalStateException("An instance of OnFlingListener already set.");
        }
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(this);
    }

    /**
     * Called when the instance of a {@link RecyclerView} is detached.
     */
    private void destroyCallbacks() {
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(null);
    }

    /**
     * Helper method to facilitate for snapping triggered by a fling.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param velocityX     Fling velocity on the horizontal axis.
     * @param velocityY     Fling velocity on the vertical axis.
     *
     * @return true if it is handled, false otherwise.
     */
    private boolean snapFromFling(@NonNull RecyclerView.LayoutManager layoutManager, int velocityX,
                                  int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return false;
        }

        if(mGravityScroller != null){
            int[] snapDistance = calculateDistanceAfterSnap(layoutManager, velocityX, velocityY);
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1], new DecelerateInterpolator(), flippingDuration);
        }

        return true;
    }

    /**
     * Snaps to a target view which currently exists in the attached {@link RecyclerView}. This
     * method is used to snap the view when the {@link RecyclerView} is first attached; when
     * snapping was triggered by a scroll and when the fling is at its final stages.
     */
    void snapToTargetExistingView() {
        if (mRecyclerView == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }

        int[] snapDistance = calculateDistanceToFinalSnap(layoutManager);
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1], new DecelerateInterpolator(), flippingDuration);
        }
    }

    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager) {
        int[] out = new int[]{ 0, 0 };
        if(layoutManager.canScrollHorizontally()){
            out[0] = distanceToBalance(layoutManager);
            log("calculateDistanceToFinalSnap:" + out[0]);
        }else{
            out[1] = distanceToBalance(layoutManager);
            log("calculateDistanceToFinalSnap:" + out[1]);
        }
        return out;
    }

    private int[] calculateDistanceAfterSnap(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        int[] out = new int[2];
        if(layoutManager instanceof VirtualScroll){
            VirtualScroll lt = ((VirtualScroll)layoutManager);
            int itemSize = lt.getScrollItemSize();
            int total = layoutManager.getItemCount() * itemSize;
            int offset = lt.getScrollOffset();
            if(offset < 0){
                offset += total;
            }

            int itemOffset = offset % itemSize;
            float itemPercent = ((float)itemOffset) / itemSize;

            int index = layoutManager.canScrollHorizontally() ? 0 : 1;
            int velocity = layoutManager.canScrollHorizontally() ? velocityX : velocityY;

            if(itemPercent >= 0.5f){
                out[index] = itemSize - itemOffset;
            }else{
                if(velocity > 0){
                    out[index] = itemSize - itemOffset;
                }else{
                    out[index] = -itemOffset;
                }
            }
        }
        return out;
    }

    private int distanceToBalance(@NonNull RecyclerView.LayoutManager layoutManager) {
        if(layoutManager instanceof VirtualScroll){
            VirtualScroll lt = ((VirtualScroll)layoutManager);
            int itemSize = lt.getScrollItemSize();
            int total = layoutManager.getItemCount() * itemSize;
            int offset = lt.getScrollOffset();
            if(offset < 0){
                offset += total;
            }

            int itemOffset = offset % itemSize;
            float itemPercent = ((float)itemOffset) / itemSize;
            if(itemPercent >= 0.5f){
                return itemSize - itemOffset;
            }else{
                return -itemOffset;
            }
        }
        return 0;
    }

//    @Nullable
//    protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
//        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
//            return null;
//        }
//        return new LinearSmoothScroller(mRecyclerView.getContext()) {
//
//
//
//            @Override
//            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
//            }
//
//            @Override
//            protected int calculateTimeForScrolling(int dx) {
//                return Math.min(MAX_SCROLL_ON_FLING_DURATION, super.calculateTimeForScrolling(dx));
//            }
//        };
//    }

    public boolean debug = true;

    private void log(String msg){
        if(!debug){
            return;
        }
        LogUtils.d(TAG, msg);
    }
}
