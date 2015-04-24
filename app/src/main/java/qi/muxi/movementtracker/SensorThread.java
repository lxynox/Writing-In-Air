package qi.muxi.movementtracker;

import android.content.Context;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by lxynox on 4/23/15.
 */
public class SensorThread extends Thread {

    //    private boolean sensorEnabled;
    private static Handler mHandler;
    private static Looper mLooper;
    private final String LOG_TAG = "SensorThread";
    private Context context;

    //    private MeasuredDatabaseManager measuredDatabaseManager;
    static {
        mHandler = new Handler();
        mLooper = mHandler.getLooper();
    }

    public SensorThread(String threadName) {
        super(threadName);
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static Looper getLooper() {
        return mLooper;
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Sensor thread starts running !");
        Log.i(LOG_TAG, "Running thread is : " + Thread.currentThread());

        Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // process incoming messages here
                Log.i(LOG_TAG, "handleMessage reached ...........");
                Log.i(LOG_TAG, "start processing msg ..........");
                Log.i(LOG_TAG, "Current running thread is : " + Thread.currentThread());
                SensorEvent sensorEvent = (SensorEvent) msg.obj;

                Log.i(LOG_TAG, "X-axis value: " + sensorEvent.values[0]);
                Log.i(LOG_TAG, "Y-axis value: " + sensorEvent.values[1]);
                Log.i(LOG_TAG, "Z-axis value: " + sensorEvent.values[2]);
               /* Message eventMSG = SensorActivity.uiHandler.obtainMessage(1, sensorEvent.values);
                eventMSG.setTarget(SensorActivity.uiHandler);
                eventMSG.sendToTarget();*/
            }
        };

        Looper.loop();
//        sensorEnabled = true;
    }

}
