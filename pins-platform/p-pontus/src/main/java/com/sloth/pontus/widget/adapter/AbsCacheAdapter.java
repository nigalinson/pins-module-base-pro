package com.sloth.pontus.widget.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import com.sloth.widget.adapter.BaseAdapter;

public abstract class AbsCacheAdapter<T> extends BaseAdapter<AbsCacheViewHolder<T>, T> {

    public AbsCacheAdapter(Context context) {
        super(context);
    }

    protected boolean isImportResource(){
        return false;
    }

    @Override
    public void onViewRecycled(@NonNull AbsCacheViewHolder<T> holder) {
        System.out.println("onViewRecycled:" + holder.getClass().getSimpleName() + "," + holder.hashCode());
        holder.detach();
        super.onViewRecycled(holder);
    }

}
