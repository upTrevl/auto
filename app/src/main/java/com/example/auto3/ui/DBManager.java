package com.example.auto3.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Меенеджер работы с базой данных
 */
public class DBManager extends SQLiteOpenHelper {
    Context mContext;
    SQLiteDatabase db;

    private static final String TAG = DBManager.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";


    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * @return Список всех маршрутов
     */
    public ArrayList<Route> getRoutes() {
        db = mContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        ArrayList<Route> routeItems = new ArrayList<>();
        db.execSQL("CREATE TABLE IF NOT EXISTS routes (name TEXT, number INTEGER, price DOUBLE)");
        Cursor query = db.rawQuery("SELECT * FROM routes;", null);
        while (query.moveToNext()) {
            routeItems.add(new Route(query));
        }
        query.close();
        db.close();
        return routeItems;
    }

    public ArrayList<Station> getStations(int id) {
        db = mContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        ArrayList<Station> stationItems = new ArrayList<>();
        db.execSQL("CREATE TABLE IF NOT EXISTS stations (name TEXT, number INTEGER, price DOUBLE)");
        Cursor query = db.rawQuery("SELECT * FROM stations WHERE route_id = " + id + ";", null);
        while (query.moveToNext()) {
            stationItems.add(new Station(query));
        }
        query.close();
        db.close();
        return stationItems;
    }
    public int setDownloaded(int id) {

        db = mContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        ContentValues cv = new ContentValues();
        cv.put("isDownloaded", 1);
        int updCount = db.update("routes", cv, "id = ?", new String[] { String.valueOf(id) });
        db.close();
        return 1;
    }
    public Station getNextStation(int id){
        db = mContext.openOrCreateDatabase("app.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS stations (name TEXT, number INTEGER, price DOUBLE)");
        Cursor query = db.rawQuery("SELECT * FROM stations WHERE id = " + id + ";", null);
        Station station = new Station(query);
        query.close();
        db.close();
        return station;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
