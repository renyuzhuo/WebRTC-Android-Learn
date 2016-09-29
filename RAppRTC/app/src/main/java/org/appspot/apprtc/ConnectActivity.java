/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

import cn.renyuzhuo.rlib.NumUtil;
import cn.renyuzhuo.rlib.rlog;
import cn.renyuzhuo.rlib.rlog.DEBUG_LEVEL;

/**
 * Handles the initial setup where the user selects which room to join.
 */
public class ConnectActivity extends Activity {
    private static final String TAG = "ConnectActivity";
    private static final int CONNECTION_REQUEST = 1;
    private static final int REMOVE_FAVORITE_INDEX = 0;
    private static boolean commandLineRun = false;

    private ImageButton connectButton;
    private ImageButton addFavoriteButton;
    private EditText roomEditText;
    private ListView roomListView;
    private SharedPreferences sharedPref;
    private String keyprefVideoCallEnabled;
    private String keyprefCamera2;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefCaptureQualitySlider;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefVideoCodec;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefAudioCodec;
    private String keyprefHwCodecAcceleration;
    private String keyprefCaptureToTexture;
    private String keyprefNoAudioProcessingPipeline;
    private String keyprefAecDump;
    private String keyprefOpenSLES;
    private String keyprefDisableBuiltInAec;
    private String keyprefDisableBuiltInAgc;
    private String keyprefDisableBuiltInNs;
    private String keyprefEnableLevelControl;
    private String keyprefDisplayHud;
    private String keyprefTracing;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;
    private String keyprefRoomList;
    private String keyScreenCapture;
    private String keyCameraCapture;
    private ArrayList<String> roomList;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rlog.setTAG("rlog.RAppRTC:" + NumUtil.getRandomNum(100));
        rlog.setDebugLever(DEBUG_LEVEL.debug);

        rlog.d("进入应用");

        // Get setting keys.
        rlog.d("获取设置默认值");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefCamera2 = getString(R.string.pref_camera2_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
        keyprefVideoBitrateType = getString(R.string.pref_maxvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_maxvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
        keyprefAecDump = getString(R.string.pref_aecdump_key);
        keyprefOpenSLES = getString(R.string.pref_opensles_key);
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key);
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key);
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key);
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefTracing = getString(R.string.pref_tracing_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);
        keyScreenCapture = getString(R.string.pref_screen_key);
        keyCameraCapture = getString(R.string.pref_camera_key);

        rlog.d("加载界面");

        setContentView(R.layout.activity_connect);

        roomEditText = (EditText) findViewById(R.id.room_edittext);
        roomEditText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(
                            TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            addFavoriteButton.performClick();
                            return true;
                        }
                        return false;
                    }
                });
        roomEditText.requestFocus();
        rlog.d("获取房间输入文本框，设置监听事件，添加回车按键响应");

        roomListView = (ListView) findViewById(R.id.room_listview);
        roomListView.setEmptyView(findViewById(android.R.id.empty));
        roomListView.setOnItemClickListener(roomListClickListener);
        registerForContextMenu(roomListView);
        rlog.d("为视图注册上下文菜单");
        connectButton = (ImageButton) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectListener);
        addFavoriteButton = (ImageButton) findViewById(R.id.add_favorite_button);
        addFavoriteButton.setOnClickListener(addFavoriteListener);

        // If an implicit VIEW intent is launching the app, go directly to that URL.
        final Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction())
                && !commandLineRun) {
            boolean loopback = intent.getBooleanExtra(
                    CallActivity.EXTRA_LOOPBACK, false);
            int runTimeMs = intent.getIntExtra(
                    CallActivity.EXTRA_RUNTIME, 0);
            String room = sharedPref.getString(keyprefRoom, "");
            rlog.d("进入房间：" + room);
            connectToRoom(room, true, loopback, runTimeMs);
        }
        rlog.d("如果其他应用发送Intent启动应用，直接跳转到传递过来的URL");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connect_menu, menu);
        rlog.d("创建菜单");
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        rlog.d("创建长按已保存菜单列表菜单");
        if (v.getId() == R.id.room_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(roomList.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.roomListContextMenu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        } else {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        rlog.d("删除列表某一项");
        if (item.getItemId() == REMOVE_FAVORITE_INDEX) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            roomList.remove(info.position);
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items.
        if (item.getItemId() == R.id.action_settings) {
            rlog.d("设置按钮");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_loopback) {
            rlog.d("随意创建房间");
            connectToRoom(null, false, true, 0);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        rlog.d("onPause():后台，可见");
        String room = roomEditText.getText().toString();
        String roomListJson = new JSONArray(roomList).toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyprefRoom, room);
        editor.putString(keyprefRoomList, roomListJson);
        editor.commit();
        rlog.d("保存列表");
    }

    @Override
    public void onResume() {
        super.onResume();
        rlog.d("活跃状态");
        String room = sharedPref.getString(keyprefRoom, "");
        roomEditText.setText(room);
        roomList = new ArrayList<>();
        String roomListJson = sharedPref.getString(keyprefRoomList, null);
        if (roomListJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(roomListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    roomList.add(jsonArray.get(i).toString());
                }
                rlog.d("favorites列表初始化");
            } catch (JSONException e) {
                Log.e(TAG, "Failed to load room list: " + e.toString());
            }
        }
        adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, roomList);
        roomListView.setAdapter(adapter);
        if (adapter.getCount() > 0) {
            roomListView.requestFocus();
            roomListView.setItemChecked(0, true);
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        rlog.d("主onActivityResult()");
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            rlog.d("设置是否在房间中");
            finish();
        }
    }

    private void connectToRoom(
            String roomId, boolean commandLineRun, boolean loopback, int runTimeMs) {
        rlog.d("连接到某一房间");
        this.commandLineRun = commandLineRun;

        // roomId is random for loopback.
        if (loopback) {
            rlog.d("随意进入某一房间");
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        String roomUrl = sharedPref.getString(
                keyprefRoomServerUrl,
                getString(R.string.pref_room_server_url_default));
        rlog.d("获取默认服务器路径");

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPref.getBoolean(keyprefVideoCallEnabled,
                Boolean.valueOf(getString(R.string.pref_videocall_default)));
        rlog.d("获取是否使用视频");

        // Use Camera2 option.
        boolean useCamera2 = sharedPref.getBoolean(keyprefCamera2,
                Boolean.valueOf(getString(R.string.pref_camera2_default)));
        rlog.d("是否使用camera2");

        // Get default codecs.
        String videoCodec = sharedPref.getString(keyprefVideoCodec,
                getString(R.string.pref_videocodec_default));
        String audioCodec = sharedPref.getString(keyprefAudioCodec,
                getString(R.string.pref_audiocodec_default));
        rlog.d("获取默认编码方式");

        // Check HW codec flag.
        boolean hwCodec = sharedPref.getBoolean(keyprefHwCodecAcceleration,
                Boolean.valueOf(getString(R.string.pref_hwcodec_default)));
        rlog.d("硬件编码");

        // Check Capture to texture.
        boolean captureToTexture = sharedPref.getBoolean(keyprefCaptureToTexture,
                Boolean.valueOf(getString(R.string.pref_capturetotexture_default)));
        rlog.d("不懂,capture to texture");

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPref.getBoolean(
                keyprefNoAudioProcessingPipeline,
                Boolean.valueOf(getString(R.string.pref_noaudioprocessing_default)));

        // Check Disable Audio Processing flag.
        boolean aecDump = sharedPref.getBoolean(
                keyprefAecDump,
                Boolean.valueOf(getString(R.string.pref_aecdump_default)));
        rlog.d("加载音频相关设置音频");

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPref.getBoolean(
                keyprefOpenSLES,
                Boolean.valueOf(getString(R.string.pref_opensles_default)));
        rlog.d("OpenSLES，不懂");

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPref.getBoolean(
                keyprefDisableBuiltInAec,
                Boolean.valueOf(getString(R.string.pref_disable_built_in_aec_default)));

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPref.getBoolean(
                keyprefDisableBuiltInAgc,
                Boolean.valueOf(getString(R.string.pref_disable_built_in_agc_default)));

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPref.getBoolean(
                keyprefDisableBuiltInNs,
                Boolean.valueOf(getString(R.string.pref_disable_built_in_ns_default)));

        // Check Enable level control.
        boolean enableLevelControl = sharedPref.getBoolean(
                keyprefEnableLevelControl,
                Boolean.valueOf(getString(R.string.pref_enable_level_control_key)));
        rlog.d("加载了一些看不懂的参数");

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        String resolution = sharedPref.getString(keyprefResolution,
                getString(R.string.pref_resolution_default));
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
                Log.e(TAG, "Wrong video resolution setting: " + resolution);
            }
        }
        rlog.d("获取视频分辨率");

        // Get camera fps from settings.
        int cameraFps = 0;
        String fps = sharedPref.getString(keyprefFps,
                getString(R.string.pref_fps_default));
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong camera fps setting: " + fps);
            }
        }
        rlog.d("获取相机采样帧数fps");

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPref.getBoolean(keyprefCaptureQualitySlider,
                Boolean.valueOf(getString(R.string.pref_capturequalityslider_default)));
        rlog.d("TODO 捕捉屏幕滑块质量标志");

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        String bitrateTypeDefault = getString(
                R.string.pref_maxvideobitrate_default);
        String bitrateType = sharedPref.getString(
                keyprefVideoBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefVideoBitrateValue,
                    getString(R.string.pref_maxvideobitratevalue_default));
            videoStartBitrate = Integer.parseInt(bitrateValue);
        }
        int audioStartBitrate = 0;
        bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
        bitrateType = sharedPref.getString(
                keyprefAudioBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefAudioBitrateValue,
                    getString(R.string.pref_startaudiobitratevalue_default));
            audioStartBitrate = Integer.parseInt(bitrateValue);
        }
        rlog.d("音视频开始比特率，Video 1700, Audio 32");

        // Check statistics display option.
        boolean displayHud = sharedPref.getBoolean(keyprefDisplayHud,
                Boolean.valueOf(getString(R.string.pref_displayhud_default)));
        rlog.d("统计显示选项");

        boolean tracing = sharedPref.getBoolean(
                keyprefTracing, Boolean.valueOf(getString(R.string.pref_tracing_default)));
        rlog.d("追踪描述：tracing");

        boolean screenCan = sharedPref.getBoolean(keyScreenCapture, Boolean.valueOf(getString(R.string.pref_screen_default)));

        boolean cameraCan = sharedPref.getBoolean(keyCameraCapture, Boolean.valueOf(getString(R.string.pref_camera_default)));

        // Start AppRTCMobile activity.
        Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
        rlog.d("创建房间：" + "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                    captureQualitySlider);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED,
                    noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(CallActivity.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);
            intent.putExtra(CallActivity.EXTRA_SCREEN_CAN, screenCan);
            intent.putExtra(CallActivity.EXTRA_CAMERA_CAN, cameraCan);

            rlog.d("将从配置文件中读到的值设置到CallActivity并启动CallActivity");

            startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }

    private boolean validateUrl(String url) {
        rlog.d("校验URL");
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
        rlog.d("提示用户URL错误");
        return false;
    }

    private final AdapterView.OnItemClickListener
            roomListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String roomId = ((TextView) view).getText().toString();
            rlog.d("点击Favorites列表并连接Room");
            connectToRoom(roomId, false, false, 0);
        }
    };

    private final OnClickListener addFavoriteListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String newRoom = roomEditText.getText().toString();
            rlog.d("添加新的Room：" + newRoom);
            if (newRoom.length() > 0 && !roomList.contains(newRoom)) {
                adapter.add(newRoom);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private final OnClickListener connectListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            rlog.d("输入房间号点击接通按钮");
            connectToRoom(roomEditText.getText().toString(), false, false, 0);
        }
    };
}
