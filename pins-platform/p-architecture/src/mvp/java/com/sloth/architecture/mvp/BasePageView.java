package com.sloth.architecture.mvp;

import java.util.List;

public interface BasePageView<D> extends BaseView {

    void refreshSuccess(List<D> t);

    void loadMoreSuccess(List<D> t);

    void refreshFailed(String msg);

    void loadModeFailed(String msg);

}
