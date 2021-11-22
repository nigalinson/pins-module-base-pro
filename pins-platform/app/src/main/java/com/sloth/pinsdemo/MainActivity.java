package com.sloth.pinsdemo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sankuai.waimai.router.Router;
import com.sloth.functions.adapter.BaseAdapter;
import com.sloth.functions.adapter.BaseViewHolder;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private final String[] menus = new String[]{"banner", "barcode"};

    private RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(this));
        BaseAdapter<VH, String> adp = new BaseAdapter<VH, String>(this) {
            @NonNull
            @Override
            public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VH(new AppCompatButton(parent.getContext()));
            }

            @Override
            public void onBindViewHolder(@NonNull VH holder, int position) {
                holder.bindViewData(menus[position]);
                holder.itemView.setOnClickListener(v -> Router.startUri(MainActivity.this, "/" + menus[position]));
            }
        };
        rvList.setAdapter(adp);

        adp.resetItems(Arrays.asList(menus));

    }

    private static class VH extends BaseViewHolder<String> {

        AppCompatButton tv;

        public VH(View itemView) {
            super(itemView);
            tv = (AppCompatButton) itemView;
        }

        @Override
        public void bindViewData(String data) {
            tv.setText(data);
        }
    }
}