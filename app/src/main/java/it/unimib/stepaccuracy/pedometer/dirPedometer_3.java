package it.unimib.stepaccuracy.pedometer;

import android.hardware.SensorManager;

import it.unimib.stepaccuracy.dataset;

public class dirPedometer_3 {

    private static int STEP = 0;
    private static float SENSITIVITY = 3.5f; // SENSITIVITY灵敏度
    //public static float SENSITIVITY = 10f; // SENSITIVITY灵敏度
    private static float mLastValues[] = new float[3 * 2];
    private static float mScale[] = new float[2];
    private static  float mYOffset;

    private static float mLastDirections[] = new float[3 * 2];
    private static float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private static float mLastDiff[] = new float[3 * 2];
    private static int mLastMatch = -1;


    public static void setting(){
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    public static int count_step(dataset d){
        setting();
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
        float vet[] = {x, y, z};
        float vSum = 0;
        for (int i = 0; i < 3; i++) {
            final float v = mYOffset + vet[i] * mScale[1];
            vSum += v;
        }
        int k = 0;
        float v = vSum / 3;
        float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
        if (direction == -mLastDirections[k]) {
            // Direction changed
            int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
            mLastExtremes[extType][k] = mLastValues[k];
            float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);
            if (diff > SENSITIVITY) {
                boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                boolean isNotContra = (mLastMatch != 1 - extType);
                if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                    STEP++;
                    mLastMatch = extType;
                }
                else {
                    mLastMatch = -1;
                }
            }
            mLastDiff[k] = diff;
        }
        mLastDirections[k] = direction;
        mLastValues[k] = v;
    }

    private static void clean(){
        for(int i = 0; i < 6; i ++){
            mLastValues[i] = 0;
            mLastDirections[i] = 0;
            mLastDiff[i] = 0;
            mLastExtremes[0][i] = 0;
            mLastExtremes[1][i] = 0;
        }
        mLastMatch = -1;
    }

    public static int getSTEP() {
        return STEP;
    }

    public static void setSTEP(int STEP) {
        clean();
        dirPedometer_3.STEP = STEP;
    }


}
