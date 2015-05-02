package qi.muxi.movementtracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

/**
 * Second activity used to display the final output of the user
 * precondition: user opened the notification bar and clicked on the icon of this app
 * postcondition: this activity started with proper result on the UI
 */
public class ResultActivity extends ActionBarActivity {

    private static final String LOG_TAG = "ResultActivity";
    ImageView outputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        /* 1. BroadcastReceiver to receive background service/intents from other background threads/backend
         * 2. Handler to deal with the Message using handleMessage(Message inputMessage)
         * 3. Asynctask to deliver intents as service using putExtra("",obj)
         */
//      UI handler class used to update the views on main Thread (Perhaps the final 2D-image of the user's gesture input )
//      TODO: Retrieving data from the backend service(IntentService) and display result on the UI (suggested ways of implementations)
        Intent resultIntent = getIntent();
        byte[] byteArray = resultIntent.getByteArrayExtra ("resultByteArray");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,  0, byteArray.length);
        outputView = (ImageView) findViewById (R.id.output);
        outputView.setImageBitmap (bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        return super.onCreateOptionsMenu(menu);
    }

    private Intent getDefaultIntent() {
        Intent defaultIntent = new Intent(Intent.ACTION_SEND);
        Uri path = Uri.parse ("android.resource://qi.muxi.movementtracker/" + R.drawable.sample_img);
        defaultIntent.putExtra(Intent.EXTRA_STREAM, path);
        defaultIntent.setType("image/jpeg");
        return defaultIntent;
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
        } else if (id == R.id.action_share) {
            startActivity(Intent.createChooser(getDefaultIntent(), "Share your input image to other applications :)"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
