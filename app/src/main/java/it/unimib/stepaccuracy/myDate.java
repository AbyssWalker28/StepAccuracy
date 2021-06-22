package it.unimib.stepaccuracy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class myDate{
    private int time;

    public static String dateConvert(){
        Date d = new Date();
        String temp[] = d.toString().split(" ");
        String data = temp[2] + "-" + temp[1] + "-" + temp[5];
        return data;
    }

}
