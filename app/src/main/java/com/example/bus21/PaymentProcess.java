package com.example.bus21;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Random;

public class PaymentProcess extends AppCompatActivity {

    String itemValue, itemAmount, bid, time;

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String CONTACT_KEY = "contact_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    public static final String MARKERTITLE_KEY = "markertitle_key";

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String stitle, scontact;

    ListView listView, amountView;

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> amountList = new ArrayList<>();

    String[] s = {"8:00am ", "9:00am", "10:00am", "11:00am", "12:00nn", "1:00pm",
            "2:00pm", "3:00pm", "4:00pm ", "5:00pm"};

    Spinner spinner;

    String bfname, blname;

    DOABooking dao = new DOABooking();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_process);

        Bundle extras = getIntent().getExtras();

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        stitle = sharedpreferences.getString(MARKERTITLE_KEY, null);
        scontact = sharedpreferences.getString(CONTACT_KEY, null);

        Intent intent = getIntent();

        if(itemValue == null) {

            itemValue = intent.getStringExtra("itemValue");
            itemAmount = intent.getStringExtra("itemAmount");
            bid = intent.getStringExtra("bid");
            time = intent.getStringExtra("time");

            Booking booking = new Booking(bid.toString().trim(),bfname.toString().trim(), blname.toString().trim(),scontact.toString().trim(),
                    itemValue.toString().trim(),time.toString().trim(),itemValue.toString().trim(),"0");
            dao.add(booking).addOnSuccessListener(suc -> {
                Toast.makeText(this, bfname+"\n"+blname+"\n"+scontact+"\n"+itemValue+"\n"+time+"\n"+itemValue, Toast.LENGTH_SHORT).show();
                Intent registerIntent = new Intent(PaymentProcess.this, Dashboard.class);
                startActivity(registerIntent);
            }).addOnFailureListener(er ->
            {
                Toast.makeText(this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }
    }
}