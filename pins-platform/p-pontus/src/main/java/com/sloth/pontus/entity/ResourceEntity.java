package com.sloth.pontus.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class ResourceEntity {

    @Id
    @Unique
    private Long id;

    /**
     * 源路径
     */
    @Unique
    private String originUrl;

    /**
     * 本地缓存路径
     */
    private String localPath;

    /**
     * 资源MD5，用于校验数据
     */
    private String md5;

    /**
     * 资源是否就绪
     */
    @ResourceState
    private int state;

    /**
     * 资源热度
     */
    private long hotness;

    /**
     * 组信息
     */
    @Property(nameInDb = "RS_GROUP")
    private String group;

    /**
     * 附加信息
     */
    private String additionInfo;

    /**
     * 最后调用时间
     */
    private long updateTime;

    @Generated(hash = 1253578399)
    public ResourceEntity(Long id, String originUrl, String localPath, String md5,
            int state, long hotness, String group, String additionInfo,
            long updateTime) {
        this.id = id;
        this.originUrl = originUrl;
        this.localPath = localPath;
        this.md5 = md5;
        this.state = state;
        this.hotness = hotness;
        this.group = group;
        this.additionInfo = additionInfo;
        this.updateTime = updateTime;
    }

    @Generated(hash = 1399301811)
    public ResourceEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    @ResourceState
    public int getState() {
        return state;
    }

    public void setState(@ResourceState int state) {
        this.state = state;
    }

    public long getHotness() {
        return hotness;
    }

    public void setHotness(long hotness) {
        this.hotness = hotness;
    }

    @Keep
    public long increaseHotness() {
        if(hotness < Integer.MAX_VALUE){
            this.hotness++;
        }
        return this.hotness;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(String additionInfo) {
        this.additionInfo = additionInfo;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }


}
