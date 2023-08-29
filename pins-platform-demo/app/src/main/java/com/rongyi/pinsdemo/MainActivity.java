package com.rongyi.pinsdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;

import com.rongyi.address.AddressRpc;
import com.rongyi.base.Iprinter;
import com.rongyi.info_api.LoginRpc;
import com.rongyi.order.OrderConst;
import com.rongyi.order.OrderRpc;
import com.sankuai.waimai.router.Router;
import java.util.Optional;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_order).setOnClickListener((v)->{
            Router.startUri(MainActivity.this, OrderConst.ORDER_SCHEME);
        });

        Router.getService(AddressRpc.class).getAddress("123456");
        Router.getService(OrderRpc.class).makeOrder("9998877");
        Optional.ofNullable((OrderRpc)Router.getService(OrderRpc.class, "pear")).ifPresent(rpc -> rpc.makeOrder("9998877"));
        Optional.ofNullable(Router.getService(LoginRpc.class)).ifPresent(loginRpc ->
                loginRpc.login(new LoginRpc.LoginCallback() {
                    @Override
                    public void loginSuccess() {
                        Router.getService(Iprinter.class).print("login success");
                    }

                    @Override
                    public void loginFailed(String err) {
                        Router.getService(Iprinter.class).print("login failed:" + err);
                    }
                })
        );

    }
}