package kucc.org.ku_map;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /** DB **/
    private SQLiteDatabase db;
    ArrayList<Node> nodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** database **/
        db = openOrCreateDatabase("kumap", MODE_PRIVATE, null);
        init_table();
        save_rows();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }

    /** Create table **/
    private void init_table(){
        if(db != null){
            String createTable = "CREATE TABLE IF NOT EXISTS LOCATION_INFO (" +
                    "NAME " + "TEXT," +
                    "LOCATION_INDEX " + "INTEGER" +")";
            db.execSQL(createTable);
        }
    }

    /** Save data in db **/
    private void save_rows(){
        if(db != null){
            db.execSQL("DELETE FROM LOCATION_INFO");

            String insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정경대 후문" + "'" + "," + 0 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "민주광장" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정문" + "'" + "," + 2 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "백주년기념관" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고려대역 1번출구" + "'" + "," + 4 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙도서관" + "'" + "," + 5 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "운초우선교육관" + "'" + "," + 6 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "동원글로벌리더십홀" + "'" + "," + 7 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법대 후문" + "'" + "," + 8 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "문과대학" + "'" + "," + 9 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "본관" + "'" + "," + 10 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "수당삼양 패컬티하우스" + "'" + "," + 11 + ")";
            db.execSQL(insertRow);

        }
    }

}
