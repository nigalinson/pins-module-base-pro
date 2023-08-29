package com.rongyi.order.pear;

import android.content.Context;
import android.widget.Toast;

import com.rongyi.base.Iprinter;
import com.rongyi.base.User;
import com.rongyi.info_api.InfoRpc;
import com.rongyi.order.OrderRpc;
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
@RouterService(interfaces = OrderRpc.class, singleton = true, key = "pear")
public class PearOrderImpl implements OrderRpc {

    @Override
    public void makeOrder(String id) {
        User user = Router.getService(InfoRpc.class).getUser();
        String hint = user.getName() + " has make an pear order, order id is:" + id;
        Router.getService(Iprinter.class).print(hint);
        Toast.makeText(Router.getService(Context.class, "/application"), hint, Toast.LENGTH_SHORT).show();
    }

}
