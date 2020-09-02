package com.example.citiesandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnAdd;
    Button btnRemove;
    Button btnModify;
    EditText txtCity;
    EditText txtOldValue;
    EditText txtNewValue;
    TextView txtResult;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        btnRemove = findViewById(R.id.btnRemove);
        btnModify = findViewById(R.id.btnModify);
        txtCity = findViewById(R.id.txtCity);
        txtOldValue = findViewById(R.id.txtOldValue);
        txtNewValue = findViewById(R.id.txtNewValue);
        txtResult = findViewById(R.id.txtResult);

        //Create Database
        try {
            database = this.openOrCreateDatabase("Cities", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS cities (id INTEGER PRIMARY KEY, name VARCHAR)");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void save(View view) {
        try {
            String cityName = txtCity.getText().toString().toUpperCase();
            if(cityName.length()<1){
                txtResult.setText("Please Write City Name");
            }else{
                if (dbCheck(cityName)) {
                    txtResult.setText("This City Name Has Already Exists");
                } else {
                    String sqlString = "INSERT INTO cities (name) VALUES(?)";
                    SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                    sqLiteStatement.bindString(1, cityName);
                    sqLiteStatement.execute();
                    txtResult.setText(cityName + " Successfully saved");
                    list();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void delete(View view) {
        try {
            String cityName = txtCity.getText().toString().toUpperCase();
            if (dbCheck(cityName)) {
                String sqlString = "DELETE FROM cities WHERE name = ? ";
                SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                sqLiteStatement.bindString(1, cityName);
                sqLiteStatement.execute();
                txtResult.setText(cityName + " Successfully deleted");
                list();
            } else {
                txtResult.setText("This City Name Does Not Exists");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void update(View view) {
        try {
            String oldCityName = txtOldValue.getText().toString().toUpperCase();
            String newCityName = txtNewValue.getText().toString().toUpperCase();

            if (dbCheck(oldCityName)) {
                if (dbCheck(newCityName)) {
                    txtResult.setText("This New City Name Already Exists");
                } else {
                    String sqlString = "UPDATE cities SET name = ? WHERE name = ? ";
                    SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
                    sqLiteStatement.bindString(1, newCityName);
                    sqLiteStatement.bindString(2, oldCityName);
                    sqLiteStatement.execute();
                    txtResult.setText(oldCityName + " Modified to " + newCityName);
                    list();
                }

            } else {
                txtResult.setText("This Old City Name has Not Exists");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void list() {
        Cursor cursor = database.rawQuery("SELECT * FROM cities", null);
        int nameIx = cursor.getColumnIndex("name");
        int idIx = cursor.getColumnIndex("id");
        while (cursor.moveToNext()) {
            System.out.println(cursor.getInt(idIx) + " City Name : " + cursor.getString(nameIx));
        }
        cursor.close();
    }

    public boolean dbCheck(String city) {
        Cursor cursor = database.rawQuery("SELECT * FROM cities", null);
        int nameIx = cursor.getColumnIndex("name");
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIx).equals(city)) {
                return true;
            }
        }
        cursor.close();
        return false;
    }

}