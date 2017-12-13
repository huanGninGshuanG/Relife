package com.hfad.relife.Task;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 18359 on 2017/12/13.
 */

public class TaskItem implements Parcelable {
    private String Task;
    boolean status;
    String deadlineDate;

    TaskItem(String Task,boolean status,String deadlineDate){
        this.Task=Task;
        this.status=status;
        this.deadlineDate=deadlineDate;
    }

    TaskItem(Parcel in) {
        Task = in.readString();
        status = in.readByte() != 0;
        deadlineDate = in.readString();
    }

    public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    public String getTask() {
        return Task;
    }

    public boolean getStatus() {
        return status;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {this.Task,
                String.valueOf(this.status),
                this.deadlineDate});
    }
}
