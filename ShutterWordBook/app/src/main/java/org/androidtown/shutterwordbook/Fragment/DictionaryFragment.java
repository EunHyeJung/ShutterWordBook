package org.androidtown.shutterwordbook.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
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

import org.androidtown.shutterwordbook.Activity.StartActivity;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;
import java.util.Locale;

public class DictionaryFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {
    // DB
    private static String DB_PATH = "/sdcard/";
    private static String DB_NAME = "dictionary.sqlite";
    private int listCount = 0;
    private String[] wordList = null;

    private Button buttonSearch;
    private Button buttonCamera;
    private Button buttonSpeak;
    private EditText editWord;  // �Է��� �ܾ�
    private TextView textMean;  // �����ǹ�
    private TextView textWord;  // �������� �����ִ� �ܾ�

    // List
    private ArrayList<String> words;
    private ArrayAdapter<String> adapter;
    private ListView listWord;  // �ܾ��Ʈ

    // DB����
    private SQLiteDatabase db;
    DictionaryOpenHelper mHelper;

    // ����
    TextToSpeech tts;
    boolean ttsActive = false;

    // �ܾ� ã��
    String toFind="null";
    String result="null";

    // �ܾ� ���� Ȯ��
    FragmentTransaction fragementTransaction;

    public DictionaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView =  inflater.inflate(R.layout.fragment_dictionary, container, false);

        // ���̾ƿ� ����
        buttonCamera = (Button) rootView.findViewById(R.id.button_camera);
        buttonSearch = (Button) rootView.findViewById(R.id.button_search);
        buttonSpeak = (Button) rootView.findViewById(R.id.button_speak);
        editWord = (EditText) rootView.findViewById(R.id.editText_word);
        listWord = (ListView) rootView.findViewById(R.id.listView_words);
        textMean = (TextView) rootView.findViewById(R.id.textView_meaning);
        textWord = (TextView) rootView.findViewById(R.id.textView_word);

        // �ʱ�ȭ
        //   words = new ArrayList<String>();
        mHelper = new DictionaryOpenHelper(getActivity());
        tts = new TextToSpeech(getActivity(), this);
        buttonSearch.setEnabled(true);

        // list
        //     initListView();

        // adapter
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, StartActivity.getWords());

        // adapter����
        listWord.setAdapter(adapter);

        // ������ ���
        buttonSearch.setOnClickListener((View.OnClickListener) this);
        buttonCamera.setOnClickListener((View.OnClickListener) this);
        buttonSpeak.setOnClickListener((View.OnClickListener) this);

        // ����Ʈ�� ������ ��
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


        textWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragementTransaction = getFragmentManager().beginTransaction();
                search(toFind, true);
                fragementTransaction.replace(R.id.first_page, new WordmeanFragment(toFind, result));
                System.out.println(result);
                fragementTransaction.addToBackStack(null);

                fragementTransaction.commit();

            }
        });



        return rootView;
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.button_search :
                toFind = editWord.getText().toString();
                search(toFind, true);
                break;

            case R.id.button_camera :
                Toast.makeText(getActivity(), "camera_button", Toast.LENGTH_LONG).show();
                camera();
                break;

            case R.id.button_speak :
                speak();
                break;
        }
    }

    // �˻���ư ������ ��
    public void search(String toFind, Boolean move) {
        db = mHelper.getReadableDatabase();
        Cursor cursor;

        String sql = "SELECT * from Dictionary where word like \"" + toFind + "%\"";
        cursor = db.rawQuery(sql, null);

        if(cursor.getCount()==0) {
            Toast.makeText(getActivity(), "ã�� �ܾ �������� �ʽ��ϴ�", Toast.LENGTH_LONG).show();

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

    //camera ��ư ������ ��
    public void camera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(cameraIntent);
    }

    // tts
    public void speak() {

        // ���� �ܾ �����´�. ������ ȭ�鿡�� ������.
        String toSpeak = textWord.getText().toString();

        // queue�� ���� ������ �ܾ ������ �ϰ� �Ѵ�
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    // tts listener
    @Override
    public void onInit(int status) {

        // tts�� ������ ���
        if(status == TextToSpeech.SUCCESS) {
            buttonSearch.setEnabled(true);

            String toSpeak = textWord.getText().toString();

            int result = tts.setLanguage(Locale.US); // ����. �̱�, ����,ȣ�� �� �پ��� ���� ���� ������ ��
            // setPitch() - ������ ������
            // setSpeechRate() - ���� �ӵ�

            // �����Ͱ� ���ų� �������� �ʴ� ���
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

    @Override
    public void onDestroy() {
        // tts�� �� ������Ѿ���!!!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}