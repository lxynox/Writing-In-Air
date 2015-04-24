package qi.muxi.movementtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

/**
 * @author Muxi & Chen
 */
public class MeasuredDatabaseManager {
    public static final String LOG_TAG = "MeasuredDatabaseManager";

    private static MeasuredDatabaseManager measuredDatabaseManager;
    private MeasuredDatabaseHelper mHelper;
    private SQLiteDatabase db;

    private MeasuredDatabaseManager(Context context) {
        mHelper = new MeasuredDatabaseHelper(context);
        db = mHelper.getWritableDatabase();
    }

    public synchronized static MeasuredDatabaseManager getInstance(Context context) {
        if (measuredDatabaseManager == null) {
            measuredDatabaseManager = new MeasuredDatabaseManager(context);
        }
        return measuredDatabaseManager;
    }


    public void saveSample(Sample sample) {
        if (sample != null) {
            mHelper.insertSample(sample);
        }
    }

    public void printAll() throws IOException {
        mHelper.printAll();
    }

    public int clearDatabase() {
        return mHelper.clearDatabase();
    }
}