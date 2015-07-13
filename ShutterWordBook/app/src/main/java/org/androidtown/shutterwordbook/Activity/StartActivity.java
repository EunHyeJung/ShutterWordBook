package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Helper.MySQLiteOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;

/**
 * Created by lab on 15. 7. 9..
 */
public class StartActivity extends Activity {

    // DB
    private MySQLiteOpenHelper mHelper;
    private SQLiteDatabase db;

    // List
    private static ArrayList<String> words;

    public static ArrayList<String> getWords() {
        return words;
    }

    // private ArrayAdapter<String> adapter;
    private ListView listWord;  // 단어리스트

    private SharedPreferences hasDatabase;
    private boolean existDB;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        listWord = (ListView) findViewById(R.id.listView_words);

        // 초기화
        words = new ArrayList<String>();
        mHelper = new MySQLiteOpenHelper(this);

        hasDatabase = getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
        existDB = hasDatabase.getBoolean("exists", false);    // default : false
//        Toast.makeText(getApplicationContext(), "pref:" + existDB, Toast.LENGTH_LONG).show();

        if (existDB != true) {
            Toast.makeText(getApplicationContext(), "데이터베이스를 불러오고 있습니다", Toast.LENGTH_LONG).show();
            mHelper.copyDB();
            existDB = true;
            SharedPreferences.Editor editor = hasDatabase.edit();
            editor.putBoolean("exists", true);
            editor.apply();

//            boolean test = hasDatabase.getBoolean("exists", false);
//            Toast.makeText(getApplicationContext(), "test:" + test, Toast.LENGTH_LONG).show();

        }

        // list
        initListView();
/*
 //       hasDatabase = getSharedPreferences("db", MODE_PRIVATE);
 //       hasDatabase.getBoolean("exists", existDB);

        if(existDB) {
            Toast.makeText(getApplicationContext(), "이미있음", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"초기화", Toast.LENGTH_LONG).show();
            existDB = mHelper.copyDB();
            Log.i("db", ""+existDB);
            //existDB = true;
            SharedPreferences.Editor ed = hasDatabase.edit();
            ed.putBoolean("exists", existDB);
            ed.commit();
        }*/
        Toast.makeText(getApplicationContext(), "데이터베이스를 불러오는데에 성공하였습니다", Toast.LENGTH_LONG).show();

        Intent in = new Intent(StartActivity.this, MainActivity.class);
        startActivity(in);
        finish();
    }

    // listview 초기화
    public void initListView() {
        // data
        Log.i("z", "initListView");

        db = mHelper.getReadableDatabase();
        Cursor cursor;

        String sql = "SELECT word from Dictionary";
        cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            String word = cursor.getString(0);
            words.add(word);
        }
    }
}
