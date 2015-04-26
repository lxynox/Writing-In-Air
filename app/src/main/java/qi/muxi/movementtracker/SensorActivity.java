package qi.muxi.movementtracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class SensorActivity extends ActionBarActivity implements SensorEventListener {

    private static final String LOG_TAG = "SensorActivity";
    public static Handler uiHandler;

    TextView xAxisValue, yAxisValue, zAxisValue;
    private boolean enableSensing;
    private SensorManager mSensorManager;
    private Sensor laccSensor, gravity, mSensor;
    private MeasuredDatabaseManager measuredDatabaseManager;

    private SensorThread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Log.i(LOG_TAG, "onCreate reached");

        xAxisValue = (TextView) findViewById(R.id.x_value);
        yAxisValue = (TextView) findViewById(R.id.y_value);
        zAxisValue = (TextView) findViewById(R.id.z_value);

//        sample values(Change on what the user wants to see) used to update the UI widget views
        xAxisValue.setText(String.valueOf(1));
        yAxisValue.setText(String.valueOf(2));
        zAxisValue.setText(String.valueOf(3));

        myThread = new SensorThread("SensorThread");
        myThread.start();

        enableSensing = getIntent().getBooleanExtra("enableSensingFlag", true);
//       TODO: EVETYTIME the SensorActivity started, clear the database (Change the pos of this method call anywhere if needed )
        measuredDatabaseManager = MeasuredDatabaseManager.getInstance(getApplicationContext());
        Log.i(LOG_TAG, "database is already cleared !");
        measuredDatabaseManager.clearDatabase();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        laccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

//        TODO: Click the end_button to end (fetching data && start new background service)
        final Button endButton = (Button) findViewById(R.id.end_button2);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              Stop adding new service-intents in the background,
//              onHandleIntent() is still processing those intents left in the message queues
                enableSensing = false;
//                SensorDataProcessService.endActionFetchSensorData (getApplicationContext());
//                SensorThread.getLooper().quit();
                Log.i(LOG_TAG, "end button pressed");
                Toast.makeText(getApplicationContext(), "endButton pressed", Toast.LENGTH_SHORT).show();
            }
        });

        measuredDatabaseManager = MeasuredDatabaseManager.getInstance(this);
        final Button reviewButton = (Button) findViewById (R.id.review_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i(LOG_TAG, "reviewButton pressing");

                try {
                    measuredDatabaseManager.printAll();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Saving Failed");
                    e.printStackTrace();
                }

                Log.i(LOG_TAG, "reviewButton pressed");
                Toast.makeText(getApplicationContext(), "reviewButton pressed", Toast.LENGTH_SHORT).show();
            }
        });
//        UI handler class used to update the views on main Thread (Perhaps the final 2D-image of the user's gesture input )
        /**
         * TODO: Retrieving data from backend and display on the UI (suggested ways of implementations)
         *     1. BroadcastReceiver to receive background service/intents from other background threads/backend
         *     2. Handler to deal with the Message using handleMessage(Message inputMessage)
         *     3. Asynctask to deliver intents as service using putExtra("",obj)
         */

       /* uiHandler = new Handler(Looper.getMainLooper()) {
            *//*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             *//*
            @Override
            public void handleMessage(Message inputMessage) {
                // Gets the image task from the incoming Message object.
               // super.handleMessage(inputMessage);
               Log.i (LOG_TAG, "Handle message reached ~~~");
                if (inputMessage != null) {
                    if (inputMessage.what == 1) {
                        float[] sensorVal = (float[]) inputMessage.obj;
                        xAxisValue.setText(String.valueOf(sensorVal[0]));
                        yAxisValue.setText(String.valueOf(sensorVal[1]));
                        zAxisValue.setText(String.valueOf(sensorVal[2]));
                    }
                }
            }
        };*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (enableSensing) {
            Log.i(LOG_TAG, "onSensorChanged reached!");
//          This method is called on the background thread: SensorThread

//            passing event values to background service using intents
            Message eventMessage = myThread.getHandler().obtainMessage(1, sensorEvent);
            eventMessage.setTarget(myThread.getHandler());
            eventMessage.sendToTarget();

            Log.i(LOG_TAG, "Current running thread is : " + Thread.currentThread());
//            start another background thread here: SensorDataProcessService($IntentService) thread
            SensorDataProcessService.startActionFetchSensorData(this, sensorEvent.values,
                    sensorEvent.timestamp, sensorEvent.sensor.getType());
        }

    }

    //@Override
    public void onAccuracyChanged(Sensor sensor, int i) {
//      do something here when accuracy has been changed
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume reached!");

        //  start another new thread here
        if (gravity != null)
            // registerListener (Context, Sensor, int sample_frequency, Handler handler)
            mSensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME, myThread.getHandler());

        if (laccSensor != null)
            mSensorManager.registerListener(this, laccSensor, SensorManager.SENSOR_DELAY_GAME, myThread.getHandler());

        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME, myThread.getHandler());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop reached!");
        mSensorManager.unregisterListener(this);
    }
}
