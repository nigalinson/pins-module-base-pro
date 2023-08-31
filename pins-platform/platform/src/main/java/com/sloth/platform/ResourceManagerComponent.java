package com.sloth.platform;

import java.util.List;

public interface ResourceManagerComponent {

    void addGlobalListener(ResourceListener resourceListener);

    void removeGlobalListener(ResourceListener resourceListener);

    void clearGlobalListener();

    Request get(String url);

    void submit(Request request);

    void queryUrl(String url, ResourceListener resourceListener);

    void cancel(String url);

    void cancelLocalPath(String localName);

    void cancelAll();

    void removeAllListeners();

    void removeListener(ResourceListener resourceListener);

    void removeListeners(Long id);

    void clearResource(ClearRequest clearRequest, ClearListener clearListener);

    void close();

    interface Request {
        Request setPath(String path);
        Request setMd5(String md5);
        Request setGroup(String group);
        Request setAdditionInfo(String additionInfo);
        Request setMaxHotness(boolean maxHotness);
        Request submit(ResourceListener resourceListener);

        void setId(Long id);
        Long getId();
        String url();
        String path();
        String md5();
        String group();
        String additionInfo();
        boolean maxHotness();
        ResourceListener getListener();

        void cancel();
        void detach();
    }

    interface ResourceManagerConfig {

        String dbPath();

        /**
         * 资源存放的基础路径
         * @return
         */
        String baseFolder();

        /**
         * 如果常规的清理方式无法达标，是否将所有内容删除来满足存储阈值需求
         * @return
         */
        boolean madClear();

        /**
         * 触发清理的阈值大小(bytes)
         * @return
         */
        long clearThresholdSize();

        /**
         * 清理到多少大小(bytes)，结束清理
         * @return
         */
        long clearUntilSize();

        /**
         * 每次清理几个文件
         * @return
         */
        int clearFileNumsEveryTime();

        /**
         * 是否重命名文件(MD5)
         * @return
         */
        boolean hashFileName();

        /**
         * 是否携带后缀
         * @return
         */
        boolean withSuffix();

        /**
         * 下载并发量
         * @return
         */
        int concurrent();

        /**
         * 下载失败重试次数
         * @return
         */
        int retryTimes();

        /**
         * 下载引擎
         * @return
         */
        DownloadComponent downloadEngine();
    }

    interface ResourceListener {
        void onResourceReady(Long resourceId, String url, String localPath);
        void onResourceFailed(Long resourceId, String url, String localPath, String errMsg);
    }

    interface ClearRequest {
        int byHotness();
        List<String> byAdditions();
        List<String> excepts();
    }

    interface ClearListener {
        void clearSuccess(int clearFileCounts);
        void clearFailed(String errMsg);
    }

}
