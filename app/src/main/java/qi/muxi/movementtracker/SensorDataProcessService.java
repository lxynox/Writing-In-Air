package qi.muxi.movementtracker;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;

import android.util.Log;

import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
/*
 *
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 * */


public class SensorDataProcessService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String LOG_TAG = "SensorDataService";
    private static final String ACTION_STORE_SENSOR_DATA = "qi.muxi.movementtracker.action.STORE_SENSOR_DATA";

    // TODO: Rename parameters
    private static final String EXTRA_TIME_STAMP = "qi.muxi.movementtracker.extra.TIME_STAMP";
    private static final String EXTRA_SENSOR_TYPE = "qi.muxi.movementtracker.extra.SENSOR_TYPE";
    private static final String EXTRA_SENSOR_VALUE = "qi.muxi.movementtracker.extra.SENSOR_VALUE";
    private static final String EXTRA_END_FETCH = "qi.muxi.movementtracker.extra.END_FETCH";
    // Create a constant to convert nanoseconds to seconds.
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static Sample sample;
    private static int rowCounter;
    private MeasuredDatabaseManager measuredDatabaseManager;

//  private static final String EXTRA_PARAM2 = "qi.muxi.movementtracker.extra.PARAM2";

    /**
     * static constructor for the init of static variables
     */
    static {
        sample = new Sample();
        rowCounter = 0;
    }

    public SensorDataProcessService() {
        super("SensorDataProcessService");
        Log.i(LOG_TAG, "new service constructor reached !");
    }

    /**
     * Starts this service to perform action Fetching back-end data with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFetchSensorData(Context context, float[] eventValues,
                                                  long timeStamp, int eventType, boolean endFetch) {

        Intent intent = new Intent(context, SensorDataProcessService.class);
        intent.setAction(ACTION_STORE_SENSOR_DATA);
        intent.putExtra(EXTRA_SENSOR_VALUE, eventValues);
        intent.putExtra(EXTRA_SENSOR_TYPE, eventType);
        intent.putExtra(EXTRA_TIME_STAMP, timeStamp);
        intent.putExtra(EXTRA_END_FETCH, endFetch);
        context.startService(intent);
        Log.i(LOG_TAG, "new service started!");
    }

    public static void endActionFetchSensorData(Context context, Intent intent) {
        context.stopService(intent);
    }

    /**
     * This is the method called when first time the service is started
     * onStart(), onStartCommand() are called in this method either
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "onCreate of this service reached !");
        measuredDatabaseManager = MeasuredDatabaseManager.getInstance(getApplicationContext());
//        measuredDatabaseManager.clearDatabase();
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.
     * for instance: When return button is clicked by the client
     * Clear the database before destroying Service
     */
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy of this service is reached !");
    }

    /**
     * System calls automatically to handle the Intent queues
     *
     * @param intent been created each time the onSensorChanged reached && updated values
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.i(LOG_TAG, "onHandleIntent reached on thread: " + Thread.currentThread() + "!");

            if (ACTION_STORE_SENSOR_DATA.equals(action)) {
                if (!intent.getBooleanExtra(EXTRA_END_FETCH, false)) {
                    float[] sensorVal = intent.getFloatArrayExtra(EXTRA_SENSOR_VALUE);
                    long timestamp = intent.getLongExtra(EXTRA_TIME_STAMP, 0l);
                    int sensorType = intent.getIntExtra(EXTRA_SENSOR_TYPE, 0);
                    rowCounter++;

//                update the values to database
                    sample.updateID(rowCounter);
                    sample.updateTime(timestamp);

                    switch (sensorType) {
                        case Sensor.TYPE_LINEAR_ACCELERATION:
                            sample.updateLAcc(sensorVal);
                            break;

                        case Sensor.TYPE_GRAVITY:
                            sample.updateG(sensorVal);
                            break;

                        case Sensor.TYPE_MAGNETIC_FIELD:
                            sample.updateM(sensorVal);

                        default:
                            break;
                    }

                    sample.updateSpeedPos();
                    measuredDatabaseManager.saveSample(sample);

                } else {
                    sample = new Sample();
                    rowCounter = 0;
//                    continue to call some API from the service layer
                }
            }

        }
    }

    /**
     * Handle action Fetch_Sensor_Data in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchSensorData(int sensorType, float timestamp, float[] eventValues) {
        // TODO: Handle action Foo
    }


}
