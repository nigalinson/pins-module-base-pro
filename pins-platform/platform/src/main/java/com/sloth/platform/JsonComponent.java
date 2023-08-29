package com.sloth.platform;

import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public interface JsonComponent {

    String toJson(Object obj);

    <T> T fromJson(String json, Class<T> clz);

    <T> T fromJson(String json, Type type);


    <T> List<T> fromJsonList(String json, Class<T> clz);

    <T> T fromFileJson(String filePath, Class<T> clz);

    <T> T fromFileJson(FileReader fileReader, Class<T> clz);

    <T> T fromFileJson(InputStream inputStream, Class<T> clz);


    <T> List<T> fromFileJsonList(String filePath, Class<T> clz);

    <T> List<T> fromFileJsonList(FileReader fileReader, Class<T> clz);

    <T> List<T> fromFileJsonList(InputStream inputStream, Class<List<T>> clz);


    <T> T copy(T obj);

    <T> T copy(Object obj, Type proto);

    <T> List<T> copyList(List<T> obj);

}
