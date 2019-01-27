package kucc.org.ku_map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import kucc.org.ku_map.dijkstra.Dijkstra;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    //LOG TAG
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkPermission();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startLocationService();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //set zoom controller
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //set compass functionality
        mMap.getUiSettings().setCompassEnabled(true);
        //set minimum zoom 15 (동-scale)
        mMap.setMinZoomPreference(15);
        //current location button relocation
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.setMargins(0,0,0,350);

        //permission check & enable my location
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in KoreaUniversity and move the camera
        String[] arr_locaton = getResources().getStringArray(R.array.locations);

        LatLng l0 = new LatLng(Double.valueOf(arr_locaton[0]), Double.valueOf(arr_locaton[1]));
        LatLng l1 = new LatLng(Double.valueOf(arr_locaton[2]), Double.valueOf(arr_locaton[3]));
        LatLng l2 = new LatLng(Double.valueOf(arr_locaton[4]), Double.valueOf(arr_locaton[5]));
        LatLng l3 = new LatLng(Double.valueOf(arr_locaton[6]), Double.valueOf(arr_locaton[7]));
        LatLng l4 = new LatLng(Double.valueOf(arr_locaton[8]), Double.valueOf(arr_locaton[9]));

        mMap.addMarker(new MarkerOptions().position(l0).title("본관").snippet("본관 설명"));
        mMap.addMarker(new MarkerOptions().position(l1).title("문과대학").snippet("문과대학 설명"));
        mMap.addMarker(new MarkerOptions().position(l2).title("인촌기념관").snippet("인촌기념관 설명"));
        mMap.addMarker(new MarkerOptions().position(l3).title("백주년기념관").snippet("백주념기념관 설명"));
        mMap.addMarker(new MarkerOptions().position(l4).title("418기념관").snippet("418기념관 설명"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l0,17));

        //draw path from source to destination using dijkstra algorithm
        Dijkstra dijkstra = new Dijkstra();
        ArrayList<Integer> paths = dijkstra.DA(4,2);
        for(int i = 0; i < paths.size()-1; i++){
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(Double.valueOf(arr_locaton[paths.get(i)*2]),Double.valueOf(arr_locaton[paths.get(i)*2+1]))
                        ,new LatLng(Double.valueOf(arr_locaton[paths.get(i+1)*2]),Double.valueOf(arr_locaton[paths.get(i+1)*2+1])))
                    .width(20)
                    .color(0xFF368AFF));
        }

    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "permission checked : granted.");
        }else{
            Log.i(TAG, "permission checked : not granted. requesting users location permission...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
    }

    private void startLocationService(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        GPSListener gpsListener = new GPSListener();
        long minTime = 5000;
        float minDistance = 0;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
    }

    private class GPSListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            //custom code on location changed : nothing for now
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
