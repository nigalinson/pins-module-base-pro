package com.sloth.pinsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.zxing.WriterException;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sloth.barcode.BarCodeHelper;
import com.sloth.tools.util.ExecutorUtils;

@RouterUri(path = "barcode")
public class BarCodeActivity extends AppCompatActivity {

    private AppCompatImageView bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_code);
        bar = findViewById(R.id.bar);
        ExecutorUtils.getNormal().submit(()->{
            try {
                Bitmap res = BarCodeHelper.createQRBitmapAddLogo("hello world", 200, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), 20);
                runOnUiThread(()-> bar.setImageBitmap(res));
            } catch (WriterException e) {
                e.printStackTrace();
            }

        });
    }
}