package com.hfad.relife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hfad.relife.Note.NoteActivity;
import com.hfad.relife.Task.TaskActivity;
import com.hfad.relife.Usage.UsageActivity;
import com.hfad.relife.microphone.AudioRecordingActivity;
import com.hfad.relife.toss.TossActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickNoteActivity(View view){
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    public void onClickTdlistActivity(View view){
        Intent intent = new Intent(this, TaskActivity.class);
        startActivity(intent);
    }

    public void onClickAsyncActivity(View view){
        Intent intent = new Intent(this, AsyncActivity.class);
        startActivity(intent);
    }

    public void onClickUsageActivity(View view){
        Intent intent = new Intent(this, UsageActivity.class);
        startActivity(intent);
    }

    public void onClickTossActivity(View view){
        Intent intent = new Intent(this, TossActivity.class);
        startActivity(intent);
    }

    public void onClickAudioRecordingActivity(View view){
        Intent intent = new Intent(this, AudioRecordingActivity.class);
        startActivity(intent);
    }
}
