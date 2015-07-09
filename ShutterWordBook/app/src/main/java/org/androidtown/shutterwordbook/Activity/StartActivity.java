package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.content.Intent;
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

    // DB 초기화
    private MySQLiteOpenHelper mHelper;
    // DB관련
    SQLiteDatabase db;

    // List
    private static ArrayList<String> words;

    public static ArrayList<String> getWords() {
        return words;
    }
   // private ArrayAdapter<String> adapter;
    private ListView listWord;  // 단어리스트


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        listWord = (ListView)findViewById(R.id.listView_words);

        // 초기화
        words = new ArrayList<String>();
        mHelper = new MySQLiteOpenHelper(this);

        // list
        initListView();

        if(mHelper.isExistDB()) {
            Toast.makeText(getApplicationContext(), R.string.text_db_ready, Toast.LENGTH_LONG).show();
            Intent in = new Intent(StartActivity.this, MainActivity.class);
            startActivity(in);
            finish();
        }
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