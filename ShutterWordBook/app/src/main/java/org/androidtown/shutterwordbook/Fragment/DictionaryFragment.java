package org.androidtown.shutterwordbook.Fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.shutterwordbook.R;
import org.androidtown.shutterwordbook.Helper.*;
import org.androidtown.shutterwordbook.Activity.*;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryFragment extends Fragment implements View.OnClickListener {

    private Button buttonSearch;
    private Button buttonCamera;
    private EditText editWord;  // 입력한 단어
    private TextView textMean;  // 사전의미

    // List
 //   private ArrayList<String> words;
    private ArrayAdapter<String> adapter;
    private ListView listWord;  // 단어리스트

    // DB관련
    SQLiteDatabase db;
    MySQLiteOpenHelper mHelper;

    public DictionaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =  inflater.inflate(R.layout.fragment_dictionary, container, false);


        // 레이아웃 연결
        buttonCamera = (Button) rootView.findViewById(R.id.button_camera);
        buttonSearch = (Button) rootView.findViewById(R.id.button_search);
        editWord = (EditText) rootView.findViewById(R.id.editText_word);
        listWord = (ListView) rootView.findViewById(R.id.listView_words);
        textMean = (TextView) rootView.findViewById(R.id.textView_meaning);

        // 초기화
     //   words = new ArrayList<String>();
        mHelper = new MySQLiteOpenHelper(getActivity());

        // list
   //     initListView();

        // adapter
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, StartActivity.getWords());

        // adapter연결
        listWord.setAdapter(adapter);

        // 리스너 등록
        buttonSearch.setOnClickListener((View.OnClickListener) this);
        buttonCamera.setOnClickListener((View.OnClickListener) this);
        listWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String toFind = parent.getItemAtPosition(position).toString();
                search(toFind, false);
            }
        });

        return rootView;
    }

    // listview 초기화
 /*   public void initListView() {
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

        // adapter
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, words);

        // adapter연결
        listWord.setAdapter(adapter);
//        wordList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }*/

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button_search :
                String toFind = editWord.getText().toString();
                search(toFind, true);
                break;

            case R.id.button_camera :
                Toast.makeText(getActivity(), "camera_button", Toast.LENGTH_LONG).show();
                break;
        }
    }

    // 검색버튼 눌렀을 때
    public void search(String toFind, Boolean move) {
        db = mHelper.getReadableDatabase();
        Cursor cursor;

        String sql = "SELECT * from Dictionary where word like \"" + toFind + "%\"";
        cursor = db.rawQuery(sql, null);

        cursor.moveToFirst();

        int _id = cursor.getInt(0);
        String word = cursor.getString(1);
        String result = cursor.getString(2);

        if(result.length()==0) {
            textMean.setText("not found");
        }
        else {
//            wordList.getItemAtPosition(_id-1);
            if(move)
                listWord.setSelection(_id -1);
            textMean.setText(result);
        }
        cursor.close();
        mHelper.close();
    }
}

