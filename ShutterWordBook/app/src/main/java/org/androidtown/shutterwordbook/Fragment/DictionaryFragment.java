package org.androidtown.shutterwordbook.Fragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
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
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {

    private Button buttonSearch;
    private Button buttonCamera;
    private Button buttonSpeak;
    private EditText editWord;  // 입력한 단어
    private TextView textMean;  // 사전의미
    private TextView textWord;  // 사전에서 보여주는 단어

    // List
 //   private ArrayList<String> words;
    private ArrayAdapter<String> adapter;
    private ListView listWord;  // 단어리스트

    // DB관련
    SQLiteDatabase db;
    MySQLiteOpenHelper mHelper;

    // 발음
    TextToSpeech tts;
    boolean ttsActive = false;

    // 단어 찾기
    String toFind="null";
    String result="null";

    // 단어 사전 확장
    FragmentTransaction fragementTransaction;

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
        buttonSpeak = (Button) rootView.findViewById(R.id.button_speak);
        editWord = (EditText) rootView.findViewById(R.id.editText_word);
        listWord = (ListView) rootView.findViewById(R.id.listView_words);
        textMean = (TextView) rootView.findViewById(R.id.textView_meaning);
        textWord = (TextView) rootView.findViewById(R.id.textView_word);

        // 초기화
     //   words = new ArrayList<String>();
        mHelper = new MySQLiteOpenHelper(getActivity());
        tts = new TextToSpeech(getActivity(), this);
        buttonSearch.setEnabled(true);

        // list
   //     initListView();

        // adapter
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, StartActivity.getWords());

        // adapter연결
        listWord.setAdapter(adapter);

        // 리스너 등록
        buttonSearch.setOnClickListener((View.OnClickListener) this);
        buttonCamera.setOnClickListener((View.OnClickListener) this);
        buttonSpeak.setOnClickListener((View.OnClickListener) this);

        // 리스트를 눌렀을 때
        listWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String toFind = parent.getItemAtPosition(position).toString();
                textWord.setText(toFind);
                search(toFind, false);
            }
        });

        //

            fragementTransaction = getFragmentManager().beginTransaction();

                textWord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragementTransaction.replace(R.id.first_page, new WordmeanFragment(toFind, result));
                        fragementTransaction.addToBackStack(null);
                        fragementTransaction.commit();
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
                toFind = editWord.getText().toString();
                search(toFind, true);
                break;

            case R.id.button_camera :
                Toast.makeText(getActivity(), "camera_button", Toast.LENGTH_LONG).show();
                break;

            case R.id.button_speak :
                speak();
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
       result = cursor.getString(2);

        if(result.length()==0) {
            textMean.setText("not found");
        }
        else {
//            wordList.getItemAtPosition(_id-1);
            if(move)
                listWord.setSelection(_id -1);
            textMean.setText(result);
            textWord.setText(toFind);
        }
        cursor.close();
        mHelper.close();
    }

    // tts
    public void speak() {

        // 읽을 단어를 가져온다. 오른쪽 화면에서 가져옴.
        String toSpeak = textWord.getText().toString();

        // queue를 비우고 지정한 단어를 발음을 하게 한다
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    // tts listener
    @Override
    public void onInit(int status) {

        // tts가 가능한 경우
        if(status == TextToSpeech.SUCCESS) {
            buttonSearch.setEnabled(true);

            String toSpeak = textWord.getText().toString();

            int result = tts.setLanguage(Locale.US); // 언어설정. 미국, 영국,호주 등 다양한 발음 제공 가능할 듯
            // setPitch() - 발음의 높낮이
            // setSpeechRate() - 발음 속도

            // 데이터가 없거나 지원하지 않는 경우
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(getActivity(), R.string.text_tts_error,Toast.LENGTH_SHORT).show();
            }
            else {
                buttonSpeak.setEnabled(true);
            }
        }
        else {
            Toast.makeText(getActivity(), R.string.text_tts_error,Toast.LENGTH_SHORT).show();
            buttonSpeak.setEnabled(false);
        }

//////////////////////////////////////////////////////////////


    }

    @Override
    public void onDestroy() {
        // tts를 꼭 종료시켜야함!!!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}

