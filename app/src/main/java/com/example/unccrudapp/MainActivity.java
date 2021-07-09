package com.example.unccrudapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewUser(View view){

        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    public void viewStore(View view){

        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
    }

    public void viewItem(View view){

        Intent intent = new Intent(this, ItemActivity.class);
        startActivity(intent);
    }
}
