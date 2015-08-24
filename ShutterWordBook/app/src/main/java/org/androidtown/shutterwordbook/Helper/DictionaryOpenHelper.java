package org.androidtown.shutterwordbook.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;

import org.androidtown.shutterwordbook.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015-07-06.
 */
public class DictionaryOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Dictionary.db";
    private static final String PACKAGE_DIR = "/data/data/org.androidtown.shutterwordbook/databases";

   // File file = new File(PACKAGE_DIR + "/" + DATABASE_NAME);
    //private boolean existsDB = false;

    Context mContext;

    public DictionaryOpenHelper(Context context) {
       // super(context, "Dictionary.db", null, 1);
        super(context, PACKAGE_DIR+"/"+DATABASE_NAME, null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "drop table if exists Dictionary";
        db.execSQL(sql);

        onCreate(db);
    }

    public boolean copyDB() {
        File folder = new File(PACKAGE_DIR);
        File outfile = new File(PACKAGE_DIR + "/" + DATABASE_NAME);

        if(folder.exists()) {
        } else {
            folder.mkdirs();
        }

        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        // ������ ��� ��쿡�� ��
//        if(outfile.length() <= 0) {
            AssetManager assetManager = mContext.getResources().getAssets();
            try {
                // ���� ����.
                InputStream inStream = assetManager.open(DATABASE_NAME);
                try {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inStream);
                    outfile.createNewFile();
                    fileOutputStream = new FileOutputStream(outfile);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    int read = -1;
                    byte[] buffer = new byte[1024];
                    while( (read = bufferedInputStream.read(buffer,0, 1024)) != -1) {
                        bufferedOutputStream.write(buffer, 0, read);
                    }

                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                    bufferedInputStream.close();
                    inStream.close();


                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        //}
        return true;
    }

    // 새로운 단어장 생성
    public void createWordbookTable(String tableName){

        SQLiteDatabase db = getWritableDatabase();
        String query = "CREATE TABLE `"+tableName+"` (\n" +
                "\t`book_id`\tINTEGER,\n" +
                "\t`word_id`\tINTEGER,\n" +
                "\tPRIMARY KEY(book_id,word_id),\n" +
                "\tFOREIGN KEY(`book_id`) REFERENCES WordbookInfo ( _id ),\n" +
                "\tFOREIGN KEY(`word_id`) REFERENCES Dictionary ( _id )\n" +
                "); ";
        try{
            db.execSQL(query);

        }catch(Exception ex){
            System.out.println("뭐가 문제 ? : "+ex);
            Log.e("error", "Excpetion in insert SQL", ex);
        }

    }


    public void insertWordbookInfo(int bookId, String name){
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO `WordbookInfo`(`_id`,`name`) VALUES ("+bookId+",'"+name+"');";
        try{
            db.execSQL(query);
        }catch(Exception ex){
            System.out.println("insert Word book Info 에서는 뭐가 문제 ? : "+ex);
            Log.e("error", "Excpetion in insertWordbookInfo SQL", ex);
        }
    }

    // 단어장 삭제, WordbookInfo에서 정보삭제 + 단어장 테이블 삭제
    public void deleteWordbook(String name){
        SQLiteDatabase db = getWritableDatabase();

        // WordbookInfo에서 해당 테이블 삭제
        String query = "DELETE FROM `WordbookInfo` WHERE `name`='"+name+"';";
        try{
            db.execSQL(query);
        }catch(Exception ex){
            System.out.println("delete Word book Info 에서는 뭐가 문제 ? : "+ex);
            Log.e("error", "Excpetion in DeleteWordbookInfo SQL", ex);
        }

        // 해당 단어장 테이블 삭제
        query = "DROP TABLE `"+name+"`;";
        try{
            db.execSQL(query);
        }catch(Exception ex){
            System.out.println("drop Table 에서는 뭐가 문제 ? : "+ex);
            Log.e("myerror", "Excpetion in Drop wordbook table  SQL" +  ex.toString());
        }

    }


    // 단어장에 단어 추가
    public void  insertWord(String wordbookName, int book_id, int word_id){
        SQLiteDatabase db = getWritableDatabase();
         String query  = "insert into '"+wordbookName+"' values("+ book_id + ", " + word_id + ");";

        try{
                db.execSQL(query);

        }catch(Exception ex){
            Log.e("myerror", "Excpetion in Drop wordbook table  SQL" +  ex.toString());
        }
    }


}
