package com.hfad.relife.Task;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hfad.relife.Adapter.TaskAdapter;
import com.hfad.relife.Adapter.TdListDbAdapter;
import com.hfad.relife.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 18359 on 2017/12/13.
 */

public class TaskActivity extends AppCompatActivity {
    private List<TaskItem> mainList=new ArrayList<TaskItem>();
    private ListView mainListView=null;
    private ArrayAdapter ad=null;
    private EditText addTaskEditText=null;
    private FloatingActionButton addTaskButton=null;
    private TdListDbAdapter mTdListDbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mTdListDbAdapter = new TdListDbAdapter(this);
        mTdListDbAdapter.open();
        //Toast.makeText(this,"Click on task to delete it!",Toast.LENGTH_SHORT).show();

        /**
         * This section retrieves data from DB and adds it to ArrayList @existingTasks
         */
        Cursor existingTasks = mTdListDbAdapter.fetchAllTaskItem();
        //Cursor existingTasks=sqlDB.rawQuery("SELECT * FROM ToDoList",null);
        if(existingTasks!=null){
            if(existingTasks.moveToFirst()){
                do{
                    if(existingTasks.isNull(existingTasks.getColumnIndex(TdListDbAdapter.COL_DEADLINE))){
                        mainList.add( new TaskItem( existingTasks.getString( existingTasks.getColumnIndex(TdListDbAdapter.COL_TASK)) , Boolean.valueOf( existingTasks.getString(existingTasks.getColumnIndex(TdListDbAdapter.COL_STATUS)) ) , null ) );
                    }
                    else{
                        mainList.add( new TaskItem( existingTasks.getString( existingTasks.getColumnIndex(TdListDbAdapter.COL_TASK)) , Boolean.valueOf( existingTasks.getString(existingTasks.getColumnIndex(TdListDbAdapter.COL_STATUS)) ) , existingTasks.getString( existingTasks.getColumnIndex(TdListDbAdapter.COL_DEADLINE)) ) );
                    }

                }while (existingTasks.moveToNext());
            }
        }
        Toast.makeText(this,mainList.size()+" tasks present",Toast.LENGTH_SHORT).show();

        mainListView=(ListView) findViewById(R.id.MainListView);
        ad=new TaskAdapter((ArrayList) mainList,getApplicationContext(), mTdListDbAdapter,getSupportFragmentManager());

        if(mainListView!=null) {
            mainListView.setAdapter(ad);
        }

        addTaskEditText=(EditText) findViewById(R.id.AddTaskEditText);
        addTaskButton=(FloatingActionButton) findViewById(R.id.AddTaskButton);
        addTaskButton.setBackgroundResource(R.color.colorPrimaryDarkComplementAlpha);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!addTaskEditText.getText().toString().equals("")){
                    try{
                        mTdListDbAdapter.createTaskItem(addTaskEditText.getText().toString().trim(),1,null);
                        //sqlDB.execSQL("INSERT INTO ToDoList VALUES('"+addTaskEditText.getText().toString().trim()+"','false',NULL)");
                        mainList.add( new TaskItem( addTaskEditText.getText().toString().trim() , false , null) );
                        addTaskEditText.setText("");
                        ad.notifyDataSetChanged();
                    }
                    catch (SQLiteException e){
                        Toast.makeText(getApplicationContext(),e+"",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mTdListDbAdapter.close();
    }
}
