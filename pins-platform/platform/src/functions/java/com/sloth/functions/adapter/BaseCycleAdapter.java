package com.sloth.functions.adapter;

import android.content.Context;

/**
 * Author:    CaoKang
 * Version    V1.0
 * Date:      2017/8/18
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/8/18      CaoKang            1.0                    1.0
 * Why & What is modified:
 */
public abstract class BaseCycleAdapter<VH extends RYBaseViewHolder<T>, T> extends RYBaseAdapter<VH, T> {
    private boolean isCycle = false;
    public static final int MaxCount = Short.MAX_VALUE;

    public void setCycle(boolean cycle) {
        isCycle = cycle;
    }

    public BaseCycleAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        if (isCycle) {
            return MaxCount;
        } else {
            return super.getItemCount();
        }
    }

    @Override
    public T getItemData(int position) {
        return super.getItemData(getCurrentIndex(position));
    }

    public int setCurrentIndex(int index) {
        int halfCount = MaxCount / 2;
        int dataCount = getDataCount();
        halfCount = halfCount / dataCount;
        halfCount = halfCount * dataCount;
        halfCount += index;
        return halfCount;
    }

    public int getCurrentIndex(int index) {
        if (getDataCount() == 0) {
            return 0;
        }
        return index % getDataCount();
    }
}
