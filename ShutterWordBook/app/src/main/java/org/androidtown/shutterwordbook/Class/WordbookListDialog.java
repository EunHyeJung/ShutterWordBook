package org.androidtown.shutterwordbook.Class;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;

/**
 * Created by ehye on 2015-08-10.
 */
public class WordbookListDialog extends Dialog implements DialogInterface.OnClickListener, View.OnClickListener  {


    private SQLiteDatabase db;
    DictionaryOpenHelper mHelper = new DictionaryOpenHelper(getContext());



    public WordbookListDialog(Context context, final int  wordId) {           // wordID : 추가할 단어 ID
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wordbooklist);


        Button buttonAdd = (Button) findViewById(R.id.button_addWordbook);
        ArrayAdapter<String> adapter;
        ListView listWordbook=(ListView) findViewById(R.id.listView_wordbooks);  // 단어리스트



       // 현재 존재하는 단어장 불러옴
        db = mHelper.getReadableDatabase();
        String sql = "SELECT name from WordbookInfo";
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList arrayListwordbookList = new ArrayList<String >();

        while(cursor.moveToNext())
        {
            String name = cursor.getString(0);
            arrayListwordbookList.add(name);

        }
        adapter =   new ArrayAdapter<String>(getContext(), R.layout.list_textview, arrayListwordbookList);
        listWordbook.setAdapter(adapter);

         // 단어장 추가 버튼 클릭
        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 새로운 단어장 생성하는 다이얼로그 호출
                AddWordbookDialog addWordbookDialog = new AddWordbookDialog(getContext(), wordId);
                addWordbookDialog.show();
                dismiss();

            }
        });


        listWordbook.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            try{
                                String bookName = parent.getItemAtPosition(position).toString();
                                int bookId = ((int)id)+1;
                                mHelper.insertWord(bookName, bookId, wordId);
                                  Toast.makeText(getContext(), "단어 추가 완료", Toast.LENGTH_LONG).show();
                            }catch(Exception e){
                                Log.d("Add Word","Add word Error"+e);
                            }

                }
        });


    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
