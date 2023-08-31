package com.sloth.pontus.listener;

import com.sloth.pontus.entity.ResourceEntity;

public interface DatabaseListener {

    void querySuccess(ResourceEntity resourceEntity);

    void queryFailed(String errMsg);

}
