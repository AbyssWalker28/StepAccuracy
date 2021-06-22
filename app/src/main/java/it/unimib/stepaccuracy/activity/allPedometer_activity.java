package it.unimib.stepaccuracy.activity;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.lifecycle.Observer;
        import androidx.lifecycle.ViewModelProvider;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.TextView;
        import android.widget.Toast;

        import it.unimib.stepaccuracy.R;
        import it.unimib.stepaccuracy.myDate;
        import it.unimib.stepaccuracy.pedometer.dirPedometer_3;
        import it.unimib.stepaccuracy.pedometer.gpsPedometer_2;
        import it.unimib.stepaccuracy.pedometer.simplePedometer_1;
        import it.unimib.stepaccuracy.sensor.SensorServiceAcc;
        import it.unimib.stepaccuracy.sensor.SensorServiceGPS;
        import it.unimib.stepaccuracy.viewmodels.pedometer_viewModel;

public class allPedometer_activity extends AppCompatActivity {

    private static final String TAG = "simplePedometer_1_act";
    private pedometer_viewModel vm;
    int step_ped_1 = 0;
    int step_ped_2 = 0;
    int step_ped_3 = 0;

    int db_step_1 = 0;
    int db_step_2 = 0;
    int db_step_3 = 0;

    public class SensorsValuesBroadcastReceiver extends BroadcastReceiver {
        String receiver = "";


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
            //inserimento controllo rete abilitata, aggiungere toast in assenza di rete
            upload_step_db();
        }
    }

    private TextView mSensorValuesTextView;
    private TextView mSensorValuesTextView2;
    private TextView mSensorValuesTextView3;
    private SensorsValuesBroadcastReceiver mSensorsValuesBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(this).get(pedometer_viewModel.class);

        setContentView(R.layout.activity_all_pedometer);
        mSensorValuesTextView = findViewById(R.id.sensor_values);
        mSensorValuesTextView2 = findViewById(R.id.sensor_values2);
        mSensorValuesTextView3 = findViewById(R.id.sensor_values3);

        simplePedometer_1.setSTEP(0);
        dirPedometer_3.setSTEP(0);
        gpsPedometer_2.setSTEP(0, 0);

        //get_step_db();

        Log.d("prova", db_step_1 + " ped1");
        Log.d("prova", db_step_2 + " ped2");
        Log.d("prova", db_step_3 + " ped3");

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

    private void update_failed(Boolean b){
        if(!b) {
            Context context = getApplicationContext();
            CharSequence text = "Update Fallito";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void upload_step_db(){
        vm.add_step1(myDate.dateConvert(), db_step_1 + step_ped_1).observe(allPedometer_activity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                update_failed(aBoolean);
            }
        });

        vm.add_step2(myDate.dateConvert(), db_step_2 + step_ped_2).observe(allPedometer_activity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                update_failed(aBoolean);
            }
        });

        vm.add_step3(myDate.dateConvert(), db_step_3 + step_ped_3).observe(allPedometer_activity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                update_failed(aBoolean);
            }
        });
    }

    public void get_step_db(){
        vm.get_step1(myDate.dateConvert()).observe(allPedometer_activity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d("prova", "integer" + integer);
                db_step_1 = integer;
            }
        });

        vm.get_step2(myDate.dateConvert()).observe(allPedometer_activity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                db_step_2 = integer;
            }
        });

        vm.get_step3(myDate.dateConvert()).observe(allPedometer_activity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                db_step_3 = integer;
            }
        });



    }

}