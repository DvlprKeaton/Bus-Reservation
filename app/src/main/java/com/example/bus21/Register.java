package com.example.bus21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Random;

public class Register extends AppCompatActivity {

    DatabaseHelper db;
    EditText fname,lname, cont, password, conpassword, otpet, conotp;
    Button btn_register, btn_Rlogin, btn_otp;
    TextView regiter_txt, txt1;
    AwesomeValidation awesomeValidation;

    String OTPs;


    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        fname = (EditText)findViewById(R.id.et_fname);
        lname = (EditText)findViewById(R.id.et_lname);
        cont = (EditText)findViewById(R.id.et_number);
        password = (EditText) findViewById(R.id.et_pword);
        conpassword = (EditText) findViewById(R.id.et_confpassword);
        btn_register = (Button)findViewById(R.id.btn_register);
        regiter_txt = findViewById(R.id.register_txt);
        txt1 = findViewById(R.id.txt1);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        DAOPassenger dao = new DAOPassenger();

        btn_otp = findViewById(R.id.btn_otp);
        otpet = findViewById(R.id.OTP);
        conotp = findViewById(R.id.conOTP);

        Handler handler = new Handler();


        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.et_pword
                ,".{8,}",R.string.invalid_pass);

        awesomeValidation.addValidation(this,R.id.et_confpassword
                ,R.id.et_pword,R.string.invalid_confirmpass);

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Random OTP = new Random();
                int OTPn = OTP.nextInt(9999);

                OTPs = OTPn +"";

                conotp.setText(OTPs);


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Register.this, ""+OTPs, Toast.LENGTH_SHORT).show();
                        otpet.setText(OTPs);
                        conotp.setText(OTPs);
                    }
                }, 10000);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(Register.this)
                        .setSmallIcon(R.drawable.ic_lock)
                        .setContentTitle("OTP Code")
                        .setContentText(""+OTPs)
                        .setAutoCancel(true);

                Intent intent = new Intent(Register.this, Register.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("OTP", OTPs);

                PendingIntent pendingIntent = PendingIntent.getActivity(Register.this, 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(
                        Context.NOTIFICATION_SERVICE
                ) ;
                notificationManager.notify(0,builder.build());
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = otpet.getText().toString();
                String conotpp = conotp.getText().toString();

                if (otp.isEmpty()){
                    otpet.setError("You must enter OTP");
                }else if (!otp.equals(conotpp)){
                    otpet.setError("OTP Does not match");
                }else {
                    Random rand = new Random();
                    int n = rand.nextInt(999);
                    String sfname = fname.getText().toString();
                    String slname = lname.getText().toString();
                    String scont = cont.getText().toString();
                    String spass = password.getText().toString();
                    String sconpass = conpassword.getText().toString();
                    String pid = n+"-"+sfname;

                    if (spass.isEmpty() || spass.length()<6){
                        password.setError("Password must contain 6 Characters");
                    }else if (!spass.equals(sconpass)){
                        conpassword.setError("Password Does not match");
                    }else {
                        Passenger passenger = new Passenger(pid.toString().trim(),fname.getText().toString(), lname.getText().toString(), cont.getText().toString(), password.getText().toString(), "Passenger");
                        dao.add(passenger).addOnSuccessListener(suc -> {
                            Toast.makeText(Register.this, "Registration Success", Toast.LENGTH_SHORT).show();
                            Intent moveTologin = new Intent(Register.this, MainActivity.class);
                            startActivity(moveTologin);
                        }).addOnFailureListener(er ->
                        {
                            Toast.makeText(Register.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    }
                }



            }
        });


        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(Register.this, MainActivity.class);
                startActivity(registerIntent);

            }
        });
    }

}