package com.sloth.architecture.mvp;

import android.content.Context;
import com.sloth.utils.ReflectUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BaseCase<V extends BaseView> {
    protected Context context;
    protected V mView;
    private final Map<String, BaseModel> models = new HashMap<>();

    public BaseCase() {
        initPreModels();
    }

    public BaseCase(V v) {
        this.mView = v;
        initPreModels();
    }

    public BaseCase(Context context, V v) {
        this.context = context;
        this.mView = v;
        initPreModels();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setView(V mView) {
        this.mView = mView;
    }

    private void initPreModels() {
        Set<BaseModel> preModels = new HashSet<>();
        prepareModels(preModels);
        if(!preModels.isEmpty()){
            for(BaseModel item: preModels){
                item.setContext(context);
                models.put(item.getClass().getSimpleName(), item);
            }
        }
    }

    /**
     * 用户手动预置入的model实例，可以直接使用，如果未初始化，会由反射自动生成一个实例
     * @return
     */
    protected void prepareModels(Set<BaseModel> list){ }

    /**
     * 外部置入model
     */
    public void setModelsFromOutside(Set<BaseModel> outside){
        if(!outside.isEmpty()){
            for(BaseModel item: outside){
                item.setContext(context);
                models.put(item.getClass().getSimpleName(), item);
            }
        }
    }

    public <M extends BaseModel> M getModel(Class<M> clz){
        String className = clz.getSimpleName();

        if(models.get(className) != null){
            return (M) models.get(className);
        }

        M ins = ReflectUtils.reflect(clz).get();
        ins.setContext(context);
        models.put(className, ins);
        return ins;
    }

    /**
     * 有些需要直接执行的事件流
     * 由Case借 Model的生命周期管理功能来执行
     * 达到Activity销毁时，任务自动终止的效果
     */
    public void execute(Disposable disposable){
        getModel(DefaultModel.class).execute(disposable);
    }

    public void execute(int code, Disposable disposable){
        getModel(DefaultModel.class).execute(code, disposable);
    }

    protected void delay(long delayTime, Observer<Long> observer){
        delay(-1, delayTime, observer);
    }

    protected void delay(int code, long delayTime, Observer<Long> observer){
        getModel(DefaultModel.class).delay(code, delayTime, observer);
    }

    protected void interval(long delayTime, long interval, Observer<Long> observer){
        interval(-1, delayTime, interval, observer);
    }

    protected void interval(int code, long delayTime, long interval, Observer<Long> observer){
        getModel(DefaultModel.class).interval(code, delayTime, interval, observer);
    }

    public void cancel(int code){
        getModel(DefaultModel.class).cancel(code);
    }

    public void cancel(Disposable disposable){
        getModel(DefaultModel.class).cancel(disposable);
    }

    /**
     * 取消所有model中的请求
     */
    public void cancelAll(){
        for(String mk: models.keySet()){
            BaseModel model = models.get(mk);
            if(model != null){
                model.cancelAll();
            }
        }
    }

    /**
     * 取消指定model中的请求
     * @param clz
     * @param <M>
     */
    public <M> void cancelAll(Class<M> clz){
        String className = clz.getSimpleName();

        BaseModel model = models.get(className);
        if(model != null){
            model.cancelAll();
        }
    }

    public void destroy(){
        if(models.size() > 0){
            for(BaseModel model: models.values()){
                model.destroy();
            }
        }
        models.clear();
    }

}
