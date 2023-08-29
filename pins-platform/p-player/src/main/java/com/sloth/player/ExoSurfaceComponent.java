package com.sloth.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.PlayerComponent;
import com.sloth.platform.Platform;

@RouterService(interfaces = PlayerComponent.class, key = ComponentTypes.Player.EXO_SURFACE)
public class ExoSurfaceComponent extends AbsPlayer<ExoCodec, SurfaceView> implements SurfaceHolder.Callback {

    private static final String TAG = ExoSurfaceComponent.class.getSimpleName();

    public ExoSurfaceComponent(@NonNull Context context) {
        this(context, null);
    }

    public ExoSurfaceComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ExoSurfaceComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initCore(Context context, AttributeSet attrs, int defStyleAttr) {
        if(attrs != null){
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RyPlayer, 0, 0);
            try {
                setScaleType(a.getInt(R.styleable.RyPlayer_player_scale_type, getScaleType()));
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                a.recycle();
            }
        }
        super.initCore(context, attrs, defStyleAttr);
    }

    @Override
    protected SurfaceView initPlayerView(Context context) {
        SurfaceView surfaceView = new SurfaceView(getContext());
        surfaceView.getHolder().addCallback(this);
        return surfaceView;
    }

    @Override
    protected ExoCodec onInitPlayerCodec(Context context) {
        return new ExoCodec(context);
    }

    @Override
    protected void onReleasePlayerView(SurfaceView surfaceView) {
        surfaceView.getHolder().removeCallback(this);
    }

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

    @Override
    public void clearSurface() {
        if(getSurfaceReference() != null){
            GL.clearSurface(getSurfaceReference());
        }
    }

    @Override
    protected void onTopCodecPrepared() {
        onHidePreView();
    }

    @Override
    protected void onTopCodecBuffering() { }

    @Override
    protected void onTopCodecSizeChanged(int width, int height) { }

    @Override
    protected void onTopCodecEnd() { }

    @Override
    protected void onTopCodecError(int code, String msg) { }

}
