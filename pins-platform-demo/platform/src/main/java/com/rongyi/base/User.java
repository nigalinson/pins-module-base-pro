package com.rongyi.base;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/11 14:13
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/11         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
