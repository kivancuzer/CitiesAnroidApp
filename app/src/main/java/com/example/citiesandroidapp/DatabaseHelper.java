package com.example.citiesandroidapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Instance
    private static DatabaseHelper sInstance;
    //Database Info
    private static final String DATABASE_NAME = "Cities";
    private static final int DATABASE_VERSION = 1;

    //Table Name
    private static final String TABLE_CITIES = "cities";

    //cities Table Column
    private static final String KEY_CITIES_ID = "id";
    private static final String KEY_CITIES_NAME = "name";

    //Status for the txtResult
    public String status = "Result Of The Action";

    /**
     * Setting Status after a crud operation.
     *
     * @param status is a success or fail message which received from crud operations.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Getting status
     *
     * @return status which is last one.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Synchronize Database Helper class
     *
     * @param context Context which will be used.
     * @return Instance
     */
    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * Called when the database connection is being configured.
     *
     * @param db Database which will be configured.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Create database if not created before.
     *
     * @param sqLiteDatabase Database which will be created.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CITIES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CITIES +
                "(" + KEY_CITIES_ID + " INTEGER PRIMARY KEY, " + KEY_CITIES_NAME + " VARCHAR" + ")";
        sqLiteDatabase.execSQL(CREATE_CITIES_TABLE);
    }


    /**
     * Upgrade Database version.
     *
     * @param sqLiteDatabase SQLiteDatabase which is used.
     * @param oldVersion old version number which version of database used.
     * @param newVersion new version number which version of database will be used.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
            onCreate(sqLiteDatabase);

        }
    }

    /**
     * Insert a city into the database
     *
     * @param city City which will be add into the database
     */
    public void addCity(City city) {
        try {
            if (city.name.length() < 1) {
                setStatus("Please Enter City Name Correctly");
            } else {
                if (dbCheck(city)) {
                    setStatus("This City Name Has Already Exist");
                } else {
                    SQLiteDatabase sqLiteDatabase = getWritableDatabase();
                    String sqlString = "INSERT INTO " + TABLE_CITIES + " (" + KEY_CITIES_NAME + ")" + " VALUES(?)";
                    SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlString);
                    sqLiteStatement.bindString(1, city.name);
                    sqLiteStatement.execute();
                    setStatus(city.name + " Successfully Saved");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error while trying to add city to database");
        }

    }


    /**
     * Delete a city into the database
     *
     * @param city City which will be deleted.
     */
    public void deleteCity(City city) {
        try {
            if (dbCheck(city)) {
                SQLiteDatabase sqLiteDatabase = getWritableDatabase();
                String sqlString = "DELETE FROM " + TABLE_CITIES + " WHERE " + KEY_CITIES_NAME + " = ? ";
                SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlString);
                sqLiteStatement.bindString(1, city.name);
                sqLiteStatement.execute();
                setStatus(city.name + " Successfully Deleted");
            } else {
                setStatus("You Cannot Delete City That Has Not Exists in DB");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error while trying to delete city from database");
        }
    }

    /**
     * Update old City to new City into the database
     *
     * @param oldCity Old City which will be deleted.
     * @param newCity New City which will be added to the city list
     */
    public void updateCity(City oldCity, City newCity) {
        try {
            //Name has to be more than 1 character.
            if (newCity.name.length() < 1) {
                setStatus("Please Enter City Name Correctly");
            } else {
                if (dbCheck(oldCity)) {
                    if (dbCheck(newCity)) {
                        setStatus(newCity.name + " Has Already in DB");
                    } else {
                        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
                        String sqlString = "UPDATE " + TABLE_CITIES + " SET " + KEY_CITIES_NAME + " = ? WHERE " + KEY_CITIES_NAME + " = ? ";
                        SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlString);
                        sqLiteStatement.bindString(1, newCity.name);
                        sqLiteStatement.bindString(2, oldCity.name);
                        sqLiteStatement.execute();
                        setStatus(oldCity.name + " Modified to " + newCity.name);
                    }
                } else {
                    setStatus("You Cannot Modify a City That Has Not Exist");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Error while trying to update city");
        }

    }


    /**
     * Getting All Cities from database
     *
     * @return cities, all City object in an ArrayList
     */
    public ArrayList<City> getAllCities() {
        ArrayList<City> cities = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_CITIES, null);
        int nameIx = cursor.getColumnIndex("name");
        int idIx = cursor.getColumnIndex("id");
        try {
            if (cursor.moveToFirst()) {
                do {
                    City city = new City();
                    city.name = cursor.getString(cursor.getColumnIndex(KEY_CITIES_NAME));
                    cities.add(city);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return cities;
    }

    /**
     * Checks the database to avoid duplication
     *
     * @param city City which will be checked.
     * @return True for the matching, False for not matching in database.
     */
    public boolean dbCheck(City city) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_CITIES, null);
        int nameIx = cursor.getColumnIndex(KEY_CITIES_NAME);
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIx).equals(city.name)) {
                return true;
            }
        }
        cursor.close();
        return false;
    }


}
