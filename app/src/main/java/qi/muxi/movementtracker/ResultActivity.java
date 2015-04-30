package qi.muxi.movementtracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Second activity used to display the final output of the user
 * precondition: user opened the notification bar and clicked on the icon of this app
 * postcondition: this activity started with proper result on the UI
 */
public class ResultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
         /*     1. BroadcastReceiver to receive background service/intents from other background threads/backend
         *     2. Handler to deal with the Message using handleMessage(Message inputMessage)
         *     3. Asynctask to deliver intents as service using putExtra("",obj)
         */
//        UI handler class used to update the views on main Thread (Perhaps the final 2D-image of the user's gesture input )
//        TODO: Retrieving data from the backend service(IntentService) and display result on the UI (suggested ways of implementations)
        Intent resultIntent = getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
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
}
