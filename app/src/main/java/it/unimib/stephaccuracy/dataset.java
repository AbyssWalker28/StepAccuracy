package it.unimib.stephaccuracy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.unimib.stephaccuracy.pedometer.dirPedometer_3;
import it.unimib.stephaccuracy.pedometer.simplePedometer_1;

public class dataset {
    private int iterator = 0;
    private ArrayList<Float> x;
    private ArrayList<Float> y;
    private ArrayList<Float> z;

    private static final String TAG = "dataset";

    public dataset() {
        x = new ArrayList<Float>();
        y = new ArrayList<Float>();
        z = new ArrayList<Float>();
    }

    public void setDataAccel(float x_ext, float y_ext, float z_ext){
        x.add(x_ext);
        y.add(y_ext);
        z.add(z_ext);
        iterator++;
    }

    public int track_step_ped_1(){
        simplePedometer_1.count_step(this);
        return simplePedometer_1.getSTEP();
    }

    public int track_step_ped_3(){
        dirPedometer_3.count_step(this);
        return dirPedometer_3.getSTEP();
    }

    public void read_data(InputStream dataset){
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataset));
        String editLong = "";
        try {
            String csvLine;
            reader.readLine();
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                setDataAccel(Float.parseFloat(row[1]), Float.parseFloat(row[2]), Float.parseFloat(row[3]));
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                dataset.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }

    public ArrayList<Float> getX() {
        return x;
    }

    public ArrayList<Float> getY() {
        return y;
    }

    public ArrayList<Float> getZ() {
        return z;
    }

    public int getSize(){
        return x.size();
    }


}


