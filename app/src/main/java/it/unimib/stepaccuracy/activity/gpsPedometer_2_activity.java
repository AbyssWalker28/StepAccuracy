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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;

import it.unimib.stepaccuracy.pedometer.gpsPedometer_2;
import it.unimib.stepaccuracy.sensor.SensorServiceGPS;
import it.unimib.stepaccuracy.R;
import it.unimib.stepaccuracy.constants;

public class gpsPedometer_2_activity extends AppCompatActivity {

    private static final String TAG = "gpsPedometer_2_act";
    private int step = 0;
    private int step_percent = 0;
    private float distance = 0;
    private int time = 0;
    private int total_step = 0;
    private Thread t;
    private boolean timer = false;
    private boolean goal = false;
    private String date = "";
    private String main_date = "";

    public class SensorsValuesBroadcastReceiver extends BroadcastReceiver {
        String receiver = "";

        public SensorsValuesBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("gps")) {
                String row[];
                SharedPreferences sharedPref = getSharedPreferences(constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
                receiver = intent.getStringExtra("gps");
                step = gpsPedometer_2.updateStep(receiver);
                row = receiver.split(",");

                mSensorValuesTextView.setText("" + step);
                latitudeText.setText("" + row[0]);
                longitudeText.setText("" + row[1]);
                distance = gpsPedometer_2.getFullDistance();

                if(sharedPref.getString("preferences_distance", "").equals("m"))
                    distanceText.setText("" + (int)distance + "m");
                else
                    distanceText.setText("" + round(distance/1000) + "km");

                //total_step = sharedPref.getInt("total_daily_step", 100);
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
                    Context context2 = getApplicationContext();
                    CharSequence text = "Hai raggiunto il tuo obbiettivo giornaliero!!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context2, text, duration);
                    toast.show();
                }
                saveStep();
            }
        }
    }

    private TextView mSensorValuesTextView;
    private TextView longitudeText;
    private TextView latitudeText;
    private TextView distanceText;
    private TextView timeText;
    private TextView viewProgress;
    private TextView goalStep;
    private ProgressBar Progressbar;
    private SensorsValuesBroadcastReceiver mSensorsValuesBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_pedometer2);
        mSensorValuesTextView = findViewById(R.id.id_addressText);
        viewProgress = findViewById(R.id.text_view_progress);
        longitudeText = findViewById(R.id.id_Longitude);
        latitudeText = findViewById(R.id.id_Latitude);
        distanceText = findViewById(R.id.id_distanceText);
        timeText = findViewById(R.id.id_timeText);
        Progressbar = findViewById(R.id.progressBar);
        goalStep = findViewById(R.id.goal_step);

        SharedPreferences sharedPref = getSharedPreferences(constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        main_date = sharedPref.getString("date_pedometer", "");
        date = sharedPref.getString("date_pedometer_2", "");
        if(sharedPref.contains("step_pedometer_2") && main_date.equals(date)){
            step = sharedPref.getInt("step_pedometer_2", 0);
            time = sharedPref.getInt("time_pedometer_2", 0);
            distance = sharedPref.getFloat("distance_pedometer_2",0);
            total_step = sharedPref.getInt("total_daily_step", 9000);
            Progressbar.setMax(total_step);
            goalStep.setText(total_step + "");
            gpsPedometer_2.setSTEP(step, distance);
            //mSensorValuesTextView.setText(prev_step + "");
        }
        else {
            editor.putString("date_pedometer_2", main_date);
            editor.apply();
            gpsPedometer_2.setSTEP(0, 0);
            total_step = sharedPref.getInt("total_daily_step", 9000);
            goalStep.setText(total_step + "");
        }

        calculate_time();
        Intent intent = new Intent(this, SensorServiceGPS.class);
        startService(intent);
        saveStep();
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
        //t.interrupt();
        //Log.d("time", t.isInterrupted() + "");
    }

    private void saveStep(){
        SharedPreferences sharedPref = getSharedPreferences(constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("step_pedometer_2", step);
        editor.putFloat("distance_pedometer_2", distance);
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

                                editor.putInt("time_pedometer_2", time);
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