package org.webrtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;

import org.appspot.apprtc.CallActivity;

import java.util.List;

import cn.renyuzhuo.rlib.rlog;

/**
 * Created by renyuzhuo on 16-9-18.
 */
public class ScreenCaptureActivity extends Activity implements VideoCapturer, SurfaceTextureHelper.OnTextureFrameAvailableListener {

    MediaProjectionManager mediaProjectionManager;
    private final int START_SCREEN = 1;

    private SurfaceTextureHelper surfaceTextureHelper;
    private Context applicationContext;
    public static CapturerObserver capturerObserver;

    public Surface surface;
    private int displayHeight;
    private int displayWidth;

    private boolean isInitialized() {
        return applicationContext != null && capturerObserver != null;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        rlog.d("ScreenCapture");
        if (requestCode == START_SCREEN) {
            if (resultCode == RESULT_OK) {
                startCapturerBegin(resultCode, data);
            }
        }
    }

    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        displayWidth = size.x;
        displayHeight = size.y;

        final SurfaceTexture surfaceTexture = surfaceTextureHelper.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(displayWidth, displayHeight);
        surface = new Surface(surfaceTexture);
    }

    private int requestedWidth;
    private int requestedHeight;
    private int requestedFramerate;

    @Override
    public void startCapture(int requestedWidth, int requestedHeight, int requestedFramerate) {
        this.requestedWidth = requestedWidth;
        this.requestedHeight = requestedHeight;
        this.requestedFramerate = requestedFramerate;
        startScreenCapture();
        this.surfaceTextureHelper.startListening(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenCapture() {
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, START_SCREEN);
    }

    @Override
    public void stopCapture() throws InterruptedException {
    }

    @Override
    public void onOutputFormatRequest(final int width, final int height, final int framerate) {
        capturerObserver.onOutputFormatRequest(width, height, framerate);
    }

    @Override
    public void changeCaptureFormat(int width, int height, int framerate) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isScreencast() {
        return false;
    }

    @Override
    public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
        capturerObserver.onTextureFrameCaptured(requestedHeight, requestedWidth, oesTextureId, transformMatrix, 0, timestampNs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startCapturerBegin(int resultCode, Intent data) {
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;


        Handler handler = new Handler();
        mediaProjection.createVirtualDisplay("screencap",
                displayWidth, displayHeight, density,
                flags, surface, null, handler);
    }

}
