package com.example.bus21.ui.gallery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bus21.DAOPassenger;
import com.example.bus21.Dashboard;
import com.example.bus21.DatabaseHelper;
import com.example.bus21.MainActivity;
import com.example.bus21.Passenger;
import com.example.bus21.R;
import com.example.bus21.databinding.FragmentGalleryBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;

    // creating constant keys for shared preferences.
    public static final String SHARED_PREFS = "shared_prefs";

    // key for storing email.
    public static final String CONTACT_KEY = "contact_key";

    // key for storing password.
    public static final String PASSWORD_KEY = "password_key";

    // variable for shared preferences.
    SharedPreferences sharedpreferences;
    String scontact;

    String firstname = "",
            lastname = "",
            contact = "",
            password = "",
            conpassword = "",
            usertype = "";

    TextView pfirstname, plastname, pcontact, pusertype;
    EditText ed_fname, ed_lname, ed_contact, ed_pass, ed_conpass;

    DatabaseReference reference;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pfirstname = root.findViewById(R.id.prof_firstname);
        plastname = root.findViewById(R.id.prof_lastname);
        pcontact = root.findViewById(R.id.prof_contact);
        pusertype = root.findViewById(R.id.prof_usertype);
        ed_fname = root.findViewById(R.id.ed_fname);
        ed_lname = root.findViewById(R.id.ed_lastname);
        ed_contact = root.findViewById(R.id.ed_contact);
        ed_pass = root.findViewById(R.id.ed_password);
        ed_conpass = root.findViewById(R.id.ed_conpassword);
        final Button editinfo = root.findViewById(R.id.edit_info);
        final Button cancel = root.findViewById(R.id.cancel);
        final Button submit = root.findViewById(R.id.submit);
        final AlertDialog builder = new AlertDialog.Builder(getContext()).create();
        final DatabaseHelper db = new DatabaseHelper(getContext());

        reference = FirebaseDatabase.getInstance().getReference("Users");

        sharedpreferences = getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        scontact = sharedpreferences.getString(CONTACT_KEY, null);

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

                            pfirstname.setText("Firstname: "+fname);
                            plastname.setText("Lastname: "+lname);
                            pcontact.setText("Contact: "+ contact);
                            pusertype.setText("User Type: "+usertype);




                        } else {

                            Toast.makeText(getContext(), "Data Does Not Exist", Toast.LENGTH_SHORT).show();

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

        editinfo.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View v) {
                visible = !visible;

                pfirstname.setVisibility(View.GONE);
                plastname.setVisibility(View.GONE);
                pcontact.setVisibility(View.GONE);
                pusertype.setVisibility(View.GONE);
                editinfo.setVisibility(View.GONE);

                ed_fname.setVisibility(View.VISIBLE);
                ed_lname.setVisibility(View.VISIBLE);
                ed_pass.setVisibility(View.VISIBLE);
                ed_conpass.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View v) {
                visible = !visible;
                pfirstname.setVisibility(View.VISIBLE);
                plastname.setVisibility(View.VISIBLE);
                pcontact.setVisibility(View.VISIBLE);
                pusertype.setVisibility(View.VISIBLE);
                editinfo.setVisibility(View.VISIBLE);

                ed_fname.setVisibility(View.GONE);
                ed_lname.setVisibility(View.GONE);
                ed_pass.setVisibility(View.GONE);
                ed_conpass.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                submit.setVisibility(View.GONE);

                ed_contact.setText("");
                ed_pass.setText("");
                ed_conpass.setText("");


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stred_fname = ed_fname.getText().toString().trim();
                String stred_lanme = ed_lname.getText().toString().trim();
                String stred_contact = ed_contact.getText().toString().trim();
                String stred_password = ed_pass.getText().toString().trim();
                String stred_conpassword = ed_conpass.getText().toString().trim();

                if(stred_password.equals(stred_conpassword)){


                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("To continue you need to login your updated account");
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (isInformationChange()){
                                Toast.makeText(getContext(), "Data has been updated", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(getContext(), "Data is same and can not be updated", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "Updating Cancelled!",Toast.LENGTH_SHORT).show();
                            Intent moveTologin = new Intent(getActivity(), Dashboard.class);
                            startActivity(moveTologin);
                        }
                    });
                    builder.show();


                }  else {

                    Toast.makeText(getActivity(), "Password is not match",Toast.LENGTH_SHORT).show();

                }
            }
        });

        return root;
    }

    public void update(View view){
        if (isInformationChange() || isPasswordChanged()){
            Toast.makeText(getContext(), "Data has been updated", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(getContext(), "Data is same and can not be updated", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPasswordChanged() {

        if (!password.equals(ed_pass.getEditableText().toString())){

            reference.child(scontact).child("password").setValue(ed_pass.getEditableText().toString());
            return true;

        }else {
            return false;
        }

    }

    private boolean isInformationChange() {

        if (!firstname.equals(ed_fname.getEditableText().toString()) || !lastname.equals(ed_lname.getEditableText().toString()) || !password.equals(ed_pass.getEditableText().toString()) ){

            reference.child(scontact).child("firstname").setValue(ed_fname.getEditableText().toString());
            reference.child(scontact).child("lastname").setValue(ed_lname.getEditableText().toString());
            reference.child(scontact).child("password").setValue(ed_pass.getEditableText().toString());
            return true;

        }else {
            return false;
        }

    }
/*
    public void prepareUpdate() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(emailAddress, passwordOG);
            firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (!password.equals(passwordOG)) {
                        firebaseUser.updatePassword(password).addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                updateProfile();
                            } else {
                                Snackbar snackbar = Snackbar.make(requireView(), "Cannot update account", Snackbar.LENGTH_LONG);
                                snackbar.setAction("DISMISS", view -> snackbar.dismiss());
                                snackbar.show();
                            }
                        });
                    } else {
                        updateProfile();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(requireView(), "Cannot update account", Snackbar.LENGTH_LONG);
                    snackbar.setAction("DISMISS", view -> snackbar.dismiss());
                    snackbar.show();
                }
            });
        } else {
            new LoginDataSource().signInWithEmailAndPassword(emailAddress, passwordOG).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(emailAddress, passwordOG);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!password.equals(passwordOG)) {
                                firebaseUser.updatePassword(password).addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        updateProfile();
                                    } else {
                                        Snackbar snackbar = Snackbar.make(requireView(), "Cannot update account", Snackbar.LENGTH_LONG);
                                        snackbar.setAction("DISMISS", view -> snackbar.dismiss());
                                        snackbar.show();
                                    }
                                });
                            } else {
                                updateProfile();
                            }
                        } else {
                            Snackbar snackbar = Snackbar.make(requireView(), "Cannot update account", Snackbar.LENGTH_LONG);
                            snackbar.setAction("DISMISS", view -> snackbar.dismiss());
                            snackbar.show();
                        }
                    });
                } else {
                    Snackbar snackbar = Snackbar.make(requireView(), "Cannot update account", Snackbar.LENGTH_LONG);
                    snackbar.setAction("DISMISS", view -> snackbar.dismiss());
                    snackbar.show();
                }
            });
        }
    }

    private void updateProfile() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Passenger passenger = new Passenger(firstname, lastname, contact,password,usertype);
        conpassword = password;
        editor.putString("password", password);
        editor.apply();
        new DAOPassenger().update(passenger).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                editor.putString("Form complete", "Already complete");
                editor.putString("firstname", firstname);
                editor.putString("lastlame", lastname);
                editor.putString("password", password);
                editor.apply();

                Snackbar snackbar = Snackbar.make(requireView(), "Account updated", Snackbar.LENGTH_LONG);
                snackbar.setAction("DISMISS", view -> snackbar.dismiss());
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(requireView(), "Cannot resolve to server", Snackbar.LENGTH_LONG);
                snackbar.setAction("DISMISS", view -> snackbar.dismiss());
                snackbar.show();
            }
        });
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}