package it.unimib.stepaccuracy.pedometer;

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import java.util.ArrayList;

public class gpsPedometer_2 {

    private static int STEP = 0;
    private static ArrayList<Location> fullLocationList = new ArrayList<>();
    private static ArrayList<Float> fullDistanceList = new ArrayList<>();
    private static float fullDistance = 0;
    private static ArrayList<Long> timeLocationList = new ArrayList<>();

    private static String viewProgress;
    private static String distanceText;
    private static String Progressbar;



    /*private static double dist = 0;
    public static double prev_latitude = 0;
    public static double prev_longitude = 0;
    private static float arr[] = new float[4];
    public static Location prev;


    public static double updateLocation(double latitude, double longitude){
        if(prev_latitude == 0 && prev_longitude == 0 || ((prev_longitude == longitude) && (prev_latitude == latitude))){
            prev_latitude = latitude;
            prev_longitude = longitude;
        }
        else {
            double dlong = toRad(latitude - prev_latitude);
            double dlat = toRad(longitude - prev_longitude);
            //double a = Math.pow(Math.sin(toRad(dlat) / 2.0), 2) + Math.cos(toRad(prev_latitude)) * Math.cos(toRad(latitude)) * Math.pow(Math.sin(toRad(dlong) / 2.0), 2);
            double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(toRad(prev_latitude)) * Math.cos(toRad(latitude)) * Math.pow(Math.sin(dlong / 2.0), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = 6367 * c;
            d *= 1000;
            dist = dist + d;
            //Log.d("distance in mt", dist + "");
            prev_latitude = latitude;
            prev_longitude = longitude;

            if (oldDist < dist + 0.005) {
                oldDist = dist;
                interimLocation = location;
                if (interimlocation == null) {
                    interimlocation = "{" + "Long:" + location.getLongitude() + "," + "Lat:" + location.getLatitude() + "}";

                } else {
                    interimlocation = interimlocation + "{" + "Long:" + location.getLongitude() + "," + "Lat:" + location.getLatitude() + "}";
                }
            }

            double newSpeed = location.getSpeed();
            if (newSpeed > speed) {
                speed = newSpeed;
                DecimalFormat df = new DecimalFormat("#.##");
                maxspeed.setText("velocità:  " + df.format(speed) + " m/s");
            }
        }
        return dist;
    }*/

    public static int updateStep(String receiver){
        String[] row = receiver.split(",");
        updateLocation(Double.parseDouble(row[0]), Double.parseDouble(row[1]));
        return STEP;
    }

    private static void updateLocation(double latitude, double longitude) {
        try {
            //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            long time = SystemClock.elapsedRealtime() / 1000;
            if (location != null) {
                fullLocationList.add(location);
                if (timeLocationList.size() == 0) {
                    timeLocationList.add(time);
                } else {
                    Long currentTime = timeLocationList.get(timeLocationList.size() - 1);
                    if (currentTime < 20000) {
                        timeLocationList.add(time - currentTime);
                    }
                }
                if (fullLocationList.size() > 2) {
                    //fullDistanceList.add(location.distanceTo(fullLocationList.get(fullDistanceList.size() - 1)));
                    fullDistance += location.distanceTo(fullLocationList.get(fullLocationList.size() - 2));
                    STEP = (int)(fullDistance / 0.65);
                }
            }
        } catch (SecurityException e) {
        }
    }


    private static Double toRad(Double value) {
        return value * Math.PI / 180;
    }

    private static void clean(){
        fullLocationList.removeAll(fullLocationList);
        fullDistanceList.removeAll(fullDistanceList);
        timeLocationList.removeAll(timeLocationList);
    }

    public static void setSTEP(int STEP, float distance) {
        clean();
        gpsPedometer_2.STEP = STEP;
        gpsPedometer_2.fullDistance = distance;
    }

    public static int getSTEP() {
        return STEP;
    }

    public static float getFullDistance() {
        return fullDistance;
    }
}
