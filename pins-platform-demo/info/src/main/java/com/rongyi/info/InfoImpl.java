package com.rongyi.info;

import com.rongyi.base.Iprinter;
import com.rongyi.base.User;
import com.rongyi.info_api.InfoRpc;
import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterService;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/11 14:19
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/11         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = InfoRpc.class, singleton = true)
public class InfoImpl implements InfoRpc {

    @Override
    public User getUser() {
        User user = new User("jack");
        Router.getService(Iprinter.class).print("create user" + user.getName());
        return user;
    }

}
