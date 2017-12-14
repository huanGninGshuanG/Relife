package com.hfad.relife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hfad.relife.Note.NoteActivity;
import com.hfad.relife.Task.TaskActivity;

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
}
