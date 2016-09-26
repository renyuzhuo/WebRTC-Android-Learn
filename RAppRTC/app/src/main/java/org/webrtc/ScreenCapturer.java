package org.webrtc;

import android.annotation.TargetApi;
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
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import org.appspot.apprtc.CallActivity;

import java.util.List;

/**
 * Created by renyuzhuo on 16-8-12.
 */
public class ScreenCapturer implements VideoCapturer, SurfaceTextureHelper.OnTextureFrameAvailableListener {

    private SurfaceTextureHelper surfaceTextureHelper;
    private Context applicationContext;
    public static CapturerObserver capturerObserver;
    private Handler cameraThreadHandler;

    private Context context;
    public Surface surface;
    private int displayHeight;
    private int displayWidth;

    public ScreenCapturer(Context context) {
        this.context = context;
    }

    private boolean isInitialized() {
        return applicationContext != null && capturerObserver != null;
    }

    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
        Log.d("RRR", "初始化initialize");
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
        this.cameraThreadHandler = surfaceTextureHelper == null ? null : surfaceTextureHelper.getHandler();

        Display display = ((CallActivity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        displayWidth = size.x;
        displayHeight = size.y;

        final SurfaceTexture surfaceTexture = surfaceTextureHelper.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(displayWidth, displayHeight);
        surface = new Surface(surfaceTexture);

        Log.d("RRR", "initialize finish");
    }

    private int requestedWidth;
    private int requestedHeight;
    private int requestedFramerate;

    @Override
    public void startCapture(int requestedWidth, int requestedHeight, int requestedFramerate) {
        Log.d("RRR", "startCapture");

        this.requestedWidth = requestedWidth;
        Log.d("RRR", "startCapture111");
        this.requestedHeight = requestedHeight;
        Log.d("RRR", "startCapture112");
        this.requestedFramerate = requestedFramerate;
        Log.d("RRR", "startCapture113");
        startScreenCapture();
        Log.d("RRR", "startCapture114");
        this.surfaceTextureHelper.startListening(this);
        Log.d("RRR", "this.surfaceTextureHelper.startListening(this)");
    }

    private final int START_SCREEN = 1;

    MediaProjectionManager mediaProjectionManager;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenCapture() {
        Log.d("RRR", "startScreenCapturer001");
        ((CallActivity) context).screenCapturer = this;
        mediaProjectionManager = (MediaProjectionManager) (context).getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        ((CallActivity) context).startActivityForResult(intent, START_SCREEN);

//        ((CallActivity) context).startScreen();
//        capturerObserver.onCapturerStarted(false);
        Log.d("RRR", "startScreenCapture002");
    }

    @Override
    public void stopCapture() throws InterruptedException {
        Log.d("RRR", "stopCapture");
    }

    @Override
    public void onOutputFormatRequest(final int width, final int height, final int framerate) {
        capturerObserver.onOutputFormatRequest(width, height, framerate);
        Log.d("RRR", "onOutputFormatRequest");
    }

    @Override
    public void changeCaptureFormat(int width, int height, int framerate) {
        Log.d("RRR", "changeCaptureFormat");
    }

    @Override
    public void dispose() {
        Log.d("RRR", "dispose");
    }

    @Override
    public boolean isScreencast() {
        return true;
    }

    @Override
    public void onTextureFrameAvailable(int oesTextureId, float[] transformMatrix, long timestampNs) {
        capturerObserver.onTextureFrameCaptured(requestedHeight, requestedWidth, oesTextureId, transformMatrix, 0, timestampNs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startCapturerBegin(int resultCode, Intent data) {
        Log.d("RRR", "open screen");
//        surfaceView = (SurfaceView) findViewById(R.id.surface);
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR;


        Handler handler = new Handler();
        Log.d("RRR", "onActivityResult()");
        mediaProjection.createVirtualDisplay("screencap",
                displayWidth, displayHeight, density,
                flags, surface, null, handler);
    }
}
