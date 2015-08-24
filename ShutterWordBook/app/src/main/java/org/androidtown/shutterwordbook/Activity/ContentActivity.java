package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import org.androidtown.shutterwordbook.Class.AdapterWordbookContent;
import org.androidtown.shutterwordbook.Class.ItemWordbookContent;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;

import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;

public class ContentActivity extends Activity {
    //
    private ListView listViewContent;
    private ArrayList<ItemWordbookContent> data;


    private Button buttonAddWord;


    // 데이터를 연결할 Adapter
    AdapterWordbookContent adapterWordbookContent;

    //

    // DB 관련
    private SQLiteDatabase dictionaryDatabase;
    DictionaryOpenHelper dbHelper;

    private String name = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // 보여주고자 하는 단어장 이름을 받아옴
        Intent intent = getIntent();
        name = intent.getExtras().getString("wordbookName");

        TextView title = (TextView) findViewById(R.id.textView_title);
        title.setText(name);

        data = new ArrayList<ItemWordbookContent>();
        listViewContent = (ListView) findViewById(R.id.listView_content);
        adapterWordbookContent = new AdapterWordbookContent(this, data);
        listViewContent.setAdapter(adapterWordbookContent);

        boolean isOpen = openDictionaryDatabase();
        if (isOpen) {
            showContent();
        }



    }
    /* End of onCreateVeiw() */


    /*  사전 데이터베이스 열기 */
    public boolean openDictionaryDatabase() {
        System.out.println("opening database");
        dbHelper = new DictionaryOpenHelper(this);
        return true;
    }

    /* 단어장의 내용을 출력 */
    public void showContent() {
        try {
            dictionaryDatabase = dbHelper.getReadableDatabase();
            String sql = "SELECT  word_id from " + name;
            Cursor cursor = dictionaryDatabase.rawQuery(sql, null);
            //  cursor.moveToFirst();

            while (cursor.moveToNext()) {
                int wordId = cursor.getInt(0);
                search(wordId);
            }
            if (cursor != null)
                cursor.close();

        } catch (Exception e) {
            Log.d("contentAcvtivity", "error in init : " + e.toString());
        }

    }
    /* End of ShowContent */




    //
    public void search(int wordId){
            try {
                dictionaryDatabase = dbHelper.getReadableDatabase();

                Cursor cursor;

                String sql = "SELECT * from Dictionary where _id like " +wordId;
                cursor = dictionaryDatabase.rawQuery(sql, null);
                cursor.moveToFirst();
                String word = cursor.getString(1);
                String mean = cursor.getString(2);

                // DataAdapter를 통해서 Arraylist에 자료 저장
                // 하나의 String 값을 저장하던 것을 ListViewClass의 객체를 저장하던 것으로 변경
                // ListViewClass 객체는 생성자에 리스트 표시

                adapterWordbookContent.add(new ItemWordbookContent(getApplicationContext(), word, mean));

            } catch(Exception e){
                System.out.println("DictionaryDatabase 에러 "+e.toString());
            }



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
