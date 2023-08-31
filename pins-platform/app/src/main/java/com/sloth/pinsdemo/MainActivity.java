package com.sloth.pinsdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.sloth.functions.api.API;
import com.sloth.functions.api.DefaultApiConfig;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.Platform;
import com.sloth.rx.Obx;
import com.sloth.rx.Rx;
import com.sloth.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Apple apple = new Apple();
        apple.setName("red fuji");
        apple.setWeight(1);

        String json1 = Platform.json().toJson(apple);
        System.out.println("json1:" + json1);

        String json2 = Platform.json(ComponentTypes.JSON_SERIALIZE.GSON,
                GsonBuilder.class,
                new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
        ).toJson(apple);
        System.out.println("json2:" + json2);

        API.getInstance().create(new DefaultApiConfig("http://api.rongyiguang.com/"), TimeApiStore.class);

        Rx.delegate(TimeApiStore.class)
                .run(it -> it.getServerTime(System.currentTimeMillis() + ""))
                .ui()
                .execute(new Obx<Object>() {
                    @Override
                    protected void onExe(Object o) {
                        super.onExe(o);
                        ToastUtils.showShort(Platform.json().toJson(o));
                    }
                });
    }

    public static final class Apple {

        @Expose
        public String name;
        @Expose(serialize = false)
        public int weight;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

}