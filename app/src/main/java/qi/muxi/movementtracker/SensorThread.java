package qi.muxi.movementtracker;

import android.content.Context;
import android.hardware.SensorEvent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by lxynox on 4/23/15.
 * description: background thread used to monitor the sensor events and start background service to interact
 * with the database
 * precondition: start() is called on the main thread && proper mHandler is created on this thread for handling of messages
 * postcondition: All messages are handled and background service created and stopped with the flag of stop informed
 */
public class SensorThread extends Thread {

    //    private boolean sensorEnabled;
    private static Handler mHandler;
    private Looper mLooper;
    private final String LOG_TAG = "SensorThread";
    private Context context;

    //    private MeasuredDatabaseManager measuredDatabaseManager;
    public SensorThread(String threadName) {
        super(threadName);
    }

    // obtain handler from SensorActivity (main thread)
    public synchronized Handler getHandler() {
        while (mHandler == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.i(LOG_TAG, "This handler's looper is : " + Looper.myLooper());
        return mHandler;
    }

    public  Looper getLooper() {
        return mLooper;
    }

    @Override
    public void run() {
        Log.i(LOG_TAG, "Sensor thread starts running !");
        Log.i(LOG_TAG, "Running thread is : " + Thread.currentThread());

        Looper.prepare(); // often the prepare of Looper has some latency which may block the initialization of mHandler

        synchronized (this) {
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
            notify();
        }

        Looper.loop();
//        sensorEnabled = true;
    }

}
