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
    private Button btn_hide_marker;
    private TextView tv_title;
    private AutoCompleteTextView tv_source;
    private AutoCompleteTextView tv_dest;
    private ConstraintLayout markerWindow;

    private String[] arr_latlng;
    private LatLng[] latlngs;
    private ArrayList<Node> nodes;
    private ArrayList<Integer> paths;

    private long backbtn_pressed_time;
    private int source;
    private int dest;
    private int marker_hidden = 1;

    private boolean pathfind_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /** Database **/
        db = openOrCreateDatabase("kumap", MODE_PRIVATE, null);

        /** instance initialization **/
        tv_title = findViewById(R.id.tv_title);
        tv_source = findViewById(R.id.actv_source);
        tv_dest = findViewById(R.id.actv_dest);
        btn_pathfind = findViewById(R.id.btn_pathfind);
        btn_set_source = findViewById(R.id.setSource);
        btn_set_dest = findViewById(R.id.setDest);
        btn_hide_marker = findViewById(R.id.btn_hide_marker);
        markerWindow = findViewById(R.id.markerWindow);
        astar = new A_STAR();
        nodes = new ArrayList<>();
        paths = new ArrayList<>();

        /** Instantiate markers **/
        arr_latlng = getResources().getStringArray(R.array.latlng);
        latlngs = new LatLng[195];
        for(int i = 0; i < latlngs.length; i++){
            latlngs[i] = new LatLng(Double.valueOf(arr_latlng[3*i]),Double.valueOf(arr_latlng[3*i+1]));
        }

        /** Nodes initialization **/
        for(int i = 0; i < latlngs.length; i++){
            nodes.add(new Node(i, arr_latlng[3*i+2]));
            nodes.get(i).latitude = Double.valueOf(arr_latlng[3*i]);
            nodes.get(i).longitude = Double.valueOf(arr_latlng[3*i+1]);
        }

        /** Edges initialization **/
        set_cost();

        /** Check location permission **/
        checkPermission();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startLocationService();
        }

        /** MarkerWindow & time_tv set INVISIBLE **/
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

        /** Add TextChangedListener and set ic_clear drawable on the right side of text view if text length > 0 **/
        tv_source.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0){
                    tv_source.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_clear), null);
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
                    tv_dest.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_clear), null);
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

        /** Set OnMarkerClickListener & OnMapClickListener **/
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        /** Set zoom controller **/
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /** Set myLocation button **/
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        /** Set compass functionality **/
        mMap.getUiSettings().setCompassEnabled(true);

        /** Set minimum zoom to 15 (동-scale) **/
        mMap.setMinZoomPreference(15);

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

        /** Set source and destination onClickListener in Marker Window **/
        btn_set_source.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_source.setText(tv_title.getText());
                markerWindowSlideDown(null);
            }
        });
        btn_set_dest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_dest.setText(tv_title.getText());
                markerWindowSlideDown(null);
            }
        });

        /** Marker hide button click listener **/
        btn_hide_marker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(marker_hidden == 1){
                    btn_hide_marker.setText("건물 숨기기");
                    marker_hidden = 0;
                    if(pathfind_start){
                        mMap.clear();
                        init_markers(source, dest);
                        drawPaths(paths);
                    }else{
                        init_markers();
                    }
                }else{
                    btn_hide_marker.setText("건물 나타내기");
                    marker_hidden = 1;
                    mMap.clear();
                    if(pathfind_start){
                        init_markers(source, dest);
                        drawPaths(paths);
                    }
                }
            }
        });

    }

    /** Location Permission Check **/
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

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "위치 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
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

    /** Retrieve index of location from database **/
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
            if(!nodes.get(i).value.equals("waypoint")) {
                mMap.addMarker(new MarkerOptions().position(latlngs[i]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_tiger)).title(nodes.get(i).value));
            }else{
                mMap.addMarker(new MarkerOptions().position(latlngs[i]).title(nodes.get(i).value).visible(false));
            }
        }
}
    /** Initiate Markers with source and destination **/
    private void init_markers(int s, int d){
        for(int i = 0; i < latlngs.length; i++){
            if(!nodes.get(i).value.equals("waypoint")) {
                if(i == s)
                    mMap.addMarker(new MarkerOptions().position(latlngs[i]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_source_flag)).title(nodes.get(i).value));
                else if(i == d)
                    mMap.addMarker(new MarkerOptions().position(latlngs[i]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_destination_flag)).title(nodes.get(i).value));
                else{
                    if(marker_hidden == 0){
                        mMap.addMarker(new MarkerOptions().position(latlngs[i]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_tiger)).title(nodes.get(i).value));
                    }
                }
            }else{
                mMap.addMarker(new MarkerOptions().position(latlngs[i]).title(nodes.get(i).value).visible(false));
            }
        }
    }

    /** On marker clicked **/
    @Override
    public boolean onMarkerClick(Marker marker) {

        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

        markerWindow.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,0,markerWindow.getHeight(),0
        );
        animate.setDuration(300);
        markerWindow.startAnimation(animate);
        tv_title.setText(marker.getTitle());
        return true;
    }

    /** On Googlemap clicked **/
    @Override
    public void onMapClick(LatLng latLng) {
        markerWindowSlideDown(null);
    }

    /** Path find **/
    private void findPath(){
        pathfind_start = true;
        if(!isEmpty(tv_source) && !isEmpty(tv_dest )){
            /** Retrieve source and destination marker indeces from database**/
            source = retrieve_index(tv_source.getText().toString());
            dest = retrieve_index(tv_dest.getText().toString());

            Log.i(TAG, "source : " + tv_source.getText().toString() + " index = " + source);
            Log.i(TAG, "destination : " + tv_dest.getText().toString() + " index = " + dest);
            if(source == -1 || dest == -1) return;
            if(source == dest) return;

            /** Clear google map & add markers **/
            mMap.clear();
            if(marker_hidden == 0) {
                init_markers(source, dest);
            }else{
                mMap.addMarker(new MarkerOptions().position(latlngs[source]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_source_flag)).title(nodes.get(source).value));
                mMap.addMarker(new MarkerOptions().position(latlngs[dest]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_destination_flag)).title(nodes.get(dest).value));
            }

            /** Clear parent information & add heuristic values(distacne from source in meters) **/
            for(Node n : nodes){
                n.parent = null;
                n.h_scores = cal_distance(nodes.get(dest), n);
            }
            astar.search(nodes.get(source), nodes.get(dest));
            paths = (ArrayList)astar.printPath(nodes.get(dest));
            Log.i(TAG, "paths : " + paths);

            double distance_total = 0;
            /** Draw path from source to destination using A* algorithm **/
            PolylineOptions polyLine = new PolylineOptions().width(15).color(0xFF368AFF);
            for(int i = 0; i < paths.size()-1; i++){
                polyLine.add(latlngs[paths.get(i)], latlngs[paths.get(i+1)]);
                distance_total += cal_distance(nodes.get(paths.get(i)), nodes.get(paths.get(i+1)));
            }
            mMap.addPolyline(polyLine);
            LatLng source_latlng = new LatLng(latlngs[paths.get(0)].latitude, latlngs[paths.get(0)].longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_latlng, 17));
            Toast.makeText(this, "예상 소요 시간 : " + Math.round((distance_total/1.4)/60) + "분 " + Math.round((distance_total/1.4)%60) +"초", Toast.LENGTH_SHORT).show();
        }
    }

    /** Redraw last-saved path **/
    private void drawPaths(ArrayList<Integer> paths){
        if(pathfind_start){
            PolylineOptions polyLine = new PolylineOptions().width(10).color(0xFF368AFF);
            for(int i = 0; i < paths.size()-1; i++){
                polyLine.add(latlngs[paths.get(i)], latlngs[paths.get(i+1)]);
            }
            mMap.addPolyline(polyLine);
            init_markers(source, dest);
        }
    }

    /** Slide-down marker window **/
    public void markerWindowSlideDown(View v){
        if(markerWindow.getVisibility() == View.VISIBLE) {
            markerWindow.setVisibility(View.INVISIBLE);
            TranslateAnimation animate = new TranslateAnimation(
                    0, 0, 0, markerWindow.getHeight()
            );
            animate.setDuration(300);
            markerWindow.startAnimation(animate);
        }
    }

    /** Switch source and destination **/
    public void source_dest_switch(View v){
        Editable source = tv_source.getText();
        Editable dest = tv_dest.getText();
        tv_source.setText(dest);
        tv_dest.setText(source);
    }

    /** On BackButton Pressed **/
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

    /** Calculate distacne between two points **/
    private double cal_distance(Node n1, Node n2) {
        double theta = n1.longitude - n2.longitude;
        double dist = Math.sin(deg2rad(n1.latitude)) * Math.sin(deg2rad(n2.latitude)) + Math.cos(deg2rad(n1.latitude)) * Math.cos(deg2rad(n2.latitude)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = (dist * 180 / Math.PI);
        dist = dist * 60 * 1.1515 * 1609.344;

        return dist;
    }
    private  double deg2rad(double deg) {
        return deg * (Math.PI/180.0);
    }

    /** Set edge information for every nodes **/
    private void set_cost(){
        set_adjacency(nodes.get(0), new int[]{1,14,121});
        set_adjacency(nodes.get(1), new int[]{2,56,57,0});
        set_adjacency(nodes.get(2), new int[]{1,52,3});
        set_adjacency(nodes.get(3), new int[]{2,53,54,7,4,5});
        set_adjacency(nodes.get(4), new int[]{3,5});
        set_adjacency(nodes.get(5), new int[]{4,3,6});
        set_adjacency(nodes.get(6), new int[]{5,7});
        set_adjacency(nodes.get(7), new int[]{3,54,6,26});
        set_adjacency(nodes.get(8), new int[]{26,9});
        set_adjacency(nodes.get(9), new int[]{8,10});
        set_adjacency(nodes.get(10), new int[]{9});
        set_adjacency(nodes.get(11), new int[]{12,15});
        set_adjacency(nodes.get(12), new int[]{11,13,28});
        set_adjacency(nodes.get(13), new int[]{12,14});
        set_adjacency(nodes.get(14), new int[]{13,0});
        set_adjacency(nodes.get(15), new int[]{11,27,29,16});
        set_adjacency(nodes.get(16), new int[]{15,17});
        set_adjacency(nodes.get(17), new int[]{16,18});
        set_adjacency(nodes.get(18), new int[]{17,19,20,24});
        set_adjacency(nodes.get(19), new int[]{18,20,21,29});
        set_adjacency(nodes.get(20), new int[]{24,18,19,22});
        set_adjacency(nodes.get(21), new int[]{19,22});
        set_adjacency(nodes.get(22), new int[]{23,20,21,35});
        set_adjacency(nodes.get(23), new int[]{48,24,22});
        set_adjacency(nodes.get(24), new int[]{23,20,18,25});
        set_adjacency(nodes.get(25), new int[]{24,26});
        set_adjacency(nodes.get(26), new int[]{7,8,25});
        set_adjacency(nodes.get(27), new int[]{15,28,31,29});
        set_adjacency(nodes.get(28), new int[]{12,27});
        set_adjacency(nodes.get(29), new int[]{19,15,27,30});
        set_adjacency(nodes.get(30), new int[]{29,31,33});
        set_adjacency(nodes.get(31), new int[]{27,30,32});
        set_adjacency(nodes.get(32), new int[]{31,33});
        set_adjacency(nodes.get(33), new int[]{30,32,34});
        set_adjacency(nodes.get(34), new int[]{33,35});
        set_adjacency(nodes.get(35), new int[]{22,34,37,36});
        set_adjacency(nodes.get(36), new int[]{46,48,35,38});
        set_adjacency(nodes.get(37), new int[]{35,38});
        set_adjacency(nodes.get(38), new int[]{36,37,39,41});
        set_adjacency(nodes.get(39), new int[]{38,40});
        set_adjacency(nodes.get(40), new int[]{39,42});
        set_adjacency(nodes.get(41), new int[]{38,43});
        set_adjacency(nodes.get(42), new int[]{40,43});
        set_adjacency(nodes.get(43), new int[]{44,41,42});
        set_adjacency(nodes.get(44), new int[]{45,46,43});
        set_adjacency(nodes.get(45), new int[]{50,47,46,44,49});
        set_adjacency(nodes.get(46), new int[]{45,47,48,36,44});
        set_adjacency(nodes.get(47), new int[]{45,46,48});
        set_adjacency(nodes.get(48), new int[]{47,46,36,23,54});
        set_adjacency(nodes.get(49), new int[]{45,55});
        set_adjacency(nodes.get(50), new int[]{52,51,45});
        set_adjacency(nodes.get(51), new int[]{50,53});
        set_adjacency(nodes.get(52), new int[]{2,50,53});
        set_adjacency(nodes.get(53), new int[]{3,51,52});
        set_adjacency(nodes.get(54), new int[]{3,7,48});
        set_adjacency(nodes.get(55), new int[]{49,57});
        set_adjacency(nodes.get(56), new int[]{1,59,60});
        set_adjacency(nodes.get(57), new int[]{55,58,1});
        set_adjacency(nodes.get(58), new int[]{57});
        set_adjacency(nodes.get(59), new int[]{56});
        set_adjacency(nodes.get(60), new int[]{56,61});
        set_adjacency(nodes.get(61), new int[]{60,62});
        set_adjacency(nodes.get(62), new int[]{61,63});
        set_adjacency(nodes.get(63), new int[]{62,64});
        set_adjacency(nodes.get(64), new int[]{63,65,185});
        set_adjacency(nodes.get(65), new int[]{64,66});
        set_adjacency(nodes.get(66), new int[]{65,67});
        set_adjacency(nodes.get(67), new int[]{66,186});
        set_adjacency(nodes.get(68), new int[]{0,69,71});
        set_adjacency(nodes.get(69), new int[]{68,70});
        set_adjacency(nodes.get(70), new int[]{69,71,72});
        set_adjacency(nodes.get(71), new int[]{68,70,73});
        set_adjacency(nodes.get(72), new int[]{70,120,74,77});
        set_adjacency(nodes.get(73), new int[]{71,89});
        set_adjacency(nodes.get(74), new int[]{72,75,80});
        set_adjacency(nodes.get(75), new int[]{74,76,80});
        set_adjacency(nodes.get(76), new int[]{75,84});
        set_adjacency(nodes.get(77), new int[]{72,78});
        set_adjacency(nodes.get(78), new int[]{77,79});
        set_adjacency(nodes.get(79), new int[]{78,80});
        set_adjacency(nodes.get(80), new int[]{74,75,79,82,81});
        set_adjacency(nodes.get(81), new int[]{80,86,89});
        set_adjacency(nodes.get(82), new int[]{80,83,86});
        set_adjacency(nodes.get(83), new int[]{84,82,87});
        set_adjacency(nodes.get(84), new int[]{76,90,85,83});
        set_adjacency(nodes.get(85), new int[]{97,84});
        set_adjacency(nodes.get(86), new int[]{82,81,88});
        set_adjacency(nodes.get(87), new int[]{83,88,112});
        set_adjacency(nodes.get(88), new int[]{86,89,87});
        set_adjacency(nodes.get(89), new int[]{81,88,73});
        set_adjacency(nodes.get(90), new int[]{84,91,162});
        set_adjacency(nodes.get(91), new int[]{90,92,94});
        set_adjacency(nodes.get(92), new int[]{91,93});
        set_adjacency(nodes.get(93), new int[]{92,95,158,194});
        set_adjacency(nodes.get(94), new int[]{91,95});
        set_adjacency(nodes.get(95), new int[]{93,96});
        set_adjacency(nodes.get(96), new int[]{95,97,98,157});
        set_adjacency(nodes.get(97), new int[]{85,96});
        set_adjacency(nodes.get(98), new int[]{96,99});
        set_adjacency(nodes.get(99), new int[]{98,100});
        set_adjacency(nodes.get(100), new int[]{99,101,154});
        set_adjacency(nodes.get(101), new int[]{100,102,103});
        set_adjacency(nodes.get(102), new int[]{152,101,113});
        set_adjacency(nodes.get(103), new int[]{101,106,108,104});
        set_adjacency(nodes.get(104), new int[]{103,109});
        set_adjacency(nodes.get(105), new int[]{103,107,108});
        set_adjacency(nodes.get(106), new int[]{113,103,107});
        set_adjacency(nodes.get(107), new int[]{106,105,108,115});
        set_adjacency(nodes.get(108), new int[]{109,103,105,107});
        set_adjacency(nodes.get(109), new int[]{104,108,110});
        set_adjacency(nodes.get(110), new int[]{109,111});
        set_adjacency(nodes.get(111), new int[]{110,112});
        set_adjacency(nodes.get(112), new int[]{87,111});
        set_adjacency(nodes.get(113), new int[]{102,114,106});
        set_adjacency(nodes.get(114), new int[]{113,123,192});
        set_adjacency(nodes.get(115), new int[]{107,116,117});
        set_adjacency(nodes.get(116), new int[]{115,118});
        set_adjacency(nodes.get(117), new int[]{115});
        set_adjacency(nodes.get(118), new int[]{116,192,126,119,125});
        set_adjacency(nodes.get(119), new int[]{118,126,125});
        set_adjacency(nodes.get(120), new int[]{72,121});
        set_adjacency(nodes.get(121), new int[]{187,120,122,0});
        set_adjacency(nodes.get(122), new int[]{121,191});
        set_adjacency(nodes.get(123), new int[]{114,124,141,150});
        set_adjacency(nodes.get(124), new int[]{192,123,140,132});
        set_adjacency(nodes.get(125), new int[]{118,119,127});
        set_adjacency(nodes.get(126), new int[]{118,119,128});
        set_adjacency(nodes.get(127), new int[]{125,129});
        set_adjacency(nodes.get(128), new int[]{126});
        set_adjacency(nodes.get(129), new int[]{127,130,131});
        set_adjacency(nodes.get(130), new int[]{129,189});
        set_adjacency(nodes.get(131), new int[]{129,133,132});
        set_adjacency(nodes.get(132), new int[]{124,131});
        set_adjacency(nodes.get(133), new int[]{131,134,135});
        set_adjacency(nodes.get(134), new int[]{133});
        set_adjacency(nodes.get(135), new int[]{133,136});
        set_adjacency(nodes.get(136), new int[]{135,137});
        set_adjacency(nodes.get(137), new int[]{138,136,188});
        set_adjacency(nodes.get(138), new int[]{137,139,142});
        set_adjacency(nodes.get(139), new int[]{138,190,140});
        set_adjacency(nodes.get(140), new int[]{139,141,124});
        set_adjacency(nodes.get(141), new int[]{123,140,142,147});
        set_adjacency(nodes.get(142), new int[]{138,141,147});
        set_adjacency(nodes.get(143), new int[]{188,144,145});
        set_adjacency(nodes.get(144), new int[]{143});
        set_adjacency(nodes.get(145), new int[]{143,146});
        set_adjacency(nodes.get(146), new int[]{145,147});
        set_adjacency(nodes.get(147), new int[]{146,142,148,141});
        set_adjacency(nodes.get(148), new int[]{147,149,193});
        set_adjacency(nodes.get(149), new int[]{148,151,150});
        set_adjacency(nodes.get(150), new int[]{149,151,123});
        set_adjacency(nodes.get(151), new int[]{149,150,156,159});
        set_adjacency(nodes.get(152), new int[]{156,155,153,102});
        set_adjacency(nodes.get(153), new int[]{155,152,154});
        set_adjacency(nodes.get(154), new int[]{157,155,153,100});
        set_adjacency(nodes.get(155), new int[]{156,157,154,152});
        set_adjacency(nodes.get(156), new int[]{152,155,151});
        set_adjacency(nodes.get(157), new int[]{155,154,158});
        set_adjacency(nodes.get(158), new int[]{157,93,159});
        set_adjacency(nodes.get(159), new int[]{158,151,194});
        set_adjacency(nodes.get(160), new int[]{162,161,164,194});
        set_adjacency(nodes.get(161), new int[]{160,162});
        set_adjacency(nodes.get(162), new int[]{160,161,163,90});
        set_adjacency(nodes.get(163), new int[]{162});
        set_adjacency(nodes.get(164), new int[]{160,165});
        set_adjacency(nodes.get(165), new int[]{189,164,171});
        set_adjacency(nodes.get(166), new int[]{191,167});
        set_adjacency(nodes.get(167), new int[]{174,168,166});
        set_adjacency(nodes.get(168), new int[]{169,167});
        set_adjacency(nodes.get(169), new int[]{168,170});
        set_adjacency(nodes.get(170), new int[]{169,175});
        set_adjacency(nodes.get(171), new int[]{165,172});
        set_adjacency(nodes.get(172), new int[]{173,171});
        set_adjacency(nodes.get(173), new int[]{174,174});
        set_adjacency(nodes.get(174), new int[]{173,167});
        set_adjacency(nodes.get(175), new int[]{176,170,179});
        set_adjacency(nodes.get(176), new int[]{175,177});
        set_adjacency(nodes.get(177), new int[]{176,178});
        set_adjacency(nodes.get(178), new int[]{177});
        set_adjacency(nodes.get(179), new int[]{175,181,180});
        set_adjacency(nodes.get(180), new int[]{179});
        set_adjacency(nodes.get(181), new int[]{179,182,183});
        set_adjacency(nodes.get(182), new int[]{181});
        set_adjacency(nodes.get(183), new int[]{181,184,186});
        set_adjacency(nodes.get(184), new int[]{183,185});
        set_adjacency(nodes.get(185), new int[]{184,64});
        set_adjacency(nodes.get(186), new int[]{183,67});
        set_adjacency(nodes.get(187), new int[]{62,121});
        set_adjacency(nodes.get(188), new int[]{137,143,189});
        set_adjacency(nodes.get(189), new int[]{188,130,165});
        set_adjacency(nodes.get(190), new int[]{139});
        set_adjacency(nodes.get(191), new int[]{122,166});
        set_adjacency(nodes.get(192), new int[]{124,118,114});
        set_adjacency(nodes.get(193), new int[]{194,148});
        set_adjacency(nodes.get(194), new int[]{160,193,159,93});
    }

    private void set_adjacency(Node n, int[] arr){
        Edge[] edges = new Edge[arr.length];
        for(int i = 0; i < arr.length; i++){
            edges[i] = new Edge(nodes.get(arr[i]), cal_distance(n, nodes.get(arr[i])));
        }
        n.adjacencies = edges;
    }

}


