package com.sloth.pinsdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import com.sloth.platform.Platform;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static final String[] MOCK_IMAGES = new String[]{
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512258154505834497.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512266022139334657.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512271335152291840.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512275288791322625.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/jpg/1526508306649190401.jpg",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/jpg/1526508090185355264.jpg",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1526829860067086337.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1526829947962920960.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512256005214441473.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512258350778290177.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512260300823465985.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512260821009436672.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512265968225751041.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512271451317735425.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512272501319798785.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512273589443891200.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512274767892320256.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512309665176883200.png",
            "http://terminalfs.rongyi.com/system/application/server/v8/file/png/1512310362224070657.png"
    };

    private AppCompatImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = findViewById(R.id.iv);

        Platform.resourceManager().batch(Arrays.asList(MOCK_IMAGES), null, null, null);

    }


}