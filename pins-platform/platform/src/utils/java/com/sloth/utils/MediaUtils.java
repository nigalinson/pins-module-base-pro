package com.sloth.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class MediaUtils {

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

    public static long getMediaDuration(String filePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        long duration = 0;
        try {
            retriever.setDataSource(filePath);
            duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return duration;
    }

    /**
     * 获取最近的关键帧截图
     * [ps: 如果源视频关键帧比较少，实际截出来的图片跟预想的差距会比较大]
     * @param filePath
     * @return
     */
    public static Bitmap getMediaSnapshot(String filePath) {
        return getMediaSnapshot(filePath, -1);
    }

    /**
     * 获取最近的关键帧截图
     * [ps: 如果源视频关键帧比较少，实际截出来的图片跟预想的差距会比较大]
     * @param filePath 想要截取的目标时间点
     * @return
     */
    public static Bitmap getMediaSnapshot(String filePath, long ms) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap res = null;
        try {
            retriever.setDataSource(filePath);
            res = retriever.getFrameAtTime(ms, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return res;
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

}
