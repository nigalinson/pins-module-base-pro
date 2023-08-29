package com.sloth.architecture;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.sloth.platform.Platform;

public abstract class BaseActivity extends AppCompatActivity {

    private boolean activityResumed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView(savedInstanceState);

        if(getContentLayoutRes() != -1 && getContentLayoutRes() != 0){
            onSetContentView(getContentLayoutRes());
        }

        onStartIntent(true, getIntent());
    }

    protected void onSetContentView(int contentLayoutRes) {
        setContentView(contentLayoutRes);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onStartIntent(false, intent);
    }

    protected void beforeSetContentView(Bundle savedInstanceState) { }

    protected abstract int getContentLayoutRes();

    protected void onStartIntent(boolean firstCreate, Intent intent) {
        if(intent != null){
            onStartIntentAvailable(firstCreate, intent);
        }
    }

    protected void onStartIntentAvailable(boolean firstCreate, Intent intent) { }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityResumed = false;
    }

    public boolean isActivityResumed(){
        return activityResumed;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //日志输出
        Platform.log().flush(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
