package com.example.bus21;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class nav_side extends AppCompatActivity {

    TextView uname, email;
    DatabaseHelper db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        email = findViewById(R.id.nav_email);
        db = new DatabaseHelper(this);
        Cursor res = db.getdata();
        StringBuffer buffer = new StringBuffer();
        res.moveToNext();
        email.append(res.getString (3));
        uname.append(res.getString(4));
    }
}
