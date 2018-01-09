package com.hfad.relife.Usage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.hfad.relife.R;

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
