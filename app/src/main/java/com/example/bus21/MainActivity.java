package com.example.bus21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    public EditText uname;
    public EditText password;
    public Button login;
    public TextView register, forgot;
    DatabaseHelper db;

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String CONTACT_KEY = "contact_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String scontact, spassword;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        uname = (EditText) findViewById(R.id.et_name);
        password = (EditText) findViewById(R.id.et_pw);
        login = (Button) findViewById(R.id.btn_login);
        register = findViewById(R.id.txt);
        progressBar = new ProgressBar(this);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        scontact = sharedpreferences.getString(CONTACT_KEY, null);
        spassword = sharedpreferences.getString(PASSWORD_KEY, null);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(MainActivity.this, Register.class);
                startActivity(registerIntent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String suname = uname.getText().toString().trim();
                        String pword = password.getText().toString().trim();

                        progressBar.setVisibility(View.VISIBLE);

                        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("contact").equalTo(suname);
                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {
                                    uname.setError(null);

                                    String systemPassword = snapshot.child(suname).child("password").getValue(String.class);
                                    if (systemPassword.equals(pword)) {
                                        password.setError(null);

                                        String fname = snapshot.child(suname).child("firstname").getValue(String.class);
                                        String lname = snapshot.child(suname).child("lastname").getValue(String.class);
                                        String usertype = snapshot.child(suname).child("usertype").getValue(String.class);

                                        SharedPreferences.Editor editor = sharedpreferences.edit();

                                        // below two lines will put values for
                                        // email and password in shared preferences.
                                        editor.putString(CONTACT_KEY, suname);
                                        editor.putString(PASSWORD_KEY, pword);

                                        // to save our data with key and value.
                                        editor.apply();

                                        if (usertype.equals("Passenger")){
                                            startActivity(new Intent(MainActivity.this, Dashboard.class));
                                        }else if (usertype.equals("Conductor")){
                                            startActivity(new Intent(MainActivity.this, sDashboard.class));
                                        }else {
                                            Toast.makeText(MainActivity.this, "User Type was not Declared", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Password Does not exist", Toast.LENGTH_SHORT).show();
                                    }
                                } else {

                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Data Does Not Exist", Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                       /* if (snapshot.child(suname).exists()){
                            if (snapshot.child(suname).child("contact").equals(pword)){
                                if (snapshot.child(suname).child("usertype").getValue(String.class).equals("Passenger")){
                                    Preferences.setDataLogin(MainActivity.this, true);
                                    Preferences.setDataUsertype(MainActivity.this,"Passenger");
                                    startActivity(new Intent(MainActivity.this, Dashboard.class));

                                }else if (snapshot.child(suname).child("usertype").getValue(String.class).equals("Conductor")){
                                    Preferences.setDataLogin(MainActivity.this, true);
                                    Preferences.setDataUsertype(MainActivity.this,"Conductor");
                                    Toast.makeText(MainActivity.this,"Conductor",Toast.LENGTH_SHORT).show();
                                }

                            }else {
                                Toast.makeText(MainActivity.this, "Password Invalid", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(MainActivity.this, "Username Does not exist" +suname, Toast.LENGTH_SHORT).show();
                        }*/

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Preferences.getDataLogin(this)) {
            if (Preferences.getDataUsertype(this).equals("Passenger")) {
                startActivity(new Intent(MainActivity.this, Dashboard.class));
                finish();
            } else if (Preferences.getDataUsertype(this).equals("Conductor")) {
                Toast.makeText(MainActivity.this, "Conductor", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /*public void loginbtn(View view)
    {

        String user = uname.getText().toString().trim();
        String pwd = password.getText().toString().trim();
        String type = "login";

        Boolean res = db.checkUser(user, pwd);
        if (res) {
            Intent registerIntent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(registerIntent);
        }

    }*/

    /*
     * startActivity(new Intent (Dashboard.this, MainActivity.class));
     * finish();
     * */

}