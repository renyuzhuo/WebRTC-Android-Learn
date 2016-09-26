package org.webrtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;

import cn.renyuzhuo.rlib.rlog;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BaseScreenCaptureActivity extends Activity implements VideoCapturer, SurfaceTextureHelper.OnTextureFrameAvailableListener {

    private static VideoCapturer videoCapturer;
    private MediaProjectionManager mediaProjectionManager;
    private static final int REQUEST_USER_TO_RECORD = 1;
    private static final int CREATE_SCREEN_CAPTURE = 2;
    private Surface surface;
    private Context applicationContext;
    private CapturerObserver capturerObserver;
    private SurfaceTextureHelper surfaceTextureHelper;
    private int displayWidth;
    private int displayHeight;
    private int requestedWidth;
    private int requestedHeight;
    private Handler captureThreadHandler;
    MediaProjection mediaProjection;
    private int mResultCode;
    private Intent mResultData;
    private VirtualDisplay mVirtualDisplay;

    public static VideoCapturer getVideoCapturer() {
        return videoCapturer;
    }

    public static void setVideoCapturer(VideoCapturer videoCapturer) {
        BaseScreenCaptureActivity.videoCapturer = videoCapturer;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoCapturer = this;
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    /**
     * 录屏
     */
    public void startRecord() {
        rlog.d("请求录屏");
        requestUserToRecord();
    }

    /**
     * 请求用户同意录屏
     */
    private void requestUserToRecord() {
        rlog.d("请求用户同意录屏");
        if (mediaProjectionManager != null) {
            startActivityForResult(
                    mediaProjectionManager.createScreenCaptureIntent(),
                    CREATE_SCREEN_CAPTURE);
        } else {
            rlog.e("mediaProjectionManager == null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_USER_TO_RECORD) {
            if (resultCode != Activity.RESULT_OK) {
                rlog.e("User cancelled");
                return;
            }
            mResultCode = resultCode;
            mResultData = data;
            setUpMediaProjection();
            createScreenCapture();
        }
    }
    private void setUpMediaProjection() {
        mediaProjection = mediaProjectionManager.getMediaProjection(mResultCode, mResultData);
    }

    private void createScreenCapture() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;

        mVirtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
                displayWidth, displayHeight, density,
                flags, surface, null, captureThreadHandler);
    }

    private void stopScreenCapture() {
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    private boolean isInitialized() {
        return applicationContext != null && capturerObserver != null;
    }

    @Override
    public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
        rlog.d("发送数据");
        rlog.objects(oesTextureId, transformMatrix, timestampNs);
        capturerObserver.onTextureFrameCaptured(requestedHeight, requestedWidth, oesTextureId, transformMatrix, 0, timestampNs);
    }

    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
        rlog.d();
        rlog.d("初始化成功");
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
        captureThreadHandler =
                surfaceTextureHelper == null ? null : surfaceTextureHelper.getHandler();

        this.surfaceTextureHelper.startListening(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        displayWidth = size.x;
        displayHeight = size.y;

        final SurfaceTexture surfaceTexture = surfaceTextureHelper.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(displayWidth, displayHeight);
        surface = new Surface(surfaceTexture);
    }

    @Override
    public void startCapture(int requestedWidth, int requestedHeight, int requestedFramerate) {
        this.requestedWidth = requestedWidth;
        this.requestedHeight = requestedHeight;
        startRecord();
    }

    @Override
    public void stopCapture() throws InterruptedException {
        stopScreenCapture();
    }

    @Override
    public void onOutputFormatRequest(final int width, final int height, final int framerate) {
        capturerObserver.onOutputFormatRequest(width, height, framerate);
    }

    @Override
    public void changeCaptureFormat(int i, int i1, int i2) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isScreencast() {
        return true;
    }

}
