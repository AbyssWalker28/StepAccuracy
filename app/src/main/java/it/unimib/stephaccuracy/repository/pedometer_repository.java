package it.unimib.stephaccuracy.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import it.unimib.stephaccuracy.constants;

public class pedometer_repository {
    private final Application app;
    private final DatabaseReference db;

    public pedometer_repository(Application app) {
        this.app = app;
        this.db = FirebaseDatabase.getInstance(constants.REALTIME_DATABASE).getReference();
    }

    public LiveData<Boolean> push_step1(String date, int step1){
        MutableLiveData<Boolean> ld = new MutableLiveData<Boolean>();
        db.child(date).child("step_1").setValue(step1).addOnCompleteListener(ContextCompat.getMainExecutor(app), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful())
                    ld.postValue(true);
                else
                    ld.postValue(false);
            }
        });
        return ld;
    }

    public LiveData<Boolean> push_step2(String date, int step2){
        MutableLiveData<Boolean> ld = new MutableLiveData<Boolean>();
        db.child(date).child("step_2").setValue(step2).addOnCompleteListener(ContextCompat.getMainExecutor(app), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful())
                    ld.postValue(true);
                else
                    ld.postValue(false);
            }
        });
        return ld;
    }

    public LiveData<Boolean> push_step3(String date, int step3){
        MutableLiveData<Boolean> ld = new MutableLiveData<Boolean>();
        db.child(date).child("step_3").setValue(step3).addOnCompleteListener(ContextCompat.getMainExecutor(app), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful())
                    ld.postValue(true);
                else
                    ld.postValue(false);
            }
        });
        return ld;
    }

    public LiveData<Integer> get_step1(String date){
        MutableLiveData<Integer> ld = new MutableLiveData<Integer>();
        Log.d("prova", date);
        db.child(date).child("step_1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                int step;
                if(task.isSuccessful() && task.getResult() != null){
                    step = task.getResult().getValue(Integer.class);
                    Log.d("prova", step + " livedata");
                    ld.postValue(step);
                }
                else{
                    ld.postValue(0);
                }
            }
        });
        return ld;
    }

    public LiveData<Integer> get_step2(String date){
        MutableLiveData<Integer> ld = new MutableLiveData<Integer>();
        db.child(date).child("step_2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                int step;
                if(task.isSuccessful() && task.getResult() != null){
                    step = task.getResult().getValue(Integer.class);
                    ld.postValue(step);
                }
                else{
                    ld.postValue(0);
                }
            }
        });
        return ld;
    }

    public LiveData<Integer> get_step3(String date){
        MutableLiveData<Integer> ld = new MutableLiveData<Integer>();
        db.child(date).child("step_3").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                int step;
                if(task.isSuccessful() && task.getResult() != null){
                    step = task.getResult().getValue(Integer.class);
                    ld.postValue(step);
                }
                else{
                    ld.postValue(0);
                }
            }
        });
        return ld;
    }
}
