package qi.muxi.movementtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Muxi
 */
public class MeasuredDatabaseHelper extends SQLiteOpenHelper { //TODO when to close the database?
    public static final String LOG_TAG = "MeasuredDatabaseHelper";

    private static final String DB_NAME = "measuredData.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_T = "timeIntervalData";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_T = "timeInterval";

    private static final String TABLE_ACC = "accelerometerData";
    private static final String COLUMN_ACC_X = "accelerometerXData";
    private static final String COLUMN_ACC_Y = "accelerometerYData";
    private static final String COLUMN_ACC_Z = "accelerometerZData";

    private static final String TABLE_G = "gravityData";
    private static final String COLUMN_G_X = "gravityXData";
    private static final String COLUMN_G_Y = "gravityYData";
    private static final String COLUMN_G_Z = "gravityZData";

    private static final String TABLE_LACC = "linearAccelerometerData";
    private static final String COLUMN_LACC_X = "linearAccelerometerXData";
    private static final String COLUMN_LACC_Y = "linearAccelerometerYData";
    private static final String COLUMN_LACC_Z = "linearAccelerometerZData";

    private static final String TABLE_M = "magneticData";
    private static final String COLUMN_M_X = "magneticXData";
    private static final String COLUMN_M_Y = "magneticYData";
    private static final String COLUMN_M_Z = "magneticZData";

    private static final String TABLE_WACC = "worldAccelerationData";
    private static final String COLUMN_WACC_X = "worldAccelerationXData";
    private static final String COLUMN_WACC_Y = "worldAccelerationYData";
    private static final String COLUMN_WACC_Z = "worldAccelerationZData";

    private static final String TABLE_SPEED = "worldSpeedData";
    private static final String COLUMN_SPEED_X = "worldSpeedXData";
    private static final String COLUMN_SPEED_Y = "worldSpeedYData";
    private static final String COLUMN_SPEED_Z = "worldSpeedZData";

    private static final String TABLE_POS = "worldPositionData";
    private static final String COLUMN_POS_X = "worldPositionXData";
    private static final String COLUMN_POS_Y = "worldPositionYData";
    private static final String COLUMN_POS_Z = "worldPositionZData";

    public MeasuredDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

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

    // Set private to disable deletion
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
     * Selects the time from table T by _id.
     *
     * @param _id the value of target id.
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
     * @param _id   the the value of target id.
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
     * Storage all table data into external storage files in path "yourExternalStorageDocumentDirectory/movementTracker".
     * Post an error log if external storage is not writable.
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
