package com.sloth.functions.layoutmanager.base;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.functions.log.LogUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/2 14:04
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/2         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AbstractStackLayoutManager extends AbstractVirtualScrollLayoutManager {

    private static final String TAG = AbstractStackLayoutManager.class.getSimpleName();

    private int itemOffset = 20;

    private int maxVisibleItemCount = 4;

    private float itemScale = 0.9f;

    private SavedState mPendingSavedState;

    public AbstractStackLayoutManager(Context context) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
    }

    public AbstractStackLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public AbstractStackLayoutManager(Context context, int itemOffset, int maxVisibleItemCount, float itemScale) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
        this.itemOffset = itemOffset;
        this.maxVisibleItemCount = maxVisibleItemCount;
        this.itemScale = itemScale;
    }

    public AbstractStackLayoutManager(Context context, int orientation, boolean reverseLayout, int itemOffset, int maxVisibleItemCount, float itemScale) {
        super(context, orientation, reverseLayout);
        this.itemOffset = itemOffset;
        this.maxVisibleItemCount = maxVisibleItemCount;
        this.itemScale = itemScale;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (mPendingSavedState != null) {
            return new SavedState(mPendingSavedState);
        }
        SavedState savedState = new SavedState();
        savedState.offset = scrollOffset;
        savedState.infinite = infinite;
        savedState.itemScale = itemScale;
        savedState.maxVisibleItems = maxVisibleItemCount;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            mPendingSavedState = new SavedState((SavedState) state);
            requestLayout();
        }
    }

    private void adjustVisibleItems(){
        int max = getItemCount();
        if(maxVisibleItemCount > max){
            LogUtils.e(TAG, "需要显示的个数多余置入的个数，降低显示个数为:" + max);
            maxVisibleItemCount = max;
        }
    }

    @Override
    protected void layoutNormal(RecyclerView.Recycler recycler) {
        adjustVisibleItems();
        int firstPosition = (int) Math.floor(scrollOffset / getScrollItemSize());
        firstPosition = Math.max(firstPosition, 0);

        int lastPosition = firstPosition + maxVisibleItemCount - 1;
        lastPosition = Math.min(lastPosition, getItemCount() - 1);

        for(int i = 0; i < getItemCount(); i++){
            if(i < firstPosition || i > lastPosition ){
                if(recycleIndex(i)){
                    View rec = recycler.getViewForPosition(i);
                    removeAndRecycleView(rec, recycler);
                }
            }
        }

        detachAndScrapAttachedViews(recycler);

        float percent = getPercent();
        int renderCount = lastPosition - firstPosition;

        //真实可见元素
        for(int i = lastPosition, index = renderCount; i >= firstPosition; i--,index--){
            View v = recycler.getViewForPosition(i);
            addView(v);
            measureChild(v);
            layoutChildPosition(v, i, renderCount, index, i == firstPosition, percent);
            needRecycleCache.add(i);
        }
    }

    @Override
    protected void layoutInfinite(RecyclerView.Recycler recycler) {
        adjustVisibleItems();
        int maxOffset = getItemCount() * getScrollItemSize();

        int firstPosition = 0;
        if(scrollOffset >= 0){
            firstPosition = (int) Math.floor(scrollOffset / getScrollItemSize());
        }else{
            int ancho = (int) Math.floor((maxOffset + scrollOffset) / getScrollItemSize());
            firstPosition = (scrollOffset % getScrollItemSize() == 0 ? ancho - 1 : ancho);
        }

        int lastPosition = firstPosition + maxVisibleItemCount - 1;

        if(lastPosition < getItemCount()){
            for(int i = 0; i < getItemCount(); i++){
                if(i < firstPosition || i > lastPosition){
                    if(recycleIndex(i)){
                        View rec = recycler.getViewForPosition(i);
                        removeAndRecycleView(rec, recycler);
                    }
                }
            }
        }else{
            int realLast = lastPosition - getItemCount();
            for(int i = 0; i < getItemCount(); i++){
                if(i > realLast && i < firstPosition){
                    if(recycleIndex(i)){
                        View rec = recycler.getViewForPosition(i);
                        removeAndRecycleView(rec, recycler);
                    }
                }
            }
        }

        detachAndScrapAttachedViews(recycler);

        float percent = getPercent();
        int renderCount = lastPosition - firstPosition;

        for(int i = lastPosition, index = renderCount; i >= firstPosition; i--,index--){
            int realPosition = i % getItemCount();
            View v = recycler.getViewForPosition(realPosition);
            addView(v);
            measureChild(v);
            layoutChildPosition(v, realPosition, renderCount, index, index == 0, percent);
            needRecycleCache.add(i);
        }
    }

    public int getItemOffset() {
        return itemOffset;
    }

    public void setItemOffset(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    public int getMaxVisibleItemCount() {
        return maxVisibleItemCount;
    }

    public void setMaxVisibleItemCount(int maxVisibleItemCount) {
        this.maxVisibleItemCount = maxVisibleItemCount;
    }

    public float getItemScale() {
        return itemScale;
    }

    public void setItemScale(float itemScale) {
        this.itemScale = itemScale;
    }

    static class SavedState implements Parcelable {
        int offset;
        boolean infinite;
        int maxVisibleItems;
        float itemScale;

        public SavedState() { }

        public SavedState(SavedState savedState) {
            this.offset = savedState.offset;
            this.infinite = savedState.infinite;
            this.maxVisibleItems = savedState.maxVisibleItems;
            this.itemScale = savedState.itemScale;
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
            parcel.writeInt(maxVisibleItems);
            parcel.writeFloat(itemScale);
        }
    }

}
