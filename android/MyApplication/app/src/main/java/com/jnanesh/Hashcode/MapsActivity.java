package com.jnanesh.Hashcode;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jnanesh.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.icu.text.Normalizer.YES;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng LTEMP;
    private static final LatLng BANSHANKARI = new LatLng(12.917019, 77.573141);
    public  Marker mfinalmarker;
    public Marker mTemp;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        db=FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override

    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this,"Maps ready",Toast.LENGTH_SHORT).show();
        // Ad   d a marker in Sydney, Australia,
        mMap = googleMap;
        db.collection("geotags").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            @SuppressWarnings("unchecked")
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Map> list = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String,String> temp=new HashMap<>();
                        temp.put("name",document.get("name").toString());
                        temp.put("latitude",String.valueOf(document.getGeoPoint("location").getLatitude()));
                        temp.put("longitude",String.valueOf(document.getGeoPoint("location").getLongitude()));
                        temp.put("status",document.get("status").toString());
                        list.put(document.getId(),temp );
                    }
                    for(HashMap.Entry<String,Map> map: list.entrySet())
                    {
                        double lat,lng;
                        String name=map.getValue().get("name").toString();
                        String id=map.getKey().toString();
                        lat=Double.parseDouble(map.getValue().get("latitude").toString());
                        lng=Double.parseDouble(map.getValue().get("longitude").toString());
                        LatLng LTEMP = new LatLng(lat, lng);

                        String stat=map.getValue().get("status").toString();

                        mTemp=mMap.addMarker(new MarkerOptions().position(LTEMP).title(name).snippet(id));
                        if(stat.equals("FULL"))
                        {
                            mTemp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        }
                        else{
                            mTemp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                        mTemp.setTag(0);
                    }


                    Log.d("!", list.toString());
                } else {
                    Log.d("1", "Error getting documents: ", task.getException());
                }
            }
        });
        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BANSHANKARI, zoomLevel));
        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mfinalmarker=marker;

                dialog();
               return false;
            }

        });
    }
    public void dialog()
    {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("Trash Status")
                .setMessage("Set the trash status of the location")
                .setCancelable(false)
                .setPositiveButton("FULL!!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mfinalmarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        Toast.makeText(getApplicationContext(),mfinalmarker.getSnippet(),Toast.LENGTH_SHORT).show();
                        String status="status";
                        db.collection("geotags").document(mfinalmarker.getSnippet()).update(status,"FULL");
                    }
                })
                .setNegativeButton("Empty :)", new DialogInterface.OnClickListener()
                {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        mfinalmarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        String status="status";
                        db.collection("geotags").document(mfinalmarker.getSnippet()).update(status,"EMPTY");
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

