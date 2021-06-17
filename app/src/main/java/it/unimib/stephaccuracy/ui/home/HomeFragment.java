package it.unimib.stephaccuracy.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.stephaccuracy.AllPedometer_activity;
import it.unimib.stephaccuracy.R;
import it.unimib.stephaccuracy.databinding.FragmentHomeBinding;
import it.unimib.stephaccuracy.dirPedometer_3_activity;
import it.unimib.stephaccuracy.gpsPedometer_2_activity;
import it.unimib.stephaccuracy.simplePedometer_1_activity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        //Button b1 = root.findViewById(R.id.button);
        CardView b1 = root.findViewById(R.id.alg1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSimplePedometerActivity = new Intent(getActivity(), simplePedometer_1_activity.class);
                startActivity(openSimplePedometerActivity);
            }
        });

        CardView b2 = root.findViewById(R.id.alg2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent opengpsPedometerActivity = new Intent(getActivity(), gpsPedometer_2_activity.class);
                startActivity(opengpsPedometerActivity);
            }
        });

        CardView b3 = root.findViewById(R.id.alg3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openDirPedometerActivity = new Intent(getActivity(), dirPedometer_3_activity.class);
                startActivity(openDirPedometerActivity);
            }
        });

        ImageView b4 = root.findViewById(R.id.start_button);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openAllPedometer = new Intent(getActivity(), AllPedometer_activity.class);
                startActivity(openAllPedometer);
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