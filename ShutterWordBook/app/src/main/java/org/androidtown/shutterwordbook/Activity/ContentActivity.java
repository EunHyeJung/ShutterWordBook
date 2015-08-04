package org.androidtown.shutterwordbook.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidtown.shutterwordbook.Fragment.WordbookFragment;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.Helper.WordbooksOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;

public class ContentActivity extends ActionBarActivity {

    private ListView listWordbook;
    private ArrayAdapter<String> adapter;
    private ArrayList content;

    // DB 관련
    private SQLiteDatabase wordbookDatabase;
    WordbooksOpenHelper wordbookDbHelper;

    private String name="null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // 보여주고자 하는 단어장 이름을 받아옴
        Intent intent = getIntent();
         name = intent.getExtras().getString("wordbookName");

        listWordbook = (ListView) findViewById(R.id.listView_wordbook);
        content = new ArrayList<String >();


        boolean isOpen = openWordbooksDatabase();
        if(isOpen){
           showContent();
        }

    }

    /*  단어장 데이터베이스 열기 */
    public boolean openWordbooksDatabase(){
        System.out.println("opening database"+WordbooksOpenHelper.DATABASE_NAME);
        wordbookDbHelper = new WordbooksOpenHelper(this);
         return true;
    }

    /* 단어장의 내용을 출력 */
    public void showContent(){
        try {
            wordbookDatabase = wordbookDbHelper.getReadableDatabase();
            String sql = "SELECT *  from "+name;
            Cursor cursor =  wordbookDatabase.rawQuery(sql, null);
            cursor.moveToFirst();
            while(cursor.moveToNext())
            {
                int  wordId= cursor.getInt(1);
                search(wordId);
            }
            if(cursor != null)
                cursor.close();
            if( wordbookDatabase != null)
                wordbookDatabase.close();
        } catch (Exception e) {
            System.out.println("wordbookDatabase 에러 "+e.toString());
            Log.d("StartActivityyyy", "error in init : " + e.toString());
        }

    }
    /* End of ShowContent */

    public void search(int wordId){
        SQLiteDatabase dictionaryDatabase;
        DictionaryOpenHelper dictionaryDbOpenHelper =  new DictionaryOpenHelper(this);
        if(wordbookDbHelper != null)
        {

            try {
                dictionaryDatabase = dictionaryDbOpenHelper.getReadableDatabase();
                Cursor cursor;

                String sql = "SELECT * from Dictionary where _id like " +wordId;
                cursor = dictionaryDatabase.rawQuery(sql, null);
                cursor.moveToFirst();
                String word = cursor.getString(1);
                String mean = cursor.getString(2);
                content.add(word);
                adapter =   new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,content);
                listWordbook.setAdapter(adapter);


            } catch(Exception e){
                System.out.println("DictionaryDatabase 에러 "+e.toString());
            }
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
