package com.jnanesh.Hashcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jnanesh.myapplication.R;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView tt1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=FirebaseFirestore.getInstance();
        //=findViewById(R.id.textView);
        tt1=findViewById(R.id.textView);
        DocumentReference user1=db.collection("fire_department").document("test");
        user1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc=task.getResult();
                    StringBuilder fields = new StringBuilder("");
                    fields.append("Location : ").append(doc.get("location"));
                    fields.append("name : ").append(doc.get("name"));
                    tt1.setText(fields.toString());
                }
            }
        });
        db.collection("geotags").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            @SuppressWarnings("unchecked")

            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String,Map> list = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String,String> temp=new HashMap<>();
                        temp.put("name",document.get("name").toString());
                        temp.put("latitude",String.valueOf(document.getGeoPoint("location").getLatitude()));
                        temp.put("longitude",String.valueOf(document.getGeoPoint("location").getLongitude()));

                        list.put(document.getId(),temp );
                    }
//                    Iterator mapiterator=list.entrySet().iterator();
//                    while (mapiterator.hasNext()){
//                        Map.Entry mapelement=(Map.Entry)mapiterator.next();
//                        Object user=mapelement.getValue();
//                        Log.i("1",mapelement.getKey()+": "+mapelement.getValue());
//                    }
                    for(HashMap.Entry<String,Map> map: list.entrySet())
                    {
                        Log.i("2",map.getValue().get("name").toString());
                        Log.i("2",map.getValue().get("latitude").toString());

                    }


                    Log.d("!", list.toString());
                } else {
                    Log.d("1", "Error getting documents: ", task.getException());
                }
            }
        });
        DocumentReference user=db.collection("central").document("test");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    StringBuilder fields = new StringBuilder("");
                    fields.append("Name: ").append(doc.get("name"));
                    fields.append("\nName two: ").append(doc.get("name1"));
                    //tt1.setText(fields.toString());

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    public void onclick(View view)
    {
        Intent intent=new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
    public void fire(View view)
    {
        Intent intent=new Intent(this,TrashMarking.class);
        startActivity(intent);
    }
}
