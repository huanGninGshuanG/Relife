package com.hfad.relife.Adapter;

/*
 * Created by 18359 on 2017/12/13.
 */

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hfad.relife.Alarm.AlarmManagerUtil;
import com.hfad.relife.R;
import com.hfad.relife.Task.TaskItem;
import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskAdapter extends ArrayAdapter<TaskItem> implements BottomSheetTimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private ArrayList<TaskItem> dataSet;
    private TdListDbAdapter mTdListDbAdapter;
    private FragmentManager fm;
    private static final boolean USE_BUILDERS = false;
    private static final String TAG = "TaskAdapter";
    private String date;
    private TaskItem dataModel;
    private TaskItem choosedModel;
    Context mContext;
    private int alarmId = 0;//闹钟的id

    // View lookup cache
    private static class ViewHolder {
        TextView taskName;
        ImageView delIcon;
        ImageView calendarIcon;
        CheckBox cbox;
        TextView dateView;
    }

    public TaskAdapter(ArrayList<TaskItem> data, Context context, TdListDbAdapter tdListDbAdapter, FragmentManager fm) {
        super(context, R.layout.task_list_row_item, data);
        this.mTdListDbAdapter = tdListDbAdapter;
        this.dataSet = data;
        this.mContext = context;
        this.fm = fm;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        dataModel = getItem(position);
        final ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_list_row_item, parent, false);
            viewHolder.taskName = (TextView) convertView.findViewById(R.id.txtView);
            viewHolder.delIcon = (ImageView) convertView.findViewById(R.id.delIcon);
            viewHolder.cbox = (CheckBox) convertView.findViewById(R.id.chckBox);
            viewHolder.calendarIcon = (ImageView) convertView.findViewById(R.id.setDate);
            viewHolder.dateView = (TextView) convertView.findViewById(R.id.dateView);
            result = convertView;
            convertView.setTag(viewHolder); //This is currently not used. This will be useful while reusing listeners
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.taskName.setText(dataModel.getTask());
        if (dataModel.getDeadlineDate() == null) {
            viewHolder.dateView.setVisibility(View.GONE);
        } else {
            viewHolder.dateView.setText(dataModel.getDeadlineDate());
            viewHolder.dateView.setVisibility(View.VISIBLE);
        }
        viewHolder.cbox.setChecked(dataModel.getStatus());

        //设置点击事件
        viewHolder.cbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cbox.isChecked()) {
                    dataModel.setStatus(true);
                    mTdListDbAdapter.updateTaskItem(dataModel);
                    //sqlDB.execSQL("UPDATE ToDoList SET Status='true' WHERE Task='"+dataModel.getTask()+"'");
                } else {
                    dataModel.setStatus(false);
                    mTdListDbAdapter.updateTaskItem(dataModel);
                    //sqlDB.execSQL("UPDATE ToDoList SET Status='false' WHERE Task='"+dataModel.getTask()+"'");
                }
            }
        });

        viewHolder.delIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * On clicking delete ImageView, this function is invoked
                 * Code for adding Task is in MainActivity.java
                 */
                final int positionOfTaskInList = dataSet.indexOf(dataModel);
                final TaskItem delElem = dataSet.remove(positionOfTaskInList);
                notifyDataSetChanged();
                mTdListDbAdapter.deleteTaskItemByName(delElem.getTask());
                //sqlDB.execSQL("DELETE FROM ToDoList WHERE Task='"+delElem.getTask()+"'");

                /*
                 * Code for Snackbar display and UNDO functionality
                 */
                final Snackbar deleteSB = Snackbar.make(view, "Task deleted", Snackbar.LENGTH_LONG);
                deleteSB.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataSet.add(positionOfTaskInList, delElem);
                        notifyDataSetChanged();
                        mTdListDbAdapter.createTaskItem(delElem);
                        //sqlDB.execSQL("INSERT INTO ToDoList VALUES('"+delElem.getTask()+"','"+delElem.getStatus()+"','"+delElem.getDeadlineDate()+"')");
                    }
                });
                deleteSB.setActionTextColor(getContext().getResources().getColor(R.color.colorAccent));
                deleteSB.show();
            }
        });

        viewHolder.calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * This part will open the dialog box containing  layout inflated and
                 * Set Date, Clear Date and Cancel buttons
                 */
                choosedModel = dataSet.get(position);
                DialogFragment dialog = createDialog();
                dialog.show(fm, TAG);
                Log.d("TaskAdapter", "called DialogFragment");
            }
        });
        //viewHolder.info.setTag(position);
        return convertView;
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        date = DateFormat.getTimeFormat(getContext()).format(cal.getTime());
        choosedModel.setDeadlineDate(date);
        notifyDataSetChanged();
        try {
            mTdListDbAdapter.updateTaskItem(choosedModel);
            //sqlDB.execSQL("UPDATE ToDoList SET DeadlineDate = '" + date + "' WHERE Task='"+choosedModel.getTask()+"'");
            Toast.makeText(getContext(), "update:" + date, Toast.LENGTH_SHORT).show();
            AlarmManagerUtil.setAlarm(getContext(), 0, Integer.valueOf(date.split(":")[0]),
                    Integer.valueOf(date.split(":")[1].split(" ")[0]), alarmId++, 0, choosedModel.getTask(), 0);
        } catch (SQLiteException e) {
            Toast.makeText(getContext(), e + "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        date = DateFormat.getDateFormat(getContext()).format(cal.getTime());
        choosedModel.setDeadlineDate(date);
        notifyDataSetChanged();
        try {
            mTdListDbAdapter.updateTaskItem(choosedModel);
            //sqlDB.execSQL("UPDATE ToDoList SET DeadlineDate = '" + date + "' WHERE Task='"+choosedModel.getTask()+"'");
            Toast.makeText(getContext(), "update:" + date, Toast.LENGTH_SHORT).show();
        } catch (SQLiteException e) {
            Toast.makeText(getContext(), e + "", Toast.LENGTH_SHORT).show();
        }
    }

    private DialogFragment createDialog() {
            return createDialogWithBuilders();
    }

    private DialogFragment createDialogWithBuilders() {
        BottomSheetPickerDialog.Builder builder;
        boolean custom = true;
        boolean customDark = true;

        Calendar now = Calendar.getInstance();
        builder = new GridTimePickerDialog.Builder(
                TaskAdapter.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getContext()));
        GridTimePickerDialog.Builder gridDialogBuilder =
                (GridTimePickerDialog.Builder) builder;
        if (custom || customDark) {
            gridDialogBuilder.setHeaderTextColorSelected(0xFFFF4081)
                    .setHeaderTextColorUnselected(0x4AFF4081)
                    .setTimeSeparatorColor(0xFF000000)
                    .setHalfDayButtonColorSelected(0xFFFF4081)
                    .setHalfDayButtonColorUnselected(0x4AFF4081);
        }
        return builder.build();
    }
}