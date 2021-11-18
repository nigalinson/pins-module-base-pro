package com.sloth.functions.layoutmanager.base;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.functions.log.LogUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/2/3 18:59
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/2/3         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AbstractVirtualScrollLayoutManager extends LinearLayoutManager implements PositionScrollable, VirtualScroll {

    private static final String TAG = "AbstractVirtualScrollLayoutManager";

    protected int scrollOffset = 0;

    protected int[] itemSize = new int[]{ -99, -99 };

    protected boolean infinite = false;

    protected Set<Integer> needRecycleCache = new HashSet<>();

    private boolean mRecycleChildrenOnDetach;

    private AbstractBannerLayoutManager.SavedState mPendingSavedState;

    public AbstractVirtualScrollLayoutManager(Context context) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
    }

    public AbstractVirtualScrollLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
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
            scrollOffset = scrollOffset % ( getItemCount() * itemSize[orientationIndex] );
        }

        layoutChildDetectAvailable(recycler, state);

        return !isInfinite() ? (scrollOffset - pendingOffset + scrollDistance) : scrollDistance;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getScrollItemSize() == -99 || getItemCount() == 0 || itemSize[0] == 0 || itemSize[1] == 0){
            preMeasureItemSize(recycler, state, itemSize);
        }

        layoutChildDetectAvailable(recycler, state);
    }

    protected void layoutChildDetectAvailable(RecyclerView.Recycler recycler, RecyclerView.State state){
        try{
            layoutChild(recycler, state);
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            LogUtils.e(TAG, "布局内容快速变更，清除所有异常内容");
            scrollOffset = 0;
            removeAndRecycleAllViews(recycler);
            detachAndScrapAttachedViews(recycler);
        }
    }

    protected void layoutChild(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(getScrollItemSize() <= 0){
            //暂未初始化滚动大小，视为无效摆放
            return;
        }

        if(!isInfinite()){
            layoutNormal(recycler);
        }else{
            layoutInfinite(recycler);
        }
    }

    protected abstract void layoutNormal(RecyclerView.Recycler recycler);

    protected abstract void layoutInfinite(RecyclerView.Recycler recycler);

    protected boolean recycleIndex(int position) {
        if(needRecycleCache.contains(position)){
            needRecycleCache.remove(position);
            return true;
        }
        return false;
    }

    protected abstract void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent);

    protected void preMeasureItemSize(RecyclerView.Recycler recycler, RecyclerView.State state, int[] itemWidthSize) {
        if(state.getItemCount() == 0 || state.isPreLayout()) {return;}
        View v = recycler.getViewForPosition(0);
        measureChild(v);
        itemWidthSize[0] = v.getMeasuredWidth();
        itemWidthSize[1] = v.getMeasuredHeight();
    }

    protected void measureChild(View child) {
        measureChildWithMargins(child,0,0);
    }

    @Override
    public int getCurrentPosition() {
        int firstPosition = 0;
        if(scrollOffset >= 0){
            firstPosition = scrollOffset / getScrollItemSize();
        }else{
            int equivalent = getItemCount() * getScrollItemSize() + scrollOffset;
            firstPosition = equivalent / getScrollItemSize();
        }
        return firstPosition;
    }


    @Override
    public int aimingPosition() {
        int aiming = -1;
        int top = getCurrentPosition();
        float per = getPercent();
        if(per < 0.5f || per == 1.0f){
            aiming = top;
        }else{
            int next = getCurrentPosition() + 1;
            int total = getItemCount();
            if(next < total){
                aiming = next;
            }else{
                aiming = next - total;
            }
        }
        return aiming;
    }

    @Override
    public int distanceToTargetPosition(int targetPosition) {
        if(targetPosition == getCurrentPosition()){
            return 0;
        }

        return (targetPosition - getCurrentPosition()) * getScrollItemSize();
    }

    protected int getSpace(){
        return canScrollHorizontally() ? getHorizontalSpace() : getVerticalSpace();
    }

    /**
     * 获取RecyclerView的显示高度
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取RecyclerView的显示宽度
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public int getScrollOffset(){
        return scrollOffset;
    }

    @Override
    public int getScrollItemSize(){
        return itemSize[canScrollHorizontally() ? 0 : 1];
    }

    public boolean isInfinite() {
        return infinite && getItemCount() > 1;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public int getHighlightOffset(){
        return scrollOffset >= 0 ? (scrollOffset % getScrollItemSize()) : (getScrollItemSize() - Math.abs(scrollOffset % getScrollItemSize()));
    }

    public float getPercent(){
        return (float)getHighlightOffset() / getScrollItemSize();
    }

    @Override
    public float getScrollPercent() {
        return getPercent();
    }

    @Override
    public int getScrollItemOffset() {
        return getHighlightOffset();
    }

    @Override
    public int firstPos() {
        return getCurrentPosition();
    }

    public int getItemWidth(){
        return itemSize[0];
    }

    public int getItemHeight(){
        return itemSize[1];
    }

    public static boolean debug = true;

    protected void log(String msg){
        if(!debug){ return; }
        LogUtils.d(TAG, msg);
    }
}