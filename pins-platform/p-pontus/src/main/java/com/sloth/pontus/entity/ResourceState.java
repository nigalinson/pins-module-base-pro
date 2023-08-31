package com.sloth.pontus.entity;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ResourceState.STATE_INIT, ResourceState.STATE_READY})
@Retention(RetentionPolicy.SOURCE)
public @interface ResourceState {

    int STATE_INIT = 0;

    int STATE_READY = 1;

}
