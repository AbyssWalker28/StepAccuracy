package it.unimib.stepaccuracy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.util.ArrayList;

import it.unimib.stepaccuracy.R;
import it.unimib.stepaccuracy.constants;
import it.unimib.stepaccuracy.pedometer.simplePedometer_1;
import it.unimib.stepaccuracy.sensor.SensorServiceAcc;



public class simplePedometer_1_activity extends AppCompatActivity {

    private static final String TAG = "simplePedometer_1_act";
    private int step = 0;
    private int step_percent = 0;
    private float distance = 0;
    private int time = 0;
    private int total_step = 0;

    private String date;
    private String main_date = "";

    private boolean timer = false;
    private boolean goal = false;

    public class SensorsValuesBroadcastReceiver extends BroadcastReceiver {
        String receiver = "";

        public SensorsValuesBroadcastReceiver() {}
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("acc")){
                SharedPreferences sharedPref = getSharedPreferences(constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
                receiver = intent.getStringExtra("acc");
                step = simplePedometer_1.updateStep(receiver);

                mSensorValuesTextView.setText("" + step);
                distance = (float)(step * 0.65);

                if(sharedPref.getString("preferences_distance", "").equals("m"))
                    distanceText.setText("" + (int)distance + "m");
                else
                    distanceText.setText("" + round(distance/1000) + "km");

                float temp = (float) step / total_step;
                step_percent = (int) (temp * 100);

                if(step_percent < 100 && goal == false){
                    viewProgress.setText(step_percent + "%");
                    Progressbar.setProgress(step);
                }
                else if(step_percent >= 100 && goal == false){
                    goal = true;
                    viewProgress.setText("100%");
                    Progressbar.setProgress(step);

                    Snackbar.make(findViewById(android.R.id.content), "Hai raggiunto il tuo obbiettivo giornaliero!!", Snackbar.LENGTH_LONG).show();
                    /*Context context2 = getApplicationContext();
                    CharSequence text = "Hai raggiunto il tuo obbiettivo giornaliero!!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context2, text, duration);
                    toast.show();*/
                }



                saveStep();
            }
        }
    }

    private TextView mSensorValuesTextView;
    private TextView distanceText;
    private TextView timeText;
    private TextView viewProgress;
    private TextView goalStep;
    private ProgressBar Progressbar;
    private TextView accuracy;
    private SensorsValuesBroadcastReceiver mSensorsValuesBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_pedometer1);
        mSensorValuesTextView = findViewById(R.id.id_addressText);
        viewProgress = findViewById(R.id.text_view_progress);
        distanceText = findViewById(R.id.id_distanceText);
        timeText = findViewById(R.id.id_timeText);
        Progressbar = findViewById(R.id.progressBar);
        goalStep = findViewById(R.id.goal_step);


        SharedPreferences sharedPref = getSharedPreferences(constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        main_date = sharedPref.getString("date_pedometer", "");
        date = sharedPref.getString("date_pedometer_1", "");
        if(sharedPref.contains("step_pedometer_1") && main_date.equals(date)){
            step = sharedPref.getInt("step_pedometer_1", 0);
            time = sharedPref.getInt("time_pedometer_1", 0);
            distance = sharedPref.getFloat("distance_pedometer_1",0);
            total_step = sharedPref.getInt("total_daily_step", 9000);
            Progressbar.setMax(total_step);
            goalStep.setText(total_step + "");
            simplePedometer_1.setSTEP(step);
            //mSensorValuesTextView.setText(prev_step + "");
        }
        else {
            editor.putString("date_pedometer_1", main_date);
            editor.apply();
            simplePedometer_1.setSTEP(0);
            total_step = sharedPref.getInt("total_daily_step", 9000);
            goalStep.setText(total_step + "");
        }

        if(sharedPref.contains("step_dataset_1")){
            accuracy = findViewById(R.id.accuracy2);
            float accuracy_perc = sharedPref.getInt("step_dataset_1", 0);
            accuracy_perc = (((accuracy_perc * 100) / 500) - 100);
            accuracy.setText((100 - accuracy_perc) + "%");
        }
        calculate_time();

        Intent intent = new Intent(this, SensorServiceAcc.class);
        intent.putExtra("type", "1");
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorsValuesBroadcastReceiver = new SensorsValuesBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("it.unimib.stephaccuracy");
        registerReceiver(mSensorsValuesBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mSensorsValuesBroadcastReceiver);
        timer = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void saveStep(){
        SharedPreferences sharedPref = getSharedPreferences(constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("step_pedometer_1", step);
        editor.putFloat("distance_pedometer_1", distance);
        //editor.putString("date_pedometer", date);
        editor.apply();
    }

    private void calculate_time(){
        SharedPreferences sharedPref = getSharedPreferences(constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        ImageView x = findViewById(R.id.step_icon);
        Thread t=new Thread(){
            @Override
            public void run(){
                while(timer == false){
                    try{
                        Thread.sleep(1000); // = 1s
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                if(time % 2 == 0)
                                    x.setImageResource(R.drawable.footprints);
                                else
                                    x.setImageResource(R.drawable.footprints_white);
                                time++;

                                if(sharedPref.getString("preferences_time", "").equals("sec"))
                                    timeText.setText("" + time + " s");
                                else
                                    timeText.setText("" + time/60 + " min");

                                editor.putInt("time_pedometer_1", time);
                                editor.apply();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                interrupt();
            }
        };
        t.start();
    }

    public static float round(float d) {
        return BigDecimal.valueOf(d).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
    }


}