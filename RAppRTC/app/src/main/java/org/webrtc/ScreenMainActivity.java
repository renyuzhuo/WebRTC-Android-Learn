package org.webrtc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.appspot.apprtc.R;
import org.appspot.apprtc.SettingsActivity;

import cn.renyuzhuo.rlib.rlog;

public class ScreenMainActivity extends ScreenCaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_main);
        rlog.d("ScreenMainActivity");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 3);
    }

    public static void createScreenMainActivity(Context context) {
        Intent intent = new Intent(context, ScreenMainActivity.class);
        context.startActivity(intent);
    }

}
