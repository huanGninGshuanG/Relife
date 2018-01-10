package com.hfad.relife.Usage;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.hfad.relife.R;

import java.util.HashMap;
import java.util.List;

public class UsageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.usage_container, UsageFragment.newInstance())
                    .commit();
        }
    }
}
