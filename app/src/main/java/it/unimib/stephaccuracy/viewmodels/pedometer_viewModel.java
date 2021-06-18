package it.unimib.stephaccuracy.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import it.unimib.stephaccuracy.repository.pedometer_repository;

public class pedometer_viewModel extends AndroidViewModel{

    private final pedometer_repository r;

    public pedometer_viewModel(@NonNull @NotNull Application application) {
        super(application);
        r = new pedometer_repository(application);

    }


    public LiveData<Boolean> add_step1(String date, int step_1){
        return r.push_step1(date, step_1);
    }

    public LiveData<Boolean> add_step2(String date, int step_2){
        return r.push_step2(date, step_2);
    }

    public LiveData<Boolean> add_step3(String date, int step_3){
        return r.push_step3(date, step_3);
    }

    public LiveData<Integer> get_step1(String date){
        return r.get_step1(date);
    }

    public LiveData<Integer> get_step2(String date){
        return r.get_step2(date);
    }

    public LiveData<Integer> get_step3(String date){
        return r.get_step3(date);
    }

}
