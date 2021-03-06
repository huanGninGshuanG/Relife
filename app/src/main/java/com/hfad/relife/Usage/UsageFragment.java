package com.hfad.relife.Usage;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hfad.relife.Adapter.UsageListAdapter;
import com.hfad.relife.R;

import java.util.*;

/**
 * @author xuxiaofeng
 */
public class UsageFragment extends Fragment {
    private static final String TAG = UsageFragment.class.getSimpleName();

    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    UsageListAdapter mUsageListAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    Button mOpenUsageSettingButton;
    Spinner mSpinner;
    PieChart mChart;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link UsageFragment}.
     */
    public static UsageFragment newInstance() {
        UsageFragment fragment = new UsageFragment();
        return fragment;
    }

    public UsageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService("usagestats"); //Context.USAGE_STATS_SERVICE
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usage, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        mUsageListAdapter = new UsageListAdapter();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
        mChart = (PieChart) rootView.findViewById(R.id.pie_chart);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        mChart.setDrawCenterText(true);
        mChart.setCenterText("App 使用时间");
        mChart.setCenterTextTypeface(tf);

        mChart.getDescription().setEnabled(true);
        mChart.setUsePercentValues(true);
        mChart.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        mChart.setHighlightPerTapEnabled(true);

        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageListAdapter);
        mOpenUsageSettingButton = (Button) rootView.findViewById(R.id.button_open_usage_setting);
        mSpinner = (Spinner) rootView.findViewById(R.id.spinner_time_span);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            String[] strings = getResources().getStringArray(R.array.action_list);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StatsUsageInterval statsUsageInterval = StatsUsageInterval
                        .getValue(strings[position]);
                if (statsUsageInterval != null) {
                    List<UsageStats> usageStatsList =
                            getUsageStatistics(statsUsageInterval.mInterval);
                    Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
                    updateAppsList(usageStatsList);
                    updatePieChart(usageStatsList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Returns the {@link #mRecyclerView} including the time span specified by the
     * intervalType argument.
     *
     * @param intervalType The time interval by which the stats are aggregated.
     *                     Corresponding to the value of {@link UsageStatsManager}.
     *                     E.g. {@link UsageStatsManager#INTERVAL_DAILY}, {@link
     *                     UsageStatsManager#INTERVAL_WEEKLY},
     *
     * @return A list of {@link android.app.usage.UsageStats}.
     */
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());
        if (queryUsageStats.size() == 0) {
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                    getString(R.string.explanation_access_to_appusage_is_not_enabled),
                    Toast.LENGTH_LONG).show();
            mOpenUsageSettingButton.setVisibility(View.VISIBLE);
            mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }
            });
        }
        return queryUsageStats;
    }



    /**
     * Updates the {@link #mRecyclerView} with the list of {@link UsageStats} passed as an argument.
     *
     * @param usageStatsList A list of {@link UsageStats} from which update the
     *                       {@link #mRecyclerView}.
     */
    //VisibleForTesting
    void updateAppsList(List<UsageStats> usageStatsList) {
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        HashMap<String, String> appNames = getAppPackageNames();
        Iterator infoIterator = appNames.entrySet().iterator();
        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = usageStatsList.get(i);
            String name ="";
            while(infoIterator.hasNext()){
                HashMap.Entry entry = (Map.Entry) infoIterator.next();
                if (entry.getKey().equals(usageStatsList.get(i).getPackageName())){
                    System.out.println(entry.getKey());
                    name = (String) entry.getValue();
                    break;
                }
            }
            infoIterator = appNames.entrySet().iterator();
            customUsageStats.appName = name;
            try {
                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                customUsageStats.appIcon = appIcon;
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, String.format("App Icon is not found for %s",
                        customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_default_app_launcher);
            }
            if(!name.equals(""))
            customUsageStatsList.add(customUsageStats);
        }
        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        mUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    void updatePieChart(List<UsageStats> usageStatsList){
        List<PieEntry> entries = new ArrayList<>();
        float totalTime = 0.0f;
        HashMap<String, String> appNames = getAppPackageNames();
        Iterator infoIterator = appNames.entrySet().iterator();
        for (int i = 0; i<usageStatsList.size();i++){
            UsageStats u = usageStatsList.get(i);
            totalTime += u.getTotalTimeInForeground();
        }
        for (int i = 0; i < usageStatsList.size(); i++){
            UsageStats u = usageStatsList.get(i);
            float time = u.getTotalTimeInForeground()/totalTime;

            String name ="";
            while(infoIterator.hasNext()){
                HashMap.Entry entry = (Map.Entry) infoIterator.next();
                //System.out.println(String.valueOf(entry.getKey()));
                //System.out.println(u.getPackageName());
                if (entry.getKey().equals(u.getPackageName())){
                    System.out.println(entry.getKey());
                    name = (String) entry.getValue();
                }
            }
            infoIterator = appNames.entrySet().iterator();
            if (name != "")
            entries.add(new PieEntry(time,name));
        }

        //System.out.println(entries.size());
        PieDataSet set = new PieDataSet(entries,"");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        PieData data = new PieData(set);
        mChart.setData(data);
        mChart.invalidate();
    }

    private HashMap<String,String> getAppPackageNames(){
        HashMap<String,String> res = new HashMap<>();
        PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(packageManager.GET_UNINSTALLED_PACKAGES);
        for(PackageInfo packageInfo : list){
            String appName=packageInfo.applicationInfo.loadLabel(packageManager).toString();
            //System.out.println(appName);
            //获取到应用所在包的名字,即在AndriodMainfest中的package的值。
            String packageName=packageInfo.packageName;
            //System.out.println(packageName);
            if((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
                res.put(packageName,appName);
                //System.out.println(packageName);
                //System.out.println(appName);
            }
        }
        return res;
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }

    /**
     * Enum represents the intervals for {@link android.app.usage.UsageStatsManager} so that
     * values for intervals can be found by a String representation.
     *
     */
    //VisibleForTesting
    static enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }
}
