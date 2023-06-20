package com.example.bus21;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DOABooking {
    private DatabaseReference databaseReference;
    public DOABooking(){
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://busbooking-a5fdd-default-rtdb.firebaseio.com/");
        databaseReference = db.getReference().child("Booking");
    }
    public Task<Void> add(Booking booking){

        return databaseReference.child(booking.getContact()).setValue(booking);
    }

    public Task<Void> update(Passenger passenger){
        HashMap<String, Object> data = new HashMap<>();
        data.put("contact", passenger.getContact());
        data.put("firstname", passenger.getFirstname());
        data.put("lastname", passenger.getLastname());
        data.put("password", passenger.getPassword());

        return databaseReference.child(passenger.getContact()).updateChildren(data);
    }

    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }
}
