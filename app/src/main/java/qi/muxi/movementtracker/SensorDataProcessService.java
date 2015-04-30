package qi.muxi.movementtracker;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

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
    private static Sample sample;
    private static int rowCounter;
    private MeasuredDatabaseManager measuredDatabaseManager;

    /**
     * static constructor for the init of static variables
     */
    static {
        sample = new Sample();
        rowCounter = 0;
    }

    public SensorDataProcessService() {
        super("SensorDataProcessService");
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
//        Log.i(LOG_TAG, "new service started!");
    }

    /**
     * System calls automatically to handle the Intent queues, once all the intents (messageQueues)
     * are handled, onDestroy() of this service is called by android os.
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
                    Log.i(LOG_TAG, "writing to database!");

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
                    Log.i(LOG_TAG, "writing to database ended!");
                    sample = new Sample();
                    rowCounter = 0;
//                 TODO: continue to call some API from the service layer which serves as
//                 TODO: converting processed backend sensor data to the output format of texts or images
//                 TODO: once the processing units is finished, start a new notification from this service and then end/destroy the service
                    Toast.makeText(getApplicationContext(), "Result is ready, check it please !", Toast.LENGTH_SHORT).show();

//                   TODO: change the the icon of this app, reset the title and contents of notification
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("New notification from Magical Writer")
                                    .setContentText("Hi, your text output is ready ~~~");
// Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(this, ResultActivity.class);
//                  TODO: set/initialize the intent contents here
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(ResultActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }
        }
    }

    /**
     * This is the method called when first time the service is started
     * onStart(), onStartCommand() are called in this method either
     */
    @Override
    public void onCreate() {
        super.onCreate();
        measuredDatabaseManager = MeasuredDatabaseManager.getInstance(getApplicationContext());
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
     * Handle action Fetch_Sensor_Data in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchSensorData(int sensorType, float timestamp, float[] eventValues) {
        // TODO: Handle action Foo
    }
}
