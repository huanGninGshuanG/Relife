package com.hfad.relife;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hfad.relife.Adapter.NoteDbAdapter;
import com.hfad.relife.Adapter.TdListDbAdapter;
import com.hfad.relife.Network.ServerCall;
import com.hfad.relife.Network.UrlConstants;

import java.util.HashMap;

public class AsyncActivity extends AppCompatActivity {

    private Cursor mCursor;
    private Button async;
    private static NoteDbAdapter mNoteDbAdapter;
    private static TdListDbAdapter mTdlistDbAdapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        async = (Button)findViewById(R.id.async);
        mNoteDbAdapter = new NoteDbAdapter(this);
        mTdlistDbAdapter = new TdListDbAdapter(this);
        mNoteDbAdapter.open();
        mTdlistDbAdapter.open();
    }

    public void onClickAsyncNoteActivity(View view){
        mCursor = mNoteDbAdapter.fetchAllNotes();
        if (mCursor == null || mCursor.isClosed()) {
            if (mCursor == null) {
                Log.d("NoteActivity", "newCursor is null");
                Toast.makeText(AsyncActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
            } else if (mCursor.isClosed()){
                Log.d("NoteActivity", "newCursor is closed");
                Toast.makeText(AsyncActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            if(mCursor.moveToFirst()){
                do{
                    String content = mCursor.getString(mCursor.getColumnIndex(NoteDbAdapter.COL_CONTENT));
                    int important = mCursor.getInt(mCursor.getColumnIndex(NoteDbAdapter.COL_IMPORTANT));
                    //int id = mCursor.getInt(mCursor.getColumnIndex(NoteDbAdapter.COL_ID));
                    String time = mCursor.getString(mCursor.getColumnIndex(NoteDbAdapter.COL_DATETIME));
                    Log.d("NoteActivity", content + important);
                    String url = UrlConstants.BASE_URL + UrlConstants.INSERTNOTE;
                    insertNote(url, important, content, time);
                }while(mCursor.moveToNext());
            }
        }
    }

    public void onClickAsyncTdlistActivity(View view){
        mCursor = mTdlistDbAdapter.fetchAllTaskItem();
        if (mCursor == null || mCursor.isClosed()) {
            if (mCursor == null) {
                Log.d("TaskActivity", "newCursor is null");
                Toast.makeText(AsyncActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
            } else if (mCursor.isClosed()){
                Log.d("TaskActivity", "newCursor is closed");
                Toast.makeText(AsyncActivity.this, "newCursor is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            if(mCursor.moveToFirst()){
                do{
                    String task = mCursor.getString(mCursor.getColumnIndex(TdListDbAdapter.COL_TASK));
                    int status = mCursor.getInt(mCursor.getColumnIndex(TdListDbAdapter.COL_STATUS));
                    //int id = mCursor.getInt(mCursor.getColumnIndex(NoteDbAdapter.COL_ID));
                    String time = mCursor.getString(mCursor.getColumnIndex(TdListDbAdapter.COL_DEADLINE));
                    Log.d("TaskActivity", task + status);
                    String url = UrlConstants.BASE_URL + UrlConstants.INSERTTASK;
                    insertTask(url, status, task, time);
                }while(mCursor.moveToNext());
            }
        }
    }

    private void insertTask(String url, final int status, final String task, final String time){
        (new AsyncTask<String,Void,String>(){
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                //pd = ProgressDialog.show(AsyncActivity.this,"","");
            }

            @Override
            protected String doInBackground(String... strings){
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("task", task);
                hashMap.put("status",String.valueOf(status));
                hashMap.put("deadlinedate",time);
                String result = ServerCall.getJsonFromUrl(strings[0], hashMap);
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                //pd.dismiss();
                int count = Integer.parseInt(result.trim());
                if(count>0){
                    Toast.makeText(AsyncActivity.this, "Response:"+result, Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(url);
    }

    private void insertNote(String url, final int important, final String content, final String time){
        (new AsyncTask<String,Void,String>(){
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                //pd = ProgressDialog.show(AsyncActivity.this,"","");
            }

            @Override
            protected String doInBackground(String... strings){
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("content",content);
                hashMap.put("important",String.valueOf(important));
                hashMap.put("last_modified_time",time);
                String result = ServerCall.getJsonFromUrl(strings[0], hashMap);
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                super.onPostExecute(result);
                //pd.dismiss();
                int count = Integer.parseInt(result.trim());
                if(count>0){
                    Toast.makeText(AsyncActivity.this, "Response:"+result, Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(url);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mNoteDbAdapter.close();
    }
}
