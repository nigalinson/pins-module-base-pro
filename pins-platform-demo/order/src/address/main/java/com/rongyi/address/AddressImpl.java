package com.rongyi.address;

import android.content.Context;
import android.widget.Toast;
import com.rongyi.base.Iprinter;
import com.rongyi.base.User;
import com.rongyi.info_api.InfoRpc;
import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterService;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/11 14:18
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/11         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = AddressRpc.class, singleton = true)
public class AddressImpl implements AddressRpc {

    @Override
    public String getAddress(String id) {
        User user = Router.getService(InfoRpc.class).getUser();
        String hint = user.getName() + " is getting address: " + id;
        Router.getService(Iprinter.class).print(hint);
        Toast.makeText(Router.getService(Context.class, "/application"), hint, Toast.LENGTH_SHORT).show();
        return null;
    }
}
