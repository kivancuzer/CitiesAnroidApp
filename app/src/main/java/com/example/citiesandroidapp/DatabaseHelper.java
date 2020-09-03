package com.example.citiesandroidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
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

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Called when the database connection is being configured.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    //Called when the database is created for the FIRST time.
    //If a database already exists on disk with the same name DATABASE_NAME, this method will not be called.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CITIES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CITIES +
                "(" + KEY_CITIES_ID + " INTEGER PRIMARY KEY, " + KEY_CITIES_NAME + " VARCHAR" + ")";
        sqLiteDatabase.execSQL(CREATE_CITIES_TABLE);
    }

    //Called when the database needs to be upgraded.
    //This method changes database version
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
            onCreate(sqLiteDatabase);

        }
    }

    //Insert a city into the database
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

    //Delete a city into the database
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

    //Update old City to new City into the database
    public void updateCity(City oldCity, City newCity) {
        try {
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

    //Select all cities from database
    public List<City> getAllCities() {
        List<City> cities = new ArrayList<>();
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

    //Checks the database to avoid duplication
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
