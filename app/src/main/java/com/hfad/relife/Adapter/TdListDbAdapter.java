package com.hfad.relife.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

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
                    COL_STATUS + " TEXT," +
                    COL_DEADLINE + " TEXT "+" );";
    private static final String UPGRADING_DATABASE = "DROP TABLE IF EXISTS" + TABLE_NAME;

    private RelifeDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;

    public TdListDbAdapter(Context context){
        mContext = context;
    }

    //open database
    public SQLiteDatabase open() throws SQLiteException {
        mDatabaseHelper = new RelifeDatabaseHelper(mContext);
        mDb = mDatabaseHelper.getWritableDatabase();
        return mDb;
    }

    //close database
    public void close(){
        if(mDatabaseHelper!=null){
            mDatabaseHelper.close();
        }
    }

    private static class RelifeDatabaseHelper extends SQLiteOpenHelper {
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
