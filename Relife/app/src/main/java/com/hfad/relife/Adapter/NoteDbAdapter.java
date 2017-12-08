package com.hfad.relife.Adapter;


import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.hfad.relife.Note.Note;

/**
 * Created by 18359 on 2017/11/20.
 */

public class NoteDbAdapter {
    //数据库中各个列的名称
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";
    public static final String COL_DATETIME = "last_modified_time";
    //相关索引
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;
    public static final int INDEX_DATETIME = INDEX_ID + 3;
    //数据库名称、表名称、数据库板本
    private static final String DB_NAME = "Relife";
    private static final int DB_VERSION = 1;
    private static String TABLE_NAME = "tb1_note";
    //创建数据库表的语句
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT," +
                    COL_IMPORTANT + " INTEGER ," +
                    COL_DATETIME + " TEXT "+" );";
    private static final String UPGRADING_DATABASE = "DROP TABLE IF EXISTS" + TABLE_NAME;

    private RelifeDatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDb;
    private final Context mContext;

    public NoteDbAdapter(Context context){
        mContext = context;
    }

    //open database
    public void open() throws SQLiteException {
        mDatabaseHelper = new RelifeDatabaseHelper(mContext);
        mDb = mDatabaseHelper.getWritableDatabase();
    }

    //close database
    public void close(){
        if(mDatabaseHelper!=null){
            mDatabaseHelper.close();
        }
    }

    //创建便签
    public long createNote(String content, boolean important, String dateTime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CONTENT, content);
        contentValues.put(COL_IMPORTANT,important?1:0);
        contentValues.put(COL_DATETIME,dateTime);
        return mDb.insert(TABLE_NAME,null,contentValues);
    }

    //创建便签重载
    public long createNote(Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CONTENT,note.getContent());
        contentValues.put(COL_IMPORTANT,note.getImportant());
        contentValues.put(COL_DATETIME,note.getDateTime());
        return mDb.insert(TABLE_NAME,null,contentValues);
    }

    //根据ID取出便签
    public Note fetchNoteById(int id){
        Cursor cursor = mDb.query(TABLE_NAME,
                new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT,COL_DATETIME},
                COL_ID+"=?", new String[]{String.valueOf(id)}
                ,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return new Note(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT),
                cursor.getString(INDEX_DATETIME));
    }

    //根据ID取出便签
    public Cursor fetchAllNotes(){
        Cursor cursor = mDb.query(TABLE_NAME,
                new String[]{COL_ID,COL_CONTENT,COL_IMPORTANT,COL_DATETIME},
                null,null,null,null,"last_modified_time desc");
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    //根据便签对象取出标签
    public int updateNote(Note note){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CONTENT,note.getContent());
        contentValues.put(COL_IMPORTANT,note.getImportant());
        contentValues.put(COL_DATETIME,note.getDateTime());
        return mDb.update(TABLE_NAME, contentValues,
                COL_ID+"=?", new String[]{String.valueOf(note.getId())});
    }

    //通过便签ID进行删除
    public void deleteNoteById(int id){
        mDb.delete(TABLE_NAME,COL_ID+"=?",new String[]{String.valueOf(id)});
    }

    //删除所有便签
    public void deleteAllNote(){
        mDb.delete(TABLE_NAME,null,null);
    }

    private static class RelifeDatabaseHelper extends SQLiteOpenHelper{
        public RelifeDatabaseHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database){
            database.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
            database.execSQL(UPGRADING_DATABASE);
            onCreate(database);
        }
    }
}
