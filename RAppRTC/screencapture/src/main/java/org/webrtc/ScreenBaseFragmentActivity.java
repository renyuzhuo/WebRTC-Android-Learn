package org.webrtc;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;

public class ScreenBaseFragmentActivity extends FragmentActivity {
    public ScreenCapturer screenCapturer;

    private final int START_SCREEN = 1;

    /**
     * 提示用户请求录屏权限
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_SCREEN) {
            if (resultCode == RESULT_OK) {
                screenCapturer.startCapturerBegin(resultCode, data);
            }
        }
    }
}
