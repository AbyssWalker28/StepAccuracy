package it.unimib.stephaccuracy.ui.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.stephaccuracy.R;
import it.unimib.stephaccuracy.databinding.FragmentSettingsBinding;

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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!goal.getText().toString().equals(null)){
                    editor.putInt("total_daily_step", Integer.parseInt(goal.getText().toString()));
                }

                if(tempo.getCheckedRadioButtonId() == R.id.radioButtonSecondi){
                    editor.putString("preferencs_time", "second");
                }
                else {
                    editor.putString("preferencs_time", "minute");
                }

                if(distance.getCheckedRadioButtonId() == R.id.radioButtonMetri){
                    editor.putString("preferencs_distance", "meter");
                }
                else {
                    editor.putString("preferencs_distance", "chilometer");
                }
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