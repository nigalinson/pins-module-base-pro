package com.rongyi.info_api;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/16 17:09
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface LoginRpc {

    void login(LoginCallback callback);

    interface LoginCallback{
        void loginSuccess();
        void loginFailed(String err);
    }
}
