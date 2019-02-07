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
        }, 2000);
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
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정후" + "'" + "," + 0 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정경대학" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정경대" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정대" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "파이빌리지" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "PI VILLIAGE" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "파이빌" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "미디어관" + "'" + "," + 4 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "시네마트랩" + "'" + "," + 5 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "CINEMA TRAP" + "'" + "," + 5 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "영화관" + "'" + "," + 5 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "타이거플라자" + "'" + "," + 7 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "TIGER PLAZA" + "'" + "," + 7 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "국제관" + "'" + "," + 9 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "홍보관" + "'" + "," + 11 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "교양관" + "'" + "," + 13 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "우당교양관" + "'" + "," + 13 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "민주광장" + "'" + "," + 14 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "민광" + "'" + "," + 14 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "학생회관" + "'" + "," + 17 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "학관" + "'" + "," + 17 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "4.18기념관" + "'" + "," + 19 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "사일팔기념관" + "'" + "," + 19 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "농구장" + "'" + "," + 20 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "닭장" + "'" + "," + 20 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정문" + "'" + "," + 23 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장" + "'" + "," + 25 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중광" + "'" + "," + 25 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장지하 왼쪽입구" + "'" + "," + 26 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중지 왼쪽입구" + "'" + "," + 26 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장지하 오른쪽입구" + "'" + "," + 27 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중지 오른쪽입구" + "'" + "," + 27 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "백주년기념 삼성관" + "'" + "," + 29 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "백주년기념관" + "'" + "," + 29 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "100주년기념관" + "'" + "," + 29 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "백기" + "'" + "," + 29 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "박물관" + "'" + "," + 30 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고려대역 1번출구" + "'" + "," + 33 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고대역 1번출구" + "'" + "," + 3 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "LG-POSCO 경영관(옆)" + "'" + "," + 34 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "엘포(옆)" + "'" + "," + 34 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "LG-POSCO 경영관(정문)" + "'" + "," + 37 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "엘포(정문)" + "'" + "," + 37 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "현대자동차 경영관" + "'" + "," + 38 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "현차" + "'" + "," + 38 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "경영대학 본관 잔디밭" + "'" + "," + 40 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "경영대학 본관" + "'" + "," + 41 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "경본" + "'" + "," + 41 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "호상" + "'" + "," + 46 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장지하 2번 게이트" + "'" + "," + 47 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙도서관(신관)" + "'" + "," + 52 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중도" + "'" + "," + 52 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "사범대학 본관" + "'" + "," + 53 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "사범대학 신관" + "'" + "," + 55 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "운초우선교육관" + "'" + "," + 57 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "교육관" + "'" + "," + 57 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법학관 구관" + "'" + "," + 61 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법구" + "'" + "," + 61 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "구법" + "'" + "," + 61 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "아세아문제연구소" + "'" + "," + 62 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙도서관(대학원)" + "'" + "," + 66 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "대학원" + "'" + "," + 66 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "동원글로벌리더십홀" + "'" + "," + 69 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "동글리" + "'" + "," + 69 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "CJ법학관" + "'" + "," + 72 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "씨법" + "'" + "," + 72 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "로스쿨" + "'" + "," + 72 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "LAW SCHOOL" + "'" + "," + 72 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법대후분" + "'" + "," + 73 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법후" + "'" + "," + 73 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "해송법학도서관" + "'" + "," + 76 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "해도" + "'" + "," + 76 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법학관 신관" + "'" + "," + 77 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법신" + "'" + "," + 77 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "신법" + "'" + "," + 77 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "다람쥐길" + "'" + "," + 79 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "다람쥐길 옆 물탱크" + "'" + "," + 80 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "인촌기념관" + "'" + "," + 83 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "인촌" + "'" + "," + 83 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "조지훈 시비" + "'" + "," + 85 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "문과대학" + "'" + "," + 94 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "문대" + "'" + "," + 94 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "서관" + "'" + "," + 94 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "본관" + "'" + "," + 103 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "김성수 동상" + "'" + "," + 105 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "인촌 김성수 동상" + "'" + "," + 105 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장지하 1번 게이트" + "'" + "," + 107 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "수당삼양 패컬티하우스" + "'" + "," + 109 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "패컬티하우스" + "'" + "," + 109 + ")";
            db.execSQL(insertRow);

        }
    }

}
