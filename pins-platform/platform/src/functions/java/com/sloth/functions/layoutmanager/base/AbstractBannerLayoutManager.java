package com.sloth.functions.layoutmanager.base;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
public abstract class AbstractBannerLayoutManager extends AbstractVirtualScrollLayoutManager{

    private SavedState mPendingSavedState;

    public AbstractBannerLayoutManager(Context context) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
    }

    public AbstractBannerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
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
    protected void layoutNormal(RecyclerView.Recycler recycler) {
        int firstPosition = (int) Math.floor(scrollOffset / getScrollItemSize());
        firstPosition = Math.max(firstPosition, 0);

        int lastPosition = firstPosition + 1;
        lastPosition = Math.min(lastPosition, getItemCount() - 1);

        for(int i = 0; i < getItemCount(); i++){
            if(i < firstPosition || i > lastPosition){
                if(recycleIndex(i)){
                    View rec = recycler.getViewForPosition(i);
                    removeAndRecycleView(rec, recycler);
                }
            }
        }

        detachAndScrapAttachedViews(recycler);

        float percent = getPercent();
        int renderCount = lastPosition - firstPosition;

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
        int maxOffset = getItemCount() * getScrollItemSize();

        int firstPosition = 0;
        if(scrollOffset >= 0){
            firstPosition = (int) Math.floor(scrollOffset / getScrollItemSize());
        }else{
            int ancho = (int) Math.floor((maxOffset + scrollOffset) / getScrollItemSize());
            firstPosition = (scrollOffset % getScrollItemSize() == 0 ? ancho - 1 : ancho);
        }

        int lastPosition = firstPosition + 1;

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
            layoutChildPosition(v, realPosition, renderCount, index, i == firstPosition, percent);
            needRecycleCache.add(i);
        }
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

}