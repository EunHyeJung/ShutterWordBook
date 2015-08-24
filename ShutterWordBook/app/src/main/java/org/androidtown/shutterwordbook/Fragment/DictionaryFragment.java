package org.androidtown.shutterwordbook.Fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.androidtown.shutterwordbook.Activity.CameraActivity;
import org.androidtown.shutterwordbook.Activity.StartActivity;
import org.androidtown.shutterwordbook.Class.DialogWordbookList;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/*Copyright 공동운명체

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

public class DictionaryFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {
    // DB

    private Button buttonSearch;
    private Button buttonCamera;
    private Button buttonSpeak;
    private EditText editWord;  // 입력한 단어
    private TextView textMean;  // 사전의미
    private TextView textWord;  // 사전에서 보여주는 단어

    // List
    private ArrayAdapter<String> adapter;
    private ListView listWord;  // 단어리스트

    // DB관련
    private SQLiteDatabase db;
    DictionaryOpenHelper mHelper;


    // 발음
    TextToSpeech tts;
    boolean ttsActive = false;

    // 단어 찾기
    String toFind="null";
    String result="null";

    // 단어 사전 확장
    FragmentTransaction fragementTransaction;

    // 단어장에 단어 추가

    String word;

    /*********************
     * 카메라 기능 관련 변수
     *********************/
    //외부 저장소의 최상의 경로 확보 (사진이 저장될 경로)
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/CameraTest/";
    //영어
    public static final String lang = "eng";
    //CameraActivity에서 recognizedText를 받아오기 위한 int값의 requestCode 값을 설정
    public static final int camera_activity = 0;
    private static final String TAG = "CameraTest.java";


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
        mHelper = new DictionaryOpenHelper(getActivity());
        tts = new TextToSpeech(getActivity(), this);
        buttonSearch.setEnabled(true);

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
                try {
                    Log.d("MyDicFrag", "click");
                    toFind = parent.getItemAtPosition(position).toString();
                    textWord.setText(toFind);
                    search(toFind, false);

                } catch (Exception e){
                    Log.d("MyDicFrag", "click error " + e.toString());
                }
            }
        });

        /* End of onItemClick methd */

        /* Start of setOnItemLongClickListener
        *  해당 단어를 길게 누를 시 단어장에 단어 추가
        * */
        listWord.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                  word  = parent.getItemAtPosition(position).toString();
                   final  int wordId =(int) id+1;

                   textWord.setText(word);
                // PopupMenu 객체 생성
                final PopupMenu popupMenu = new PopupMenu(getActivity(), view);  // Activity에서는 getContext()나 this, Fragment에서는 getActivity
                // popupMenu에 들어갈 MenuItem 추가
                popupMenu.getMenuInflater().inflate(R.menu.menu_addword, popupMenu.getMenu());

                //PopupMenu의 MenuItem을 클릭하는 것을 감지하는 listener 설정
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub

                        // 단어장 리스트에서 단어장 선택
                         showWordbookList(wordId);

                        return false;
                    }
                    });
                        popupMenu.show();
                        return false;
            }
        });
        /* End of  setOnItemLongClickListener */


        /* 단어 창을 누르면 단어 뜻 확대 */
        textWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragementTransaction = getFragmentManager().beginTransaction();
                search(toFind, true);
                fragementTransaction.replace(R.id.first_page, new WordmeanFragment(toFind, result));
                fragementTransaction.addToBackStack(null);
                 fragementTransaction.commit();

            }
        });

        textMean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragementTransaction = getFragmentManager().beginTransaction();
                search(toFind, true);
                fragementTransaction.replace(R.id.first_page, new WordmeanFragment(toFind, result));
                fragementTransaction.addToBackStack(null);
                fragementTransaction.commit();
            }
        });

        /*****************************
         * 글자 인식 학습 파일 가져오기
         *****************************/
        //사진 저장할 경로
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            Log.v(TAG, "path 는??? " + path );
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                Log.v(TAG,"if문 안에 들어옴  !(new File(DATA_PATH, 어쩌고저쩌고).exists()");
                AssetManager assetManager = getActivity().getAssets();
                Log.v(TAG,"1");
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                Log.v(TAG,"2");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((len = gin.read(buf)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        return rootView;
    }
    /* End of onCreateView() */


    /* Start of onClick */
    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button_search :
                toFind = editWord.getText().toString();
                search(toFind, true);
                break;

            case R.id.button_camera :
                camera();
                break;

            case R.id.button_speak :
                speak();
                break;
        }
    }

    /* End of onClick() */

    // 검색버튼 눌렀을 때
    public void search(String toFind, Boolean move) {
        db = mHelper.getReadableDatabase();
        Cursor cursor;

        String sql = "SELECT * from Dictionary where word like \"" + toFind + "%\"";
        cursor = db.rawQuery(sql, null);

        if(cursor.getCount()==0) {
            Toast.makeText(getActivity(), "찾는 단어가 존재하지 않습니다", Toast.LENGTH_LONG).show();

        }
        else {
            cursor.moveToFirst();

            int _id = cursor.getInt(0);
            String word = cursor.getString(1);
            result = cursor.getString(2);

            if(move)
                listWord.setSelection(_id -1);
            textMean.setText(result);
            textWord.setText(toFind);
        }
        cursor.close();
        mHelper.close();
    }

    //camera 버튼 눌렀을 때
    public void camera(){
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivityForResult(intent, camera_activity);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case camera_activity :
                if(resultCode == getActivity().RESULT_OK){
                    String resultText = intent.getExtras().getString("recognizedText");
                    if ( resultText.length() != 0 && resultText.length() < 35 ) {
                        editWord.setText(editWord.getText().toString().length() == 0 ? resultText : editWord.getText() + " " + resultText);
                        editWord.setSelection(editWord.getText().toString().length());
                        search(resultText,true);
                    }
                }

        }
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

    }
    /* End of InitList() */

    /* 기존의 단어장 리스트 보여주기  */
    public void showWordbookList(int wordId){

        DialogWordbookList dialogWordbookList = new DialogWordbookList(getActivity(), wordId);
        dialogWordbookList.show();
    }

    /* End of showWordbookList() */


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