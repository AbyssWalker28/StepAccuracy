package it.unimib.stephaccuracy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class dirPedometer_3_activity extends AppCompatActivity {

        private static final String TAG = "dirPedometer_1_act";
        int step = 0;
        private int step_percent = 0;
        private float distance = 0;
        private int time = 0;
        private int total_step = 0;
        private Thread t;
        private boolean timer = false;
        private boolean goal = false;
        private String date;

        public class SensorsValuesBroadcastReceiver extends BroadcastReceiver {
            String receiver = "";


            public SensorsValuesBroadcastReceiver() {
                dirPedometer_3.setting();
            }

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.hasExtra("acc")) {
                    SharedPreferences sharedPref = getSharedPreferences(Constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
                    receiver = intent.getStringExtra("acc");
                    step = dirPedometer_3.updateStep(receiver);

                    mSensorValuesTextView.setText("Step: " + step);
                    distance = (float)(step * 0.65);
                    distanceText.setText("Distance: " + (int)distance + "m");

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
            setContentView(R.layout.activity_dir_pedometer3);
            mSensorValuesTextView = findViewById(R.id.id_addressText);
            viewProgress = findViewById(R.id.text_view_progress);
            distanceText = findViewById(R.id.id_distanceText);
            timeText = findViewById(R.id.id_timeText);
            Progressbar = findViewById(R.id.progressBar);
            goalStep = findViewById(R.id.goal_step);


            SharedPreferences sharedPref = getSharedPreferences(Constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            date = myDate.dateConvert();
            if(sharedPref.contains("step_pedometer_3")  && date.equals(sharedPref.getString("date_pedometer", ""))){
                step = sharedPref.getInt("step_pedometer_3", 0);
                time = sharedPref.getInt("time_pedometer_3", 0);
                distance = sharedPref.getFloat("distance_pedometer_3",0);
                total_step = sharedPref.getInt("total_daily_step", 9000);
                Progressbar.setMax(total_step);
                goalStep.setText(total_step + "");
                dirPedometer_3.setSTEP(step);
                //mSensorValuesTextView.setText(prev_step + "");
            }
            else{
                dirPedometer_3.setSTEP(0);
                total_step = sharedPref.getInt("total_daily_step", 9000);
                goalStep.setText(total_step + "");
            }

            if(sharedPref.contains("step_dataset_3")){
                accuracy = findViewById(R.id.accuracy2);
                float accuracy_perc = sharedPref.getInt("step_dataset_3", 0);
                accuracy_perc = (((accuracy_perc * 100) / 500) - 100);
                accuracy.setText("accuracy: " + (100 - accuracy_perc) + "%");
            }

            calculate_time();
            Intent intent = new Intent(this, SensorServiceAcc.class);
            intent.putExtra("type", "2");
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

    private void saveStep(){
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFERENCES_FILE,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("step_pedometer_3", step);
        editor.putFloat("distance_pedometer_3", distance);
        editor.putString("date_pedometer", date);
        editor.apply();
    }

    private void calculate_time(){
        SharedPreferences sharedPref = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE);
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
                                timeText.setText("Time: " + time + " s");
                                editor.putInt("time_pedometer_3", time);
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
}
