package qi.muxi.movementtracker;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


/*
* Main activity1(this class): The entry UI(thread) to interact with the user once the app is opened
* Main activity2(SensorActivity.class): The second UI to start background service(using to store data to db) in method onSensorChanged()
* what is implemented here:
*   Using 3 threads: main Thread (UI thread), SensorThread (sensor event listener thread), SensorDataProcessService (IntentService thread)
*   one main thread: only to interact with the user
*   two background threads: 1. monitor sensor events(fetching data) 2. background service used to put data to sqlitedatabase
* TODO: Open app, activity started, onCreate()(starting another thread - SensorThread, the thread used to monitor sensor events) -> onResume()
* TODO: Click start button to start another activity (same thread as this one, pushed the second activity - SensorActivity to the top of StackTrace ), this.onPause() called
* TODO: Once start button is clicked, passing some logic intents (for eg, like boolean enableSensing) to SensorActivity is onCreate() - onResume()
* TODO: (register SensorEventListener on SensorThread(using to fetch data))- start monitoring sensor events
*
* */
public class MainActivity2Activity extends ActionBarActivity {

    public static final String LOG_TAG = "MainActivity";
    private Intent intent;
    private Runnable task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);

        final Button startButton = (Button) findViewById(R.id.start_button2);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                create new working thread using IntentService subclass
                final boolean enableSensing = true;
                Log.i(LOG_TAG, "another activity is gonna start right now, it should be main thread here: " + Thread.currentThread());
                intent = new Intent(getApplicationContext(), SensorActivity.class);
                intent.putExtra("enableSensingFlag", enableSensing);
                startActivity(intent);

                Log.i(LOG_TAG, "start button pressed");
                Toast.makeText(getApplicationContext(), "startButton pressed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
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
//            openSettings();
            return true;
        } else if (id == R.id.action_search) {
//            openSearch();
            return true;
        } else if (id == R.id.action_share) {
//            openShare();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume reached");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause reached");
    }

}
