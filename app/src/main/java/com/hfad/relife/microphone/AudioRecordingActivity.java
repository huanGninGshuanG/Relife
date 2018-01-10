package com.hfad.relife.microphone;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import com.hfad.relife.R;
import com.hfad.relife.Util.AudioRecorderUtils;

import java.io.File;

public class AudioRecordingActivity extends AppCompatActivity implements View.OnTouchListener, AudioRecorderUtils.OnAudioStatusUpdateListener {

    private AudioRecorderDialog recoderDialog;
    private AudioRecorderUtils recoderUtils;
    private ListView listView;
    private TextView button;
    private long downT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);

        //判断SDK版本是否大于等于19，大于就让他显示，小于就要隐藏，不然低版本会多出来一个
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            //还有设置View的高度，因为每个型号的手机状态栏高度都不相同
        }

        button = (TextView) findViewById(android.R.id.button1);
        button.setOnTouchListener(this);

        recoderDialog = new AudioRecorderDialog(this);
        recoderDialog.setShowAlpha(0.98f);

        recoderUtils = new AudioRecorderUtils(new File(Environment.getExternalStorageDirectory() + "/recoder.amr"));
        recoderUtils.setOnAudioStatusUpdateListener(this);
//
//        listView = (ListView) findViewById(android.R.id.list);
//        listView.setAdapter(new AudioListAdapter(this));
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                recoderUtils.startRecord();
                downT = System.currentTimeMillis();
                recoderDialog.showAtLocation(view, Gravity.CENTER, 0, 0);
                button.setBackgroundResource(R.drawable.shape_recorder_btn_recording);
                return true;
            case MotionEvent.ACTION_UP:
                recoderUtils.stopRecord();
                recoderDialog.dismiss();
                button.setBackgroundResource(R.drawable.shape_recorder_btn_normal);
                return true;
        }
        return false;
    }

    @Override
    public void onUpdate(double db) {
        if (null != recoderDialog) {
            int level = (int) db;
            recoderDialog.setLevel(level);
            recoderDialog.setTime(System.currentTimeMillis() - downT);
        }
    }
}