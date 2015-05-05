package qi.muxi.movementtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*
* Main activity - SensorActivity to interact with the user once the app is opened
* Second activity - ResultActivity (triggered by notification sent from IntentService)
*
* what is implemented here:
*   Using 3 threads:
*   main Thread (UI thread) for user interaction only
*   background Thread: SensorThread (sensor event listener thread),  for sensor events monitoring and start service only
*   background Thread2: SensorDataProcessService (IntentService thread), for interact with sqlitedatabase and send final notification only
* */
public class SensorActivity extends Activity implements SensorEventListener {

    private static final String LOG_TAG = "SensorActivity";

    TextView introLine1, introLine2, introLine3;
    ImageView testImage;// TODO: test image view (should be replaced by the chenlin's imageview/bitmap)

    private boolean enableSensing;
    private boolean endSensingFlag;
    private SensorManager mSensorManager;
    private Sensor laccSensor, gravity, mSensor;
    private MeasuredDatabaseManager measuredDatabaseManager;

    private SensorThread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        Log.i(LOG_TAG, "onCreate reached");

        introLine1 = (TextView) findViewById(R.id.manual_line1);
        introLine2 = (TextView) findViewById(R.id.manual_line2);
        introLine3 = (TextView) findViewById(R.id.manual_line3);
        introLine1.setVisibility(View.GONE);
        introLine2.setVisibility(View.GONE);
        introLine3.setVisibility(View.GONE);

//      TODO: convert the imageview to bitmap which implements parcealable to send by intent
        testImage = (ImageView) findViewById (R.id.output_image);

        myThread = new SensorThread("SensorThread");
        myThread.start();

        enableSensing = getIntent().getBooleanExtra("enableSensingFlag", false);
        endSensingFlag = false;
//      TODO: EVETYTIME the SensorActivity started, clear the database (Change the pos of this method call anywhere if needed)
        measuredDatabaseManager = MeasuredDatabaseManager.getInstance(getApplicationContext());

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        laccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        final Button manualButton = (Button) findViewById (R.id.manual_button);
        manualButton.setOnClickListener (new View.OnClickListener(){
            public void onClick (View view) {
                introLine1.setVisibility(View.VISIBLE);
                introLine2.setVisibility(View.VISIBLE);
                introLine3.setVisibility(View.VISIBLE);
             }
        });

        final Button startButton = (Button) findViewById(R.id.start_button2);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measuredDatabaseManager.clearDatabase();
                Log.i(LOG_TAG, "database is already cleared !");
                enableSensing = true;
                Log.i(LOG_TAG, "start button pressed");
                Toast.makeText(getApplicationContext(), "startButton pressed", Toast.LENGTH_SHORT).show();
            }
        });

//      click the end_button to end (fetching data && start new background service)
        final Button endButton = (Button) findViewById(R.id.end_button2);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              Stop adding new service-intents in the background,
//              onHandleIntent() is still processing those intents left in the message queues
                enableSensing = false;
                endSensingFlag = true;
                Log.i(LOG_TAG, "end button pressed");
                Toast.makeText(getApplicationContext(), "endButton pressed", Toast.LENGTH_SHORT).show();
            }
        });

//      once the test for the backend id done, which means this button is no longer in use, remove following code
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
    }

    /**
     * This methods are called when the volume button on the sides of mobile devices
     * are clicked, used to replace the Button View to save from screen display
     * @param event triggered when the volume hardware button of mobile devices are clicked
     * @return boolean indicate the operation is successful or not
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //treat it as start button, indicates the beginning of sensing
                    measuredDatabaseManager.clearDatabase();
                    Log.i(LOG_TAG, "database is already cleared !");
                    enableSensing = true;
                    Toast.makeText(getApplicationContext(), "start fetching input", Toast.LENGTH_SHORT).show();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    // treat it as end button, indicates the end of sensing
                    enableSensing = false;
                    endSensingFlag = true;
                    Toast.makeText(getApplicationContext(), "end fetching input", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
// TODO: if no action bar needed for this activity, remove all the commented lines below
// TODO: action bar for this app is mainly used for share (or some other usage i am not sure)
// TODO: so i created it on the notification activity for sharing and some other operations
//    /**
//     * inflate to display the view of the widgets of the action bar
//     * @param menu menu icons on the action bar
//     * @return  action bar icons are inflated or not
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_sensor, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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
                    sensorEvent.timestamp, sensorEvent.sensor.getType(), false);
        }

        if (endSensingFlag) {
            Log.i(LOG_TAG, "Stop sensor events, change the service intent FLAG to true");
            endSensingFlag = false;
//          Last intent service here created to: passing notification contents && lastServiceFlag (image png, jpg)
            Intent intent = new Intent(this, SensorDataProcessService.class);
            intent.setAction("qi.muxi.movementtracker.action.STORE_SENSOR_DATA");
            intent.putExtra("qi.muxi.movementtracker.extra.END_FETCH", true);
//            intent.putExtra ("outputImage", byteArray);
//            intent.putExtra ("bmpName", bmpName);
            startService(intent);
        }
    }
//   if the bitmap is very large file, create a local file is recommended
    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "resultImage";//no .png or .jpg needed
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
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
