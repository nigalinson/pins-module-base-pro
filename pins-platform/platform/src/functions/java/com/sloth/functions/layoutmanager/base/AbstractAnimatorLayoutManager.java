package com.sloth.functions.layoutmanager.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.functions.log.LogUtils;
import com.rongyi.common.utils.AutoDispose;
import com.rongyi.common.widget.recyclerview.adapter.BasePagerHighLightAdapter;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/7 16:30
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/7         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AbstractAnimatorLayoutManager extends AbstractVirtualScrollLayoutManager implements AutoDispose.AutoDisposable {

    private static final String TAG = "AnimatorLLayoutManager";

    /**
     * 需要等待重新layout完成才能出发各种监听，因此需要一个延时
     */
    public static final long HACK_PAGE_DELAY = 100;

    protected RecyclerView mRecyclerView;

    private boolean mRecycleChildrenOnDetach;

    private SavedState mPendingSavedState;

    private int increasing = 0;

    private View enterView;

    private View exitView;

    private static final int ANIMATE_STATE_IDLE = 0;
    private static final int ANIMATE_STATE_PRE_ANIMATE = 1;
    private static final int ANIMATE_STATE_ANIMATING = 2;

    private int animateState = ANIMATE_STATE_IDLE;

    private boolean firstPageIgnored = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final RecyclerViewPageScrollListener recyclerViewPageScrollListener = new RecyclerViewPageScrollListener(position -> {
        if(!firstPageIgnored){
            firstPageIgnored = true;
            if(position == 0){
                return;
            }
        }
        animateState = ANIMATE_STATE_PRE_ANIMATE;
        requestLayout();
    });

    public AbstractAnimatorLayoutManager(Context context, RecyclerView rv) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
        init(context, rv);
    }

    public AbstractAnimatorLayoutManager(Context context, int orientation, boolean reverseLayout, RecyclerView rv) {
        super(context, orientation, reverseLayout);
        init(context, rv);
    }

    private void init(Context context, RecyclerView recyclerView){
        this.mRecyclerView = recyclerView;
        recyclerView.addOnScrollListener(recyclerViewPageScrollListener);
        AutoDispose.fromPool(context.getClass().getSimpleName()).bind(context).autoDispose(this);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean getRecycleChildrenOnDetach() {
        return mRecycleChildrenOnDetach;
    }

    @Override
    public void setRecycleChildrenOnDetach(boolean recycleChildrenOnDetach) {
        mRecycleChildrenOnDetach = recycleChildrenOnDetach;
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        if (mRecycleChildrenOnDetach) {
            removeAndRecycleAllViews(recycler);
            recycler.clear();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (mPendingSavedState != null) {
            return new SavedState(mPendingSavedState);
        }
        SavedState savedState = new SavedState();
        savedState.infinite = infinite;
        savedState.offset = scrollOffset;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            mPendingSavedState = new SavedState((SavedState) state);
            requestLayout();
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return handlerScroll(recycler, state,0, dx);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return handlerScroll(recycler, state,1, dy);
    }

    private int handlerScroll(RecyclerView.Recycler recycler, RecyclerView.State state, int orientationIndex, int scrollDistance) {
        if(getScrollItemSize() == -99 || getItemCount() == 0 || itemSize[0] == 0 || itemSize[1] == 0){
            //暂未初始化滚动大小，视为无效滚动
            return 0;
        }

        int pendingOffset = scrollOffset + scrollDistance;

        if(!isInfinite()){
            int maxOffset = (getItemCount() - 1) * itemSize[orientationIndex];
            if(pendingOffset <= 0){
                scrollOffset = 0;
            }else {
                scrollOffset = Math.min(pendingOffset, maxOffset);
            }
        }else{
            scrollOffset = pendingOffset;
            //去除超过1圈的周期
            scrollOffset = scrollOffset % ( getItemCount() * itemSize[orientationIndex]);
        }

        int realDis = !isInfinite() ? (scrollOffset - pendingOffset + scrollDistance) : scrollDistance;
        increasing = (realDis >= 0) ? 1 : -1;

        return realDis;
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        log("layoutChildren");

        if(getScrollItemSize() == -99 || getItemCount() == 0 || itemSize[0] == 0 || itemSize[1] == 0){
            preMeasureItemSize(recycler, state, itemSize);
        }

        if(animateState == ANIMATE_STATE_ANIMATING){
            //动画过程中不重新 layout
            LogUtils.d(TAG, "动画中,不触发重新layout");
            return;
        }

        layoutChildDetectAvailable(recycler, state);
    }

    @Override
    protected void layoutChild(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getScrollItemSize() <= 0){
            //暂未初始化滚动大小，视为无效摆放
            return;
        }

        if(mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter() instanceof BasePagerHighLightAdapter){
            //设置page变化延迟100ms触发
            ((BasePagerHighLightAdapter)mRecyclerView.getAdapter()).setPageHackDelay(HACK_PAGE_DELAY);
        }

        if(!isInfinite()){
            if(animateState == ANIMATE_STATE_PRE_ANIMATE){
                animateState = ANIMATE_STATE_ANIMATING;
                //添加enterView
                LogUtils.d(TAG, "准备开始动画，添加enterView");
                layoutPreAnimNormal(recycler);
            }else if(animateState == ANIMATE_STATE_IDLE){
                //layout 稳定态
                LogUtils.d(TAG, "动画结束，回归正常状态");
                layoutNormal(recycler);
            }
        }else{
            if(animateState == ANIMATE_STATE_PRE_ANIMATE){
                animateState = ANIMATE_STATE_ANIMATING;
                //添加enterView
                LogUtils.d(TAG, "准备开始动画，添加enterView");
                layoutPreAnimInite(recycler);
            }else if(animateState == ANIMATE_STATE_IDLE){
//                //layout 稳定态
                LogUtils.d(TAG, "动画结束，回归正常状态");
                layoutInfinite(recycler);
            }
        }
    }

    private void layoutPreAnimNormal(RecyclerView.Recycler recycler) {

        int firstPosition = getCurrentPosition();

        log("adding pos" + firstPosition);
        View v = recycler.getViewForPosition(firstPosition);
        addView(v, 0);
        measureChild(v);
        layoutChildPosition(v, firstPosition, 1, 2, true, 0);
        needRecycleCache.add(firstPosition);
        enterView = v;

        mainHandler.postDelayed(()->{
            if(enterView != null){
                LogUtils.d(TAG, "开始播放进入动画");
                onEnterAnimatorPlay(enterView);
            }
            if(exitView != null){
                LogUtils.d(TAG, "开始播放退出动画");
                onExitAnimatorPlay(exitView);
            }

        }, HACK_PAGE_DELAY + 100);
        //触发downstage后，截图需要时间


    }

    @Override
    protected void layoutNormal(RecyclerView.Recycler recycler) {
        int firstPosition = getCurrentPosition();

        log("now pos" + firstPosition);

        for(int i = 0; i < getItemCount(); i++){
            if(i != firstPosition){
                if(recycleIndex(i)){
                    View rec = recycler.getViewForPosition(i);
                    removeAndRecycleView(rec, recycler);
                }
            }
        }

        detachAndScrapAttachedViews(recycler);

        View v = recycler.getViewForPosition(firstPosition);
        addView(v);
        measureChild(v);
        layoutChildPosition(v, firstPosition, 1, 0, true, 0);
        needRecycleCache.add(firstPosition);

        exitView = v;
    }

    private void layoutPreAnimInite(RecyclerView.Recycler recycler) {

        int firstPosition = getCurrentPosition();

        log("adding pos" + firstPosition);

        View v = recycler.getViewForPosition(firstPosition);
        addView(v, 0);
        measureChild(v);
        layoutChildPosition(v, firstPosition, 1, 0, true, 0);
        needRecycleCache.add(firstPosition);

        enterView = v;

        mainHandler.postDelayed(()->{

            if(enterView != null){
                LogUtils.d(TAG, "开始播放进入动画");
                onEnterAnimatorPlay(enterView);
            }

            if(exitView != null){
                LogUtils.d(TAG, "开始播放退出动画");
                onExitAnimatorPlay(exitView);
            }

        }, HACK_PAGE_DELAY + 100);
    }

    @Override
    protected void layoutInfinite(RecyclerView.Recycler recycler){

        int firstPosition = getCurrentPosition();

        log("now pos" + firstPosition);

        if(firstPosition < getItemCount()){
            for(int i = 0; i < getItemCount(); i++){
                if(firstPosition != i){
                    if(recycleIndex(i)){
                        View rec = recycler.getViewForPosition(i);
                        removeAndRecycleView(rec, recycler);
                    }
                }
            }
        }else{
            int realLast = firstPosition - getItemCount();
            for(int i = 0; i < getItemCount(); i++){
                if(realLast != i){
                    if(recycleIndex(i)){
                        View rec = recycler.getViewForPosition(i);
                        removeAndRecycleView(rec, recycler);
                    }
                }
            }
        }

        detachAndScrapAttachedViews(recycler);

        View v = recycler.getViewForPosition(firstPosition);
        addView(v);
        measureChild(v);
        layoutChildPosition(v, firstPosition, 1, 0, true, 0);
        needRecycleCache.add(firstPosition);

        exitView = v;

    }

    @Override
    protected void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {
        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;
        layoutDecoratedWithMargins(v, anchorLeft, anchorTop, anchorLeft + getItemWidth(), anchorTop + getItemHeight());
    }

    protected int increasingState(){
        return increasing;
    }

    protected abstract void onExitAnimatorPlay(View view);

    protected abstract void onEnterAnimatorPlay(View view);

    protected void onAnimationComplete(){
        animateState = ANIMATE_STATE_IDLE;
        requestLayout();
    }

    protected View getEnterView(){
        return enterView;
    }

    protected View getExitView(){
        return exitView;
    }

    static class SavedState implements Parcelable {
        int offset;
        boolean infinite;

        public SavedState() { }

        public SavedState(SavedState savedState) {
            this.infinite = savedState.infinite;
            this.offset = savedState.offset;
        }

        protected SavedState(Parcel in) {
            offset = in.readInt();
            infinite = in.readByte() != 0;
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(offset);
            parcel.writeByte((byte) (infinite ? 1 : 0));
        }
    }

    @Override
    public void autoDispose() {
        destroyResources();
    }

    protected void destroyResources() { }

}