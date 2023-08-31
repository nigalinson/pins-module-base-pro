package com.sloth.pontus.widget.adapter;

import android.view.View;
import com.sloth.platform.Platform;
import com.sloth.platform.ResourceManagerComponent;

public abstract class AbsRsViewHolder<T> extends AbsCacheViewHolder<T> {

    private final ResourceManagerComponent.ResourceListener resourceListener = new ResourceManagerComponent.ResourceListener() {
        @Override
        public void onResourceReady(Long resourceId, String url, String localPath) {
            onDownloadResourceSuccess(url, localPath);
        }

        @Override
        public void onResourceFailed(Long resourceId, String url, String localPath, String errMsg) {
            onDownloadResourceFailed(url, localPath);
        }
    };

    public AbsRsViewHolder(View itemView) {
        super(itemView);
    }

    public void download(String url){
        Platform.resourceManager().get(url)
                .submit(resourceListener);
    }

    public void download(String url, String local){
        Platform.resourceManager().get(url)
                .setPath(local)
                .submit(resourceListener);
    }

    protected abstract void onDownloadResourceSuccess(String url, String localPath);

    protected abstract void onDownloadResourceFailed(String url, String localPath);

    @Override
    public void detach() {
        Platform.resourceManager().removeListener(resourceListener);
    }
}
