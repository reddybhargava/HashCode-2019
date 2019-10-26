package com.jnanesh.Hashcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.jnanesh.myapplication.R;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Fire_marking extends AppCompatActivity {
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_marking);
        Intent i=getIntent();
        db=FirebaseFirestore.getInstance();
        String accidentPlace=i.getStringExtra("place");
        Map<String, Object> data = new HashMap<>();
        data.put("place", accidentPlace);
        Date date= new Date();//12.974058, 77.612170
        if(accidentPlace.equals("MG ROAD"))
        {
            double lat = 12.974058f;
            double lng = 77.612170f;
            GeoPoint gp = new GeoPoint(lat,lng);
            data.put("location",gp);
        }
        else if(accidentPlace.equals("BRIGADE ROAD"))
        {
            double lat = 12.973010f;
            double lng = 77.607367f;
            GeoPoint gp = new GeoPoint(lat,lng);
            data.put("location",gp);
        }

        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        data.put("timestamp",ts);
        data.put("status","OPEN");
        String event=i.getStringExtra("DEPARTMENT");
        data.put("department",event);

        db.collection("central")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(),"Thanks for your update, we'll work on it",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
