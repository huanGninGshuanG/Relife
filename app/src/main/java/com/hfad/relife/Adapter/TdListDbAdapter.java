package com.hfad.relife.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.hfad.relife.Task.TaskItem;

/**
 * Created by 18359 on 2017/12/13.
 */

public class TdListDbAdapter {
    //数据库中各个列的名称
    public static final String COL_TASK = "Task";
    public static final String COL_STATUS="Status";
    public static final String COL_DEADLINE="DeadlineDate";

    //数据库中各个列的索引
    public static final int INDEX_TASK = 0;
    public static final int INDEX_STATUS = INDEX_TASK + 1;
    public static final int INDEX_DEADLINE = INDEX_STATUS + 1;

    //数据库名称、表名称、数据库版本
    private static final String DB_NAME = "Relife";
    private static final int DB_VERSION = 1;
    private static String TABLE_NAME = "tb2_tdlist";

    //创建数据库表的语句
    private static final String TABLE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_TASK + " TEXT PRIMARY KEY, " +
                    COL_STATUS + " INTEGER," +
                    COL_DEADLINE + " TEXT "+" );";
    private static final String UPGRADING_DATABASE = "DROP TABLE IF EXISTS" + TABLE_NAME;

    private RelifeDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;

    public TdListDbAdapter(Context context){
        mContext = context;
    }

    //open database
    public void open() throws SQLiteException {
        mDatabaseHelper = new RelifeDatabaseHelper(mContext);
        mDb = mDatabaseHelper.getWritableDatabase();
        mDb.execSQL(TABLE_CREATE);
    }

    //close database
    public void close(){
        if(mDatabaseHelper!=null){
            mDatabaseHelper.close();
        }
    }

    //TaskItem的创建
    public long createTaskItem(String task, int status, String deadlineDate){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TASK, task);
        contentValues.put(COL_STATUS, status);
        contentValues.put(COL_DEADLINE, deadlineDate);
        return mDb.insert(TABLE_NAME, null, contentValues);
    }

    //重载TaskItem创建
    public long createTaskItem(TaskItem taskItem){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TASK, taskItem.getTask());
        contentValues.put(COL_STATUS, taskItem.getStatus());
        contentValues.put(COL_DEADLINE, taskItem.getDeadlineDate());
        return mDb.insert(TABLE_NAME, null,contentValues);
    }

    //根据task name取出TaskItem
    public TaskItem fetchTaskItemByName(String task){
        Cursor cursor = mDb.query(TABLE_NAME,
                new String[]{COL_TASK, COL_STATUS, COL_DEADLINE},
                COL_TASK+"=?", new String[]{task},
                null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return new TaskItem(
                cursor.getString(INDEX_TASK),
                cursor.getInt(INDEX_STATUS)==1,
                cursor.getString(INDEX_DEADLINE));
    }

    //取出数据库的所有TaskItem
    public Cursor fetchAllTaskItem(){
        Cursor cursor = mDb.query(TABLE_NAME,
                new String[]{COL_TASK,COL_STATUS,COL_DEADLINE},
                null,null,null,null,COL_DEADLINE+" desc");
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    //根据TaskItem更新TaskItem项
    public int updateTaskItem(TaskItem taskItem){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TASK,taskItem.getTask());
        contentValues.put(COL_STATUS, taskItem.getStatus()?1:0);
        contentValues.put(COL_DEADLINE, taskItem.getDeadlineDate());
        return mDb.update(TABLE_NAME, contentValues,
                COL_TASK+"=?", new String[]{taskItem.getTask()});
    }

    //通过TaskItem name进行删除
    public void deleteTaskItemByName(String task){
        mDb.delete(TABLE_NAME, COL_TASK+"=?",new String[]{task});
    }

    //删除所有TaskItem
    public void deleteAllTaskItem(){
        mDb.delete(TABLE_NAME,null,null);
    }

    private class RelifeDatabaseHelper extends SQLiteOpenHelper {
        public RelifeDatabaseHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database){
            database.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
            database.execSQL(UPGRADING_DATABASE);
            onCreate(database);
        }
    }
}
