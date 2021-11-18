package com.sloth.functions.json;

import com.rongyi.common.anotations.TestCheck;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/25 16:51
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/25         Carl            1.0                    1.0
 * Why & What is modified:
 */
@TestCheck
public class RYJson {

    public static JsonSerializer get(){
        return get(RYJsonSerializerHolder.Serializer.GSON);
    }

    public static JsonSerializer get(RYJsonSerializerHolder.Serializer serializerType){
        JsonSerializer serializer = RYJsonSerializerHolder.get(serializerType.val);

        if(serializer == null){
            throw new RuntimeException("暂未实现你需要的JSON序列化器");
        }

        return serializer;
    }
}
