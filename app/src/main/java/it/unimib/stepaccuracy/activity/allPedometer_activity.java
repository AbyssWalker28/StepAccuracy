package it.unimib.stepaccuracy.activity;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.lifecycle.Observer;
        import androidx.lifecycle.ViewModelProvider;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.github.mikephil.charting.components.Description;
        import com.github.mikephil.charting.components.YAxis;
        import com.github.mikephil.charting.data.BarData;
        import com.github.mikephil.charting.data.BarDataSet;
        import com.github.mikephil.charting.data.BarEntry;
        import com.github.mikephil.charting.utils.ColorTemplate;
        import com.google.android.material.snackbar.BaseTransientBottomBar;
        import com.google.android.material.snackbar.Snackbar;

        import java.util.ArrayList;

        import it.unimib.stepaccuracy.R;
        import it.unimib.stepaccuracy.myDate;
        import it.unimib.stepaccuracy.pedometer.dirPedometer_3;
        import it.unimib.stepaccuracy.pedometer.gpsPedometer_2;
        import it.unimib.stepaccuracy.pedometer.simplePedometer_1;
        import it.unimib.stepaccuracy.sensor.SensorServiceAcc;
        import it.unimib.stepaccuracy.sensor.SensorServiceGPS;
        import it.unimib.stepaccuracy.viewmodels.pedometer_viewModel;

        import com.github.mikephil.charting.charts.BarChart;
        import com.github.mikephil.charting.data.BarData;
        import com.github.mikephil.charting.data.BarDataSet;
        import com.github.mikephil.charting.data.BarEntry;
        import com.github.mikephil.charting.utils.ColorTemplate;

public class allPedometer_activity extends AppCompatActivity {

    private static final String TAG = "allPedometer_1_act";
    private pedometer_viewModel vm;
    int step_ped_1 = 0;
    int step_ped_2 = 0;
    int step_ped_3 = 0;

    BarEntry ped1;
    BarEntry ped2;
    BarEntry ped3;

    Snackbar connection_error;

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

            ped1.setY(step_ped_1);
            ped2.setY(step_ped_2);
            ped3.setY(step_ped_3);

            if(isNetworkAvailable()) {
                upload_step_db();
                if(connection_error.isShown())
                    connection_error.dismiss();
            }
            else if(!connection_error.isShown())
                    connection_error.show();
        }
    }

    private TextView mSensorValuesTextView;
    private TextView mSensorValuesTextView2;
    private TextView mSensorValuesTextView3;
    private SensorsValuesBroadcastReceiver mSensorsValuesBroadcastReceiver;
    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(this).get(pedometer_viewModel.class);

        setContentView(R.layout.activity_all_pedometer);
        mSensorValuesTextView = findViewById(R.id.sensor_values);
        mSensorValuesTextView2 = findViewById(R.id.sensor_values2);
        mSensorValuesTextView3 = findViewById(R.id.sensor_values3);
        connection_error = Snackbar.make(findViewById(android.R.id.content), "Connessione persa, impossibile salvare i dati", Snackbar.LENGTH_INDEFINITE);
        chart = findViewById(R.id.fragment_verticalbarchart_chart);



        ArrayList<BarEntry> entries = new ArrayList<>();
        ped1 = new BarEntry(1f, 0);
        ped2 = new BarEntry(3f, 0);
        ped3 = new BarEntry(5f, 0);
        entries.add(ped1);
        entries.add(ped2);
        entries.add(ped3);



        BarDataSet bardataset = new BarDataSet(entries, "Step");

        /*ArrayList<String> labels = new ArrayList<String>();
        labels.add("2016");
        labels.add("2015");
        labels.add("2014");
        labels.add("2013");
        labels.add("2012");
        labels.add("2011");*/

        BarData data = new BarData(bardataset);
        chart.setData(data); // set the data and list of labels into chart
        Description x = new Description();
        x.setText("");
        chart.setDescription(x);  // set the description

        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setDoubleTapToZoomEnabled(false);
        /*chart.setPinchZoom(false);
        chart.setFocusable(false);*/
        chart.setAutoScaleMinMaxEnabled(true);
        //chart.animateY(3000);


        //get_step_db();

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
        vm.add_step1(myDate.dateConvert(), /*db_step_1 + */step_ped_1).observe(allPedometer_activity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                update_failed(aBoolean);
            }
        });

        vm.add_step2(myDate.dateConvert(), /*db_step_2 + */step_ped_2).observe(allPedometer_activity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                update_failed(aBoolean);
            }
        });

        vm.add_step3(myDate.dateConvert(), /*db_step_3 + */step_ped_3).observe(allPedometer_activity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                update_failed(aBoolean);
            }
        });
    }

    /*public void get_step_db(){
        vm.get_step1(myDate.dateConvert()).observe(allPedometer_activity.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
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

    }*/

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}