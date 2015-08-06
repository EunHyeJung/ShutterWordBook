package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

public class LockScreenActivity extends Activity implements View.OnClickListener {

    private Button buttonOk;
    private Button buttonSelect;
    private TextView textWordbookName;
    private TextView textWordMean;

    // DB관련
    private SQLiteDatabase db;
    DictionaryOpenHelper mHelper;

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
        textWordbookName = (TextView) findViewById(R.id.textView_lock_wordbook_name);
        textWordMean = (TextView) findViewById(R.id.textView_lock_wordmean);

        // listener 정의
        buttonOk.setOnClickListener(this);
        buttonSelect.setOnClickListener(this);
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
        }
    }

    /*
    * 단어장 리스트를 팝업으로 보여준다
    * 단어장 정보가 저장된 db : Dictionary.db의 WordbookInfo 테이블
    * */
    private void showWordbookInfo() {

        // open database
        db = mHelper.getReadableDatabase();
        Cursor cursor;

        final int ids[];
        final String names[];
        int index = 0;

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(LockScreenActivity.this, android.R.layout.select_dialog_multichoice);

        // get data from database
        String sql = "select * from WordbookInfo";
        cursor = db.rawQuery(sql, null);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "단어장이 존재하지 않습니다", Toast.LENGTH_LONG).show();
        } else {
            // set list
            ids = new int[cursor.getCount()];
            names = new String[cursor.getCount()];

            while (cursor.moveToNext()) {
                ids[index] = cursor.getInt(0);
                names[index] = cursor.getString(1);
                index++;
            }
            adapter.addAll(names);

            // create dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(LockScreenActivity.this);

            /* start of builder setting */

            // button
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(LockScreenActivity.this, "확인", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(LockScreenActivity.this, "취소", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            // adapter
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = adapter.getItem(which);
                    Toast.makeText(LockScreenActivity.this, name, Toast.LENGTH_SHORT).show();
                }
            });
            /* end of builder setting */

            // popup
            builder.show();
        }

        cursor.close();
        mHelper.close();
    }

    /*
    * 선택된 단어장에 저장된 단어를 불러온다
    * 단어를 하나씩 보여주며, 버튼을 이용하여 이전 단어 혹은 다음 단어를 볼 수 있다
    * */
    private void showWords(int id) {

        // 단어장 이름 : Wordbook_xx (xx는 book_id와 동일)
        // 단어장 id : book_id
        // 단어 id : word_id

        // select word,
        Toast.makeText(this, "select", Toast.LENGTH_SHORT).show();
        textWordbookName.setText("kk");
    }
}