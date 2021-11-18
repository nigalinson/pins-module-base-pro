package com.sloth.functions.json;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/27 11:57
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/27         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class RYJsonSerializerHolder {

    private static final RYJsonSerializerHolder holder = new RYJsonSerializerHolder();

    public Map<Integer, JsonSerializer> jsonSerializerMap = new ConcurrentHashMap<>();

    private RYJsonSerializerHolder() {
        jsonSerializerMap.put(Serializer.GSON.val, new GsonSerializer());
        //在此添加新的序列化器
    }

    public static JsonSerializer get(int type){
        return holder.jsonSerializerMap.get(type);
    }

    public enum Serializer {
        /**
         * gson
         */
        GSON(1),
        /**
         * 高性能json序列化
         */
        FastJson(2)
        ;

        Serializer(int val) {
            this.val = val;
        }

        public Integer val;
    }

}
