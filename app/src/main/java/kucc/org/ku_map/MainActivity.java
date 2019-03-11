package kucc.org.ku_map;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /** DB **/
    private SQLiteDatabase db;

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
        }, 1000);
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
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "안암역" + "'" + "," + 0 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "이공계 정문" + "'" + "," + 1 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "생명과학관 동관" + "'" + "," + 4 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "우정정보관" + "'" + "," + 5 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "미래융합기술관" + "'" + "," + 6 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정보전산처" + "'" + "," + 10 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "노벨광장" + "'" + "," + 11 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "이공계 후문(소울키친)" + "'" + "," + 12 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "참살이길" + "'" + "," + 14 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "이학관별관" + "'" + "," + 16 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "환경실험관" + "'" + "," + 17 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "애기능 동산" + "'" + "," + 20 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "애기능생활관 옆문" + "'" + "," + 25 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "이공계 후문(문화사)" + "'" + "," + 28 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "제2공학관" + "'" + "," + 29 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "농구코트(이공계)" + "'" + "," + 30 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "애기능 학생회관" + "'" + "," + 31 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "신공학관" + "'" + "," + 32 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "창의관" + "'" + "," + 34 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "공학관" + "'" + "," + 37 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "산학관" + "'" + "," + 40 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "하나스퀘어 입구(3)" + "'" + "," + 41 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "아산이학관" + "'" + "," + 44 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "하나스퀘어 지상" + "'" + "," + 46 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "하나스퀘어 입구(2)" + "'" + "," + 47 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "과학도서관" + "'" + "," + 48 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "하나과학관" + "'" + "," + 49 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "생명과학관 서관" + "'" + "," + 50 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "하나스퀘어 입구(1)" + "'" + "," + 51 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "셔틀버스정류장(이공계)" + "'" + "," + 54 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고려대학교병원 응급의료센터" + "'" + "," + 58 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고려대학교 안암병원" + "'" + "," + 59 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "의과대학" + "'" + "," + 64 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "장례식장" + "'" + "," + 67 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정경대 후문" + "'" + "," + 72 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "정경대학(2)" + "'" + "," + 74 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "타이거플라자" + "'" + "," + 75 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "국제관" + "'" + "," + 76 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "파이빌리지" + "'" + "," + 78 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "미디어관" + "'" + "," + 79 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "우당교양관" + "'" + "," + 81 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "홍보관" + "'" + "," + 82 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "민주광장" + "'" + "," + 86 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "학생회관" + "'" + "," + 88 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "문과대학" + "'" + "," + 97 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장" + "'" + "," + 103 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장 입구(좌)" + "'" + "," + 104 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙광장 입구(우)" + "'" + "," + 105 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "백주년기념관" + "'" + "," + 106 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고려대학교 정문" + "'" + "," + 108 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "농구코트(인문계)" + "'" + "," + 111 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "호상" + "'" + "," + 113 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "LG-POSCO 경영관(2)" + "'" + "," + 116 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고려대역 1번 출구" + "'" + "," + 117 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "경영본관" + "'" + "," + 119 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "개운사" + "'" + "," + 122 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "LG-POSCO 경영관(1)" + "'" + "," + 126 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "현대자동차 경영관(1)" + "'" + "," + 127 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "현대자동차 경영관(2)" + "'" + "," + 128 + ")";
            db.execSQL(insertRow);
            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "교우회관" + "'" + "," + 130 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "사범대학 본관" + "'" + "," + 131 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "중앙도서관" + "'" + "," + 132 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "사범대학 신관" + "'" + "," + 134 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "운초우선교육관" + "'" + "," + 135 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법학관 구관" + "'" + "," + 139 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "해송법학도서관" + "'" + "," + 144 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법학관 신관" + "'" + "," + 145 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "동원글로벌리더십홀" + "'" + "," + 148 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "대학원" + "'" + "," + 150 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "인촌 김성수 동상" + "'" + "," + 153 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "본관" + "'" + "," + 155 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "인촌기념관" + "'" + "," + 161 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "수당삼양패컬티하우스" + "'" + "," + 163 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "안암학사(기숙사)" + "'" + "," + 168 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "CJ인터내셔널 하우스" + "'" + "," + 170 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "고려대학교 출판부" + "'" + "," + 172 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "한국학관" + "'" + "," + 176 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "아이스링크" + "'" + "," + 178 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "화정체육관" + "'" + "," + 180 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "녹지운동장" + "'" + "," + 181 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "간호대학" + "'" + "," + 182 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "CJ법학관" + "'" + "," + 188 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "법대 후문" + "'" + "," + 189 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "아세아문제연구소" + "'" + "," + 190 + ")";
            db.execSQL(insertRow);

            insertRow = "INSERT INTO LOCATION_INFO " +
                    "(NAME, LOCATION_INDEX) VALUES (" + "'"+ "다람쥐길" + "'" + "," + 193 + ")";
            db.execSQL(insertRow);

        }
    }

}
