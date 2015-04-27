package qi.muxi.movementtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A database helper class helping with database interactions directly.
 *
 * @author Muxi
 */
public class MeasuredDatabaseHelper extends SQLiteOpenHelper { //TODO when to close the database?
    /**
     * the log tag string for debugging this class.
     */
    public static final String LOG_TAG = "MeasuredDatabaseHelper";

    /**
     * the name string of database.
     */
    private static final String DB_NAME = "measuredData.sqlite";
    /**
     * the int value of database version.
     */
    private static final int VERSION = 1;

    /**
     * the table name string for timeIntervalData table.
     */
    private static final String TABLE_T = "timeIntervalData";
    /**
     * the _id column name string.
     */
    private static final String COLUMN_ID = "_id";
    /**
     * the timeInterval column name string.
     */
    private static final String COLUMN_T = "timeInterval";

    /**
     * the table name string for accelerometerData table.
     */
    private static final String TABLE_ACC = "accelerometerData";
    /**
     * the acceleration X-axis column name string.
     */
    private static final String COLUMN_ACC_X = "accelerometerXData";
    /**
     * the acceleration Y-axis column name string.
     */
    private static final String COLUMN_ACC_Y = "accelerometerYData";
    /**
     * the acceleration Z-axis column name string.
     */
    private static final String COLUMN_ACC_Z = "accelerometerZData";

    /**
     * the table name string for gravityData table.
     */
    private static final String TABLE_G = "gravityData";
    /**
     * the gravity X-axis column name string.
     */
    private static final String COLUMN_G_X = "gravityXData";
    /**
     * the gravity Y-axis column name string.
     */
    private static final String COLUMN_G_Y = "gravityYData";
    /**
     * the gravity Z-axis column name string.
     */
    private static final String COLUMN_G_Z = "gravityZData";

    /**
     * the table name string for linearAccelerometerData table.
     */
    private static final String TABLE_LACC = "linearAccelerometerData";
    /**
     * the linearAcceleration X-axis column name string.
     */
    private static final String COLUMN_LACC_X = "linearAccelerometerXData";
    /**
     * the linearAcceleration Y-axis column name string.
     */
    private static final String COLUMN_LACC_Y = "linearAccelerometerYData";
    /**
     * the linearAcceleration Z-axis column name string.
     */
    private static final String COLUMN_LACC_Z = "linearAccelerometerZData";

    /**
     * the table name string for magneticData table.
     */
    private static final String TABLE_M = "magneticData";
    /**
     * the magnetic X-axis column name string.
     */
    private static final String COLUMN_M_X = "magneticXData";
    /**
     * the magnetic Y-axis column name string.
     */
    private static final String COLUMN_M_Y = "magneticYData";
    /**
     * the magnetic Z-axis column name string.
     */
    private static final String COLUMN_M_Z = "magneticZData";

    /**
     * the table name string for worldAccelerationData table.
     */
    private static final String TABLE_WACC = "worldAccelerationData";
    /**
     * the worldAcceleration X-axis column name string.
     */
    private static final String COLUMN_WACC_X = "worldAccelerationXData";
    /**
     * the worldAcceleration Y-axis column name string.
     */
    private static final String COLUMN_WACC_Y = "worldAccelerationYData";
    /**
     * the worldAcceleration Z-axis column name string.
     */
    private static final String COLUMN_WACC_Z = "worldAccelerationZData";

    /**
     * the table name string for worldSpeedData table.
     */
    private static final String TABLE_SPEED = "worldSpeedData";
    /**
     * the worldSpeed X-axis column name string.
     */
    private static final String COLUMN_SPEED_X = "worldSpeedXData";
    /**
     * the worldSpeed Y-axis column name string.
     */
    private static final String COLUMN_SPEED_Y = "worldSpeedYData";
    /**
     * the worldSpeed Z-axis column name string.
     */
    private static final String COLUMN_SPEED_Z = "worldSpeedZData";

    /**
     * the table name string for worldPositionData table.
     */
    private static final String TABLE_POS = "worldPositionData";
    /**
     * the worldPosition X-axis column name string.
     */
    private static final String COLUMN_POS_X = "worldPositionXData";
    /**
     * the worldPosition Y-axis column name string.
     */
    private static final String COLUMN_POS_Y = "worldPositionYData";
    /**
     * the worldPosition Z-axis column name string.
     */
    private static final String COLUMN_POS_Z = "worldPositionZData";

    /**
     * Construct measuredDatabaseHelper.
     *
     * @param context the context for construction.
     */
    public MeasuredDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * <p>Called when the database is created for the first time. </p>
     * <p>This is where the creation of tables and the initial population of the tables should happen.</p>
     * <p>This method create tables: </br>
     * <ul>
     * <li>Time Table: <i>Table_T
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>timeInterval</th>
     * </tr>
     * </table></i></li>
     * <li>Accelerometer Table: <i>Table_ACC
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>AccelerometerXData</th>
     * <th>AccelerometerYData</th>
     * <th>AccelerometerZData</th>
     * </tr>
     * </table></i></li>
     * <li>GravityTable: <i>Table_G
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>GravityXData</th>
     * <th>GravityYData</th>
     * <th>GravityZData</th>
     * </tr>
     * </table></i></li>
     * <li>LinearAccelerometerTable: <i>Table_LACC
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>LinearAccelerometerXData</th>
     * <th>LinearAccelerometerYData</th>
     * <th>LinearAccelerometerZData</th>
     * </tr>
     * </table></i></li>
     * <li>MagneticTable: <i>Table_M
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>MagneticXData</th>
     * <th>MagneticYData</th>
     * <th>MagneticZData</th>
     * </tr>
     * </table></i></li>
     * <li>WorldAccelerationTable: <i>Table_WACC
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>WorldAccelerationXData</th>
     * <th>WorldAccelerationYData</th>
     * <th>WorldAccelerationZData</th>
     * </tr>
     * </table></i></li>
     * <li>WorldSpeedTable: <i>Table_SPEED
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>WorldSpeedXData</th>
     * <th>WorldSpeedYData</th>
     * <th>WorldSpeedZData</th>
     * </tr>
     * </table></i></li>
     * <li>WorldPositionTable: <i>Table_POS
     * <table border="1">
     * <tr>
     * <th>_id</th>
     * <th>WorldPositionXData</th>
     * <th>WorldPositionYData</th>
     * <th>WorldPositionZData</th>
     * </tr>
     * </table></i></li>
     * </ul></p>
     *
     * @param db the database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create "timeIntervalData" table
        String createT = "create table " + TABLE_T + "(" + COLUMN_ID + " integer primary key" + ", " + COLUMN_T + " real)";
        db.execSQL(createT);

        // create "accelerometerData" table
        String createAcc = "create table " + TABLE_ACC + "(" + COLUMN_ID + " integer, " + COLUMN_ACC_X + " real, " + COLUMN_ACC_Y + " real, " + COLUMN_ACC_Z + " real, foreign key(" + COLUMN_ID + ") references " + TABLE_T + "(" + COLUMN_ID + "))";
        db.execSQL(createAcc);

        // create "gravityData" table
        String createG = "create table " + TABLE_G + "(" + COLUMN_ID + " integer, " + COLUMN_G_X + " real, " + COLUMN_G_Y + " real, " + COLUMN_G_Z + " real, foreign key(" + COLUMN_ID + ") references " + TABLE_T + "(" + COLUMN_ID + "))";
        db.execSQL(createG);

        // create "linearAccelerometerData" table
        String createLAcc = "create table " + TABLE_LACC + "(" + COLUMN_ID + " integer, " + COLUMN_LACC_X + " real, " + COLUMN_LACC_Y + " real, " + COLUMN_LACC_Z + " real, foreign key(" + COLUMN_ID + ") references " + TABLE_T + "(" + COLUMN_ID + "))";
        db.execSQL(createLAcc);

        // create "magneticData" table
        String createM = "create table " + TABLE_M + "(" + COLUMN_ID + " integer, " + COLUMN_M_X + " real, " + COLUMN_M_Y + " real, " + COLUMN_M_Z + " real, foreign key(" + COLUMN_ID + ") references " + TABLE_T + "(" + COLUMN_ID + "))";
        db.execSQL(createM);

        // create "worldAccelerationData" table
        String createWAcc = "create table " + TABLE_WACC + "(" + COLUMN_ID + " integer, " + COLUMN_WACC_X + " real, " + COLUMN_WACC_Y + " real, " + COLUMN_WACC_Z + " real, foreign key(" + COLUMN_ID + ") references " + TABLE_T + "(" + COLUMN_ID + "))";
        db.execSQL(createWAcc);

        // create "worldSpeedData" table
        String createSpeed = "create table " + TABLE_SPEED + "(" + COLUMN_ID + " integer, " + COLUMN_SPEED_X + " real, " + COLUMN_SPEED_Y + " real, " + COLUMN_SPEED_Z + " real, foreign key(" + COLUMN_ID + ") references " + TABLE_T + "(" + COLUMN_ID + "))";
        db.execSQL(createSpeed);

        // create "worldPositionData" table
        String createPos = "create table " + TABLE_POS + "(" + COLUMN_ID + " integer, " + COLUMN_POS_X + " real, " + COLUMN_POS_Y + " real, " + COLUMN_POS_Z + " real, foreign key(" + COLUMN_ID + ") references " + TABLE_T + "(" + COLUMN_ID + "))";
        db.execSQL(createPos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Clear database, by selecting all content in tables and delete them.
     *
     * @return the number of rows affected if a whereClause "1" is passed in, 0 otherwise.
     */
    public int clearDatabase() {
        int rows;
        String whereClause = "1";
        rows = getWritableDatabase().delete(TABLE_T, whereClause, null);
        getWritableDatabase().delete(TABLE_ACC, whereClause, null);
        getWritableDatabase().delete(TABLE_G, whereClause, null);
        getWritableDatabase().delete(TABLE_LACC, whereClause, null);
        getWritableDatabase().delete(TABLE_M, whereClause, null);
        getWritableDatabase().delete(TABLE_WACC, whereClause, null);
        getWritableDatabase().delete(TABLE_SPEED, whereClause, null);
        getWritableDatabase().delete(TABLE_POS, whereClause, null);
        Log.i(LOG_TAG, "Database cleared");
        return rows;
    }

    /**
     * Insert a sample item into database.
     *
     * @param sample the sample to insert.
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertSample(Sample sample) {
        long rowID;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv_T = new ContentValues();
        cv_T.put(COLUMN_ID, sample.getID());
        cv_T.put(COLUMN_T, sample.getTimeInterval());
        rowID = db.insert(TABLE_T, null, cv_T);
        ContentValues cv_Acc = new ContentValues();
        cv_Acc.put(COLUMN_ID, sample.getID());
        cv_Acc.put(COLUMN_ACC_X, sample.getAcc()[0]);
        cv_Acc.put(COLUMN_ACC_Y, sample.getAcc()[1]);
        cv_Acc.put(COLUMN_ACC_Z, sample.getAcc()[2]);
        db.insert(TABLE_ACC, null, cv_Acc);
        ContentValues cv_G = new ContentValues();
        cv_G.put(COLUMN_ID, sample.getID());
        cv_G.put(COLUMN_G_X, sample.getG()[0]);
        cv_G.put(COLUMN_G_Y, sample.getG()[1]);
        cv_G.put(COLUMN_G_Z, sample.getG()[2]);
        db.insert(TABLE_G, null, cv_G);
        ContentValues cv_LAcc = new ContentValues();
        cv_LAcc.put(COLUMN_ID, sample.getID());
        cv_LAcc.put(COLUMN_LACC_X, sample.getLAcc()[0]);
        cv_LAcc.put(COLUMN_LACC_Y, sample.getLAcc()[1]);
        cv_LAcc.put(COLUMN_LACC_Z, sample.getLAcc()[2]);
        db.insert(TABLE_LACC, null, cv_LAcc);
        ContentValues cv_M = new ContentValues();
        cv_M.put(COLUMN_ID, sample.getID());
        cv_M.put(COLUMN_M_X, sample.getM()[0]);
        cv_M.put(COLUMN_M_Y, sample.getM()[1]);
        cv_M.put(COLUMN_M_Z, sample.getM()[2]);
        db.insert(TABLE_M, null, cv_M);
        ContentValues cv_WAcc = new ContentValues();
        cv_WAcc.put(COLUMN_ID, sample.getID());
        cv_WAcc.put(COLUMN_WACC_X, sample.getWAcc()[0]);
        cv_WAcc.put(COLUMN_WACC_Y, sample.getWAcc()[1]);
        cv_WAcc.put(COLUMN_WACC_Z, sample.getWAcc()[2]);
        db.insert(TABLE_WACC, null, cv_WAcc);
        ContentValues cv_Speed = new ContentValues();
        cv_Speed.put(COLUMN_ID, sample.getID());
        cv_Speed.put(COLUMN_SPEED_X, sample.getSpeed()[0]);
        cv_Speed.put(COLUMN_SPEED_Y, sample.getSpeed()[1]);
        cv_Speed.put(COLUMN_SPEED_Z, sample.getSpeed()[2]);
        db.insert(TABLE_SPEED, null, cv_Speed);
        ContentValues cv_Pos = new ContentValues();
        cv_Pos.put(COLUMN_ID, sample.getID());
        cv_Pos.put(COLUMN_POS_X, sample.getPos()[0]);
        cv_Pos.put(COLUMN_POS_Y, sample.getPos()[1]);
        cv_Pos.put(COLUMN_POS_Z, sample.getPos()[2]);
        db.insert(TABLE_POS, null, cv_Pos);
        return rowID;
    }

    /**
     * <p>Delete specific sample item in all tables, specified by _id. </p>
     * <p><font color = "red">This is private method in order to disable deletion. If ever used, please take a look at the selection and getSize methods, both in this class and {@link MeasuredDatabaseManager}. </font></p>
     *
     * @param _id the value of key _id.
     * @return 1 if passed, otherwise if failed.
     */
    private int deleteSample(long _id) {
        int rows;
        String whereClause = COLUMN_ID + " = " + String.valueOf(_id);
        rows = getWritableDatabase().delete(TABLE_T, whereClause, null);
        getWritableDatabase().delete(TABLE_ACC, whereClause, null);
        getWritableDatabase().delete(TABLE_G, whereClause, null);
        getWritableDatabase().delete(TABLE_LACC, whereClause, null);
        getWritableDatabase().delete(TABLE_M, whereClause, null);
        getWritableDatabase().delete(TABLE_WACC, whereClause, null);
        getWritableDatabase().delete(TABLE_SPEED, whereClause, null);
        getWritableDatabase().delete(TABLE_POS, whereClause, null);
        return rows;
    }

    /**
     * Get the number of rows in timeTable, which is also the number of sample items.
     * @return the number of rows of sample items.
     */
    public long getSize()
    {
        return DatabaseUtils.queryNumEntries(getWritableDatabase(), TABLE_T);
    }

    /**
     * Select the time from table T by _id.
     *
     * @param _id the value of key _id.
     * @return the selected time, if none or more than one entry are selected, return -1.
     */
    public float selectT(long _id) {
        float time = -1;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLE_T + " where " + COLUMN_ID + " = " + String.valueOf(_id);

        Cursor c = db.rawQuery(select, null);
        c.moveToFirst();
        if (c.getCount() == 1) time = c.getFloat(c.getColumnIndex(COLUMN_T));
        c.close();
        return time;
    }

    /**
     * Selects the value array from table TABLE by _id.
     *
     * @param TABLE the name of target table.
     * @param _id   the the value of key _id.
     * @return the selected value array, if none or more than one entries are selected, return all -1.
     */
    public float[] select(String TABLE, long _id) {
        float[] value = new float[3];
        value[0] = -1;
        value[1] = -1;
        value[2] = -1;
        SQLiteDatabase db = getWritableDatabase();
        String select = "select * from " + TABLE + " where " + COLUMN_ID + " = " + String.valueOf(_id);

        Cursor c = db.rawQuery(select, null);
        c.moveToFirst();
        if (c.getCount() == 1) {
            value[0] = c.getFloat(1);
            value[1] = c.getFloat(2);
            value[2] = c.getFloat(3);
        }
        c.close();
        return value;
    }

    /**
     * <p>Storage all table data into external storage files in path <i>"yourExternalStorageDocumentDirectory/movementTracker"</i>. </p>
     * <p>This method will post an error log if external storage is not writable. </p>
     *
     * @throws IOException
     */
    public void printAll() throws IOException {
        BufferedWriter timeFile = createWriter("time.txt");
        BufferedWriter accFile = createWriter("acc.txt");
        BufferedWriter gFile = createWriter("g.txt");
        BufferedWriter lAccFile = createWriter("lacc.txt");
        BufferedWriter mFile = createWriter("m.txt");
        BufferedWriter wAccFile = createWriter("wacc.txt");
        BufferedWriter speedFile = createWriter("speed.txt");
        BufferedWriter posFile = createWriter("pos.txt");
        if ((timeFile != null) && (accFile != null) && (gFile != null) && (lAccFile != null) && (mFile != null) && (wAccFile != null) && (speedFile != null) && (posFile != null)) {
            timeFile.write("_id timeInterval\n");
            accFile.write("_id acc_x acc_y acc_z\n");
            gFile.write("_id g_x g_y g_z\n");
            lAccFile.write("_id lacc_x lacc_y lacc_z\n");
            mFile.write("_id m_x m_y m_z\n");
            wAccFile.write("_id wacc_x wacc_y wacc_z\n");
            speedFile.write("_id speed_x speed_y speed_z\n");
            posFile.write("_id pos_x pos_y pos_z\n");

            SQLiteDatabase db = getWritableDatabase();
            String select = "select * from " + TABLE_T + " where 1";

            Cursor c = db.rawQuery(select, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                long _id = c.getLong(0);
                float time = selectT(_id);
                float[] acc = select(TABLE_ACC, _id);
                float[] g = select(TABLE_G, _id);
                float[] lAcc = select(TABLE_LACC, _id);
                float[] m = select(TABLE_M, _id);
                float[] wAcc = select(TABLE_WACC, _id);
                float[] speed = select(TABLE_SPEED, _id);
                float[] pos = select(TABLE_POS, _id);
                timeFile.write(String.valueOf(_id) + " " + String.valueOf(time) + "\n");
                accFile.write(String.valueOf(_id) + " " + String.valueOf(acc[0]) + " " + String.valueOf(acc[1]) + " " + String.valueOf(acc[2]) + "\n");
                gFile.write(String.valueOf(_id) + " " + String.valueOf(g[0]) + " " + String.valueOf(g[1]) + " " + String.valueOf(g[2]) + "\n");
                lAccFile.write(String.valueOf(_id) + " " + String.valueOf(lAcc[0]) + " " + String.valueOf(lAcc[1]) + " " + String.valueOf(lAcc[2]) + "\n");
                mFile.write(String.valueOf(_id) + " " + String.valueOf(m[0]) + " " + String.valueOf(m[1]) + " " + String.valueOf(m[2]) + "\n");
                wAccFile.write(String.valueOf(_id) + " " + String.valueOf(wAcc[0]) + " " + String.valueOf(wAcc[1]) + " " + String.valueOf(wAcc[2]) + "\n");
                speedFile.write(String.valueOf(_id) + " " + String.valueOf(speed[0]) + " " + String.valueOf(speed[1]) + " " + String.valueOf(speed[2]) + "\n");
                posFile.write(String.valueOf(_id) + " " + String.valueOf(pos[0]) + " " + String.valueOf(pos[1]) + " " + String.valueOf(pos[2]) + "\n");
                c.moveToNext();
            }

            c.close();
            timeFile.close();
            accFile.close();
            gFile.close();
            lAccFile.close();
            mFile.close();
            wAccFile.close();
            speedFile.close();
            posFile.close();
        } else Log.e(LOG_TAG, "Printing failed");
    }

    /**
     * Creates an external storage BufferedWriter by fileName
     *
     * @param fileName the name of file.
     * @return the new writable BufferedWriter, if not new, or not writable, return null.
     * @throws IOException
     */
    private BufferedWriter createWriter(String fileName) throws IOException {
        if (isExternalStorageWritable()) {
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "movementTracker");
            // Log.i(LOG_TAG, dir.getPath() + ":" + dir.getName());
            boolean dirCreated = true;
            if (!dir.exists()) dirCreated = dir.mkdirs(); // if not exists, create it
            if (!dirCreated) {
                Log.e(LOG_TAG, "Directory not created " + fileName);
                return null;
            } else {
                File file = new File(dir.getPath(), fileName);
                boolean deleted = true, created, writable = true;
                if (file.exists()) deleted = file.delete(); // if exists, delete it
                created = file.createNewFile(); // create it no matter exists or not right now
                if (!file.canWrite())
                    writable = file.setWritable(true); // if not writable, set it writable
                if (deleted && created && writable) return new BufferedWriter(new FileWriter(file));
                else {
                    Log.e(LOG_TAG, "File " + fileName + " not created");
                    return null;
                }
            }
        } else return null;
    }

    /**
     * Checks if external storage is available for read and write
     *
     * @return true for writable, false for else
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}
