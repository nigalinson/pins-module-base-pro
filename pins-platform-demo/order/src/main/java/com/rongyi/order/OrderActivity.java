package com.rongyi.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.rongyi.info_api.InfoConstant;
import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sankuai.waimai.router.common.DefaultPageUriRequest;

@RouterUri(path = OrderConst.ORDER_SCHEME)
public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setTitle("Order");

        findViewById(R.id.btn_info).setOnClickListener(v -> {
            Router.startUri(OrderActivity.this, InfoConstant.INFO_SCHEME);
        });
    }
}