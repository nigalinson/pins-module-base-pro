package com.sloth.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.sloth.platform.Platform;
import com.sloth.platform.PlayerComponent;
import com.sloth.utils.FileUtils;
import com.sloth.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2022/7/5 18:53
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2022/7/5         Carl            1.0                    1.0
 * Why & What is modified:
 */
abstract class AbsPlayer<Codec extends com.sloth.player.Codec<?>, PlayerView extends View>
        extends FrameLayout implements PlayerComponent {

    private static final String TAG = AbsPlayer.class.getSimpleName();

    private AppCompatImageView preView;

    /**
     * 默认按照原比例
     */
    private int scaleType;

    /**
     * 是否循环播放
     */
    private boolean loop;

    //目前在播放的预览图链接，如果一致，不需要重复加载
    private String showingPreviewUrl;

    /**
     * 显示封面中，播放器的onPrepared回调后画面还会黑100ms左右
     * 如果直接隐藏封面，会有黑一下的视觉效果，因此onPrepared调用后，延迟一定时间再关闭封面
     */
    private long closePreviewDelay = 100;

    /**
     * 解码器个数
     */
    private int codecCount;

    //播放器实例
    private List<Codec> mCodecList;

    //记录某链接对应的已经预加载好的播放器下标，用于快速定位下一个需要切换的播放器核心
    private final Map<String, Integer> URL_PLAYER_INDEX = new HashMap<>();

    /**
     * 当前使用的播放器下标
     */
    private int playerIndex;

    //播放器View
    private PlayerView mPlayerView;

    //用来持有surface的引用，防止surface对应的bufferQueue销毁
    private Surface surfaceReference;

    /**
     * 播放监听
     */
    private List<RyPlayerListener> mControllerListenerList;

    /**
     * 开始播放前需要手动清除surface
     */
    private boolean clearSurfaceBeforePlay = false;

    public AbsPlayer(@NonNull Context context) {
        this(context, null, -1);
    }

    public AbsPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AbsPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCore(context, attrs, defStyleAttr);
    }

    protected void initCore(Context context, AttributeSet attrs, int defStyleAttr) {
        codecCount = 1;
        scaleType = PlayerGlobal.ScaleType.FIT_XY.code;
        initPlayer(context);
        initPreView(context);
        mControllerListenerList = new ArrayList<>();
    }

    public void initPlayer(Context context){
        setBackgroundColor(PlayerGlobal.surfaceColor);
        mPlayerView = initPlayerView(context);
        mCodecList = new ArrayList<>();
        //默认先初始化一个播放器核心
        mCodecList.add(initPlayerCodec(context));
        playerIndex = 0;

        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        param.gravity = Gravity.CENTER;
        addView(mPlayerView, param);
    }

    protected abstract PlayerView initPlayerView(Context context);

    private Codec initPlayerCodec(Context context){
        Codec codec = onInitPlayerCodec(context);
        codec.setListener(new RyPlayerListener() {
            @Override
            public void onPlayerPrepared() {
                if(codec == getPlayerCodec()){
                    onTopCodecPrepared();
                    Stream.of(mControllerListenerList).forEach(lis -> lis.onPlayerPrepared());
                }
            }

            @Override
            public void onPlayerBuffering() {
                if(codec == getPlayerCodec()){
                    onTopCodecBuffering();
                    Stream.of(mControllerListenerList).forEach(lis -> lis.onPlayerBuffering());
                }
            }

            @Override
            public void onPlayerSizeChanged(int width, int height) {
                if(codec == getPlayerCodec()){
                    applyPlayerViewScaleType(width, height);
                    onTopCodecSizeChanged(width, height);
                    Stream.of(mControllerListenerList).forEach(lis -> lis.onPlayerSizeChanged(width, height));
                }
            }

            @Override
            public void onPlayerEnd() {
                if(codec == getPlayerCodec()){
                    onTopCodecEnd();
                    Stream.of(mControllerListenerList).forEach(lis -> lis.onPlayerEnd());
                }
            }

            @Override
            public void onPlayerError(int code, String msg) {
                if(codec == getPlayerCodec()){
                    onTopCodecError(code, msg);
                    Stream.of(mControllerListenerList).forEach(lis -> onPlayerError(code, msg));
                }
            }
        });
        return codec;
    }

    protected abstract Codec onInitPlayerCodec(Context context);

    private void initPreView(Context context) {
        preView = new AppCompatImageView(getContext());
        preView.setScaleType(toImgScaleType(scaleType));
        preView.setBackgroundColor(PlayerGlobal.surfaceColor);
        preView.setAlpha(0.99f);
        addView(preView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        preView.setVisibility(View.GONE);
    }

    protected abstract void onTopCodecPrepared();

    protected abstract void onTopCodecBuffering();

    protected abstract void onTopCodecSizeChanged(int width, int height);

    protected abstract void onTopCodecEnd();

    protected abstract void onTopCodecError(int code, String msg);

    /**
     * 如果等到videoSize回调，会出现先铺满全屏，然后突然一闪，变成正确比例的情况
     * 所以在有预览图的情况下，可以根据预览图提前设置好视频比例
     * @param videoWidth
     * @param videoHeight
     */
    @Override
    public void applyPlayerViewScaleType(int videoWidth, int videoHeight) {
        //视窗为空，无法变化比例
        if(mPlayerView == null) return;

        //无效视频size
        if(videoWidth == -1 || videoHeight == -1){ return; }

        if(scaleType == PlayerGlobal.ScaleType.FIT_CENTER.code){
            //等比缩放
            onApplyPlayerViewFitCenter(videoWidth, videoHeight);
        }else {
            onApplyPlayerViewFitXY(videoWidth, videoHeight);
        }
    }

    @Override
    public void preView(String localPath) {
        clearSurfaceBeforePlay = false;
        if(StringUtils.notEmpty(localPath)){
            Platform.log().d(TAG, "加载预览图：" + localPath);
            //加载预览图
            if(preView.getVisibility() != View.VISIBLE){
                preView.setVisibility(View.VISIBLE);
            }
            if(!localPath.equals(showingPreviewUrl)){
                showingPreviewUrl = localPath;
                //todo 加载预览图
            }

        }else{
            //清空预览图
            Platform.log().d(TAG, "释放预览图内存");
            showingPreviewUrl = null;
            preView.setImageBitmap(null);
        }
    }

    @Override
    public void loadCover(String coverImagePath) {
        clearSurfaceBeforePlay = false;
        if(FileUtils.isFileExists(coverImagePath)){
            Platform.log().d(TAG, "加载封面图：" + coverImagePath);
            if(preView.getVisibility() != View.VISIBLE){
                preView.setVisibility(View.VISIBLE);
            }
            if(!coverImagePath.equals(showingPreviewUrl)){
                showingPreviewUrl = coverImagePath;
                Glide.with(preView.getContext()).load(coverImagePath).into(preView);
            }
        }else{
            //清空预览图
            Platform.log().d(TAG, "释放预览图内存");
            showingPreviewUrl = null;
            preView.setImageBitmap(null);
        }
    }

    @Override
    public void loadCover(Bitmap coverBitmap) {
        clearSurfaceBeforePlay = false;
        if(coverBitmap != null && !coverBitmap.isRecycled()){
            Platform.log().d(TAG, "加载封面图： hex");
            if(preView.getVisibility() != View.VISIBLE){
                preView.setVisibility(View.VISIBLE);
            }
            showingPreviewUrl = null;
            preView.setImageBitmap(coverBitmap);
        }else{
            //清空预览图
            Platform.log().d(TAG, "释放预览图内存");
            showingPreviewUrl = null;
            preView.setImageBitmap(null);
        }
    }

    @Override
    public void loadCover(int coverImageResPath) {
        clearSurfaceBeforePlay = false;
        if(coverImageResPath != -1){
            Platform.log().d(TAG, "加载封面图");
            if(preView.getVisibility() != View.VISIBLE){
                preView.setVisibility(View.VISIBLE);
            }
            Glide.with(preView.getContext()).load(coverImageResPath).into(preView);
        }else{
            //清空预览图
            Platform.log().d(TAG, "释放预览图内存");
            showingPreviewUrl = null;
            preView.setImageBitmap(null);
        }
    }

    @Override
    public void play(Uri uri) {
        Platform.log().d(TAG, "链接：uri:" + uri.toString());
        detectIfNeedClearSurface();
        detectIfNeedSwitchCodec(uri);
        if(getPlayerCodec() != null){
            getPlayerCodec().play(uri);
        }
    }

    @Override
    public void play(String local) {
        play(local, null);
    }

    @Override
    public void play(String local, String online) {
        Platform.log().d(TAG, "play --> 链接：local:" + local + ", online:" + online);
        detectIfNeedClearSurface();
        detectIfNeedSwitchCodec(local);
        if(getPlayerCodec() != null){
            getPlayerCodec().play(local, online);
        }
    }

    /**
     * fixme 无法手动清除surfaceView，会保留最后一个视频的lastFrame
     *  因此需要加载一个黑图来临时解决这个问题
     */
    private void detectIfNeedClearSurface() {
        if(clearSurfaceBeforePlay){
            clearSurfaceBeforePlay = false;
            if(preView.getVisibility() != View.VISIBLE){
                preView.setVisibility(View.VISIBLE);
            }
            preView.setImageBitmap(null);
        }
    }

    @Override
    public void prepare(String local, String online) {
        Platform.log().d(TAG, "prepare --> 链接：local:" + local + ", online:" + online);
        detectIfNeedSwitchCodec(local);
        if(getPlayerCodec() != null){
            getPlayerCodec().prepare(local, online);
        }
    }

    /**
     * 检测是否需要替换核心
     * @param originUrl
     */
    protected void detectIfNeedSwitchCodec(Object originUrl) {
        if(codecCount == 1 || mCodecList.size() <= 1){
            Platform.log().w(TAG, "单核播放，直接加载");
            return;
        }

        if(originUrl == null || StringUtils.isTrimEmpty(originUrl.toString())){
            Platform.log().w(TAG, "无效路径，不尝试切换核心");
            return;
        }

        String url = originUrl.toString();

        int oldIndex = playerIndex;
        boolean find = false;
        if(URL_PLAYER_INDEX.containsKey(url)){
            Integer index = URL_PLAYER_INDEX.get(url);
            Codec switchTo = (index != null && index >= 0 && index < mCodecList.size() ? mCodecList.get(index) : null);
            if(switchTo != null){
                playerIndex = index;
                find = true;
            }
        }

        if(playerIndex != oldIndex){
            //换了一个
            if(oldIndex >=0 && oldIndex < mCodecList.size()){
                mCodecList.get(oldIndex).seekTo(0);
                mCodecList.get(oldIndex).pause();
                mCodecList.get(oldIndex).bindSurface(null);
            }

            if(playerIndex >= 0 && playerIndex < mCodecList.size()){
                if(surfaceReference != null){
                    mCodecList.get(playerIndex).bindSurface(surfaceReference);
                }

                if(originUrl instanceof Uri){
                    mCodecList.get(playerIndex).play((Uri)originUrl);
                }else{
                    mCodecList.get(playerIndex).play((String) originUrl);
                }
            }

        }else if(find){
            //同一个正在播放的
            if(oldIndex >=0 && oldIndex < mCodecList.size()){
                if(originUrl instanceof Uri){
                    mCodecList.get(oldIndex).play((Uri)originUrl);
                }else{
                    mCodecList.get(oldIndex).play((String) originUrl);
                }
            }
        }else{
            //没找到，继续用当前的
            if(oldIndex >=0 && oldIndex < mCodecList.size()){
                if(originUrl instanceof Uri){
                    mCodecList.get(oldIndex).play((Uri)originUrl);
                }else{
                    mCodecList.get(oldIndex).play((String) originUrl);
                }
            }
        }
    }

    @Override
    public void reset() {
        if(getPlayerCodec() != null){
            getPlayerCodec().reset();
        }
    }

    @Override
    public void start() {
        Platform.log().d(TAG, "开启播放");
        if(getPlayerCodec() != null){
            getPlayerCodec().start();
        }
    }

    @Override
    public void pause() {
        if(getPlayerCodec() != null){
            getPlayerCodec().pause();
        }
    }

    @Override
    public void stop(){
        Platform.log().d(TAG, "停止播放");
        if(getPlayerCodec() != null){
            getPlayerCodec().stop();
        }
    }

    @Override
    public void release() {
        for(Codec codec: mCodecList){
            codec.pause();
            codec.stop();
        }

        if(surfaceReference != null){
            setSurfaceReference(null);
        }

        if(getPlayerView() != null){
            onReleasePlayerView(getPlayerView());
        }

        for(Codec codec: mCodecList){
            codec.release();
        }
        clearCodecs();

        clearPlayerView();

        removeCallbacks(HIDE_PREVIEW);

    }

    protected abstract void onReleasePlayerView(PlayerView playerView);

    @Override
    public void clearSurface() {
        clearSurfaceBeforePlay = true;
    }

    @Override
    public boolean isPlaying() {
        if(getPlayerCodec() == null){
            return false;
        }
        return getPlayerCodec().isPlaying();
    }

    @Override
    public boolean isPrepared() {
        if(getPlayerCodec() == null){
            return false;
        }
        return getPlayerCodec().isPrepared();
    }

    @Override
    public void seekTo(int dur) {
        if(getPlayerCodec() != null){
            getPlayerCodec().seekTo(dur);
        }
    }

    @Override
    public int getCurrentPosition() {
        if(getPlayerCodec() == null){
            return 0;
        }
        return getPlayerCodec().getCurrentPosition();
    }

    @Override
    public void setVolume(float v) {
        if(getPlayerCodec() != null){
            getPlayerCodec().setVolume(v);
        }
    }

    @Override
    public void forward(boolean forward, int duration) {
        if(getPlayerCodec() != null){
            getPlayerCodec().forward(forward, duration);
        }
    }

    @Override
    public void setCodecCount(int c) {
        this.codecCount = (Math.max(c, 1));
    }

    /**
     * 用于记录哪些地址需要 prepare
     */
    private final List<String> TMP_URLS_NEED_RELOAD = new ArrayList<>();

    @Override
    public void prepareWithAdditionalCodecs(List<String> localPaths) {
        if(codecCount > 1 && localPaths != null && localPaths.size() > 0){
            //允许多解码器核心的情况下 可以预加载
            URL_PLAYER_INDEX.clear();
            TMP_URLS_NEED_RELOAD.addAll(localPaths);

            //记录解码器可用状态(是否被占据)
            boolean[] availableCodecs = new boolean[codecCount];
            Arrays.fill(availableCodecs, true);

            for(int i = 0; i < mCodecList.size(); i++){
                Codec codec = mCodecList.get(i);
                if(i == playerIndex){
                    availableCodecs[i] = false;
                    if(codec.getPlayingUri() != null){
                        TMP_URLS_NEED_RELOAD.remove(codec.getPlayingUri());
                        URL_PLAYER_INDEX.put(codec.getPlayingUri(), i);
                    }
                    //跳过当前正在使用的
                    continue;
                }
                if(codec.getPlayingUri() != null && localPaths.contains(codec.getPlayingUri())){
                    availableCodecs[i] = false;
                    TMP_URLS_NEED_RELOAD.remove(codec.getPlayingUri());
                    URL_PLAYER_INDEX.put(codec.getPlayingUri(), i);
                }else{
                    availableCodecs[i] = true;
                }
            }

            int codecInx = 0;
            for(String local: TMP_URLS_NEED_RELOAD){
                Codec ava = null;
                while(codecInx < codecCount){
                    if(codecInx == playerIndex || !availableCodecs[codecInx]){
                        //如果是播放中或已占用 - 跳过
                        codecInx++;
                        continue;
                    }
                    if(codecInx >= mCodecList.size()){
                        ava = initPlayerCodec(getContext());
                        mCodecList.add(ava);
                    }else{
                        ava = mCodecList.get(codecInx);
                        ava.stop();
                    }
                    break;
                }
                if(ava != null){
                    ava.prepare(local, null);
                    URL_PLAYER_INDEX.put(local, codecInx);
                }else{
                    //本次未加载成功，说明已经没有继续加载的潜力，结束加载
                    break;
                }
            }

            TMP_URLS_NEED_RELOAD.clear();
        }
    }

    protected void setSurfaceReference(Surface surfaceReference) {
        this.surfaceReference = surfaceReference;
        if(surfaceReference != null){
            Platform.log().i(TAG, "setSurfaceReference ： 就绪！");
            if(getPlayerCodec() != null){
                getPlayerCodec().bindSurface(surfaceReference);
            }
        }else{
            Platform.log().i(TAG, "setSurfaceReference ： 销毁！");
            if(getPlayerCodec() != null){
                getPlayerCodec().bindSurface(null);
            }
        }
    }

    protected Surface getSurfaceReference(){
        return surfaceReference;
    }

    @Override
    public void setListener(RyPlayerListener playerListener){
        mControllerListenerList.clear();

        if(playerListener == null) return;
        mControllerListenerList.add(playerListener);
    }

    @Override
    public void addListener(RyPlayerListener playerListener) {
        if(playerListener == null) return;
        if(mControllerListenerList != null){
            mControllerListenerList.add(playerListener);
        }
    }

    @Override
    public void removeListener(RyPlayerListener playerListener) {
        if(playerListener == null) return;
        int index = mControllerListenerList.indexOf(playerListener);
        if(index != -1){
            mControllerListenerList.remove(index);
        }
    }

    @Override
    public void loop(boolean loop) {
        this.loop = loop;
        for(com.sloth.player.Codec<?> codec: mCodecList){
            codec.loop(loop);
        }
    }

    @Override
    public boolean isLoop(){
        return loop;
    }

    public int getScaleType() {
        return scaleType;
    }

    @Override
    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    public long getClosePreviewDelay() {
        return closePreviewDelay;
    }

    public void setClosePreviewDelay(long closePreviewDelay) {
        this.closePreviewDelay = closePreviewDelay;
    }

    public com.sloth.player.Codec<?> getPlayerCodec() {
        return (playerIndex >= 0 && playerIndex < mCodecList.size())
                ? mCodecList.get(playerIndex)
                : null;
    }

    public PlayerView getPlayerView() {
        return mPlayerView;
    }

    public void clearCodecs() {
        mCodecList.clear();
        URL_PLAYER_INDEX.clear();
    }

    public void clearPlayerView() {
        this.mPlayerView = null;
    }

    private final Runnable HIDE_PREVIEW = ()-> {
        if(preView != null && preView.getVisibility() == View.VISIBLE){
            preView.setVisibility(View.GONE);
        }
    };

    protected void onHidePreView(){
        postDelayed(HIDE_PREVIEW, closePreviewDelay);
    }

    protected ImageView.ScaleType toImgScaleType(int scaleType) {
        Platform.log().d(TAG, "scale Type:" + scaleType);
        if(scaleType == PlayerGlobal.ScaleType.FIT_XY.code){
            return ImageView.ScaleType.FIT_XY;
        }else if(scaleType == PlayerGlobal.ScaleType.FIT_CENTER.code){
            return ImageView.ScaleType.FIT_CENTER;
        }
        return ImageView.ScaleType.FIT_XY;
    }

    protected void onApplyPlayerViewFitCenter(int videoWidth, int videoHeight) {
        Platform.log().d(TAG, "缩放模式： 等比居中");
        float vidRatio = 1.0f * videoWidth / videoHeight;
        float surfaceRatio = 1.0f * getWidth() / getHeight();
        LayoutParams params = (LayoutParams) mPlayerView.getLayoutParams();
        if(vidRatio > surfaceRatio){
            //视频比容器更扁，以容器width为锚点缩放
            params.width = LayoutParams.MATCH_PARENT;
            params.height = (int) (getWidth() / vidRatio);
            mPlayerView.setLayoutParams(params);
        }else if(vidRatio < surfaceRatio){
            //视频比容器更窄，以容器height为锚点缩放
            params.width = (int) (getHeight() * vidRatio);
            params.height = LayoutParams.MATCH_PARENT;
            mPlayerView.setLayoutParams(params);
        }else{
            //比例一致 或 未知比例
            //无脑填充满
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;
            mPlayerView.setLayoutParams(params);
        }
    }

    protected void onApplyPlayerViewFitXY(int videoWidth, int videoHeight) {
        Platform.log().d(TAG, "缩放模式： 铺满:");
        //所有播放器默认都支持铺满，不用特殊处理
    }

}
