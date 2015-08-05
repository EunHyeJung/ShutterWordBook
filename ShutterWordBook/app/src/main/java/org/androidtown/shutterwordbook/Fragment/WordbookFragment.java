package org.androidtown.shutterwordbook.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidtown.shutterwordbook.Activity.MainActivity;
import org.androidtown.shutterwordbook.Helper.WordbooksOpenHelper;
import org.androidtown.shutterwordbook.Activity.StartActivity;
import org.androidtown.shutterwordbook.Activity.ContentActivity;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;
import org.androidtown.shutterwordbook.Helper.WordbooksOpenHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordbookFragment extends Fragment {


    //
    //  DB 관련
    private SQLiteDatabase db;
    WordbooksOpenHelper dbHelper;

    //

    private ListView listWordbooks;  // 단어장 리스트
    private ArrayAdapter<String> adapter;
    private ArrayList wordbooks;
//
    // Fragment와 통신하는 부분
        AccidentListener mCallback;
    //

    /* Start of onCreate View*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_wordbook, container, false);


        listWordbooks = (ListView) rootView.findViewById(R.id.listView_wordbooks);
        wordbooks = new ArrayList<String >();
       boolean isOpen = openDatabase();
        if(isOpen){
            initList();
        }
//

        deliverWordbookList();
        //

        //
        listWordbooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                String wordbookName =  parent.getItemAtPosition(position).toString();

                mCallback.showWordbook(wordbookName);

/*
                toFind = parent.getItemAtPosition(position).toString();
                textWord.setText(toFind);
                search(toFind, false);
*/

            } catch (Exception e){
                Log.d("WordbookFrag", "click error " + e.toString());
            }
        }
    });


      return rootView;
    }
/*  End of onCreateView()*/



    /* 데이터베이스 열기 */
    public boolean openDatabase(){
        System.out.println("opening database"+WordbooksOpenHelper.DATABASE_NAME);
        dbHelper = new WordbooksOpenHelper(getActivity());
        //    db = dbHelper.getWritableDatabase();
        return true;
    }

    //

    private void initList(){
        try {
            db = dbHelper.getReadableDatabase();
              String sql = "SELECT name  from Wordbooks";
             Cursor cursor = db.rawQuery(sql, null);
            while(cursor.moveToNext())
            {
                String name = cursor.getString(0);
                 wordbooks.add(name);

            }
            adapter =   new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, wordbooks);
            listWordbooks.setAdapter(adapter);
            if(cursor != null)
                cursor.close();
            if(db != null)
                db.close();


        } catch (Exception e) {
            System.out.println("에러 "+e.toString());
            Log.d("StartActivityyyy", "error in init : " + e.toString());
        }
    }
    /* End of InitList */



    /* DictionaryFragment로 단어장 리스트를 전달하는 메소드 */
    public void deliverWordbookList()
    {
        Fragment dictionaryFragment = new DictionaryFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("wordbookList", wordbooks);

        android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        dictionaryFragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_layout_dicitonaray, dictionaryFragment);
        fragmentTransaction.commit();


    }


    /* MainActivity와 통신하기 위한 interface
    *  MainActivity가 이 인터페이스를 구현해야만 한다. */
    public interface AccidentListener{
        void showWordbook(String wordbookName);
   }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // Activity(MainActivity)가 onSelectedListener를 구현했는지 확인
        try{
            mCallback = (AccidentListener) activity;
        } catch(ClassCastException e){
            System.out.println("에러 ? : "+e.toString());
            Log.d("GUN", activity.toString() + "must implement AccidentListner");
        }
    }

}
