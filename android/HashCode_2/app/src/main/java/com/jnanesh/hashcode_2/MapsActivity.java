package com.jnanesh.hashcode_2;

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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    int i=0;
    private GoogleMap mMap;
    private LatLng LTEMP;
    private static final LatLng BANSHANKARI = new LatLng(12.917019, 77.573141);
    public  Marker mfinalmarker;
    public Marker mTemp;
    public double minimumResult=0;
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
    HashMap<String,String> clk=new HashMap<>();
    @Override

    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this,"Maps ready",Toast.LENGTH_SHORT).show();
        // Ad   d a marker in Sydney, Australia,
        mMap = googleMap;
        db.collection("hospital_department").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            @SuppressWarnings("unchecked")
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Map> list = new HashMap<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String,String> temp=new HashMap<>();
                        temp.put("latitude",String.valueOf(document.getGeoPoint("location").getLatitude()));
                        temp.put("longitude",String.valueOf(document.getGeoPoint("location").getLongitude()));
                        temp.put("status",document.get("status").toString());

                        list.put(document.getId(),temp );
                    }
                    for(HashMap.Entry<String,Map> map: list.entrySet())
                    {
                        double lat,lng;
                        String id=map.getKey().toString();
                        lat=Double.parseDouble(map.getValue().get("latitude").toString());
                        lng=Double.parseDouble(map.getValue().get("longitude").toString());
                        LatLng LTEMP = new LatLng(lat, lng);

                        String stat=map.getValue().get("status").toString();

                        mTemp=mMap.addMarker(new MarkerOptions().position(LTEMP).title("ACCIDENT").snippet(id));
                        if(stat.equals("OPEN"))
                        {
                            double res=CalculationByDistance(BANSHANKARI,LTEMP);
                            if()
                            clk.put(id,"OPEN");
                            mTemp.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                        }
                        else{
                            clk.put(id,"CLOSED");
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

                if("OPEN".equals(clk.get(mfinalmarker.getSnippet())))
                {
                    dialog();
                }
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
                .setNegativeButton("CLOSED :)", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mfinalmarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        String status="status";
                        db.collection("hospital_department").document(mfinalmarker.getSnippet()).update(status,"CLOSED");
                        clk.put(mfinalmarker.getSnippet(),"CLOSED");
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
}

