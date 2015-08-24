package org.androidtown.shutterwordbook.Class;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

/**
 * Created by ehye on 2015-08-08.
 */
public class DialogAddWordbook extends Dialog implements DialogInterface.OnClickListener, View.OnClickListener {

    private SQLiteDatabase db;
    DictionaryOpenHelper mHelper = new DictionaryOpenHelper(getContext());

    int wordId;

    Button buttonAdd;
    public  String name=null;

    private OnDismissListener onDismissListener = null;


    public DialogAddWordbook(Context context, int wordId){
        super(context);

        this.wordId = wordId;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wordbook);

        buttonAdd = (Button) findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(this);

    }




    public void onClick(View view){
        EditText editTextName = (EditText) findViewById(R.id.editText_name);
         name = editTextName.getText().toString();        // 추가할 영어 단어장 이름


         db = mHelper.getReadableDatabase();
         Cursor cursor;

        String sql = "SELECT _id from WordbookInfo";
        cursor = db.rawQuery(sql, null);
        int wordbookId = cursor.getCount()+1;

        mHelper.createWordbookTable(name);     // 테이블 생성 함수 호출
        mHelper.insertWordbookInfo(wordbookId, name);

        Toast.makeText(this.getContext(), name+"추가 완료", Toast.LENGTH_LONG).show();



        if(view == buttonAdd ){

            if(wordId != 0) {
                //단어장 추가가 이루어지면 다시 단어장 리스트를 보여주는 다이얼로그 호출
                DialogWordbookList dialogWordbookList = new DialogWordbookList(getContext(), wordId);
                dialogWordbookList.show();
                dismiss();
            }
            else {
                if(onDismissListener != null){
                    onDismissListener.onDismiss(DialogAddWordbook.this);
                }
                dismiss();
            }
        }


    }


    public String getNewWordbookName(){
        return name;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

}
