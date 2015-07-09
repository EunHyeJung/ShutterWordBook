package org.androidtown.shutterwordbook.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.androidtown.shutterwordbook.Helper.MySQLiteOpenHelper;
import org.androidtown.shutterwordbook.R;

/**
 * Created by lab on 15. 7. 9..
 */
public class StartActivity extends Activity {

    // DB 초기화
    private MySQLiteOpenHelper mHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mHelper = new MySQLiteOpenHelper(this);

        if(mHelper.isExistDB()) {
            Toast.makeText(getApplicationContext(), R.string.text_db_ready, Toast.LENGTH_LONG).show();
            Intent in = new Intent(StartActivity.this, MainActivity.class);
            startActivity(in);
            finish();
        }
    }
}