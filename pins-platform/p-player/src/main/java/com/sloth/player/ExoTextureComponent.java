package com.sloth.player;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.PlayerComponent;
import com.sloth.platform.Platform;

@RouterService(interfaces = PlayerComponent.class, key = ComponentTypes.Player.EXO_TEXTURE)
public class ExoTextureComponent extends AbsPlayer<ExoCodec, TextureView> implements TextureView.SurfaceTextureListener {

    private static final String TAG = ExoTextureComponent.class.getSimpleName();

    public ExoTextureComponent(@NonNull Context context) {
        this(context, null);
    }

    public ExoTextureComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ExoTextureComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    protected TextureView initPlayerView(Context context) {
        TextureView textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);
        return textureView;
    }

    @Override
    protected ExoCodec onInitPlayerCodec(Context context) {
        return new ExoCodec(context);
    }

    @Override
    protected void onReleasePlayerView(TextureView textureView) {
        textureView.setSurfaceTextureListener(null);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
        Platform.log().i(TAG, "Texture就绪");
        Surface surface = new Surface(texture);
        setSurfaceReference(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Platform.log().i(TAG, "surface销毁");
        setSurfaceReference(null);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }

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

    @Override
    public void clearSurface() {
        if(getSurfaceReference() != null){
            GL.clearSurface(getSurfaceReference());
        }
    }

}
