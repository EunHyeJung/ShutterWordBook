package org.androidtown.shutterwordbook.Fragment;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import org.androidtown.shutterwordbook.Class.DialogAddWordbook;
import org.androidtown.shutterwordbook.Class.AdapterWordbook;
import org.androidtown.shutterwordbook.Class.ItemWordbook;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */


public class WordbookFragment extends Fragment {


    //
    //  DB 관련
    private SQLiteDatabase db;
    DictionaryOpenHelper dbHelper;

    //
    private ListView listViewWordbooks;  // 단어장 리스트
    private AdapterWordbook  adapterWordbook;
    private ArrayList<ItemWordbook> arrayListWordbook;
//
    //
    private Button buttonAddWordbook;
    private Button buttonDeleteWordbook;
    //Fragment와 통신하는 부분
    AccidentListener mCallback;
    //

    String newWordbookName=null;
    //

    FragmentTransaction fragementTransaction2;                   // 삭제모드전환
    //

    /* Start of onCreate View*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_wordbook, container, false);

        arrayListWordbook = new ArrayList<ItemWordbook>();


        boolean isOpen = openDatabase();
        if(isOpen){
            initList();
        }

        listViewWordbooks = (ListView) rootView.findViewById(R.id.listView_wordbooks);
         adapterWordbook = new AdapterWordbook(getActivity(), arrayListWordbook);
        listViewWordbooks.setAdapter(adapterWordbook);
        buttonAddWordbook = (Button) rootView.findViewById(R.id.button_addWordbook);
        buttonDeleteWordbook = (Button) rootView.findViewById(R.id.button_deleteWordbook);

//
        //

        // 단어장 내용 보여줌
        listViewWordbooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                String wordbookName =  parent.getItemAtPosition(position).toString();
                System.out.println(wordbookName);
                mCallback.showWordbook(wordbookName);
            } catch (Exception e){
                Log.d("WordbookFrag", "click error " + e.toString());
            }
        }
    });

       // 단어장 추가 버튼 클릭
       buttonAddWordbook.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               int temp  = 0;
               DialogAddWordbook dialogAddWordbook = new DialogAddWordbook(getActivity(), temp);
               dialogAddWordbook.show();
               dialogAddWordbook.setOnDismissListener((DialogInterface.OnDismissListener) getActivity());
           }
       });

        // 단어장 삭제 버튼 클릭
        buttonDeleteWordbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragementTransaction2 = getFragmentManager().beginTransaction();
                fragementTransaction2.replace(R.id.frag_wordbook, new DeleteWordbookFragment(),"DeleteWordbookFragment");
                fragementTransaction2.addToBackStack(null);
                fragementTransaction2.commit();

            }
        });



      return rootView;
    }
/*  End of onCreateView()*/



    /* 데이터베이스 열기 */
    public boolean openDatabase(){
        System.out.println("opening database");
        dbHelper = new DictionaryOpenHelper(getActivity());
        //    db = dbHelper.getWritableDatabase();
        return true;
    }

    //
/*
    arrayListWordbook.add(wordbook);
}

listViewWordbook.setAdapter(adapterDeleteWordbook);
*/

    private void initList(){
        try {
            db = dbHelper.getReadableDatabase();
              String sql = "SELECT name from WordbookInfo";
             Cursor cursor = db.rawQuery(sql, null);
            adapterWordbook.clear();
            System.out.println("cursorSIze : "+cursor.getCount());
            while(cursor.moveToNext())
            {
                String name = cursor.getString(0);
                ItemWordbook itemWordbook = new ItemWordbook(name);
                arrayListWordbook.add(itemWordbook);
            }

            adapterWordbook = new AdapterWordbook(getActivity(), arrayListWordbook);
            System.out.println("adapterSize"+adapterWordbook.getCount());
            listViewWordbooks.setAdapter(adapterWordbook);
            adapterWordbook.notifyDataSetChanged();

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








    @Override
    public void onResume(){
        super.onResume();

        // 리스트 갱신
        initList();

    }


    public interface AccidentListener {
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
