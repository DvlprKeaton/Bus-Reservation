package com.example.bus21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

public class sDashboard extends AppCompatActivity {

    private CodeScanner mCodeScanner;

    public static final String SHARED_PREFS = "shared_prefs";

    public static final String CONTACT_KEY = "contact_key";

    String bid;
    String contact;
    String scontact;

    DatabaseReference reference;

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdashboard);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        scontact = sharedpreferences.getString(CONTACT_KEY, null);
        reference = FirebaseDatabase.getInstance().getReference("Booking");





        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Booking").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                bid = result.toString().trim();
                                Toast.makeText(sDashboard.this, bid, Toast.LENGTH_SHORT).show();

                                Query checkUser = FirebaseDatabase.getInstance().getReference("Booking").orderByChild("contact").equalTo(bid);
                                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {

                                            String fname = snapshot.child(bid).child("contact").getValue(String.class);
                                            String lname = snapshot.child(bid).child("status").getValue(String.class);

                                            Toast.makeText(sDashboard.this, ""+fname + lname, Toast.LENGTH_SHORT).show();

                                            bookValidate();


                                        } else {

                                            Toast.makeText(sDashboard.this, "Data Does Not Exist", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(sDashboard.this, bid, Toast.LENGTH_SHORT).show();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                        Toast.makeText(sDashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    private boolean bookValidate() {

        if (!bid.equals(null)){

            reference.child(bid).child("status").setValue("1");
            Toast.makeText(this, "Passenger is in the BUS", Toast.LENGTH_SHORT).show();
            return true;

        }else {
            return false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


    }

