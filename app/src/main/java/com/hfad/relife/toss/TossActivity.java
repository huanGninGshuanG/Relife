package com.hfad.relife.toss;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import com.hfad.relife.R;
import com.hfad.relife.toss.custom.StereoView;

import java.lang.ref.WeakReference;

import static java.lang.Math.abs;

public class TossActivity extends AppCompatActivity implements SensorEventListener {
    private final static String LOG_TAG = TossActivity.class.getSimpleName();
    private final static int START_TOSS = 1;

    private TossHandler handler;
    private StereoView stereoView;
    private Button btnToss;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private boolean isShaked;
    private boolean isTossed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toss);
        isShaked = false;
        isTossed = false;
        handler = new TossHandler(this);

        //initialize sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        initView();
        //initStereoView();
        btnToss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stereoView.toss();
            }
        });
    }

    @Override
    protected void onPause() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onPause();

    }

    private void initView() {
        stereoView = (StereoView) findViewById(R.id.stereoView1);
        btnToss = (Button) findViewById(R.id.btn_toss);
    }

    private void initStereoView() {
        stereoView.setResistance(4f)
                .setInterpolator(new BounceInterpolator())
                .setStartScreen(2);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.d(LOG_TAG, "x = " + event.values[0] + "y =" + event.values[1] + "z = " + event.values[2]);
            for (float delta : event.values) {
                if (abs(delta) > 0.5) isTossed = true;
            }
            if (isTossed) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            handler.obtainMessage(START_TOSS).sendToTarget();
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            for (float delta : event.values) {
                if (abs(delta) > 17) isShaked = true;
            }
            if (isShaked) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            handler.obtainMessage(START_TOSS).sendToTarget();
                            Thread.sleep(501);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    private class TossHandler extends Handler {
        private WeakReference<TossActivity> mReference;
        private TossActivity mActivity;

        public TossHandler(TossActivity activity) {
            mReference = new WeakReference<TossActivity>(activity);
            if (mReference != null) {
                mActivity = mReference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mActivity.stereoView.toss();
                    mActivity.isTossed = false;
                    mActivity.isShaked = false;
                    break;
                default:
                    break;
            }
        }
    }
}
