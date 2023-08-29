package com.sloth.player;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.PlayerComponent;
import com.sloth.platform.Platform;

@RouterService(interfaces = PlayerComponent.class, key = ComponentTypes.Player.NATIVE_SURFACE, defaultImpl = true)
public class NativeSurfaceComponent extends AbsPlayer<NativeCodec, SurfaceView>
        implements SurfaceHolder.Callback{

    private static final String TAG = NativeSurfaceComponent.class.getSimpleName();

    public NativeSurfaceComponent(@NonNull Context context) {
        this(context, null);
    }

    public NativeSurfaceComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NativeSurfaceComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected SurfaceView initPlayerView(Context context) {
        SurfaceView surfaceView = new SurfaceView(getContext());
        surfaceView.getHolder().addCallback(this);
        surfaceView.setLayerPaint(new Paint(){
            @Override
            public int getColor() {
                return Color.WHITE;
            }
        });
        return surfaceView;
    }

    @Override
    protected NativeCodec onInitPlayerCodec(Context context) {
        return new NativeCodec(context);
    }

    @Override
    protected void onReleasePlayerView(SurfaceView surfaceView) {
        surfaceView.getHolder().removeCallback(this);
    }

    @Override
    protected void onTopCodecPrepared() {
        onHidePreView();
    }

    @Override
    protected void onTopCodecBuffering() {
        onHidePreView();
    }

    @Override
    protected void onTopCodecSizeChanged(int width, int height) { }

    @Override
    protected void onTopCodecEnd() { }

    @Override
    protected void onTopCodecError(int code, String msg) { }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Platform.log().i(TAG, "surface就绪");
        setSurfaceReference(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Platform.log().i(TAG, "surface销毁");
        //surface重新绑定时，会自动播放，但多次播放时会出现黑屏，故干脆直接不允许resume
        stop();
        setSurfaceReference(null);
    }

}
