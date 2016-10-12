package org.webrtc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

/**
 * Created by renyuzhuo on 16-9-27.
 */
public class ScreenCapturer implements VideoCapturer,
        SurfaceTextureHelper.OnTextureFrameAvailableListener {

    private SurfaceTextureHelper surfaceTextureHelper;
    private Context applicationContext;
    public static CapturerObserver capturerObserver;

    private Activity baseActivity;
    public Surface surface;
    private int displayHeight;
    private int displayWidth;
    private VirtualDisplay virtualDisplay;
    private int requestedWidth;
    private int requestedHeight;
    private final int START_SCREEN = 1;
    MediaProjectionManager mediaProjectionManager;
    private SurfaceTexture surfaceTexture;

    /**
     * 创建ScreenCapturer
     *
     * @param baseActivity 继承自BaseActivity的子类
     */
    public ScreenCapturer(Activity baseActivity) {
        this.baseActivity = baseActivity;
    }

    /**
     * 是否已经初始化成功
     *
     * @return 初始化成功：true
     */
    private boolean isInitialized() {
        return applicationContext != null && capturerObserver != null;
    }

    /**
     * 由JNI层调用的初始化
     */
    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext,
                           CapturerObserver capturerObserver) {
        if (applicationContext == null) {
            throw new IllegalArgumentException("applicationContext not set.");
        }
        if (capturerObserver == null) {
            throw new IllegalArgumentException("capturerObserver not set.");
        }
        if (isInitialized()) {
            throw new IllegalStateException("Already initialized");
        }
        this.applicationContext = applicationContext;
        this.capturerObserver = capturerObserver;
        this.surfaceTextureHelper = surfaceTextureHelper;

        Display display = baseActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        displayWidth = size.x;
        displayHeight = size.y;

        surfaceTexture = surfaceTextureHelper.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(displayWidth, displayHeight);
        surface = new Surface(surfaceTexture);
    }

    /**
     * 开始捕捉
     */
    @Override
    public void startCapture(int requestedWidth, int requestedHeight, int requestedFramerate) {
        this.requestedWidth = requestedWidth;
        this.requestedHeight = requestedHeight;
        startScreenCapture();
        // 设置Surface监听
        this.surfaceTextureHelper.startListening(this);
    }

    /**
     * 向用户询问是够允许屏幕录像
     */
    private void startScreenCapture() {
        ScreenBase.ScreenBaseHealper.screenCapturer = this;
        mediaProjectionManager = (MediaProjectionManager) baseActivity.getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        baseActivity.startActivityForResult(intent, START_SCREEN);
    }

    /**
     * 停止屏幕录像
     *
     * @throws InterruptedException 中断异常
     */
    @Override
    public void stopCapture() throws InterruptedException {
        Log.d("rlog", "关闭连接，关闭屏幕录像");
        if (virtualDisplay == null) {
            return;
        }
        virtualDisplay.release();
        virtualDisplay = null;
    }

    @Override
    public void onOutputFormatRequest(final int width, final int height, final int framerate) {
        capturerObserver.onOutputFormatRequest(width, height, framerate);
    }

    /**
     * 修改录屏格式
     */
    @Override
    public void changeCaptureFormat(int width, int height, int framerate) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isScreencast() {
        return virtualDisplay != null;
    }

    @Override
    public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
        capturerObserver.onTextureFrameCaptured(requestedHeight, requestedWidth,
                oesTextureId, transformMatrix, 0, timestampNs);
    }

    /**
     * 用户允许录屏，开始录屏
     *
     * @param resultCode resultCode
     * @param data       数据
     */
    public void startCapturerBegin(int resultCode, Intent data) {
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        DisplayMetrics metrics = baseActivity.getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;

        virtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
                displayWidth, displayHeight, density,
                flags, surface, null, null);
    }
}
