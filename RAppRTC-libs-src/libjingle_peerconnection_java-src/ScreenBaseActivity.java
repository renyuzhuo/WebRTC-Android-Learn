package org.webrtc;

import android.app.Activity;
import android.content.Intent;

/**
 * 屏幕录像基本Activity，若继承该Activity，
 */
public class ScreenBaseActivity extends Activity implements ScreenBase {
    /**
     * 提示用户请求录屏权限
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScreenBaseHealper.START_SCREEN) {
            if (resultCode == RESULT_OK) {
                ScreenBaseHealper.screenCapturer.startCapturerBegin(resultCode, data);
            }
        }
    }

}
