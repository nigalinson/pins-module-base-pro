package com.sloth.functions.layoutmanager.base;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LifecycleObserver;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/3 14:02
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/3         Carl            1.0                    1.0
 * Why & What is modified:
 * rv滚动监听 - 包装成翻页监听
 */
public class RecyclerViewPageScrollListener extends RecyclerView.OnScrollListener implements LifecycleObserver {
    private final OnRecyclerViewPageChangeListener onPageChangeListener;
    private int oldPosition = -1;//防止同一Position多次触发

    private final Handler main = new Handler(Looper.getMainLooper());

    /**
     * 一些虚拟滑动的layoutmanager在完成滑动后，实际还没立即layout，所以无法获取viewHolder，
     * 因此需要一个延时，等待layout完毕后，再触发pageChange来获取viewholder
     */
    private long pageChangeHackDelay = 0;

    public RecyclerViewPageScrollListener( OnRecyclerViewPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (onPageChangeListener != null) {
            onPageChangeListener.onScrolled(recyclerView, dx, dy);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if(layoutManager == null){
            return;
        }

        int position = -1;

        //获取当前选中的itemView
        if(layoutManager instanceof PositionScrollable){
            position = ((PositionScrollable)layoutManager).aimingPosition();
        }else if(layoutManager instanceof LinearLayoutManager){
            int first = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
            int end = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
            position = first + ((end - first) / 2);
        }

        if(position == -1){
            return;
        }

        if (onPageChangeListener != null) {
            onPageChangeListener.onScrollStateChanged(recyclerView, newState);
            //newState == RecyclerView.SCROLL_STATE_IDLE 当滚动停止时触发防止在滚动过程中不停触发
            if (newState == RecyclerView.SCROLL_STATE_IDLE && oldPosition != position) {
                oldPosition = position;

                int finalPosition = position;
                onPageChangeListener.beforePageChanged(finalPosition);

                if(pageChangeHackDelay == 0L){
                    onPageChangeListener.onPageChanged(finalPosition);
                }else{
                    //触发下一页时，可能仍未完成layout,无法获取view，因此给个延时，后续重构
                    main.postDelayed(()-> onPageChangeListener.onPageChanged(finalPosition), pageChangeHackDelay);
                }

            }
        }
    }

    public void setPageHackDelay(long delay){
        this.pageChangeHackDelay = delay;
    }
}