package com.sloth.pinsdemo.banner;

import com.sloth.banner.data.Playable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/19 16:59
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class BannerItem implements Playable {

    private int type;
    private String url;
    private int resId;

    public BannerItem(int resId) {
        this.resId = resId;
        this.type = MediaType.Image.type;
    }


    public BannerItem(String url) {
        this.url = url;
        this.type = ((null != url && url.endsWith("mp4")) ? MediaType.Video.type : MediaType.Image.type);
    }

    public BannerItem(int type, String url) {
        this.type = type;
        this.url = url;
    }

    public BannerItem(int type, int resId) {
        this.type = type;
        this.resId = resId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public int mediaType() {
        return type;
    }

    @Override
    public String localPath() {
        return url;
    }

    @Override
    public String webUrl() {
        return null;
    }

    @Override
    public int resId() {
        return resId;
    }

    @Override
    public long playDuration() {
        return 0;
    }
}
