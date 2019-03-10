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
import java.util.Date;

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
    private ImageButton btn_bus_route;
    private ImageButton btn_bus_info;
    private Button btn_set_source;
    private Button btn_set_dest;
    private TextView tv_title;
    private AutoCompleteTextView tv_source;
    private AutoCompleteTextView tv_dest;
    private ConstraintLayout markerWindow;
    private ConstraintLayout busLayout;
    private ConstraintLayout busInfoLayout;

    private String[] arr_latlng;
    private String[] arr_buslatlng;
    private LatLng[] latlngs_bus;
    private LatLng[] latlngs;
    private ArrayList<Node> nodes;
    private ArrayList<Integer> paths;
    private ArrayList<Integer> bus_location;

    private int interval_renew = 0;
    private long backbtn_pressed_time;

    private int last_source;
    private int last_dest;
    private String busMessage;
    private boolean pathfind_start;
    private boolean busroute_on;

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
        btn_bus_route = findViewById(R.id.bus_route);
        btn_bus_info = findViewById(R.id.bus_info);
        btn_pathfind = findViewById(R.id.btn_pathfind);
        btn_set_source = findViewById(R.id.setSource);
        btn_set_dest = findViewById(R.id.setDest);
        markerWindow = findViewById(R.id.markerWindow);
        busLayout = findViewById(R.id.busLayout);
        busInfoLayout = findViewById(R.id.busInfoLayout);
        astar = new A_STAR();
        nodes = new ArrayList<>();
        paths = new ArrayList<>();

        /** Instantiate markers **/
        arr_latlng = getResources().getStringArray(R.array.latlng);
        latlngs = new LatLng[13];
        for(int i = 0; i < latlngs.length; i++){
            latlngs[i] = new LatLng(Double.valueOf(arr_latlng[3*i]),Double.valueOf(arr_latlng[3*i+1]));
        }

        /** Instantiate bus route markers **/
        arr_buslatlng = getResources().getStringArray(R.array.latlng_busroute);
        latlngs_bus = new LatLng[87];
        for(int i = 0; i < latlngs_bus.length; i++){
            latlngs_bus[i] = new LatLng(Double.valueOf(arr_buslatlng[3*i]),Double.valueOf(arr_buslatlng[3*i+1]));
        }
        bus_location = new ArrayList<>();

        /** Nodes initialization **/
        for(int i = 0; i < latlngs.length; i++){
            nodes.add(new Node(i, arr_latlng[3*i+2]));
            nodes.get(i).latitude = Double.valueOf(arr_latlng[3*i]);
            nodes.get(i).longitude = Double.valueOf(arr_latlng[3*i+1]);
        }

        /** Edges initialization **/
        nodes.get(0).adjacencies = new Edge[]{new Edge(nodes.get(11),3)};
        nodes.get(1).adjacencies = new Edge[]{new Edge(nodes.get(9),2),new Edge(nodes.get(12),1)};
        nodes.get(2).adjacencies = new Edge[]{new Edge(nodes.get(3),1),new Edge(nodes.get(10),5),new Edge(nodes.get(12),1)};
        nodes.get(3).adjacencies = new Edge[]{new Edge(nodes.get(2), 1)};
        nodes.get(4).adjacencies = new Edge[]{new Edge(nodes.get(6),4)};
        nodes.get(5).adjacencies = new Edge[]{new Edge(nodes.get(6),2),new Edge(nodes.get(7),1)};
        nodes.get(6).adjacencies = new Edge[]{new Edge(nodes.get(4),4),new Edge(nodes.get(5),2),new Edge(nodes.get(8),3)};
        nodes.get(7).adjacencies = new Edge[]{new Edge(nodes.get(5),1),new Edge(nodes.get(10),4)};
        nodes.get(8).adjacencies = new Edge[]{new Edge(nodes.get(6),3)};
        nodes.get(9).adjacencies = new Edge[]{new Edge(nodes.get(1),2),new Edge(nodes.get(10),3),new Edge(nodes.get(11),1)};
        nodes.get(10).adjacencies = new Edge[]{new Edge(nodes.get(2),5),new Edge(nodes.get(7),4),new Edge(nodes.get(9),3)};
        nodes.get(11).adjacencies = new Edge[]{new Edge(nodes.get(0),3),new Edge(nodes.get(9),1)};
        nodes.get(12).adjacencies = new Edge[]{new Edge(nodes.get(1),1),new Edge(nodes.get(2),1)};

        /** Check location permission **/
        checkPermission();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            startLocationService();
        }

        /** MarkerWindow & bus layout & time_tv set INVISIBLE **/
        markerWindow.setVisibility(View.INVISIBLE);
        busLayout.setVisibility(View.INVISIBLE);
        busInfoLayout.setVisibility(View.INVISIBLE);

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

        /** Thread start **/
        Runnable runnable = new TimeRunner();
        Thread timeThread= new Thread(runnable);
        timeThread.start();

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

        /** Bus route & real time location button click listener **/
        btn_bus_route.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(busroute_on){
                    mMap.clear();
                    if(pathfind_start){
                        drawPaths(paths);
                    }else{
                        init_markers();
                    }
                    busroute_on = false;
                }else{
                    if(busMessage != ""){
                        Toast.makeText(getApplicationContext(), busMessage, Toast.LENGTH_SHORT).show();
                    }
                    update_bus_location(bus_location);
                    busroute_on = true;
                }
                busLayoutFold();
            }
        });

        /** bus timetable button click listener **/
        btn_bus_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                busInfoLayoutFold(null);
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
                else
                    mMap.addMarker(new MarkerOptions().position(latlngs[i]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_tiger)).title(nodes.get(i).value));
            }else{
                mMap.addMarker(new MarkerOptions().position(latlngs[i]).title(nodes.get(i).value).visible(false));
            }
        }
    }

    /** Make markers on circulator shuttle route **/
    private void init_bus_markers(){
        PolylineOptions polyLine = new PolylineOptions().width(10).color(0xFF990000);
        for(int i = 0; i < latlngs_bus.length; i++){
            if(arr_buslatlng[3*i+2].equals("1")){
                mMap.addMarker(new MarkerOptions().position(latlngs_bus[i]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_busstop)).title("busmarker"));
            }
            polyLine.add(latlngs_bus[i]);
        }
        mMap.addPolyline(polyLine);
    }

    /** Update real-time shuttle location **/
    private void update_bus_location(ArrayList<Integer> buses){
        mMap.clear();
        if(pathfind_start){
            drawPaths(paths);
            init_markers(last_source, last_dest);
        }else{
            init_markers();
        }
        init_bus_markers();
        for(Integer i : buses){
            mMap.addMarker(new MarkerOptions().position(latlngs_bus[i]).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_bus_current)).title("busmarker"));
        }

    }

    /** On marker clicked **/
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTitle().equals("busmarker")){
            return true;
        }
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
        if (busLayout.getVisibility() == View.VISIBLE) {
            busLayout.setVisibility(View.INVISIBLE);
        }
        if(busInfoLayout.getVisibility() == View.VISIBLE){
            busInfoLayout.setVisibility(View.INVISIBLE);
        }
    }

    /** Path find **/
    private void findPath(){
        pathfind_start = true;
        if(!isEmpty(tv_source) && !isEmpty(tv_dest )){
            /** Retrieve source and destination marker indeces from database**/
            int source = retrieve_index(tv_source.getText().toString());
            int dest = retrieve_index(tv_dest.getText().toString());
            last_source = source;
            last_dest = dest;
            Log.i(TAG, "source : " + tv_source.getText().toString() + " index = " + source);
            Log.i(TAG, "destination : " + tv_dest.getText().toString() + " index = " + dest);
            if(source == -1 || dest == -1) return;
            if(source == dest) return;

            /** Clear google map & add markers **/
            mMap.clear();
            if(busroute_on){
                init_bus_markers();
                update_bus_location(bus_location);
            }
            init_markers(source, dest);

            /** Clear parent information & add heuristic values(distacne from source in meters) **/
            for(Node n : nodes){
                n.parent = null;
                n.h_scores = cal_distance(nodes.get(dest).latitude, nodes.get(dest).longitude, n.latitude, n.longitude);
            }
            astar.search(nodes.get(source), nodes.get(dest));
            paths = (ArrayList)astar.printPath(nodes.get(dest));
            Log.i(TAG, "paths : " + paths);

            /** Draw path from source to destination using dijkstra algorithm **/
            PolylineOptions polyLine = new PolylineOptions().width(15).color(0xFF368AFF);
            for(int i = 0; i < paths.size()-1; i++){
                polyLine.add(latlngs[paths.get(i)], latlngs[paths.get(i+1)]);
            }
            mMap.addPolyline(polyLine);
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
            init_markers(last_source, last_dest);
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
    private double cal_distance(double lat1, double lon1, double lat2, double lon2) {
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

    /**  Shuttle functionality onclick method **/
    public void busOnClick(View v){
        busLayoutFold();
    }

    /** Fold shuttle window **/
    private void busLayoutFold(){
        if(busLayout.getVisibility() == View.VISIBLE){
            busLayout.setVisibility(View.INVISIBLE);
        }else{
            busLayout.setVisibility(View.VISIBLE);
        }
    }

    /** Fold shuttle info window **/
    public void busInfoLayoutFold(View v){
        if(busInfoLayout.getVisibility() == View.VISIBLE){
            busInfoLayout.setVisibility(View.INVISIBLE);
        }else{
            onMapClick(new LatLng(37.589503, 127.032323));
            busInfoLayout.setVisibility(View.VISIBLE);
        }
    }

    class TimeRunner implements Runnable{

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    shuttle();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }

    private void shuttle() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Date date = new Date();
                    int day = date.getDay();
                    int hours = date.getHours();
                    int minutes = date.getMinutes();
                    int seconds = date.getSeconds();
                    if(interval_renew >= 10){
                        interval_renew = 0;
                    }
                    interval_renew++;
                    if( day == 0 || day == 6 ){
                        busMessage = "주말에는 셔틀버스를 운영하지 않습니다.";
                    }else if((hours <= 7 || (hours == 8 && minutes < 25)) || ((hours >= 19) || (hours == 18 && minutes > 15))){
                        busMessage = "현재 운영중인 셔틀버스가 없습니다.";
                    }else{
                        busMessage = "";
                        if((hours == 8 && minutes == 25 && seconds == 0) || (hours == 8 && minutes == 35 && seconds == 0) || (hours == 8 && minutes == 45 && seconds == 0) || (hours == 8 && minutes == 55 && seconds == 0) ||
                           (hours == 9 && minutes == 0 && seconds == 0) || (hours == 9 && minutes == 10 && seconds == 0) || (hours == 9 && minutes == 20 && seconds == 0) || (hours == 9 && minutes == 30 && seconds == 0) || (hours == 9 && minutes == 40 && seconds == 0) || (hours == 9 && minutes == 50 && seconds == 0) ||
                           (hours == 10 && minutes == 0 && seconds == 0) || (hours == 10 && minutes == 10 && seconds == 0) || (hours == 10 && minutes == 20 && seconds == 0) || (hours == 10 && minutes == 30 && seconds == 0) || (hours == 10 && minutes == 40 && seconds == 0) || (hours == 10 && minutes == 50 && seconds == 0) ||
                           (hours == 11 && minutes == 0 && seconds == 0) || (hours == 11 && minutes == 10 && seconds == 0) || (hours == 11 && minutes == 20 && seconds == 0) || (hours == 11 && minutes == 25 && seconds == 0) || (hours == 11 && minutes == 30 && seconds == 0) || (hours == 11 && minutes == 35 && seconds == 0) || (hours == 11 && minutes == 45 && seconds == 0) || (hours == 11 && minutes == 55 && seconds == 0) ||
                           (hours == 12 && minutes == 40 && seconds == 0) || (hours == 12 && minutes == 50 && seconds == 0) ||
                           (hours == 13 && minutes == 0 && seconds == 0) || (hours == 13 && minutes == 10 && seconds == 0) || (hours == 13 && minutes == 20 && seconds == 0) || (hours == 13 && minutes == 25 && seconds == 0) || (hours == 13 && minutes == 30 && seconds == 0) || (hours == 13 && minutes == 35 && seconds == 0) || (hours == 11 && minutes == 45 && seconds == 0) || (hours == 13 && minutes == 55 && seconds == 0) ||
                           (hours == 14 && minutes == 0 && seconds == 0) || (hours == 14 && minutes == 10 && seconds == 0) || (hours == 14 && minutes == 20 && seconds == 0) || (hours == 14 && minutes == 30 && seconds == 0) || (hours == 14 && minutes == 40 && seconds == 0) || (hours == 14 && minutes == 50 && seconds == 0) ||
                           (hours == 15 && minutes == 0 && seconds == 0) || (hours == 15 && minutes == 10 && seconds == 0) || (hours == 15 && minutes == 20 && seconds == 0) || (hours == 15 && minutes == 30 && seconds == 0) || (hours == 15 && minutes == 40 && seconds == 0) || (hours == 15 && minutes == 50 && seconds == 0) ||
                           (hours == 16 && minutes == 0 && seconds == 0) || (hours == 16 && minutes == 20 && seconds == 0) || (hours == 16 && minutes == 40 && seconds == 0) ||
                           (hours == 17 && minutes == 0 && seconds == 0) || (hours == 17 && minutes == 20 && seconds == 0) || (hours == 17 && minutes == 40 && seconds == 0) || (hours == 17 && minutes == 50 && seconds == 0)){
                            bus_location.add(0);
                            if(busroute_on) {
                                update_bus_location(bus_location);
                            }
                        }
                        if(interval_renew == 10 && busroute_on){
                            for(int i = 0; i < bus_location.size(); i++){
                                int loc = bus_location.get(i);
                                if(loc > 87){
                                    bus_location.remove(i);
                                    continue;
                                }
                                loc += 1;
                                bus_location.set(i, loc);
                            }
                            update_bus_location(bus_location);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

}


