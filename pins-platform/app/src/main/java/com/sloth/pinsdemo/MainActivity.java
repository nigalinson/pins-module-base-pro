package com.sloth.pinsdemo;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.sloth.tools.util.LogUtils;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_order).setOnClickListener((v)->{
            LogUtils.e("haha", "hello pins !!!");
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.exit();
    }
}