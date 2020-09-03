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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnAdd;
    Button btnRemove;
    Button btnModify;
    EditText txtCity;
    EditText txtOldValue;
    EditText txtNewValue;
    TextView txtResult;
    //SQLiteDatabase database;
    DatabaseHelper databaseHelper;

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

       try{
           databaseHelper = DatabaseHelper.getInstance(this);
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    //btnAdd onClick function delete
    public void save(View view) {
        //Create a city with name
        City city = new City();
        city.name = txtCity.getText().toString().toUpperCase();
        databaseHelper.addCity(city);

        List<City> cities = databaseHelper.getAllCities();
        for (City cityList : cities) {
            System.out.println(cityList.name.toString());
        }
        //
        txtResult.setText(databaseHelper.getStatus());
    }

    //btnRemove onClick function delete
    public void delete(View view) {
        City city = new City();
        city.name = txtCity.getText().toString().toUpperCase();
        databaseHelper.deleteCity(city);
        List<City> cities = databaseHelper.getAllCities();
        for (City cityList : cities) {
            System.out.println(cityList.name.toString());
        }
        txtResult.setText(databaseHelper.getStatus());
    }

    //btnModify onClick function update
    public void update(View view) {
        City oldCity = new City();
        City newCity = new City();
        oldCity.name = txtOldValue.getText().toString().toUpperCase();
        newCity.name = txtNewValue.getText().toString().toUpperCase();
        databaseHelper.updateCity(oldCity,newCity);

        List<City> cities = databaseHelper.getAllCities();
        for (City cityList : cities) {
            System.out.println(cityList.name.toString());
        }
        txtResult.setText(databaseHelper.getStatus());
    }
}