package it.unimib.stepaccuracy.sensor;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationRequest;

import java.util.List;

public class SensorServiceGPS extends Service implements LocationListener {

    private static final String TAG = "SensorServiceGPS";
    private Location startLocation = null;
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 8f, this);
        startLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(startLocation != null){
            Intent intent2 = new Intent();
            intent2.setAction("it.unimib.stephaccuracy");
            intent2.putExtra("gps", startLocation.getLatitude() + "," + startLocation.getLongitude());
            sendBroadcast(intent2);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Intent intent = new Intent();
        intent.setAction("it.unimib.stephaccuracy");
        intent.putExtra("gps", location.getLatitude() + "," + location.getLongitude());
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopSelf();
    }
}