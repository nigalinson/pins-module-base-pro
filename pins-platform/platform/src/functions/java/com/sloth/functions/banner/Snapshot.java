package com.sloth.functions.banner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import com.sloth.pinsplatform.common.functions.storage.RYFileHelper;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.LogUtils;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/1/11 11:55
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/1/11         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Snapshot {
    private static final String TAG = Snapshot.class.getSimpleName();

    public static class MediaInfo {
        private String width;
        private String height;
        private String rotation;
        private long duration;

        public MediaInfo(String width, String height, String rotation, long duration) {
            this.width = width;
            this.height = height;
            this.rotation = rotation;
            this.duration = duration;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getRotation() {
            return rotation;
        }

        public void setRotation(String rotation) {
            this.rotation = rotation;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }
    }

    public static MediaInfo getVideoInfo(String path){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try{
            retriever.setDataSource(path);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); //宽
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); //高
            String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);//视频的方向角度
            long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));//视频的长度
            return new MediaInfo(width, height, rotation, duration);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            retriever.release();
        }
        return null;
    }

    private static final class SnapShotHolder{
        private static final SnapshotCache snapshotCache = new SnapshotCache();
    }

    public static SnapshotCache snapshotCache(){
        return SnapShotHolder.snapshotCache;
    }

    public static class SnapshotCache {

        private final List<String> writing = new ArrayList<>();

        public android.database.Observable<Bitmap> snapshot(String localVideoPath){
            return snapshot(localVideoPath, true);
        }


        public android.database.Observable<Bitmap> snapshot(String localVideoPath, boolean ifHasFreeMem) {
            return snapshot(localVideoPath, ifHasFreeMem, 0.3f);
        }

        /**
         * 保存视频截图到本地
         * @param localVideoPath 视频地址
         * @param ifHasFreeMem 内存是否空闲
         */
        public android.database.Observable<Bitmap> snapshot(String localVideoPath, boolean ifHasFreeMem, float mixedQuality) {

            return Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
                if(ifHasFreeMem){
                    //只在内存空闲时缓存图片
                    int freeMem = RYDeviceUtils.getFreeMemory();
                    LogUtils.d(TAG, "空闲内存：" + freeMem);
                    if( freeMem < 20){
                        emitter.onError(new RYApiException("空闲内存不足20M，不显示预览图"));
                    }
                }

                String cacheFile = cacheFileName(localVideoPath);
                LogUtils.d(TAG, "preview file name:" + cacheFile);
                if(RYFileHelper.isFileExist(cacheFile)){
                    //文件已存在
                    boolean isWriting = false;
                    synchronized (writing){
                        if(writing.contains(localVideoPath)){
                            isWriting = true;
                        }
                    }
                    if(isWriting){
                        //文件存在，但不完整，正在写入
                        emitter.onError(new RYApiException("封面生成中"));
                    }else{
                        //文件存在，且不是写入中状态 - 可以加载
                        LogUtils.d(TAG, "封面已准备好，加载中");
                        emitter.onNext(BitmapFactory.decodeFile(cacheFile));
                        emitter.onComplete();
                    }
                }else{
                    //文件不存在
                    boolean isWaiting = false;
                    synchronized (writing){
                        if(writing.contains(localVideoPath)){
                            isWaiting = true;
                        }
                    }

                    if(!isWaiting){
                        writing.add(localVideoPath);
                        //任务不存在，开始生成并写到本地
                        Bitmap b = FileUtils.BitmapUtils.compress(RYFileHelper.getFfmpegMediaSnapshot(localVideoPath), mixedQuality, mixedQuality);
                        if(b == null){
                            writing.remove(localVideoPath);
                            emitter.onError(new RYApiException("获取封面失败"));
                        }else{
                            emitter.onNext(b);
                            FileOutputStream opStream = null;
                            try{
                                opStream = new FileOutputStream(cacheFile);
                                b.compress(Bitmap.CompressFormat.JPEG, 100, opStream);
                                opStream.flush();
                                opStream.close();
                            }catch(Exception e){
                                e.printStackTrace();
                                LogUtils.e(TAG, e.getMessage());
                            }finally {
                                if(opStream != null){
                                    opStream.close();
                                }
                            }
                            writing.remove(localVideoPath);
                            emitter.onComplete();
                        }
                    }else{
                        //任务已存在，重复任务，跳过本次
                        emitter.onError(new RYApiException("封面正在等待生成"));
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }

        private static String cacheFileName(String localVideoPath) {
            String fileName = FZStringHelper.getFileName(localVideoPath) + ".jpg";
            return AppDirectoryConstant.APP_LOCAL_RESOURCE_PICTURE_DIRECTORY + fileName;
        }

    }

}
