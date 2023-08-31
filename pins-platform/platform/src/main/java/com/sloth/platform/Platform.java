package com.sloth.platform;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.annimon.stream.Optional;
import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.service.IFactory;

public class Platform {
    private static final String TAG = Platform.class.getSimpleName();

    private static final PlatformConfig defaultConfig = new PlatformConfig.Default();

    public static <CP, B> CP getComponentOrDefault(Class<CP> clz, String key, Context ctx,
                                                   ConfigProxy configProxy, CP def, Class<B> bClass, Object bObj){
        CP cp = null;
        IFactory factory = null;
        if(bClass != null){
            factory = new IFactory() {
                @NonNull
                @Override
                public <T> T create(@NonNull Class<T> clazz) {
                    try{
                        return clazz.getConstructor(bClass).newInstance(bObj);
                    }catch(Exception e){
                        Log.e(TAG, "组件初始化异常");
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        if(key != null){
            if(factory != null){
                cp = Router.getService(clz, key, factory);
            }else if(ctx != null){
                cp = Router.getService(clz, key, ctx);
            }else {
                cp = Router.getService(clz, key);
            }
        }else if(configProxy != null){
            PlatformConfig config = Router.getService(PlatformConfig.class);
            if(config == null) config = defaultConfig;
            if(factory != null){
                cp = Router.getService(clz, configProxy.componentName(config), factory);
            }else if(ctx != null){
                cp = Router.getService(clz, configProxy.componentName(config), ctx);
            }else{
                cp = Router.getService(clz, configProxy.componentName(config));
            }
        }

        return (cp != null ? cp : def);
    }

    public static LogComponent log(){
        return log(null);
    }

    public static LogComponent log(String key){
        return log(key, null, null);
    }

    public static <B> LogComponent log(String key, Class<B> bClz, Object bObj){
        return getComponentOrDefault(LogComponent.class, key, null, PlatformConfig::logComponent, EMPTY.LOG, bClz, bObj);
    }

    public static PlayerComponent player(Context ctx){
        return player(null, ctx);
    }

    public static PlayerComponent player(String key, Context ctx){
        return player(key, ctx, null, null);
    }

    public static <B> PlayerComponent player(String key, Context ctx, Class<B> bClz, Object bObj){
        return getComponentOrDefault(PlayerComponent.class, key, ctx, PlatformConfig::playerComponent, EMPTY.PLAYER, bClz, bObj);
    }

    public static DownloadComponent download(){
        return download(null);
    }

    public static DownloadComponent download(String key){
        return download(key, null, null);
    }

    public static <B> DownloadComponent download(String key, Class<B> bClass, Object bObj){
        return getComponentOrDefault(DownloadComponent.class, key, null, PlatformConfig::downloaderComponent, EMPTY.DOWNLOADER, bClass, bObj);
    }

    public static JsonComponent json(){
        return json(null);
    }

    public static JsonComponent json(String key){
        return json(key, null, null);
    }

    public static <B> JsonComponent json(String key, Class<B> bClz, Object bObj){
        return getComponentOrDefault(JsonComponent.class, key, null, PlatformConfig::jsonComponent, EMPTY.JSON, bClz, bObj);
    }

    public static ResourceManagerComponent resourceManager(){
        return resourceManager(null);
    }

    public static ResourceManagerComponent resourceManager(String key){
        return resourceManager(key, null, null);
    }

    public static <B> ResourceManagerComponent resourceManager(String key, Class<B> bClz, Object bObj){
        return getComponentOrDefault(ResourceManagerComponent.class, key, null, PlatformConfig::resourceManagerComponent, EMPTY.RESOURCE_MANAGER, bClz, bObj);
    }

    public static <C> Optional<C> service(Class<C> clz){
        return service(clz, null, null);
    }

    public static <C> Optional<C> service(Class<C> clz, Context ctx){
        return service(clz, null, ctx);
    }

    public static <C> Optional<C> service(Class<C> clz, String key){
        return service(clz, key, null, null, null);
    }

    public static <C> Optional<C> service(Class<C> clz, String key, Context ctx){
        return service(clz, key, ctx, null, null);
    }

    public static <C,B> Optional<C> service(Class<C> clz, String key, Context ctx, Class<B> bClz, Object bObj){
        return Optional.ofNullable(getComponentOrDefault(clz, key, ctx, null, null, bClz, bObj));
    }

    private interface ConfigProxy {
        String componentName(PlatformConfig config);
    }

}
