package com.rongyi.base;

import com.sankuai.waimai.router.annotation.RouterService;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/16 16:31
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = Iprinter.class, singleton = true)
public class Printer implements Iprinter{

    @Override
    public void print(String msg) {
        System.out.println("----- printed: " + msg + " -----");
    }

}
