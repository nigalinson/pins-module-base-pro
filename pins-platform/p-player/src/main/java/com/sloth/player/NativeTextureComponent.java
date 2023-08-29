package com.sloth.player;

import android.content.Context;
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

@RouterService(interfaces = PlayerComponent.class, key = ComponentTypes.Player.NATIVE_TEXTURE)
public class NativeTextureComponent extends AbsPlayer<NativeCodec, TextureView>
        implements TextureView.SurfaceTextureListener {

    private static final String TAG = NativeTextureComponent.class.getSimpleName();

    public NativeTextureComponent(@NonNull Context context) {
        this(context, null);
    }

    public NativeTextureComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NativeTextureComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected TextureView initPlayerView(Context context) {
        TextureView textureView = new TextureView(getContext());
        textureView.setSurfaceTextureListener(this);
        return textureView;
    }

    @Override
    protected NativeCodec onInitPlayerCodec(Context context) {
        return new NativeCodec(context);
    }

    @Override
    protected void onReleasePlayerView(TextureView surfaceView) {
        surfaceView.setSurfaceTextureListener(null);
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
    public void clearSurface() {
        if(getSurfaceReference() != null){
            GL.clearSurface(getSurfaceReference());
        }
    }
}
