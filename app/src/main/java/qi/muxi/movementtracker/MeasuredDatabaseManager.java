package qi.muxi.movementtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A manager class to manage database operation, providing methods for other non-database classes to interact with database.
 *
 * @author Muxi & Chen
 */
public class MeasuredDatabaseManager {
    /**
     * the log tag string for debugging this class.
     */
    public static final String LOG_TAG = "MeasuredDatabaseManager";

    /**
     * the database manager, declared static for uniqueness.
     */
    private static MeasuredDatabaseManager measuredDatabaseManager;
    /**
     * the database helper, interacting with database directly.
     */
    private MeasuredDatabaseHelper mHelper;
    /**
     * the database, which is not used here, but can be used if necessary.
     */
    private SQLiteDatabase db;

    /**
     * Construct measuredDatabaseManager.
     *
     * @param context the context provided for construction, used for constructing mHelper.
     */
    private MeasuredDatabaseManager(Context context) {
        mHelper = new MeasuredDatabaseHelper(context);
        db = mHelper.getWritableDatabase();
    }

    /**
     * Get instance measuredDatabaseManager, which is synchronized and unique.
     *
     * @param context the context provided for construction.
     * @return the synchronized measuredDataManager instance.
     */
    public synchronized static MeasuredDatabaseManager getInstance(Context context) {
        if (measuredDatabaseManager == null) {
            measuredDatabaseManager = new MeasuredDatabaseManager(context);
        }
        return measuredDatabaseManager;
    }


    /**
     * Save a sample item into database, as a row.
     *
     * @param sample the sample item to save.
     */
    public void saveSample(Sample sample) {
        if (sample != null) {
            mHelper.insertSample(sample);
        }
    }

    /**
     * Print all items in database into text file, which is in dir <i>"yourExternalStorageDocumentDirectory/movementTracker"</i>.
     *
     * @throws java.io.IOException
     */
    public void printAll() throws IOException {
        mHelper.printAll();
    }

    /**
     * Clear database, by emptying all tables created by this app.
     *
     * @return the number of rows affected if a whereClause "1" is passed in, 0 otherwise.
     */
    public int clearDatabase() {
        return mHelper.clearDatabase();
    }

    /**
     * Load the position data as a float array.
     * @return the Array of position data.
     */
    public float[] loadPos()
    {
        long size = mHelper.getSize();
        float[] posList = new float[(int)size*3];
        for (long _id = 1;_id<=size;_id++) {
            float[] pos = mHelper.select("worldPositionData", _id);
            posList[(int) (_id - 1) * 3] = pos[0];
            posList[(int) (_id - 1) * 3 + 1] = pos[1];
            posList[(int) (_id - 1) * 3 + 2] = pos[2];
        }
        return posList;
    }
}