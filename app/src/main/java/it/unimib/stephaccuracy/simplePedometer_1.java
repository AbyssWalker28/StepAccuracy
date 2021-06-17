package it.unimib.stephaccuracy;

import android.util.Log;

public class simplePedometer_1 {

    private static int STEP = 0;

    private static int ACCEL_RING_SIZE = 50;
    private static final int VEL_RING_SIZE = 10;
    private static final float STEP_THRESHOLD = 2f;
    //private static final float STEP_THRESHOLD = 0.4f;

    private static int accelRingCounter = 0;
    private static float[] accelRingX = new float[ACCEL_RING_SIZE];
    private static float[] accelRingY = new float[ACCEL_RING_SIZE];
    private static float[] accelRingZ = new float[ACCEL_RING_SIZE];
    private static int velRingCounter = 0;
    private static float[] velRing = new float[VEL_RING_SIZE];
    private static float oldVelocityEstimate = 0;

    public static int count_step(data d){
        for(int i = 0; i < d.getSize(); i++) {
            updateAccel(d.getX().get(i), d.getY().get(i), d.getZ().get(i));
        }
        return STEP;
    }

    public static int updateStep(String receiver){
        String[] row = receiver.split(",");
        updateAccel(Float.parseFloat(row[0]), Float.parseFloat(row[1]), Float.parseFloat(row[2]));
        return STEP;
    }

    private static void updateAccel(float x, float y, float z) {
        float[] currentAccel = new float[3];
        currentAccel[0] = x;
        currentAccel[1] = y;
        currentAccel[2] = z;

        // First step is to update our guess of where the global z vector is.
        accelRingCounter++;
        accelRingX[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[0];
        accelRingY[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[1];
        accelRingZ[accelRingCounter % ACCEL_RING_SIZE] = currentAccel[2];

        float[] worldZ = new float[3];
        worldZ[0] = sum(accelRingX) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[1] = sum(accelRingY) / Math.min(accelRingCounter, ACCEL_RING_SIZE);
        worldZ[2] = sum(accelRingZ) / Math.min(accelRingCounter, ACCEL_RING_SIZE);

        float normalization_factor = norm(worldZ);

        worldZ[0] = worldZ[0] / normalization_factor;
        worldZ[1] = worldZ[1] / normalization_factor;
        worldZ[2] = worldZ[2] / normalization_factor;

        // Next step is to figure out the component of the current acceleration
        // in the direction of world_z and subtract gravity's contribution

        float currentZ = dot(worldZ, currentAccel) - normalization_factor;
        velRingCounter++;
        velRing[velRingCounter % VEL_RING_SIZE] = currentZ;

        float velocityEstimate = sum(velRing);

        if (velocityEstimate > STEP_THRESHOLD && oldVelocityEstimate <= STEP_THRESHOLD) {
            STEP++;
        }
        oldVelocityEstimate = velocityEstimate;
    }


    private static float norm(float[] array) {
        float retval = 0;
        for (int i = 0; i < array.length; i++) {
            retval += array[i] * array[i];
        }
        return (float) Math.sqrt(retval);
    }

    private static float dot(float[] a, float[] b) {
        float retval = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        return retval;
    }

    private static float sum(float[] array) {
        float retval = 0;
        for (int i = 0; i < array.length; i++) {
            retval += array[i];
        }
        return retval;
    }

    private static void clean(){
        accelRingCounter = 0;
        oldVelocityEstimate = 0;
        velRingCounter = 0;
        for(int i = 0; i < ACCEL_RING_SIZE; i++){
            accelRingX[i] = 0;
            accelRingY[i] = 0;
            accelRingZ[i] = 0;
            if(i < VEL_RING_SIZE)
                velRing[i] = 0;
        }
    }

    public static int getSTEP() {
        return STEP;
    }

    public static void setSTEP(int STEP) {
        clean();
        simplePedometer_1.STEP = STEP;
    }
}
