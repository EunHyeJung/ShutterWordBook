package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.Helper.ScreenService;
import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;
import java.util.Locale;

public class LockScreenActivity extends Activity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private Button buttonOk;
    private Button buttonSelect;
    private Button buttonShow;
    private Button buttonNext;
    private Button buttonPrevious;
    private Button buttonOnOff;
    private Button buttonSpeak;

    private TextView textWordbookName;
    private TextView textWord;
    private TextView textWordMean;
    private TextView textIndex;

    // DB관련
    private SQLiteDatabase db;
    DictionaryOpenHelper mHelper;

    // 발음
    TextToSpeech tts;
    boolean ttsActive = false;


    final ArrayList<String> words = new ArrayList<String>();
    final ArrayList<String> meaning = new ArrayList<String>();

    int ids[];
    String names[];
    boolean[] id_check;

    int wordbookNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        mHelper = new DictionaryOpenHelper(LockScreenActivity.this);

        // layout 연결
        buttonOk = (Button) findViewById(R.id.button_lock_ok);
        buttonSelect = (Button) findViewById(R.id.button_lock_select);
        buttonShow = (Button) findViewById(R.id.button_lock_show);
        buttonNext = (Button) findViewById(R.id.button_lock_next);
        buttonPrevious = (Button) findViewById(R.id.button_lock_previous);
        buttonOnOff = (Button) findViewById(R.id.button_lock_onoff);
        buttonSpeak = (Button) findViewById(R.id.button_lock_speak);

        textWordbookName = (TextView) findViewById(R.id.textView_lock_wordbook_name);
        textWord = (TextView) findViewById(R.id.textView_lock_word);
        textWordMean = (TextView) findViewById(R.id.textView_lock_wordmean);
        textIndex = (TextView) findViewById(R.id.textView_lock_index);

        textWordMean.setVisibility(View.INVISIBLE);
        textWord.setVisibility(View.INVISIBLE);
        buttonShow.setVisibility(View.INVISIBLE);

        // listener 정의
        buttonOk.setOnClickListener(this);
        buttonSelect.setOnClickListener(this);
        buttonOnOff.setOnClickListener(this);
        buttonSpeak.setOnClickListener(this);

        // tts
        tts = new TextToSpeech(LockScreenActivity.this, this);


        // default setting
        db = mHelper.getReadableDatabase();
        Cursor cursor;
        wordbookNum = 0;

        // get data from database
        String sql = "select * from WordbookInfo";
        cursor = db.rawQuery(sql, null);
        wordbookNum = cursor.getCount();

        try {
            if (wordbookNum != 0) {
                ids = new int[wordbookNum];
                names = new String[wordbookNum];
                id_check = new boolean[wordbookNum];

                int index = 0;
                while (cursor.moveToNext()) {
                    ids[index] = cursor.getInt(0);
                    names[index] = cursor.getString(1);
                    id_check[index] = false;
                    index++;
                }
            }
            setWordbookInfo(0);

        } catch (Exception e) {
            Log.e("mmlock2", e.toString());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_lock_ok:
                finish();
                break;
            case R.id.button_lock_select:
                //popup
                showWordbookInfo();
                break;
            case R.id.button_lock_onoff:
                onoff();
                break;
            case R.id.button_lock_speak:
                speak();
                break;
        }
    }

    private void onoff() {
        // Dialog setting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setTitle("잠금화면 설정을 해제하시겠습니까?");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.i("mmlock", "onclick");
                // 단어장 선택 후 확인 버튼 누를 때
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    SharedPreferences pref = getSharedPreferences("lockscreen", Context.MODE_PRIVATE);
                    // getSharedPreference는 Activity에서만 호출이 가능하기 때문에 getActivity()부터 호출해야한다

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("lockOn", false);
                    editor.commit();

                    // lockscreen service 중지
                    Intent intent = new Intent(LockScreenActivity.this, ScreenService.class);
                    stopService(intent);

                    finish();
                }

                // 취소 버튼 누를 때
                else if (which == DialogInterface.BUTTON_NEUTRAL) {
                    dialog.dismiss();
                }

            }
        };
        builder.setPositiveButton("확인", listener);
        builder.setNeutralButton("취소", listener);
        builder.setCancelable(true);
        builder.create().show();
    }

    /*
    * 단어장 리스트를 팝업으로 보여준다
    * 단어장 정보가 저장된 db : Dictionary.db의 WordbookInfo 테이블
    * */
    private void showWordbookInfo() {

        if (wordbookNum == 0) {
            Toast.makeText(this, "단어장이 존재하지 않습니다", Toast.LENGTH_LONG).show();
            return;
        } else {

            // Dialog setting
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.create();
            builder.setTitle("단어장 선택");

        /* multichoice 삭제 */

        /* single choice */


            builder.setSingleChoiceItems(names, 0, null);

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Log.i("mmlock", "onclick");
                    // 단어장 선택 후 확인 버튼 누를 때
                    try {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            int idx_select = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            setWordbookInfo(idx_select);
                        }
                        // 취소 버튼 누를 때
                        else if (which == DialogInterface.BUTTON_NEUTRAL) {
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        Log.e("mmlock", e.toString());
                    }
                }
            };
            builder.setPositiveButton("확인", listener);
            builder.setNeutralButton("취소", listener);
            builder.setCancelable(true);
            builder.create().show();
        }
    }
    //   }

    private void setWordbookInfo(int idx_select) {

        int wordbook_id = ids[idx_select];
        String wordbook_name = names[idx_select];
        textWordbookName.setText(wordbook_name);

        Log.i("mmlock", "id:" + wordbook_id);
//select word,meaning from Wordbook,Dictionary where Wordbook.wordbook_id = 1 and Wordbook.word_id = Dictionary._id

        String findWordQuery = "select word, meaning from Dictionary, '" + wordbook_name + "' where book_id = '"
                + wordbook_id + "' and Dictionary._id = word_id;";

        Log.i("mmlock", findWordQuery);
        Cursor cursor2 = db.rawQuery(findWordQuery, null);
        int index = 0;
        words.clear();
        meaning.clear();

        int wordCount = cursor2.getCount();
        Log.i("mmlock", "wordCount:" + wordCount);

        if (wordCount != 0) {
            while (cursor2.moveToNext()) {
                words.add(index, cursor2.getString(0));
                meaning.add(index, cursor2.getString(1));
                index++;
            }
            showWords(words, meaning);
        } else { // 저장된 단어가 없을 때
            Toast.makeText(LockScreenActivity.this, "저장된 단어가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 선택된 단어장에 저장된 단어를 불러온다
    * 단어를 하나씩 보여주며, 버튼을 이용하여 이전 단어 혹은 다음 단어를 볼 수 있다
    * */
    private void showWords(final ArrayList<String> words, final ArrayList<String> meaning) {

        // 단어장 이름 :
        // 단어장 id : book_id
        // 단어 id : word_id

        final int count = words.size();
        final int index = (int) Math.random() % count;

        buttonShow.setVisibility(View.VISIBLE);
        buttonShow.setText("▼ 단어 의미 보기 ▼");
        textWord.setVisibility(View.VISIBLE);
        textIndex.setText((index + 1) + " / " + count);

        textWord.setText(words.get(index));
        textWordMean.setText(meaning.get(index));

        // button
        buttonShow.setOnClickListener(new View.OnClickListener() {
            boolean show_temp = false;

            @Override
            public void onClick(View v) {
                if (show_temp == false) {
                    textWordMean.setVisibility(View.VISIBLE);
                    show_temp = true;
                    buttonShow.setText("▲ 단어 의미 숨기기 ▲");
                } else {
                    textWordMean.setVisibility(View.INVISIBLE);
                    show_temp = false;
                    buttonShow.setText("▼ 단어 의미 보기 ▼");
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            int index_temp = index;

            @Override
            public void onClick(View v) {
                if (index_temp == count - 1) {
                    index_temp = 0;
                } else {
                    index_temp++;
                }
                textWord.setText(words.get(index_temp));
                textWordMean.setText(meaning.get(index_temp));
                textIndex.setText((index_temp + 1) + " / " + count);
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            int index_temp = index;

            @Override
            public void onClick(View v) {
                try {
                    if (index_temp == 0) {
                        index_temp = count - 1;
                    } else {
                        index_temp--;
                    }
                    textWord.setText(words.get(index_temp));
                    textWordMean.setText(meaning.get(index_temp));
                    textIndex.setText((index_temp + 1) + " / " + count);
                } catch (Exception e) {
                    Log.e("lock_error", e.toString());
                }
            }
        });
    }

    // tts
    public void speak() {

        try {
            // 읽을 단어를 가져온다. 오른쪽 화면에서 가져옴.
            String toSpeak = textWord.getText().toString();
            Log.i("mmlock", toSpeak);

            // queue를 비우고 지정한 단어를 발음을 하게 한다
            tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

        } catch (Exception e) {
            Log.e("mmlock", e.toString() + " error in speak");
        }
    }

    // tts listener
    @Override
    public void onInit(int status) {

        // tts가 가능한 경우
        if(status == TextToSpeech.SUCCESS) {

            String toSpeak = textWord.getText().toString();

            int result = tts.setLanguage(Locale.US); // 언어설정. 미국, 영국,호주 등 다양한 발음 제공 가능할 듯
            // setPitch() - 발음의 높낮이
            // setSpeechRate() - 발음 속도

            // 데이터가 없거나 지원하지 않는 경우
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(LockScreenActivity.this, R.string.text_tts_error,Toast.LENGTH_SHORT).show();
            }
            else {
                buttonSpeak.setEnabled(true);
            }
        }
        else {
            Toast.makeText(LockScreenActivity.this, R.string.text_tts_error,Toast.LENGTH_SHORT).show();
            buttonSpeak.setEnabled(false);
        }

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
            /* multichoice
        builder.setMultiChoiceItems(names, id_check, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                id_check[which] = isChecked;
            }
        });

        final ArrayList<String> words = new ArrayList<String>();
        final ArrayList<String> meaning = new ArrayList<String>();

        // query example
        // select word, meaning from Dictionary, Wordbook, WordbookInfo where WordbookInfo._id = 1 and Dictionary._id = Wordbook.word_id
        // wordbook 1번에 있는 단어들을 가져옴
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.i("mmlock", "onclick");
                // 단어장 선택 후 확인 버튼 누를 때
                if(which == DialogInterface.BUTTON_POSITIVE) {

                    for(int i=0; i<wordBookNum; i++) {

}                           if (id_check[i] = true) {
                            String findWordQuery = "select word, meaning from Dictionary, Wordbook, WordbookInfo where WordbookInfo._id = "
                                    + ids[i] + " and Dictionary._id = Wordbook.word_id";
                            Log.i ("mmlock", findWordQuery);
                            Cursor cursor = db.rawQuery(findWordQuery, null);
                            int index = 0;
                            while (cursor.moveToNext()) {
                                words.add(index, cursor.getString(0));
                                meaning.add(index, cursor.getString(1));
                                index++;
                            }
                        }
                    }
                    dialog.dismiss();
                    showWords(words,meaning);
                }

                // 취소 버튼 누를 때
                else if(which == DialogInterface.BUTTON_NEUTRAL) {
                    dialog.dismiss();
                }
            }
        };
*/