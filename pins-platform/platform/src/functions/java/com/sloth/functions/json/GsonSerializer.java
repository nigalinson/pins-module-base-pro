package com.sloth.functions.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rongyi.common.anotations.TestCheck;
import com.rongyi.common.functions.storage.RYFileHelper;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/25 16:30
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/25         Carl            1.0                    1.0
 * Why & What is modified:
 */
@TestCheck
public class GsonSerializer extends JsonSerializer {

    private final Gson gson = new Gson();

    @Override
    public String toJson(Object obj) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clz) {
        return gson.fromJson(json, clz);
    }

    @Override
    public <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    @Override
    public <T> List<T> fromJsonList(String json, Class<T> clz) {
        List<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(new Gson().fromJson(elem, clz));
        }
        return list;
    }

    @Override
    public <T> T fromFileJson(String filePath, Class<T> clz) {
        try {
            JsonObject jsonObject = new JsonParser().parse(new FileReader(filePath)).getAsJsonObject();
            return gson.fromJson(jsonObject, clz);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T fromFileJson(FileReader fileReader, Class<T> clz) {
        JsonObject jsonObject = new JsonParser().parse(fileReader).getAsJsonObject();
        return gson.fromJson(jsonObject, clz);
    }

    @Override
    public <T> T fromFileJson(InputStream inputStream, Class<T> clz) {
        String tmp = RYFileHelper.readFileToString(inputStream);
        if(tmp == null || "".equals(tmp)){ return null; }
        return fromJson(tmp, clz);
    }

    @Override
    public <T> List<T> fromFileJsonList(String filePath, Class<T> clz) {
        try {
            return fromFileJsonList(new FileReader(filePath), clz);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> List<T> fromFileJsonList(FileReader fileReader, Class<T> clz) {
        List<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(fileReader).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(new Gson().fromJson(elem, clz));
        }
        return list;
    }

    @Override
    public <T> List<T> fromFileJsonList(InputStream inputStream, Class<List<T>> clz) {
        String tmp = RYFileHelper.readFileToString(inputStream);
        if(tmp == null || "".equals(tmp)){ return null; }
        return fromJson(tmp, clz);
    }

    @Override
    public <T> T copy(T obj) {
        if(obj == null){
            return null;
        }
        return fromJson(toJson(obj), (Class<T>)obj.getClass());
    }

    @Override
    public <T> T copy(Object obj, Type proto) {
        return fromJson(toJson(obj), proto);
    }

    @Override
    public <T> List<T> copyList(List<T> obj) {
        List<T> res = new ArrayList<>(obj.size());
        for(T item: obj){
            res.add(copy(item));
        }
        return res;
    }

}
