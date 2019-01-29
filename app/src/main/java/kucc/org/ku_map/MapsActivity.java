package kucc.org.ku_map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
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

    /** LOG TAG **/
    private static final String TAG = "MapsActivity";

    /** DB **/
    private SQLiteDatabase db;

    private GoogleMap mMap;
    private View mapView;

    private ImageButton btn_pathfind;
    private AutoCompleteTextView tv_source;
    private AutoCompleteTextView tv_dest;

    private String[] arr_latlng;
    private Dijkstra dijkstra;

    private LatLng l0,l1,l2,l3,l4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /** database **/
        db = openOrCreateDatabase("kumap", MODE_PRIVATE, null);
        init_tables();
        save_rows();

        /** Check location permission **/
        checkPermission();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startLocationService();
        }

        /** Get suggestion array from res **/
        String[] suggestions = getResources().getStringArray(R.array.suggestion);

        /** Instantiate ArrayAdapter object with suggestion array **/
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_select_dialog_item, suggestions);

        /** Set adapter & threshold of auto-complete text view eac **/
        tv_source = (AutoCompleteTextView)findViewById(R.id.actv_source);
        tv_source.setThreshold(1);
        tv_source.setAdapter(arrayAdapter);
        tv_dest = (AutoCompleteTextView)findViewById(R.id.actv_dest);
        tv_dest.setThreshold(1);
        tv_dest.setAdapter(arrayAdapter);

        /** Obtain the SupportMapFragment and get notified when the map is ready to be used. **/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        /** Reference to a google map object and animate camera to main building of KU**/
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.589503, 127.032323),17));

        /** Set zoom controller **/
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /** Set compass functionality **/
        mMap.getUiSettings().setCompassEnabled(true);

        /** Set minimum zoom to 15 (동-scale) **/
        mMap.setMinZoomPreference(15);

        /** Instantiate markers **/
        arr_latlng = getResources().getStringArray(R.array.latlng);
        l0 = new LatLng(Double.valueOf(arr_latlng[0]), Double.valueOf(arr_latlng[1]));
        l1 = new LatLng(Double.valueOf(arr_latlng[2]), Double.valueOf(arr_latlng[3]));
        l2 = new LatLng(Double.valueOf(arr_latlng[4]), Double.valueOf(arr_latlng[5]));
        l3 = new LatLng(Double.valueOf(arr_latlng[6]), Double.valueOf(arr_latlng[7]));
        l4 = new LatLng(Double.valueOf(arr_latlng[8]), Double.valueOf(arr_latlng[9]));

        /** Add markers **/
        init_markers();

        /** Current location button relocation **/
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.setMargins(0,0,0,300);

        /** Permission check & enable my location **/
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }

        /** Add path-find button and onClickListener **/
        dijkstra = new Dijkstra();
        btn_pathfind = findViewById(R.id.btn_pathfind);
        btn_pathfind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!isEmpty(tv_source) && !isEmpty(tv_dest )){

                    /** retrieve source and destination marker indeces from database**/
                    int source = retrieve_index(tv_source.getText().toString());
                    int dest = retrieve_index(tv_dest.getText().toString());
                    if(source == -1 || dest == -1) return;

                    /** Clear google map & add markers **/
                    mMap.clear();
                    init_markers();

                    /** Get marker indeces of markers on the path **/
                    ArrayList<Integer> paths = dijkstra.DA(source,dest);

                    /** Draw path from source to destination using dijkstra algorithm **/
                    for(int i = 0; i < paths.size()-1; i++){
                        mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(Double.valueOf(arr_latlng[paths.get(i)*2]),Double.valueOf(arr_latlng[paths.get(i)*2+1]))
                                        ,new LatLng(Double.valueOf(arr_latlng[paths.get(i+1)*2]),Double.valueOf(arr_latlng[paths.get(i+1)*2+1])))
                                .width(20)
                                .color(0xFF368AFF));
                    }
                }
            }
        });

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
            /** Custom code on location changed : nothing for now **/
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

    /** Convert vector image into bitmap format **/
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void init_tables(){
        if(db != null){
            String createTable = "CREATE TABLE IF NOT EXISTS LOCATION_INFO (" +
                    "NAME " + "TEXT," +
                    "LOCATION_INDEX " + "INTEGER" +")";
            db.execSQL(createTable);
        }
    }

    private void save_rows(){
        if(db != null){
            db.execSQL("DELETE FROM LOCATION_INFO");

            String insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "본관" + "'" + "," + 0 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "문과대학" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "문대" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "서관" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "인촌기념관" + "'" + "," + 2 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "인촌" + "'" + "," + 2 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "백주년기념관" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "100주년기념관" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "백기" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "418기념관" + "'" + "," + 4 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "418" + "'" + "," + 4 + ")";
            db.execSQL(insertRow);

        }
    }

    private int retrieve_index(String location){

        int i = -1;
        if(db != null){
            String retrieve_index = "SELECT LOCATION_INDEX FROM LOCATION_INFO WHERE NAME = " + "'" + location + "'";
            Cursor c = db.rawQuery(retrieve_index,null);
            if(c.moveToNext()){
                i = c.getInt(0);
            }
        }
        return i;
    }

    /** Check if EditText is empty or not **/
    private boolean isEmpty(EditText et){
       if(et.getText().toString().trim().length() > 0){
           return false;
       }else{
           return true;
       }
    }

    /** Initiate Markers **/
    private void init_markers(){
        mMap.addMarker(new MarkerOptions().position(l0).title("본관").snippet("본관 설명"));
        mMap.addMarker(new MarkerOptions().position(l1).title("문과대학").snippet("문과대학 설명"));
        mMap.addMarker(new MarkerOptions().position(l2).title("인촌기념관").snippet("인촌기념관 설명"));
        mMap.addMarker(new MarkerOptions().position(l3).title("백주년기념관").snippet("백주념기념관 설명"));
        mMap.addMarker(new MarkerOptions().position(l4).title("418기념관").snippet("418기념관 설명"));
    }

}
