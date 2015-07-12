package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Fragment.DictionaryFragment;
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
    private boolean existDB;

    // List
    private static ArrayList<String> words;
    public static ArrayList<String> getWords() {
        return words;
    }

   // private ArrayAdapter<String> adapter;
    private ListView listWord;  // 단어리스트

//    private static SharedPreferences hasDatabase;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        listWord = (ListView)findViewById(R.id.listView_words);

        // 초기화
        words = new ArrayList<String>();
        mHelper = new MySQLiteOpenHelper(this);

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

        mHelper.copyDB();
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

    // 값 불러오기
    private void getPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        pref.getString("hi", "");
    }

    // 값 저장하기
    private void savePreferences(String key, String value){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // 값(Key Data) 삭제하기
    private void removePreferences(String key){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    // 값(ALL Data) 삭제하기
    private void removeAllPreferences(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
