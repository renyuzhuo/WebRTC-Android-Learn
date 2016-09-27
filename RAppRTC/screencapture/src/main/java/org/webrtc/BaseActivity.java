package org.webrtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;

public class BaseActivity extends Activity {
    public ScreenCapturer screenCapturer;

    private final int START_SCREEN = 1;

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
