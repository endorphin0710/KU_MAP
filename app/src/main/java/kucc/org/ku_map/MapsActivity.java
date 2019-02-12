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
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener{

    /** LOG TAG **/
    private final String TAG = "MapsActivity";

    /** DB **/
    private SQLiteDatabase db;

    private GoogleMap mMap;
    private View mapView;
    private A_STAR astar;

    private ImageButton btn_pathfind;
    private Button btn_set_source;
    private Button btn_set_dest;
    private TextView tv_title;
    private AutoCompleteTextView tv_source;
    private AutoCompleteTextView tv_dest;
    private ConstraintLayout markerWindow;

    private String[] arr_latlng;
    private ArrayList<Node> nodes;
    private ArrayList<Integer> paths;
    private long backbtn_pressed_time;

    private LatLng l0,l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11;
    private LatLng[] latlngs = {
            l0,l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /** database **/
        db = openOrCreateDatabase("kumap", MODE_PRIVATE, null);

        /** instance initialization **/
        tv_title = findViewById(R.id.tv_title);
        tv_source = findViewById(R.id.actv_source);
        tv_dest = findViewById(R.id.actv_dest);
        btn_pathfind = findViewById(R.id.btn_pathfind);
        btn_set_source = findViewById(R.id.setSource);
        btn_set_dest = findViewById(R.id.setDest);
        markerWindow = findViewById(R.id.markerWindow);
        astar = new A_STAR();
        nodes = new ArrayList<>();
        Random r = new Random();

        /** Instantiate markers **/
        arr_latlng = getResources().getStringArray(R.array.latlng);
        for(int i = 0; i < latlngs.length; i++){
            latlngs[i] = new LatLng(Double.valueOf(arr_latlng[3*i]),Double.valueOf(arr_latlng[3*i+1]));
        }

        /** Nodes initialization **/
        for(int i = 0; i < 12; i++){
            nodes.add(new Node(i, arr_latlng[3*i+2], 0));
            nodes.get(i).latitude = Double.valueOf(arr_latlng[3*i]);
            nodes.get(i).longitude = Double.valueOf(arr_latlng[3*i+1]);
        }

        /** Edges initialization **/
        nodes.get(0).adjacencies = new Edge[]{new Edge(nodes.get(11),3)};
        nodes.get(1).adjacencies = new Edge[]{new Edge(nodes.get(9),2)};
        nodes.get(2).adjacencies = new Edge[]{new Edge(nodes.get(3),1),new Edge(nodes.get(10),5)};
        nodes.get(3).adjacencies = new Edge[]{new Edge(nodes.get(2), 1)};
        nodes.get(4).adjacencies = new Edge[]{new Edge(nodes.get(6),4)};
        nodes.get(5).adjacencies = new Edge[]{new Edge(nodes.get(6),2),new Edge(nodes.get(7),1)};
        nodes.get(6).adjacencies = new Edge[]{new Edge(nodes.get(4),4),new Edge(nodes.get(5),2),new Edge(nodes.get(8),3)};
        nodes.get(7).adjacencies = new Edge[]{new Edge(nodes.get(5),1),new Edge(nodes.get(10),4)};
        nodes.get(8).adjacencies = new Edge[]{new Edge(nodes.get(6),3)};
        nodes.get(9).adjacencies = new Edge[]{new Edge(nodes.get(1),2),new Edge(nodes.get(10),3),new Edge(nodes.get(11),1)};
        nodes.get(10).adjacencies = new Edge[]{new Edge(nodes.get(2),5),new Edge(nodes.get(7),4),new Edge(nodes.get(9),3)};
        nodes.get(11).adjacencies = new Edge[]{new Edge(nodes.get(0),3),new Edge(nodes.get(9),1)};

        /** Check location permission **/
        checkPermission();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startLocationService();
        }

        /** markerWindow set INVISIBLE **/
        markerWindow.setVisibility(View.INVISIBLE);

        /** Get suggestion array from resource **/
        String[] suggestions = getResources().getStringArray(R.array.suggestion);

        /** Instantiate ArrayAdapter object with suggestion array **/
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.custom_select_dialog_item, suggestions);

        /** Set adapter & threshold of auto-complete text view **/
        tv_source.setThreshold(1);
        tv_source.setAdapter(arrayAdapter);
        tv_dest.setThreshold(1);
        tv_dest.setAdapter(arrayAdapter);

        /** Add TextChangedListener and set clear drawable on the right side of text view if text length > 0 **/
        tv_source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0){
                    tv_source.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.clear), null);
                }else{
                    tv_source.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        tv_dest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0){
                    tv_dest.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.clear), null);
                }else{
                    tv_dest.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /** Add OnTouchListener on drawable in the text view **/
        tv_source.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(tv_source.getText().length() > 0 && event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (tv_source.getRight() - tv_source.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        tv_source.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        tv_dest.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(tv_dest.getText().length() > 0 && event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (tv_dest.getRight() - tv_dest.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        tv_dest.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        /** Obtain the SupportMapFragment and get notified when the map is ready to be used. **/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        /** Reference to a google map object and animate camera to main building of KU **/
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.589503, 127.032323),17));

        /** set OnMarkerClickListener & OnMapClickListener **/
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        /** Set zoom controller **/
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /** Set compass functionality **/
        mMap.getUiSettings().setCompassEnabled(true);

        /** Set minimum zoom to 15 (동-scale) **/
        mMap.setMinZoomPreference(15);

        /** Add markers **/
        init_markers();

        /** Current location button relocation **/
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.setMargins(0,0,50,300);

        /** Permission check & enable my location **/
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
        /** Add path-find button and onClickListener **/
        btn_pathfind.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                findPath();
            }
        });

        btn_set_source.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_source.setText(tv_title.getText());
            }
        });
        btn_set_dest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_dest.setText(tv_title.getText());
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
        for(int i = 0; i < latlngs.length; i++){
            mMap.addMarker(new MarkerOptions().position(latlngs[i]).icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.tiger)).title(nodes.get(i).value));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        markerWindow.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,0,markerWindow.getHeight(),0
        );
        animate.setDuration(500);
        markerWindow.startAnimation(animate);
        tv_title.setText(marker.getTitle());
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        markerWindowSlideDown(null);
    }

    public void findPath(){

        if(!isEmpty(tv_source) && !isEmpty(tv_dest )){
            /** retrieve source and destination marker indeces from database**/
            int source = retrieve_index(tv_source.getText().toString());
            int dest = retrieve_index(tv_dest.getText().toString());
            Log.i(TAG, "source : " + tv_source.getText().toString() + " index = " + source);
            Log.i(TAG, "destination : " + tv_dest.getText().toString() + " index = " + dest);
            if(source == -1 || dest == -1) return;

            /** Clear google map & add markers **/
            mMap.clear();
            init_markers();

            /** Get marker indeces of markers on the path **/
            for(Node n : nodes){
                n.parent = null;
                n.h_scores = cal_distance(nodes.get(source).latitude, nodes.get(source).longitude, n.latitude, n.longitude);
                Log.i(TAG, "distance : " + n.h_scores);
            }
            astar.search(nodes.get(source), nodes.get(dest));
            paths = (ArrayList)astar.printPath(nodes.get(dest));

            /** Draw path from source to destination using dijkstra algorithm **/
            PolylineOptions polyLine = new PolylineOptions().width(20).color(0xFF368AFF);
            for(int i = 0; i < paths.size()-1; i++){
                polyLine.add(latlngs[paths.get(i)], latlngs[paths.get(i+1)]);
            }
            mMap.addPolyline(polyLine);
        }
    }

    /** Slide-down marker window **/
    public void markerWindowSlideDown(View v){
        if(markerWindow.getVisibility() == View.VISIBLE) {
            markerWindow.setVisibility(View.INVISIBLE);
            TranslateAnimation animate = new TranslateAnimation(
                    0, 0, 0, markerWindow.getHeight()
            );
            animate.setDuration(500);
            markerWindow.startAnimation(animate);
        }
    }

    /** switch source and destination **/
    public void source_dest_switch(View v){
        Editable source = tv_source.getText();
        Editable dest = tv_dest.getText();
        tv_source.setText(dest);
        tv_dest.setText(source);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        /** Terminate when interval between first and second click of back button is less than2000 milliseconds **/
        if(backbtn_pressed_time + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }else{
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            backbtn_pressed_time = System.currentTimeMillis();
        }

    }

    /** calculate distacne between two points **/
    public double cal_distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;

            /** meter = mile * 1609.34 **/
            return (dist*1609.34);
        }
    }
}
