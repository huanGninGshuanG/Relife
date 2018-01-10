package com.hfad.relife.Adapter;

import android.app.usage.UsageStats;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hfad.relife.R;
import com.hfad.relife.Usage.CustomUsageStats;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {
    private List<CustomUsageStats> mCustomUsageStatsList = new ArrayList<>();
    private DateFormat mDateFormat = new SimpleDateFormat();

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPackageName;
        private final TextView mLastTimeUsed;
        private final ImageView mAppIcon;
        private final TextView mTotalTimeForeground;
        private final TextView mLaunchCount;


        public ViewHolder(View v) {
            super(v);
            mPackageName = (TextView) v.findViewById(R.id.textview_package_name);
            mLastTimeUsed = (TextView) v.findViewById(R.id.textview_last_time_used);
            mTotalTimeForeground = (TextView) v.findViewById(R.id.textview_total_time_in_foreground);
            mLaunchCount = (TextView) v.findViewById(R.id.textview_launch_count);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
        }

        public TextView getLastTimeUsed() {
            return mLastTimeUsed;
        }

        public TextView getPackageName() {
            return mPackageName;
        }

        public ImageView getAppIcon() {
            return mAppIcon;
        }

        public TextView getmTotalTimeForeground() {
            return mTotalTimeForeground;
        }

        public TextView getmLaunchCount() {
            return mLaunchCount;
        }
    }

    public UsageListAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.usage_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) throws IllegalArgumentException {
        UsageStats u = mCustomUsageStatsList.get(position).usageStats;
        viewHolder.getPackageName().setText(
                mCustomUsageStatsList.get(position).usageStats.getPackageName());
        long lastTimeUsed = mCustomUsageStatsList.get(position).usageStats.getLastTimeUsed();
        long totalTimeInForeground = u.getTotalTimeInForeground();
        int times = 65526;
        try {
            times = mCustomUsageStatsList.get(position).usageStats.getClass().getDeclaredField("mLaunchCount").getInt(u);
            System.out.println(times);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        viewHolder.getLastTimeUsed().setText(mDateFormat.format(new Date(lastTimeUsed)));
        viewHolder.getmTotalTimeForeground().setText(formatTime(totalTimeInForeground));
        //if (times != 65526)
        viewHolder.getmLaunchCount().setText(String.valueOf(times));
        viewHolder.getAppIcon().setImageDrawable(mCustomUsageStatsList.get(position).appIcon);
    }

    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    public void setCustomUsageStatsList(List<CustomUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
    }

    public static String formatTime(long ms) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        hour += day*24;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        return strHour + "小时" + strMinute + " 分钟 " + strSecond + " 秒";
    }
}
