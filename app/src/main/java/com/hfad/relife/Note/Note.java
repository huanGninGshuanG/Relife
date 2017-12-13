package com.hfad.relife.Note;

import java.io.Serializable;

/**
 * Created by 18359 on 2017/11/20.
 */

public class Note implements Serializable {
    private int mId;
    private String mContent;
    private int mImportant;
    private String mDateTime;
    private int mType;//便签类型，1纯文本，2Html

    public Note(int id,String content, int important, String dateTime){
        mId = id;
        mContent = content;
        mImportant = important;
        mDateTime = dateTime;
    }

    public Note(int id, String content, int important){
        mId = id;
        mContent = content;
        mImportant = important;
    }

    public int getId(){
        return mId;
    }

    public void setId(int id){
        mId = id;
    }

    public String getContent(){
        return mContent;
    }

    public void setContent(String content){
        mContent = content;
    }

    public int getImportant(){
        return mImportant;
    }

    public void setImportant(int important){
        mImportant = important;
    }

    public String getDateTime(){
        return mDateTime;
    }

    public int getType(){
        return mType;
    }

    public void setType(int type){
        mType = type;
    }

    public void setDateTime(String dateTime){
        mDateTime = dateTime;
    }
}
