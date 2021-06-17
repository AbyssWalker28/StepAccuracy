package it.unimib.stephaccuracy;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.widget.TextView;

public class AllPedometer_activity extends AppCompatActivity {

    private static final String TAG = "simplePedometer_1_act";

    public class SensorsValuesBroadcastReceiver extends BroadcastReceiver {
        String receiver = "";
        int step_ped_1 = 0;
        int step_ped_2 = 0;
        int step_ped_3 = 0;

        public SensorsValuesBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("acc")) {
                receiver = intent.getStringExtra("acc");
                step_ped_1 = simplePedometer_1.updateStep(receiver);
                step_ped_3 = dirPedometer_3.updateStep(receiver);
                mSensorValuesTextView.setText(step_ped_1 + "");
                mSensorValuesTextView3.setText(step_ped_3 + "");
            }
            else if(intent.hasExtra("gps")){
                receiver = intent.getStringExtra("gps");
                step_ped_2 = gpsPedometer_2.updateStep(receiver);
                mSensorValuesTextView2.setText(step_ped_2 + "");
            }
        }

    }

    private TextView mSensorValuesTextView;
    private TextView mSensorValuesTextView2;
    private TextView mSensorValuesTextView3;
    private SensorsValuesBroadcastReceiver mSensorsValuesBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pedometer);

        mSensorValuesTextView = findViewById(R.id.sensor_values);
        mSensorValuesTextView2 = findViewById(R.id.sensor_values2);
        mSensorValuesTextView3 = findViewById(R.id.sensor_values3);

        simplePedometer_1.setSTEP(0);
        dirPedometer_3.setSTEP(0);
        gpsPedometer_2.setSTEP(0, 0);

        Intent intent = new Intent(this, SensorServiceAcc.class);
        intent.putExtra("type", "1");
        startService(intent);

        Intent intent3 = new Intent(this, SensorServiceGPS.class);
        startService(intent3);
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
    }
}