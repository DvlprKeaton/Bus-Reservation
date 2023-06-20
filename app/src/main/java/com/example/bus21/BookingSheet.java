package com.example.bus21;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.Random;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class BookingSheet extends BottomSheetDialogFragment {


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

    String bfname, blname, time;

    DOABooking dao = new DOABooking();

    public BookingSheet() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_booking_sheet, container, false);

        sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        stitle = sharedpreferences.getString(MARKERTITLE_KEY, null);
        scontact = sharedpreferences.getString(CONTACT_KEY, null);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);
        ArrayAdapter<String> amountAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, amountList);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, s);

        listView = view.findViewById(R.id.destList);
        amountView = view.findViewById(R.id.amountList);
        listView.setAdapter(arrayAdapter);
        amountView.setAdapter(amountAdapter);

        spinner = new Spinner(getContext());
        spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinner.setAdapter(adp);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String title = stitle.toString().trim();
                String contact = scontact.toString().trim();


                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("contact").equalTo(contact);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            bfname = snapshot.child(contact).child("firstname").getValue(String.class);
                            blname = snapshot.child(contact).child("lastname").getValue(String.class);


                        } else {

                            Toast.makeText(getContext(), "Data Does Not Exist", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



                Query filterQuery = FirebaseDatabase.getInstance().getReference("Booking").orderByChild("contact").equalTo(contact);
                filterQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        // String stitle = snapshot.child(title).getValue(String.class);

                        String myChildKey = snapshot.child("contact").getValue().toString().trim();
                        String myChildValues = snapshot.child("contact").getKey().toString().trim();
                        amountList.add(myChildValues);
                        amountAdapter.notifyDataSetChanged();
                        arrayList.add(myChildKey);
                        arrayAdapter.notifyDataSetChanged();

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                                String itemValue = (String) listView.getItemAtPosition(position);
                                String itemAmount = (String) amountView.getItemAtPosition(position);
                                String bid = itemValue;

                                ImageView qrImage = new ImageView(getContext());

                                QRGEncoder qrgEncoder = new QRGEncoder(bid, null, QRGContents.Type.TEXT, 1000);
                                // Getting QR-Code as Bitmap
                                Bitmap bitmap = qrgEncoder.getBitmap();
                                // Setting Bitmap to ImageView
                                qrImage.setImageBitmap(bitmap);

                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(getContext()).
                                                setMessage("Scan the QRCode").
                                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).
                                                setView(qrImage);
                                builder.create().show();

                            }
                        });

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        arrayAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                /*Query filterQuery = FirebaseDatabase.getInstance().getReference("Destinations").orderByChild("title").equalTo(title);
                filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getValue() != null){
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){

                                String R = "" + snapshot1.getValue();


                            }
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}