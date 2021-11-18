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
 * Date:      2020/12/2 14:04
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/2         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AbstractGalleryLayoutManager extends AbstractVirtualScrollLayoutManager {

    private static final String TAG = "GalleryLayoutManager";

    private int frontCount = 1;

    private int afterCount = 1;

    private SavedState mPendingSavedState;

    public AbstractGalleryLayoutManager(Context context) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
    }

    public AbstractGalleryLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setFrontCount(int frontCount) {
        this.frontCount = frontCount;
    }

    public void setAfterCount(int afterCount) {
        this.afterCount = afterCount;
    }

    public int getFrontCount() {
        return frontCount;
    }

    public int getAfterCount() {
        return afterCount;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (mPendingSavedState != null) {
            return new SavedState(mPendingSavedState);
        }
        SavedState savedState = new SavedState();
        savedState.offset = scrollOffset;
        savedState.infinite = infinite;
        savedState.frontCount = frontCount;
        savedState.afterCount = afterCount;
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
        int center = (int) Math.floor(scrollOffset / getScrollItemSize());
        center = Math.max(center, 0);

        int front = center - frontCount;
        int after = center + afterCount;

        front = Math.max(front, 0);
        after = Math.min(after, getItemCount() - 1);

        for(int i = 0; i < getItemCount(); i++){
            if(i < front || i > after ){
                if(recycleIndex(i)){
                    View rec = recycler.getViewForPosition(i);
                    removeAndRecycleView(rec, recycler);
                }
            }
        }

        detachAndScrapAttachedViews(recycler);

        float percent = getPercent();
        int renderCount = after - front;

        for(int i = after, index = after - center; i >= front; i--,index--){

            //跳过中间 - 最后layout
            if(index == 0){
                continue;
            }

            View v = recycler.getViewForPosition(i);
            addView(v);
            measureChild(v);
            layoutChildPosition(v, i, renderCount, index, false, percent);
            needRecycleCache.add(i);
        }

        //中间
        View v = recycler.getViewForPosition(center);
        addView(v);
        measureChild(v);
        layoutChildPosition(v, center, renderCount, 0, true, percent);
        needRecycleCache.add(center);
    }

    @Override
    protected void layoutInfinite(RecyclerView.Recycler recycler){
        int maxOffset = getItemCount() * getScrollItemSize();

        int center = 0;
        if(scrollOffset >= 0){
            center = (int) Math.floor(scrollOffset / getScrollItemSize());
        }else{
            int ancho = (int) Math.floor((maxOffset + scrollOffset) / getScrollItemSize());
            center = (scrollOffset % getScrollItemSize() == 0 ? ancho - 1 : ancho);
        }

        if(frontCount + afterCount + 1 > getItemCount()){
            //不够分配，手动调节
            int calCenter = (getPercent() <= 0.5f) ? center : (center + 1);

            while(true){
                //剩余item不足以渲染，需要手动调节前后个数

                if(frontCount > 0){
                    frontCount -= 1;
                    if(frontCount + afterCount + 1 <= getItemCount()){
                        //前面减一个以后可以了，退出
                        break;
                    }
                }
                if(afterCount > 0){
                    afterCount-=1;
                    if(frontCount + afterCount + 1 <= getItemCount()){
                        //后面减一个以后可以了，退出
                        break;
                    }
                }
            }
        }

        int front = center - frontCount;
        int after = center + afterCount;

        if(front < 0){
            front = getItemCount() + front;
            center = front + frontCount;
            after = center + afterCount;
        }

        if(after < getItemCount()){
            for(int i = 0; i < getItemCount(); i++){
                if(i < front || i > after){
                    if(recycleIndex(i)){
                        View rec = recycler.getViewForPosition(i);
                        removeAndRecycleView(rec, recycler);
                    }
                }
            }
        }else{
            int realLast = after - getItemCount();
            for(int i = 0; i < getItemCount(); i++){
                if(i > realLast && i < front){
                    if(recycleIndex(i)){
                        View rec = recycler.getViewForPosition(i);
                        removeAndRecycleView(rec, recycler);
                    }
                }
            }
        }

        detachAndScrapAttachedViews(recycler);

        float percent = getPercent();
        int renderCount = after - front;

        for(int i = after, index = after - center; i >= front; i--,index--){
            //跳过中间 - 最后layout
            if(index == 0){
                continue;
            }

            int realPosition = i % getItemCount();
            View v = recycler.getViewForPosition(realPosition);
            addView(v);
            measureChild(v);
            layoutChildPosition(v, realPosition, renderCount, index, false, percent);
            needRecycleCache.add(realPosition);
        }

        //中间
        int realPosition = center % getItemCount();
        View v = recycler.getViewForPosition(realPosition);
        addView(v);
        measureChild(v);
        layoutChildPosition(v, realPosition, renderCount, 0, true, percent);
        needRecycleCache.add(realPosition);

    }

    static class SavedState implements Parcelable {
        int offset;
        boolean infinite;
        int frontCount;
        int afterCount;
        float itemScale;

        public SavedState() { }

        public SavedState(SavedState savedState) {
            this.offset = savedState.offset;
            this.infinite = savedState.infinite;
            this.frontCount = savedState.frontCount;
            this.afterCount = savedState.afterCount;
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
            parcel.writeInt(frontCount);
            parcel.writeInt(afterCount);
            parcel.writeFloat(itemScale);
        }
    }

}
