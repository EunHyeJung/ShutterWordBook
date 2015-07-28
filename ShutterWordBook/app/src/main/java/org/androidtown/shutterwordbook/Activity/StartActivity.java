package org.androidtown.shutterwordbook.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class StartActivity extends FragmentActivity {

    // DB
//    private static String DB_PATH = "/sdcard/";
    private static String DB_PATH = "/data/data/org.androidtown.shutterwordbook/databases/";
    private static String DB_NAME = "Dictionary.db";
    private int listCount = 0;
    private String[] wordList = null;
    //
    Handler handler;
    ProgressRunnable runnable;
    //

    boolean check = false;

    // List
    private static ArrayList<String> words;

    public static ArrayList<String> getWords() {
        return words;
    }

    /* SD카드에 DB파일이 존재하는지를 확인 */
    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }

        if (checkDB != null) {
            checkDB.close();
        }
        Log.d("StartActivityyyy", checkDB + "");
        return checkDB != null ? true : false;
    }


    /* SD카드에 assets에 등록된 DB파일을 복사 */
    private void copyDatabase() {
        try {
            InputStream myInput = this.getAssets().open(DB_NAME);
            String outFileName = DB_PATH + DB_NAME;

/////
            File outputFile = new File(outFileName);
            outputFile.createNewFile();
/////

            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();

        } catch (Exception e) {
            Log.d("StartActivityyyy", "error in copyDatabase() : " + e.toString());
        }
    }

    /* SD카드에 DB파일이 있는지 확인하고 없을 경우에는 assets에 등록된 DB파일을 복사 */
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();
        if (dbExist) { //Nothing
        } else {
/*            try{
                copyDatabase();
            }catch(IOException e){
                throw new Error("Error copying database");
            }*/
            copyDatabase();
        }
    }


    /*   액티비티가 생성되면 DB파일을 먼저 SD카드에  저장을 하고,
           SD카드에 저장된 DB파일을 읽어서 리스트뷰에 출력                 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
/*
        words = new ArrayList<String>();
        handler = new Handler();
        runnable = new ProgressRunnable();
*/

/*
// 스레드 시작
        Thread thread1 = new Thread(new Runnable(){
            public void run(){
                Log.d("StartActivityyyy", "DB생성1");

                try{
                    createDatabase();
                    Log.d("StartActivityyyy", "DB생성");
                    System.out.println("DB 생성 성공");
                } catch(IOException ioe){
                    System.out.println("DB 생성 안됨");
                    //              Toast.makeText(this, "DB 파일을 생성할 수 없습니다.", Toast.LENGTH_LONG).show();
                }

                try{
                    Cursor cursor;
                    SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH+DB_NAME, null, 1);
                    String sql = "SELECT word from Dictionary";

                    cursor = db.rawQuery(sql, null);
                    startManagingCursor(cursor);
                    wordList = new String[cursor.getCount()];
                    Log.d("StartActivityyyy", cursor.getCount()+"");
                    //        definitionList = new String[cursor.getCount()];
                    Log.d("StartActivityyyy", "지연???");

                    System.out.println("지연의 시초??");
                    while(cursor.moveToNext())
                    {
                        String word = cursor.getString(0);
                        wordList[listCount] = word;
                        listCount++;
                    }
                    System.out.println("리스트에 뿌리기");
                    if(db!=null)
                        db.close();
                } catch (Exception e){
                    Log.d("StartActivityyyy", "SQLite error " + e.toString());

                    //   Toast.makeText(this, "ERROR IN CODE : "+e.toString(), Toast.LENGTH_LONG).show();
                }
                handler.post(runnable);
            }
        });
        thread1.start();


        //       finish();
*/

        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                DictionaryOpenHelper dbHelper = new DictionaryOpenHelper(StartActivity.this);
                dbHelper.copyDB();
                Log.d("StartActivityyyy", "dbHepler");

                // listView에 add
                initListView();
            }
        });

        
        thread1.start();
        Log.d("StartActivityyyy", "run");

    }

    public void initListView() {

        try {

            SQLiteDatabase db;
            DictionaryOpenHelper dbHelper = new DictionaryOpenHelper(StartActivity.this);

            db = dbHelper.getReadableDatabase();
            Cursor cursor;

            String sql = "SELECT word from Dictionary";

            cursor = db.rawQuery(sql, null);
            words = new ArrayList<String>();

            while (cursor.moveToNext()) {
                String word = cursor.getString(0);
                words.add(word);
            }

            Intent in = new Intent(StartActivity.this, MainActivity.class);
            startActivity(in);
            finish();

        } catch (Exception e) {
            Log.d("StartActivityyyy", "error in init : " + e.toString());
        }
    }

    public class ProgressRunnable implements Runnable {
        public void run() {
            System.out.println("listCount " + listCount);
            Log.d("StartActivityyyy", "listcount:" + listCount);
            if (listCount > 0) {
                words = new ArrayList<String>();
                for (int i = 0; i < (wordList.length); i++)
                //            for(int i=0; i<50; i++)
                {
                    words.add(wordList[i]);
                }
                System.out.println("wordsSize" + words.size());
                check = true;
            }


            if (check == true) {
                Intent in = new Intent(StartActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            }
        }
    }
}
