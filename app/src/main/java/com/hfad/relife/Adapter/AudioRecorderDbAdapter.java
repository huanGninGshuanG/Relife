package com.hfad.relife.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import com.hfad.relife.Note.Note;

public class AudioRecorderDbAdapter {
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_FILEPATH= "filepath";

    public static final int INDEX_ID = 0;
    public static final int INDEX_NAME = INDEX_ID + 1;
    public static final int INDEX_FILEPATH = INDEX_ID + 2;

    private static final String DB_NAME = "Relife";
    private static final int DB_VERSION = 1;
    private static String TABLE_NAME = "tb1_recorder";

    private static final String TABLE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_NAME + " TEXT," +
                    COL_FILEPATH + " TEXT " + " );";
    private static final String UPGRADING_DATABASE = "DROP TABLE IF EXISTS" + TABLE_NAME;

    private RelifeDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;

    public AudioRecorderDbAdapter(Context context){
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

    //创建便签
    public long createNote(String name, String filepath){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_FILEPATH,filepath);
        return mDb.insert(TABLE_NAME,null,contentValues);
    }


    //根据ID取出便签
    public Note fetchNoteById(int id){
        Cursor cursor = mDb.query(TABLE_NAME,
                new String[]{COL_ID,COL_NAME,COL_FILEPATH},
                COL_ID+"=?", new String[]{String.valueOf(id)}
                ,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return new Note(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_NAME),
                cursor.getInt(INDEX_FILEPATH));
    }

    //根据取出数据库的所有便签
    public Cursor fetchAllNotes(){
        Cursor cursor = mDb.query(TABLE_NAME,
                new String[]{COL_ID,COL_NAME,COL_FILEPATH},
                null,null,null,null,"filepath");
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }



    //通过便签ID进行删除
    public void deleteNoteById(int id){
        mDb.delete(TABLE_NAME,COL_ID+"=?",new String[]{String.valueOf(id)});
    }

    //删除所有便签
    public void deleteAllNote(){
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
