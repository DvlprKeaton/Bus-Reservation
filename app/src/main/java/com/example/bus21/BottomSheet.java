package com.example.bus21;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class BottomSheet extends BottomSheetDialogFragment {


    private TextView loccName, loccAdd, dest1;
    private Button bookBTN;

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


    public BottomSheet() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        /*Fragment frag2 = new HomeFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.bottom_sheet, frag2);
        ft.commit();*/

        sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        stitle = sharedpreferences.getString(MARKERTITLE_KEY, null);
        scontact = sharedpreferences.getString(CONTACT_KEY, null);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);
        ArrayAdapter<String> amountAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, amountList);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, s);

        loccName = view.findViewById(R.id.locName);
        loccAdd = view.findViewById(R.id.loccAdd);
        bookBTN = view.findViewById(R.id.bookBTN);
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

                Query checkStations = FirebaseDatabase.getInstance().getReference("Stations").orderByChild("title").equalTo(title);
                checkStations.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            String stitle = snapshot.child(title).child("title").getValue(String.class);
                            String saddress = snapshot.child(title).child("address").getValue(String.class);

                            loccName.setText(stitle);
                            loccAdd.setText(saddress);


                        } else {

                            Toast.makeText(getContext(), "Data Does Not Exist" + title, Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

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



                Query filterQuery = FirebaseDatabase.getInstance().getReference("Destinations").child(title).orderByChild("title");
                filterQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        // String stitle = snapshot.child(title).getValue(String.class);

                        String myChildKey = snapshot.getKey().toString().trim();
                        String myChildValues = snapshot.getValue().toString().trim();
                        amountList.add(myChildValues);
                        amountAdapter.notifyDataSetChanged();
                        arrayList.add(myChildKey);
                        arrayAdapter.notifyDataSetChanged();

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                Random rand = new Random();
                                int n = rand.nextInt(999);

                                String itemValue = (String) listView.getItemAtPosition(position);
                                String itemAmount = (String) amountView.getItemAtPosition(position);
                                String bid = n+"-"+itemValue;

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Trip to " + itemValue + " will cost ₱" + itemAmount);
                                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Choose Time of Booking");
                                        builder.setView(spinner);
                                        time = spinner.getSelectedItem().toString();
                                        //builder.setMessage("Choose Time of Booking");
                                        //builder.create().show();
                                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Booking booking = new Booking(bid.toString().trim(),bfname.toString().trim(), blname.toString().trim(),scontact.toString().trim(),
                                                        itemValue.toString().trim(),time.toString().trim(),itemValue.toString().trim(),"0");
                                                dao.add(booking).addOnSuccessListener(suc -> {
                                                    Intent moveToPayment = new Intent(getContext(), KotlinQuickStartActivity.class);
                                                    moveToPayment.putExtra("itemValue",itemValue);
                                                    moveToPayment.putExtra("itemAmount",itemAmount);
                                                    moveToPayment.putExtra("bid",bid);
                                                    moveToPayment.putExtra("time",time);
                                                    startActivity(moveToPayment);
                                                    Toast.makeText(getContext(), bfname+"\n"+blname+"\n"+scontact+"\n"+itemValue+"\n"+time+"\n"+itemValue, Toast.LENGTH_SHORT).show();
                                                }).addOnFailureListener(er ->
                                                {
                                                    Toast.makeText(getContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                                                });


                                            }
                                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getActivity(), "Choose another reservation", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        builder.show();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getActivity(), "Choose another reservation", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.show();

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


        /*//Toast.makeText(getContext(), ""+title, Toast.LENGTH_SHORT).show();
        bookBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("User").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String title = stitle.toString().trim();

                        Query checkUser = FirebaseDatabase.getInstance().getReference("Destinations").orderByChild("title").equalTo(title);
                        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists()) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Trip to " + title + " name will cost ₱00.00");
                                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(getActivity(), "Confirmed", Toast.LENGTH_SHORT).show();
                                            AlertDialog builder = new AlertDialog.Builder(getContext())
                                                    .setTitle("Select Time")
                                                    .setMessage("")
                                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .create();
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getActivity(), "Choose another reservation", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.show();


                                } else {

                                    Toast.makeText(getContext(), "Data Does Not Exist" + title, Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });*/

        return view;
    }

    public void readFileSheet() {

        try {
            FileInputStream fileInputStream = getActivity().openFileInput("sheet.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }
            //loccName.setText(""+stringBuffer.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
