

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

        Button buttonSendWordbook = (Button) findViewById(R.id.button_send_wordbook);
        Button  buttonDeleteWord = (Button) findViewById(R.id.button_send_wordbook);

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
            String sql = "SELECT word, meaning FROM Dictionary, '" + name + "' WHERE '" + name + "'.word_id = Dictionary._id";
            Cursor cursor = dictionaryDatabase.rawQuery(sql, null);

            Log.i("content_", sql);

            Log.i("content_size", cursor.getCount() + "");
            while(cursor.moveToNext()) {
                String word = cursor.getString(0);
                String mean = cursor.getString(1);
                // dataAdapter.add(new ListViewItem(getApplicationContext(), word, mean));
                adapterWordbookContent.add(new ItemWordbookContent(getApplicationContext(), word, mean));

            }

            if (cursor != null)
                cursor.close();

        } catch (Exception e) {
            Log.d("contentAcvtivity", "error in init : " + e.toString());
        }

    }
    /* End of ShowContent */


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
