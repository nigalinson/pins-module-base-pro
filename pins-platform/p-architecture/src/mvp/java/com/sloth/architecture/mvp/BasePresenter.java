package com.sloth.architecture.mvp;

import android.content.Context;
import android.os.Bundle;
import com.sloth.utils.ReflectUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BasePresenter<V extends BaseView> {
    protected Context mContext;
    protected V mView;
    private final Map<String, BaseCase> cases = new HashMap<>();

    public BasePresenter(V view) {
        this.mView = view;
        initPreCases();
    }

    public BasePresenter(Context context, V view) {
        this.mContext = context;
        this.mView = view;
        initPreCases();
    }

    private void initPreCases() {
        //初始化代理case，注入model
        initPreModels();
        //初始化自定义case
        Set<BaseCase> preCases = new HashSet<>();
        prepareCases(preCases);
        if(!preCases.isEmpty()){
            for(BaseCase item: preCases){
                item.setContext(mContext);
                item.setView(mView);
                cases.put(item.getClass().getSimpleName(), item);
            }
        }
    }

    private void initPreModels() {
        Set<BaseModel> preModels = new HashSet<>();
        prepareModels(preModels);

        if(!preModels.isEmpty()){
            //如果model不为空说明presenter直接使用了model，手动新建一个代理DefaultCase
            DefaultCase defaultCase = new DefaultCase(mContext, mView);
            cases.put(defaultCase.getClass().getSimpleName(), defaultCase);
            defaultCase.setModelsFromOutside(preModels);
        }
    }

    protected void prepareCases(Set<BaseCase> list){ }

    protected void prepareModels(Set<BaseModel> list){ }

    protected  <C extends BaseCase> C getCase(Class<C> clz){
        String className = clz.getSimpleName();

        if(cases.get(className) != null){
            return (C) cases.get(className);
        }

        C ins = ReflectUtils.reflect(clz).get();
        ins.setContext(mContext);
        ins.setView(mView);
        cases.put(className, ins);
        return ins;
    }

    /**
     * 原则上presenter应当通过case分散业务逻辑到不同用例中
     * 但某些场景下没必要新建Case
     * 例如，presenter直接访问model进行请求时，实际是通过DefaultCase来代理请求的
     * @param clz
     * @param <M>
     * @return
     */
    protected <M extends BaseModel> M getModel(Class<M> clz){
        return (M) getCase(DefaultCase.class).getModel(clz);
    }

    /**
     * 不涉及case和model的任务，直接由DefaultCase -> DefaultModel代理 来处理
     * @param disposable
     */
    protected void execute(Disposable disposable){
        getCase(DefaultCase.class).execute(disposable);
    }

    protected void execute(int code, Disposable disposable){
        getCase(DefaultCase.class).execute(code, disposable);
    }

    protected void delay(long delayTime, Observer<Long> observer){
        delay(-1, delayTime, observer);
    }

    protected void delay(int code, long delayTime, Observer<Long> observer){
        getCase(DefaultCase.class).delay(code, delayTime, observer);
    }

    protected void interval(long delayTime, long interval, Observer<Long> observer){
        interval(-1, delayTime, interval, observer);
    }

    protected void interval(int code, long delayTime, long interval, Observer<Long> observer){
        getCase(DefaultCase.class).interval(code, delayTime, interval, observer);
    }

    public void cancel(int code){
        getCase(DefaultCase.class).cancel(code);
    }

    public void cancel(Disposable disposable){
        getCase(DefaultCase.class).cancel(disposable);
    }

    public void cancelAll(){
        for(String mk: cases.keySet()){
            BaseCase<?> useCase = cases.get(mk);
            if(useCase != null){
                useCase.cancelAll();
            }
        }
    }

    public <C> void cancelAll(Class<C> clz){
        //取消同名的case中的所有请求
        String className = clz.getSimpleName();
        BaseCase<?> aimCase = cases.get(className);
        if(aimCase != null){
            aimCase.cancelAll();
        }

        //取消所有case中同名model的所有请求
        for(String mk: cases.keySet()){
            BaseCase<?> useCase = cases.get(mk);
            if(useCase != null){
                useCase.cancelAll(clz);
            }
        }
    }

    protected void onRestoreInstance(Bundle bundle){ }

    protected void onSaveInstanceRestored(Bundle bundle){ }

    public void destroy(){
        if(cases.size() > 0){
            for(BaseCase caseItem: cases.values()){
                caseItem.destroy();
            }
        }
        cases.clear();
    }

    protected boolean mock(){
        return false;
    }

}
