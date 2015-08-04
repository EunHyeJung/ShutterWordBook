package org.androidtown.shutterwordbook.Fragment;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidtown.shutterwordbook.Helper.WordbooksOpenHelper;
import org.androidtown.shutterwordbook.Activity.StartActivity;
import org.androidtown.shutterwordbook.Activity.ContentActivity;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;
import org.androidtown.shutterwordbook.Helper.WordbooksOpenHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordbookFragment extends Fragment {


    //
    //  DB 관련
    private SQLiteDatabase db;
    WordbooksOpenHelper dbHelper;

    private static String DB_NAME = "Wordbooks.db";
    private int listCount = 0;
    private String[] nameList = null;       // 단어장 이름을 담는 배열
    //

    private ListView listWordbook;  // 단어장 리스트
    private ArrayAdapter<String> adapter;
    private ArrayList wordbooks;

    /* Start of onCreate View*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_wordbook, container, false);
        listWordbook = (ListView) rootView.findViewById(R.id.listView_wordbooks);
        wordbooks = new ArrayList<String >();
       boolean isOpen = openDatabase();
        if(isOpen){
            initList();
        }

        //
        listWordbook.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                String wordbookName =  parent.getItemAtPosition(position).toString();
                showWordbook(wordbookName);
/*
                toFind = parent.getItemAtPosition(position).toString();
                textWord.setText(toFind);
                search(toFind, false);
*/

            } catch (Exception e){
                Log.d("WordbookFrag", "click error " + e.toString());
            }
        }
    });


      return rootView;
    }
//


    /* 데이터베이스 열기 */
    public boolean openDatabase(){
        System.out.println("opening database"+WordbooksOpenHelper.DATABASE_NAME);
        dbHelper = new WordbooksOpenHelper(getActivity());
    //    db = dbHelper.getWritableDatabase();
        return true;
    }

    //

    public void initList(){
        try {

            db = dbHelper.getReadableDatabase();
              String sql = "SELECT name  from Wordbooks";
             Cursor cursor = db.rawQuery(sql, null);
            //cursor.moveToFirst();

            while(cursor.moveToNext())
            {
                String name = cursor.getString(0);
                 wordbooks.add(name);
                listCount++;
            }
            adapter =   new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, wordbooks);
            listWordbook.setAdapter(adapter);
            if(cursor != null)
                cursor.close();
            if(db != null)
                db.close();
        } catch (Exception e) {
            System.out.println("에러 "+e.toString());
            Log.d("StartActivityyyy", "error in init : " + e.toString());
        }
    }
    /* End of InitList */

    // 단어장 리스트 클릭시 해당 단어장을 보여주는 메소드
    public void showWordbook(String wordbookName){
/*        db = dbHelper.getReadableDatabase();
        String sql = "SELECT name  from "+wordbookName;
        Cursor cursor = db.rawQuery(sql, null);

        while(cursor.moveToNext())
        {
            String name = cursor.getString(0);
            wordbooks.add(name);

        }*/
        Intent in = new Intent(getActivity(), ContentActivity.class);
        startActivity(in);

    }

}
