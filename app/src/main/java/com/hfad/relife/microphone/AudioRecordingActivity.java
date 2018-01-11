package com.hfad.relife.microphone;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.hfad.relife.Adapter.AudioRecorderAdapter;
import com.hfad.relife.Adapter.AudioRecorderDbAdapter;
import com.hfad.relife.Adapter.NoteDbAdapter;
import com.hfad.relife.Note.Note;
import com.hfad.relife.Note.NoteActivity;
import com.hfad.relife.Note.NoteContentActivity;
import com.hfad.relife.R;
import com.hfad.relife.Util.AudioRecorderUtils;
import com.hfad.relife.Util.DateUtil;

import java.io.File;
import java.io.IOException;

public class AudioRecordingActivity extends AppCompatActivity implements View.OnTouchListener, AudioRecorderUtils.OnAudioStatusUpdateListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] PERMISSIONS={
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private AudioRecorderDialog recoderDialog;
    private AudioRecorderUtils recoderUtils;

    private static AudioRecorderAdapter mAudioRecorderAdapter;
    private static AudioRecorderDbAdapter mAudioRecorderDbAdapter;
    private RecyclerView mRecyclerView;
    private TextView button;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Cursor mCursor;
    private String filepath;
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

        filepath = Environment.getExternalStorageDirectory()+ "/" + DateUtil.time()+".amr";
        //filepath = Environment.getExternalStorageDirectory() + "/recoder.amr";
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }else{
            recoderUtils = new AudioRecorderUtils(new File(filepath));
            recoderUtils.setOnAudioStatusUpdateListener(this);
        }


        mAudioRecorderDbAdapter = new AudioRecorderDbAdapter(this);
        mAudioRecorderDbAdapter.open();


    }

    @Override
    protected void onResume(){
        super.onResume();
        initialRecyclerView();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mAudioRecorderDbAdapter.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == PERMISSION_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                recoderUtils = new AudioRecorderUtils(new File(filepath));
                recoderUtils.setOnAudioStatusUpdateListener(this);
            } else
            {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                mAudioRecorderDbAdapter.createNote(filepath,filepath);
                mCursor = mAudioRecorderDbAdapter.fetchAllNotes();
                mAudioRecorderAdapter.changeCursor(mCursor);
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

    public void initialRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_recorder_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCursor = mAudioRecorderDbAdapter.fetchAllNotes();
        System.out.println("Sqlite :");
        System.out.println(mCursor.getCount());
        mAudioRecorderAdapter = new AudioRecorderAdapter(this,mCursor,0);
        mAudioRecorderAdapter.setImageButtonOnClickListener(new AudioRecorderAdapter.ImageButtonOnClickListener() {
            @Override
            public void onPlay(int position) {
                mCursor.moveToPosition(position);
                mediaPlayer = new MediaPlayer();
                String filepath = mCursor.getString(mCursor.getColumnIndex(AudioRecorderDbAdapter.COL_FILEPATH));
                System.out.println(filepath);
                try {
                    mediaPlayer.setDataSource(filepath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.release();
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mAudioRecorderAdapter);

    }
}