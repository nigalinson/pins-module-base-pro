package com.sloth.pontus;

import androidx.annotation.NonNull;
import com.sloth.platform.Platform;
import com.sloth.platform.ResourceManagerComponent;
import com.sloth.pontus.dao.DaoSession;
import com.sloth.pontus.dao.ResourceEntityDao;
import com.sloth.pontus.db.ResourceManagerConnector;
import com.sloth.pontus.entity.ResourceEntity;
import com.sloth.pontus.entity.ResourceState;
import com.sloth.pontus.listener.DatabaseListener;
import com.sloth.rx.Obx;
import com.sloth.rx.Runner;
import com.sloth.rx.Rx;
import com.sloth.utils.EncryptUtils;
import com.sloth.utils.FileUtils;
import com.sloth.utils.StringUtils;
import com.sloth.utils.TimeUtils;
import com.sloth.utils.Utils;
import org.greenrobot.greendao.query.QueryBuilder;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2022/3/25 18:46
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2022/3/25         Carl            1.0                    1.0
 * Why & What is modified:
 */
class ResourceDB {

    private static final String TAG = ResourceDB.class.getSimpleName();

    private volatile ResourceManagerConnector resourceManagerDB;

    private final Object ATOM_LOCK = new Object();

    ResourceDB(String dbPath) {
        resourceManagerDB = new ResourceManagerConnector(Utils.getApp(), dbPath);
    }

    void close() {
        if (resourceManagerDB != null) {
            resourceManagerDB.closeConnection();
            resourceManagerDB = null;
        }
    }

    /**
     * 入库本地 已存在/不存在/部分下载 的文件
     * @param url 网络路径
     * @param localPath 本地路径
     * @param md5 本地路径
     * @param group 组
     * @param additionInfo 额外信息
     * @param maxHotness 是否直接满热度
     * @param databaseListener 回调监听
     */
    void getAndCreateIfNotExist(String url, String localPath, String md5, String group, String additionInfo, boolean maxHotness, DatabaseListener databaseListener) {
        Observable.create((ObservableOnSubscribe<ResourceEntity>) emitter -> {
            if (resourceManagerDB != null) {
                DaoSession daoSession = resourceManagerDB.getDaoSession();
                if (daoSession != null) {
                    synchronized (ATOM_LOCK){
                        List<ResourceEntity> result = daoSession.getResourceEntityDao().queryBuilder()
                                .where(ResourceEntityDao.Properties.OriginUrl.eq(url)).list();
                        if (result.size() > 0) {
                            ResourceEntity entity = result.get(0);

                            if (FileUtils.isFileExists(entity.getLocalPath())) {
                                //已经入库，且文件存在，按照数据库状态如实返回
                            }else{
                                //已经入库，但文件不存在，校准状态为 未就绪
                                Platform.log().d(TAG, entity.getId() + ":校准不存在");
                                entity.setState(ResourceState.STATE_INIT);
                            }
                            entity.setMd5(md5);
                            entity.setGroup(group);
                            entity.setAdditionInfo(additionInfo);
                            entity.setLocalPath(localPath);
                            if(maxHotness){
                                entity.setHotness(Integer.MAX_VALUE);
                            }else{
                                entity.increaseHotness();
                            }
                            entity.setUpdateTime(System.currentTimeMillis());
                            daoSession.insertOrReplace(entity);
                            emitter.onNext(entity);
                            emitter.onComplete();
                        } else {
                            ResourceEntity newEntity = new ResourceEntity();
                            newEntity.setOriginUrl(url);
                            newEntity.setLocalPath(localPath);
                            newEntity.setMd5(md5);
                            newEntity.setGroup(group);
                            newEntity.setAdditionInfo(additionInfo);
                            newEntity.setHotness(maxHotness ? Integer.MAX_VALUE : 1L);
                            if (FileUtils.isFileExists(localPath)) {
                                //尚未入库， 文件存在， 实际是外部导入数据，置为 就绪
                                if(StringUtils.notTrimEmpty(md5)){
                                    //存在MD5
                                    if(md5.equalsIgnoreCase(new String(EncryptUtils.encryptMD5File(localPath), StandardCharsets.UTF_8))){
                                        //MD5校验成功
                                        newEntity.setState(ResourceState.STATE_READY);
                                    }else{
                                        //MD5校验失败
                                        newEntity.setState(ResourceState.STATE_INIT);
                                    }
                                }else{
                                    //不存在MD5，不校验
                                    newEntity.setState(ResourceState.STATE_READY);
                                }
                            }else{
                                //尚未入库， 且不存在文件， 未就绪
                                newEntity.setState(ResourceState.STATE_INIT);
                            }
                            Long id = daoSession.insert(newEntity);
                            newEntity.setId(id);
                            newEntity.setUpdateTime(System.currentTimeMillis());
                            emitter.onNext(newEntity);
                            emitter.onComplete();
                        }
                    }
                }
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResourceEntity>() {
                    @Override
                    public void onNext(@NonNull ResourceEntity resourceEntity) {
                        if (databaseListener != null) {
                            databaseListener.querySuccess(resourceEntity);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Platform.log().e(TAG, throwable.getMessage());
                        if (databaseListener != null) {
                            databaseListener.queryFailed(throwable.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void getOnlyIfExist(String url, DatabaseListener databaseListener) {
        Observable.create((ObservableOnSubscribe<ResourceEntity>) emitter -> {
            if (resourceManagerDB != null) {
                DaoSession daoSession = resourceManagerDB.getDaoSession();
                if (daoSession != null) {
                    List<ResourceEntity> result = daoSession.getResourceEntityDao().queryBuilder()
                            .where(ResourceEntityDao.Properties.OriginUrl.eq(url)).list();
                    if (result.size() > 0) {
                        ResourceEntity entity = result.get(0);
                        emitter.onNext(entity);
                        emitter.onComplete();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResourceEntity>() {
                    @Override
                    public void onNext(@NonNull ResourceEntity resourceEntity) {
                        if (databaseListener != null) {
                            databaseListener.querySuccess(resourceEntity);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Platform.log().e(TAG, throwable.getMessage());
                        if (databaseListener != null) {
                            databaseListener.queryFailed(throwable.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void getByLocalOnlyIfExist(String local, DatabaseListener databaseListener) {
        Observable.create((ObservableOnSubscribe<ResourceEntity>) emitter -> {
            if (resourceManagerDB != null) {
                DaoSession daoSession = resourceManagerDB.getDaoSession();
                if (daoSession != null) {
                    List<ResourceEntity> result = daoSession.getResourceEntityDao().queryBuilder()
                            .where(ResourceEntityDao.Properties.LocalPath.eq(local)).list();
                    if (result.size() > 0) {
                        ResourceEntity entity = result.get(0);
                        emitter.onNext(entity);
                        emitter.onComplete();
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResourceEntity>() {
                    @Override
                    public void onNext(@NonNull ResourceEntity resourceEntity) {
                        if (databaseListener != null) {
                            databaseListener.querySuccess(resourceEntity);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Platform.log().e(TAG, throwable.getMessage());
                        if (databaseListener != null) {
                            databaseListener.queryFailed(throwable.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    void size(Long id, int size) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            if (resourceManagerDB != null) {
                DaoSession daoSession = resourceManagerDB.getDaoSession();
                if (daoSession != null) {
                    List<ResourceEntity> result = daoSession.getResourceEntityDao().queryBuilder()
                            .where(ResourceEntityDao.Properties.Id.eq(id)).list();
                    if (result.size() > 0) {
                        ResourceEntity entity = result.get(0);
                        daoSession.getResourceEntityDao().insertOrReplaceInTx(entity);
                    }
                }
            }
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    void updateState(Long id, @ResourceState int state) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                if (resourceManagerDB != null) {
                    DaoSession daoSession = resourceManagerDB.getDaoSession();
                    if (daoSession != null) {
                        List<ResourceEntity> result = daoSession.getResourceEntityDao().queryBuilder()
                                .where(ResourceEntityDao.Properties.Id.eq(id)).list();
                        if (result.size() > 0) {
                            ResourceEntity entity = result.get(0);
                            if (state == ResourceState.STATE_READY && !FileUtils.isFileExists(entity.getLocalPath())) {
                                //如果状态是ready，但实际文件不存在，校准状态
                                Platform.log().d(TAG, entity.getId() + ":校准状态");
                                entity.setState(ResourceState.STATE_INIT);
                            } else {
                                entity.setState(state);
                            }
                            daoSession.getResourceEntityDao().insertOrReplaceInTx(entity);
                        }
                    }
                }
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    void removeUntil(ResourceManagerComponent.ClearRequest clearRequest,
                     ResourceManagerComponent.ResourceManagerConfig config,
                     ResourceManagerComponent.ClearListener clearListener) {
        AtomicInteger clearCounts = new AtomicInteger(0);
        Rx.ui(() -> {
            if (resourceManagerDB != null) {
                DaoSession daoSession = resourceManagerDB.getDaoSession();
                if (daoSession != null) {
                    File folder = new File(config.baseFolder());
                    long resourceSize = FileUtils.getLength(folder);

                    if (resourceSize > config.clearThresholdSize()) {
                        Platform.log().d(TAG, "当前目录大小：" + (resourceSize / 1024 / 1204) + " MB，循环开始每次清理" + config.clearFileNumsEveryTime() + "个资源");
                        warmClear(daoSession, clearRequest, config, clearCounts);
                    } else {
                        Platform.log().d(TAG, "当前目录大小：" + (resourceSize / 1024 / 1204) + " MB，清理阈值:"
                                + (config.clearThresholdSize() / 1024 / 1204) +" MB，暂无需清理缓存");
                    }
                }
            }

        }).subscribe(new Obx<Boolean>() {

            @Override
            protected void onExe(Boolean aBoolean) {
                super.onExe(aBoolean);
                if(clearListener != null){
                    clearListener.clearSuccess(clearCounts.get());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if(clearListener != null){
                    clearListener.clearFailed(e.getMessage());
                }
            }
        });
    }

    private void warmClear(DaoSession daoSession,
                           ResourceManagerComponent.ClearRequest clearRequest,
                           ResourceManagerComponent.ResourceManagerConfig config,
                           AtomicInteger clearCounts) {
        boolean success = false;
        while (true) {
            Platform.log().d(TAG, "开始温和清理");
            long timeBorder = TimeUtils.getNowMills() - (2 * 24 * 3600 * 1000);
            QueryBuilder<ResourceEntity> queryBuilder = daoSession.getResourceEntityDao().queryBuilder();
            queryBuilder.where(ResourceEntityDao.Properties.State.eq(ResourceState.STATE_READY));

            if(clearRequest.byHotness() != 0){
                queryBuilder.where(ResourceEntityDao.Properties.Hotness.lt(clearRequest.byHotness()));
            }

            queryBuilder.where(ResourceEntityDao.Properties.UpdateTime.lt(timeBorder));

            if(clearRequest.byAdditions() != null && !clearRequest.byAdditions().isEmpty()){
                queryBuilder.where(ResourceEntityDao.Properties.AdditionInfo.in(clearRequest.byAdditions()));
            }

            if(clearRequest.excepts() != null && !clearRequest.excepts().isEmpty()){
                queryBuilder.where(ResourceEntityDao.Properties.Group.notIn(clearRequest.excepts()));
            }

            queryBuilder.limit(config.clearFileNumsEveryTime());

            queryBuilder.orderAsc(ResourceEntityDao.Properties.Hotness);

            List<ResourceEntity> result = queryBuilder.list();

            if(result.isEmpty()){
                break;
            }

            for (ResourceEntity entity : result) {
                Platform.log().d(TAG, "清理：" + entity.getLocalPath());
                entity.setState(ResourceState.STATE_INIT);
                entity.setHotness(0);
                FileUtils.delete(entity.getLocalPath());
                daoSession.getResourceEntityDao().insertOrReplaceInTx(entity);
                clearCounts.incrementAndGet();
            }
            if (Long.parseLong(FileUtils.getSize(config.baseFolder())) < config.clearUntilSize()) {
                Platform.log().d(TAG, "温和清理完成");
                success = true;
                break;
            }
        }

        if(!success){
            //如果未完成清理
            Platform.log().d(TAG, "温和清理后未达标，继续清理");
            rushClear(daoSession, clearRequest, config, clearCounts);
        }
    }

    private void rushClear(DaoSession daoSession,
                           ResourceManagerComponent.ClearRequest clearRequest,
                           ResourceManagerComponent.ResourceManagerConfig config,
                           AtomicInteger clearCounts) {
        boolean success = false;
        while (true) {
            Platform.log().d(TAG, "开始快速清理");
            QueryBuilder<ResourceEntity> queryBuilder = daoSession.getResourceEntityDao().queryBuilder();
            queryBuilder.where(ResourceEntityDao.Properties.State.eq(ResourceState.STATE_READY));

            if(clearRequest.byHotness() != 0){
                queryBuilder.where(ResourceEntityDao.Properties.Hotness.lt(clearRequest.byHotness()));
            }

            if(clearRequest.byAdditions() != null && !clearRequest.byAdditions().isEmpty()){
                queryBuilder.where(ResourceEntityDao.Properties.AdditionInfo.in(clearRequest.byAdditions()));
            }

            if(clearRequest.excepts() != null && !clearRequest.excepts().isEmpty()){
                queryBuilder.where(ResourceEntityDao.Properties.Group.notIn(clearRequest.excepts()));
            }

            queryBuilder.limit(config.clearFileNumsEveryTime());

            queryBuilder.orderAsc(ResourceEntityDao.Properties.Hotness);

            List<ResourceEntity> result = queryBuilder.list();

            if(result.isEmpty()){
                break;
            }

            for (ResourceEntity entity : result) {
                Platform.log().d(TAG, "清理：" + entity.getLocalPath());
                entity.setState(ResourceState.STATE_INIT);
                entity.setHotness(0);
                FileUtils.delete(entity.getLocalPath());
                daoSession.getResourceEntityDao().insertOrReplaceInTx(entity);
                clearCounts.incrementAndGet();
            }
            if (Long.parseLong(FileUtils.getSize(config.baseFolder())) < config.clearUntilSize()) {
                Platform.log().d(TAG, "快速清理完成");
                success = true;
                break;
            }
        }

        if(!success){
            //如果未完成清理
            if(config.madClear()){
                Platform.log().d(TAG, "快速清理未达标，继续清理");
                madClear(daoSession, clearRequest, config, clearCounts);
            }else{
                Platform.log().d(TAG, "清理结束，未达标");
            }
        }
    }

    private void madClear(DaoSession daoSession,
                          ResourceManagerComponent.ClearRequest clearRequest,
                          ResourceManagerComponent.ResourceManagerConfig config,
                          AtomicInteger clearCounts) {
        boolean success = false;
        while (true) {
            Platform.log().d(TAG, "开始疯狂清理，按照热度逐次删除");
            QueryBuilder<ResourceEntity> queryBuilder = daoSession.getResourceEntityDao().queryBuilder();
            queryBuilder.where(ResourceEntityDao.Properties.State.eq(ResourceState.STATE_READY));
            queryBuilder.limit(config.clearFileNumsEveryTime());
            queryBuilder.orderAsc(ResourceEntityDao.Properties.Hotness);

            List<ResourceEntity> result = queryBuilder.list();

            if(result.isEmpty()){
                break;
            }

            for (ResourceEntity entity : result) {
                Platform.log().d(TAG, "清理：" + entity.getLocalPath());
                entity.setState(ResourceState.STATE_INIT);
                entity.setHotness(0);
                FileUtils.delete(entity.getLocalPath());
                daoSession.getResourceEntityDao().insertOrReplaceInTx(entity);
                clearCounts.incrementAndGet();
            }
            if (Long.parseLong(FileUtils.getSize(config.baseFolder())) < config.clearUntilSize()) {
                Platform.log().d(TAG, "疯狂清理完成");
                success = true;
                break;
            }
        }

        if(!success){
            //如果无差别清理未达标
            Platform.log().d(TAG, "清理结束，未达标");
        }
    }


}
