package qi.muxi.movementtracker;

import android.hardware.SensorManager;

import java.util.Arrays;

/**
 * Caution:
 * 1. for initialization, use Sample(), and setters.
 * 2. for updating, use updaters.
 * 3. always update ID -> then time -> then others,
 * or you can use update(long, float, float[], float[], float[]) to update all at once.
 * 4. all getters and setters are sending data not reference, please feel safe to use them.
 *
 * @author Muxi
 */
public class Sample {
    public static final String LOG_TAG = "Sample";
    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;

    private long mID;
    private long timeStamp; // in nanoseconds
    private float timeInterval; // in seconds

    private float[] acc;
    private float[] g;
    private float[] lAcc;
    private float[] m;
    private float[] wAcc;
    private float[] speed;
    private float[] pos;
    /**
     * the flag marks whether g or m has been set, used for setting wAcc if and only if g and m all set
     */
    private boolean isSetG, isSetM;
    /**
     * the rotation matrix, stored in array of length 16
     */
    private float[] rotationMatrix;

    public Sample() {
        this.mID = 0;
        this.timeStamp = 0;
        this.timeInterval = 0;
        this.acc = new float[3];
        Arrays.fill(acc, 0);
        this.g = new float[3];
        Arrays.fill(g, 0);
        this.lAcc = new float[3];
        Arrays.fill(lAcc, 0);
        this.m = new float[3];
        Arrays.fill(m, 0);
        this.wAcc = new float[3];
        Arrays.fill(wAcc, 0);
        this.speed = new float[3];
        Arrays.fill(speed, 0);
        this.pos = new float[3];
        Arrays.fill(pos, 0);
        // Initial rotation matrix to an identity matrix
        this.rotationMatrix = new float[16];
        Arrays.fill(rotationMatrix, 0);
        rotationMatrix[0] = 1;
        rotationMatrix[5] = 1;
        rotationMatrix[10] = 1;
        rotationMatrix[15] = 1;
        this.isSetG = false;
        this.isSetM = false;
    }

    public void update(long mID, long timeStamp, float[] acc, float[] g, float[] lAcc, float[] m) {
        updateID(mID);
        updateTime(timeStamp);
        updateAcc(acc);
        setG(g);
        haveSetG();
        setM(m);
        haveSetM();
        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrix(rotationMatrix, null, this.g, m);
        setRotationMatrix(rotationMatrix);
        updateLAcc(lAcc);
    }

    public long getID() {
        return mID;
    }

    private void setID(long mID) {
        this.mID = mID;
    }

    public void updateID(long mID) {
        setID(mID);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    private void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void updateTime(long timeStamp) {
        // Explain for the starting sample:
        //   the problem:
        //     the first sample in a tracing has a timestamp instead of time interval,
        //     which is significant in Speed and Pos calculation.
        //   the solution:
        //     Clear sample before each starting, thus previous wAcc, speed, pos will be 0,
        //     So the time data will not be significant because it will be multiplied with 0.
        //     (See below).
        // TODO no error in storage, but error in calculation,
        // TODO (contd) the sampled data have a small and unstable but approximate-direction-stable acceleration error
        // TODO (contd) this error is called drift noise, or random walk noise (low frequency noise).
        // TODO (contd) It needs a really really long time to average to zero and can be filtered out by a high pass filter.
        // TODO (contd) Two solution options:
        // TODO (contd) 1. as the paper, make use of previous period and holding-phone period, when ever detect a holding-phone, reset speed to zero, however it will not remove the noise, just easing it.
        // TODO (contd) 2. use a high pass filter, it will remove such noise, however, how to implement? and is low pass filter required for white noise? and is there already a filter in Linear Accelerometer?
        float timeInterval = (float) (timeStamp - getTimeStamp()) * NS2S; // this time in second
        setTimeStamp(timeStamp);
        setTimeInterval(timeInterval);
        float[] acc, speed, pos, zeros;
        acc = getAcc();
        speed = getSpeed();
        pos = getPos();
        zeros = new float[3];
        Arrays.fill(zeros, 0);
        pos[0] = pos[0] + speed[0] * timeInterval + 0.5f * acc[0] * timeInterval * timeInterval;
        pos[1] = pos[1] + speed[1] * timeInterval + 0.5f * acc[1] * timeInterval * timeInterval;
        pos[2] = pos[2] + speed[2] * timeInterval + 0.5f * acc[2] * timeInterval * timeInterval;
        speed[0] = speed[0] + acc[0] * timeInterval;
        speed[1] = speed[1] + acc[1] * timeInterval;
        speed[2] = speed[2] + acc[2] * timeInterval;
        float speedMag = (float) Math.sqrt(speed[0] * speed[0] + speed[1] * speed[1] + speed[2] * speed[2]);
        if (speedMag < 0.05)
            Arrays.fill(speed, 0);// TODO here set speed to zero when it is small, is it required?
        else setPos(pos);
        setSpeed(speed);
        // Log.i(LOG_TAG, String.valueOf(pos[0]) + ";" + String.valueOf(pos[1]) + ";" + String.valueOf(pos[2]));
        // Log.i(LOG_TAG, String.valueOf(speed[0]) + ";" + String.valueOf(speed[1]) + ";" + String.valueOf(speed[2]));
    }

    public float getTimeInterval() {
        return this.timeInterval;
    }

    private void setTimeInterval(float timeInterval) {
        this.timeInterval = timeInterval;
    }

    public float[] getAcc() {
        return Arrays.copyOf(acc, 3);
    }

    private void setAcc(float[] acc) {
        this.acc = Arrays.copyOf(acc, this.acc.length);
    }

    public void updateAcc(float[] acc) {
        // a higher alpha refers to a lower split frequency
        /*float driftAlpha = 1.0f, whiteAlpha = 0.0f;
        float[] drift, whiteBias;
        drift = getAcc();
        whiteBias = getAcc(); // TODO seems not right, think carefully
        // apply band pass filter to get rid of drift and white noise TODO how does gravity sensor work? just using accelerometer data and filtering it?
        drift[0] = driftAlpha*drift[0] + (1-driftAlpha)*acc[0];
        drift[1] = driftAlpha*drift[1] + (1-driftAlpha)*acc[1];
        drift[2] = driftAlpha*drift[2] + (1-driftAlpha)*acc[2];
        whiteBias[0] = whiteAlpha*whiteBias[0] + (1-whiteAlpha)*acc[0];
        whiteBias[1] = whiteAlpha*whiteBias[1] + (1-whiteAlpha)*acc[1];
        whiteBias[2] = whiteAlpha*whiteBias[2] + (1-whiteAlpha)*acc[2];
        // white noise = acc - whiteBias; noise-free acc = acc - drift - white noise
        acc[0] = whiteBias[0] - drift[0];
        acc[1] = whiteBias[1] - drift[1];
        acc[2] = whiteBias[2] - drift[2];
        // Log.i(LOG_TAG, String.valueOf(pos[0]) + ";" + String.valueOf(pos[1]) + ";" + String.valueOf(pos[2]));*/
        setAcc(acc);
        if (isSetG() && isSetM()) {
            updateWAcc(getWorldVector(getRotationMatrix(), this.acc));
        }
    }

    public float[] getG() {
        return Arrays.copyOf(g, 3);
    }

    private void setG(float[] g) {
        this.g = Arrays.copyOf(g, this.g.length);
    }

    public void updateG(float[] g) {
        setG(g);
        haveSetG();
        if (isSetM()) {
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrix(rotationMatrix, null, this.g, m);
            setRotationMatrix(rotationMatrix);
            updateWAcc(getWorldVector(getRotationMatrix(), lAcc));
        }
    }

    public float[] getLAcc() {
        return Arrays.copyOf(lAcc, 3);
    }

    private void setLAcc(float[] lAcc) {
        this.lAcc = Arrays.copyOf(lAcc, this.lAcc.length);

    }

    public void updateLAcc(float[] lAcc) { // TODO here applied a band pass filter. is it required?
        // a higher alpha refers to a lower split frequency
        float driftAlpha = 1.0f, whiteAlpha = 0.0f;
        float[] drift, whiteBias;
        drift = getLAcc();
        whiteBias = getLAcc();
        // apply band pass filter to get rid of drift and white noise TODO how does gravity sensor work? just using accelerometer data and filtering it?
        drift[0] = driftAlpha * drift[0] + (1 - driftAlpha) * lAcc[0];
        drift[1] = driftAlpha * drift[1] + (1 - driftAlpha) * lAcc[1];
        drift[2] = driftAlpha * drift[2] + (1 - driftAlpha) * lAcc[2];
        whiteBias[0] = whiteAlpha * whiteBias[0] + (1 - whiteAlpha) * lAcc[0];
        whiteBias[1] = whiteAlpha * whiteBias[1] + (1 - whiteAlpha) * lAcc[1];
        whiteBias[2] = whiteAlpha * whiteBias[2] + (1 - whiteAlpha) * lAcc[2];
        // white noise = lAcc - whiteBias; noise-free lAcc = lAcc - drift - white noise
        lAcc[0] = whiteBias[0] - drift[0];
        lAcc[1] = whiteBias[1] - drift[1];
        lAcc[2] = whiteBias[2] - drift[2];
        // Log.i(LOG_TAG, String.valueOf(pos[0]) + ";" + String.valueOf(pos[1]) + ";" + String.valueOf(pos[2]));
        setLAcc(lAcc);
        if (isSetG() && isSetM()) {
            updateWAcc(getWorldVector(getRotationMatrix(), this.lAcc));
        }
    }

    public float[] getM() {
        return Arrays.copyOf(m, 3);
    }

    private void setM(float[] m) {
        this.m = Arrays.copyOf(m, this.m.length);
    }

    public void updateM(float[] m) {
        setM(m);
        haveSetM();
        if (isSetG()) {
            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrix(rotationMatrix, null, g, this.m);
            setRotationMatrix(rotationMatrix);
            updateWAcc(getWorldVector(getRotationMatrix(), lAcc));
        }
    }

    public float[] getWAcc() {
        return Arrays.copyOf(wAcc, 3);
    }

    private void setWAcc(float[] wAcc) {
        this.wAcc = Arrays.copyOf(wAcc, this.wAcc.length);
    }

    private void updateWAcc(float[] wAcc) { // TODO here set small acceleration to zero. is it required to do so?
        float wAccMag = (float) Math.sqrt(wAcc[0] * wAcc[0] + wAcc[1] * wAcc[1] + wAcc[2] * wAcc[2]);
        if (wAccMag < 0.25) Arrays.fill(wAcc, 0);
        setWAcc(wAcc);
    }

    public float[] getSpeed() {
        return Arrays.copyOf(speed, 3);
    }

    private void setSpeed(float[] speed) {
        this.speed = Arrays.copyOf(speed, this.speed.length);
    }

    public float[] getPos() {
        return Arrays.copyOf(pos, 3);
    }

    private void setPos(float[] pos) {
        this.pos = Arrays.copyOf(pos, this.pos.length);
    }

    public float[] getRotationMatrix() {
        return Arrays.copyOf(rotationMatrix, 16);
    }

    private void setRotationMatrix(float[] RotationMatrix) {
        this.rotationMatrix = Arrays.copyOf(RotationMatrix, this.rotationMatrix.length);
    }

    public boolean isSetG() {
        return this.isSetG;
    }

    private void haveSetG() {
        this.isSetG = true;
    }

    public boolean isSetM() {
        return this.isSetM;
    }

    private void haveSetM() {
        this.isSetM = true;
    }

    /**
     * Clear all data in sample to return initial state
     */
    public void clear() {
        setID(0);
        setTimeStamp(0);
        setTimeInterval(0);
        float[] zeros = new float[3];
        Arrays.fill(zeros, 0);
        setAcc(zeros);
        setG(zeros);
        setLAcc(zeros);
        setM(zeros);
        setWAcc(zeros);
        setSpeed(zeros);
        setPos(zeros);
        // Initial rotation matrix to an identity matrix
        float[] identityMatrix = new float[16];
        Arrays.fill(identityMatrix, 0);
        identityMatrix[0] = 1;
        identityMatrix[5] = 1;
        identityMatrix[10] = 1;
        identityMatrix[15] = 1;
        setRotationMatrix(identityMatrix);
        this.isSetG = false;
        this.isSetM = false;
    }

    /**
     * Calculate the world vector, if rotation matrix is not valid, return null.
     *
     * @param rotationMatrix the rotation matrix, fetched by SensorManager.getRotationMatrix(float[], float[], float[] float[])
     * @param sensorVector   the sensor vector under sensor coordinate system
     * @return the world vector under real world coordinate system, null if invalid rotation matrix
     */
    private float[] getWorldVector(float[] rotationMatrix, float[] sensorVector) {
        float[] worldVector = new float[3];
        if (rotationMatrix.length == 16) {
            worldVector[0] = rotationMatrix[0] * sensorVector[0] + rotationMatrix[1] * sensorVector[1] + rotationMatrix[2] * sensorVector[2];
            worldVector[1] = rotationMatrix[4] * sensorVector[0] + rotationMatrix[5] * sensorVector[1] + rotationMatrix[6] * sensorVector[2];
            worldVector[2] = rotationMatrix[8] * sensorVector[0] + rotationMatrix[9] * sensorVector[1] + rotationMatrix[10] * sensorVector[2];
        } else if (rotationMatrix.length == 9) {
            worldVector[0] = rotationMatrix[0] * sensorVector[0] + rotationMatrix[1] * sensorVector[1] + rotationMatrix[2] * sensorVector[2];
            worldVector[1] = rotationMatrix[3] * sensorVector[0] + rotationMatrix[4] * sensorVector[1] + rotationMatrix[5] * sensorVector[2];
            worldVector[2] = rotationMatrix[6] * sensorVector[0] + rotationMatrix[7] * sensorVector[1] + rotationMatrix[8] * sensorVector[2];
        } else worldVector = null;
        return worldVector;
    }

}
