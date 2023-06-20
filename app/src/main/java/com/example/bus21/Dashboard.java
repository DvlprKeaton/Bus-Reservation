package com.example.bus21;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bus21.ui.slideshow.SlideshowFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bus21.databinding.ActivityDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityDashboardBinding binding;
    private FloatingActionButton bubble,SMSbubble,mLocation;
    private Animation openAnim, closeAnim, rotateFward, rotateBward;

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String CONTACT_KEY = "contact_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String scontact;

    boolean isOpen =false;

    String lat, longt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bubble = findViewById(R.id.bubblehead);
        SMSbubble = findViewById(R.id.smsbubble);
        mLocation = findViewById(R.id.mLocation);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        scontact = sharedpreferences.getString(CONTACT_KEY, null);

        openAnim = AnimationUtils.loadAnimation(Dashboard.this, R.anim.open_anim);
        closeAnim = AnimationUtils.loadAnimation(Dashboard.this, R.anim.close_anim);
        rotateFward = AnimationUtils.loadAnimation(Dashboard.this, R.anim.rotate_forward_anim);
        rotateBward = AnimationUtils.loadAnimation(Dashboard.this, R.anim.rotate_backward_anim);

        Intent intent = getIntent();

        if(intent != null){
            lat = getIntent().getStringExtra("lat");
            longt = getIntent().getStringExtra("longt");
            Log.d("lat", "" + lat);
            Log.d("longt", "" + longt);
/*
            Intent intent1 = new Intent (this, HomeViewModel.class);
            Bundle bundle = new Bundle();
            bundle.putString("lat", lat);
            intent1.putExtras(bundle);
            startActivity(intent1);*/

        }
        else{
            message("Intent is null");
        }

        bubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animate();

            }
        });
        SMSbubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message("SMS notification Sent!");
            }
        });
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingSheet bookingSheet = new BookingSheet();
                bookingSheet.show(getSupportFragmentManager(),"TAG");
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String contact = scontact.trim();

                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("contact").equalTo(contact);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                                String fname = snapshot.child(contact).child("firstname").getValue(String.class);
                                String lname = snapshot.child(contact).child("lastname").getValue(String.class);
                                String usertype = snapshot.child(contact).child("usertype").getValue(String.class);

                                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                                View headerView = navigationView.getHeaderView(0);
                                TextView navEmail = (TextView) headerView.findViewById(R.id.nav_email);
                                navEmail.setText(fname +" " + lname);

                                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                                //NavigationView navigationView = findViewById(R.id.nav_view);
                                // Passing each menu ID as a set of Ids because each
                                // menu should be considered as top level destinations.
                                mAppBarConfiguration = new AppBarConfiguration.Builder(
                                        R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,R.id.nav_booking)
                                        .setDrawerLayout(drawer)
                                        .build();
                                NavController navController = Navigation.findNavController(Dashboard.this, R.id.nav_host_fragment_content_dashboard);
                                NavigationUI.setupActionBarWithNavController(Dashboard.this, navController, mAppBarConfiguration);
                                NavigationUI.setupWithNavController(navigationView, navController);

                        } else {

                            Toast.makeText(Dashboard.this, "Data Does Not Exist", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(Dashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void animate(){
        if (isOpen){
            bubble.startAnimation(rotateFward);
            SMSbubble.startAnimation(closeAnim);
            mLocation.startAnimation(closeAnim);
            SMSbubble.setClickable(false);
            mLocation.setClickable(false);
            isOpen=false;

        }else {
            bubble.startAnimation(rotateBward);
            SMSbubble.startAnimation(openAnim);
            mLocation.startAnimation(openAnim);
            SMSbubble.setClickable(true);
            mLocation.setClickable(true);
            isOpen=true;
        }
    }
    public void message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}