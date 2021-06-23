package it.unimib.stepaccuracy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;

import it.unimib.stepaccuracy.pedometer.dirPedometer_3;
import it.unimib.stepaccuracy.pedometer.simplePedometer_1;
import it.unimib.stepaccuracy.R;
import it.unimib.stepaccuracy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";
    private int step_calculated_1 = 0;
    private int step_calculated_3 = 0;

    /*@Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pedometer1_dataset", step_calculated_1);
        outState.putInt("pedometer3_dataset", step_calculated_2);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home,R.id.navigation_settings).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        SharedPreferences sharedPref = getSharedPreferences("save_step_shared_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(!sharedPref.contains("step_dataset_1") && !sharedPref.contains("step_dataset_3") ){
            InputStream inputStream = getResources().openRawResource(R.raw.steps);
            dataset dt = new dataset();
            dt.read_data(inputStream);
            for (int i = 0; i < dt.getSize(); i++) {
                Log.d(TAG, "x: " + Float.toString(dt.getX().get(i)) + "   y: " + Float.toString(dt.getY().get(i)) + "   z: " + Float.toString(dt.getZ().get(i)) + "   i: " + i);
            }
            step_calculated_1 = dt.track_step_ped_1();     //simplePedometer dataset check
            step_calculated_3 = dt.track_step_ped_3();     //dirPedometer dataset check
            simplePedometer_1.setSTEP(0);
            dirPedometer_3.setSTEP(0);

            Log.d("prova", step_calculated_1 + "");
            Log.d("prova", step_calculated_3 + "");

            editor.putInt("step_dataset_1", step_calculated_1);
            editor.putInt("step_dataset_3", step_calculated_3);
            editor.apply();
        }

        if(!sharedPref.contains("date_pedometer") || !(sharedPref.getString("date_pedometer", "").equals(myDate.dateConvert()))){
            editor.putString("date_pedometer", myDate.dateConvert());
            editor.apply();
        }


    }



}