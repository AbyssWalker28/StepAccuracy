package it.unimib.stepaccuracy.ui.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.stepaccuracy.viewmodels.SettingsViewModel;
import it.unimib.stepaccuracy.R;
import it.unimib.stepaccuracy.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText goal = root.findViewById(R.id.editText1);
        RadioGroup tempo = root.findViewById(R.id.radioGroupTempo);
        RadioGroup distance = root.findViewById(R.id.radioGroupDistanza);
        Button save = root.findViewById(R.id.button3);

        SharedPreferences sharedPref = getContext().getSharedPreferences("save_step_shared_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(sharedPref.getString("preferences_distance", "").equals("m"))
            distance.check(R.id.radioButtonMetri);
        else
            distance.check(R.id.radioButtonChilometri);

        if(sharedPref.getString("preferences_time", "").equals("sec"))
            tempo.check(R.id.radioButtonSecondi);
        else
            tempo.check(R.id.radioButtonMinuti);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!goal.getText().toString().equals(""))
                    editor.putInt("total_daily_step", Integer.parseInt(goal.getText().toString()));

                if(tempo.getCheckedRadioButtonId() == R.id.radioButtonSecondi)
                    editor.putString("preferences_time", "sec");
                else
                    editor.putString("preferences_time", "min");

                if(distance.getCheckedRadioButtonId() == R.id.radioButtonMetri)
                    editor.putString("preferences_distance", "m");
                else
                    editor.putString("preferences_distance", "km");

                editor.apply();
            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}