package com.sloth.pinsdemo.banner;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.sankuai.waimai.router.annotation.RouterUri;
import com.sloth.banner.XBanner;
import com.sloth.banner.adapter.SimpleBannerAdapter;
import com.sloth.banner.transform.BlindsGridTransform;
import com.sloth.banner.transform.Card3DSlipTransform;
import com.sloth.banner.transform.ClipPathDrawer;
import com.sloth.banner.transform.ClipPathTransform;
import com.sloth.banner.transform.DifferentialTransform;
import com.sloth.banner.transform.EraseTransform;
import com.sloth.banner.transform.FadeScaleTransform;
import com.sloth.banner.transform.GalleryTransform;
import com.sloth.banner.transform.ParticleBloomTransform;
import com.sloth.banner.transform.ParticleFallTransform;
import com.sloth.banner.transform.Rotate3DTransform;
import com.sloth.banner.transform.StackCardTransform;
import com.sloth.functions.viewpager2.widget.ViewPager2;
import com.sloth.functions.widget.particle.bloom.BloomSceneMaker;
import com.sloth.functions.widget.particle.bloom.StarShapeDistributor;
import com.sloth.pinsdemo.R;
import com.sloth.player.PlayerConst;
import com.sloth.tools.util.ExecutorUtils;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RouterUri(path = "/banner")
public class BannerActivity extends AppCompatActivity {

    private final String TAG = "BannerActivity";
    private final List<BannerItem> data = new ArrayList<>();
    private XBanner<BannerItem> vp;
    private ViewGroup container;

    private final AtomicBoolean ready = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        container = findViewById(R.id.container);
        RadioGroup rgPlayer = findViewById(R.id.radio_player);
        RadioGroup rgScale = findViewById(R.id.radio_scale);
        RadioGroup rgSurface = findViewById(R.id.radio_surface);
        RadioGroup rgAnim = findViewById(R.id.radio_anim);

        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            clear();
        });


        findViewById(R.id.btn_resume).setOnClickListener(v -> {
            if (vp != null) {
                vp.startPlayer();
            }
        });


        findViewById(R.id.btn_pause).setOnClickListener(v -> {
            if (vp != null) {
                vp.stopPlayer();
            }
        });

        findViewById(R.id.btn_apply).setOnClickListener(v -> {
            render(rgPlayer.getCheckedRadioButtonId(), rgScale.getCheckedRadioButtonId(), rgSurface.getCheckedRadioButtonId(), rgAnim.getCheckedRadioButtonId());
        });
        List<MediaCodecInfo> codes = null;
        try {
            codes = MediaCodecUtil.getDecoderInfos(MimeTypes.VIDEO_H264, false, false);
            for (MediaCodecInfo code : codes) {
                LogUtils.d(TAG, "---- MediaCodec ----");
                LogUtils.d(TAG, "MediaCodec name:" + code.name);
                LogUtils.d(TAG, "MediaCodec codecMimeType:" + code.codecMimeType);
                LogUtils.d(TAG, "MediaCodec hardwareAccelerated:" + code.hardwareAccelerated);
                LogUtils.d(TAG, "MediaCodec softwareOnly:" + code.softwareOnly);
            }

            codes = MediaCodecUtil.getDecoderInfos(MimeTypes.AUDIO_AAC, false, false);
            for (MediaCodecInfo code : codes) {
                LogUtils.d(TAG, "---- AudioCodec ----");
                LogUtils.d(TAG, "MediaCodec name:" + code.name);
                LogUtils.d(TAG, "MediaCodec codecMimeType:" + code.codecMimeType);
                LogUtils.d(TAG, "MediaCodec hardwareAccelerated:" + code.hardwareAccelerated);
                LogUtils.d(TAG, "MediaCodec softwareOnly:" + code.softwareOnly);
            }

        } catch (MediaCodecUtil.DecoderQueryException e) {
            e.printStackTrace();
        }

        final String videoPath = getCacheDir().getAbsolutePath() + "/nana.mp4";
        LogUtils.d(TAG, "视频路径：" + videoPath);
        data.add(new BannerItem(R.mipmap.p1));
        data.add(new BannerItem(R.mipmap.p2));
        data.add(new BannerItem(R.mipmap.p3));
        data.add(new BannerItem(videoPath));
        data.add(new BannerItem(R.mipmap.p4));
        data.add(new BannerItem(videoPath));
        data.add(new BannerItem(R.mipmap.p1));
        data.add(new BannerItem(R.mipmap.p2));

        copyToSdcard(videoPath);
    }

    private void copyToSdcard(String videoPath) {
        ExecutorUtils.getNormal().submit(() -> {
            File out = new File(videoPath);
            if (!FileUtils.isFileExists(out.getParent())) {
                out.getParentFile().mkdirs();
            }
            if (!FileUtils.isFileExists(out)) {
                try {
                    out.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                InputStream ips = getAssets().open("nana.mp4");
                int total = ips.available();
                if((int)out.length() == total){
                    ips.close();
                    ready.set(true);
                    runOnUiThread(() -> ToastUtils.showShort("拷贝完毕"));
                    return;
                }
                FileOutputStream ops = new FileOutputStream(out);
                byte[] read = new byte[1024];
                int count = 0;
                while ((count = ips.read(read)) != -1) {
                    ops.write(read);
                }
                ips.close();
                ops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ready.set(true);
            runOnUiThread(() -> ToastUtils.showShort("拷贝完毕"));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }

    private void clear() {
        if (vp != null) {
            vp.destroy();
            vp = null;
        }
        container.removeAllViews();
    }

    private void render(int playerId, int scaleId, int surfaceId, int animId) {
        if(!ready.get()){
            ToastUtils.showShort("文件未拷贝完毕");
            return;
        }

        clear();
        vp = new XBanner<>(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(500, 500);
        lp.gravity = Gravity.CENTER;
        container.addView(vp, lp);

        vp.setOffscreenPageLimit(1);
        vp.autoClearCache(60000);
        vp.setVideoLoop(true);
        vp.setSnapshot(true, 0.3f);
//        vp.setPreloadOffset(1);
        vp.setPlayerType(playerId == R.id.rb_exo ? PlayerConst.PlayerType.EXO.type : PlayerConst.PlayerType.NATIVE.type);
        vp.setVideoScaleType(scaleId == R.id.rb_fitxy ? PlayerConst.ScaleType.FIT_XY.code : PlayerConst.ScaleType.FIT_CENTER.code);
        vp.setVideoSurfaceType(surfaceId == R.id.rb_surface_view ? PlayerConst.SurfaceType.Surface.code : PlayerConst.SurfaceType.Texture.code);
        vp.setAdapter(new SimpleBannerAdapter<BannerItem>(this));

        ViewPager2.PageTransformer transformer = null;

        if (animId == R.id.rb_anim_0) {
            transformer = null;
        } else if (animId == R.id.rb_anim_1) {
            GalleryTransform transform = new GalleryTransform(0.6f, 0.7f, 0);
            transform.setAlign(GalleryTransform.ALIGN_ITEMS_CENTER);
//        transform.setVerticalOffset(100f);
            transform.setOrientation(RecyclerView.HORIZONTAL);
            transform.setIsValueAnimator(true);
            transformer = transform;

        } else if (animId == R.id.rb_anim_2) {
            Rotate3DTransform transform = new Rotate3DTransform();
            transform.setOrientation(RecyclerView.VERTICAL);
            transformer = transform;
        } else if (animId == R.id.rb_anim_3) {
            FadeScaleTransform transform = new FadeScaleTransform();
//            FadeTransform transform = new FadeTransform();
            transform.setOrientation(RecyclerView.VERTICAL);
            transformer = transform;
        } else if (animId == R.id.rb_anim_4) {
            EraseTransform transform = new EraseTransform();
            transform.setOrientation(RecyclerView.HORIZONTAL);
            transformer = transform;
        } else if (animId == R.id.rb_anim_5) {
            DifferentialTransform transform = new DifferentialTransform();
            transform.setOrientation(RecyclerView.HORIZONTAL);
            transformer = transform;
        } else if (animId == R.id.rb_anim_6) {
            Card3DSlipTransform transform = new Card3DSlipTransform();
            transform.setOrientation(RecyclerView.HORIZONTAL);
            transformer = transform;
        } else if (animId == R.id.rb_anim_7) {
            BlindsGridTransform transform = new BlindsGridTransform();
            transform.setOrientation(RecyclerView.HORIZONTAL);
            transform.setNestedVideoSnapshotForExit(true);
            transformer = transform;
        } else if (animId == R.id.rb_anim_8) {
            //todo 卡顿优化
            ParticleBloomTransform transform = new ParticleBloomTransform(new BloomSceneMaker()
                    .setRadius(30)
                    .setShape(new StarShapeDistributor())
                    .setRowAndColumn(20, 16)
                    .setDuration(1000)
                    .setScaleRange(0.5f, 1.0f)
                    .setRotationSpeedRange(0.01f, 0.05f)
                    .setSpeedRange(0.1f, 0.5f)
                    .setAcceleration(0.00025f, 90)
                    .setFadeOut(800, new AccelerateInterpolator())
            );
            transform.setNestedVideoSnapshotForExit(true);
            transformer = transform;
        } else if (animId == R.id.rb_anim_9) {
            StackCardTransform transform = new StackCardTransform(4, 0.95f, 0.03f);
            transformer = transform;
        } else if (animId == R.id.rb_anim_10) {

            //todo 卡顿优化
            ParticleFallTransform transform = new ParticleFallTransform();
            transform.setNestedVideoSnapshotForExit(true);
            transformer = transform;
        } else if (animId == R.id.rb_anim_11) {
            ClipPathTransform transform = new ClipPathTransform(vp, ClipPathDrawer.Mode.SAWTOOTH_LEFT_TOP.code);
            transformer = transform;
        }

        vp.setPageTransformer(transformer);

        vp.bindData(data);

        vp.autoPlay(true);
    }

}