package org.androidtown.shutterwordbook.Fragment;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.androidtown.shutterwordbook.Class.ItemDeleteWordbook;
import org.androidtown.shutterwordbook.Helper.DictionaryOpenHelper;
import org.androidtown.shutterwordbook.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteWordbookFragment extends Fragment {


    //  DB 관련
    private SQLiteDatabase db;
    DictionaryOpenHelper dbHelper;

    AdapterDeleteWordbook adapterDeleteWordbook = null;
    ListView listWordbooks;
    ArrayList<ItemDeleteWordbook> wordbookList;

    Button buttonDelete;


    public DeleteWordbookFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_delete_wordbook, container, false);


        wordbookList = new ArrayList<ItemDeleteWordbook>();
        listWordbooks = (ListView) rootView.findViewById(R.id.listView_wordbooks);

        buttonDelete = (Button) rootView.findViewById(R.id.button_delete);

        boolean isOpen = openDatabase();
        if(isOpen){
            initList();
        }
        checkBoxClick();


            return rootView;
    }

    /* 데이터베이스 열기 */
    public boolean openDatabase(){
        System.out.println("opening database");
        dbHelper = new DictionaryOpenHelper(getActivity());
        return true;
    }

    private void initList(){


        try {
            db = dbHelper.getReadableDatabase();
            String sql = "SELECT name from WordbookInfo";
            Cursor cursor = db.rawQuery(sql, null);
            //System.out.println("cursor.getCount() : "+cursor.getString(0));

            //    adapterDeleteWordbook.clear();
            while(cursor.moveToNext())
            {
                String name = cursor.getString(0);
                ItemDeleteWordbook wordbook = new ItemDeleteWordbook(name, false);
                wordbookList.add(wordbook);
            }

            adapterDeleteWordbook = new AdapterDeleteWordbook(getActivity(), R.layout.item_delete_wordbook, wordbookList);
            listWordbooks.setAdapter(adapterDeleteWordbook);

            if(cursor != null)
                cursor.close();
            if(db != null)
                db.close();
//

            //

        } catch (Exception e) {
            System.out.println("에러 "+e.toString());
            Log.d("StartActivityyyy", "error in init : " + e.toString());
        }
    }

    public class AdapterDeleteWordbook extends ArrayAdapter<ItemDeleteWordbook> {

        public ArrayList<ItemDeleteWordbook> wordbookList;
        private LayoutInflater mInflater;


        public AdapterDeleteWordbook(Context context, int textViewResourceId, ArrayList<ItemDeleteWordbook> wordbookList) {
            super(context, textViewResourceId , wordbookList);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.wordbookList = new ArrayList<ItemDeleteWordbook>();
            this.wordbookList.addAll(wordbookList);
        }

        private class ViewHolder{
            TextView wordbookName;
            CheckBox checkBox;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;


            if(convertView == null){
                convertView = mInflater.inflate(R.layout.item_delete_wordbook, null);

                holder = new ViewHolder();
                holder.wordbookName = (TextView) convertView.findViewById(R.id.textView_wordbook_name);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        ItemDeleteWordbook itemDeleteWordbook = (ItemDeleteWordbook) cb.getTag();
                        itemDeleteWordbook.setChecked(cb.isChecked());

                    }
                });


            } else{
                holder = (ViewHolder) convertView.getTag();
            }

            ItemDeleteWordbook itemDeleteWordbook = wordbookList.get(position);
            holder.wordbookName.setText(itemDeleteWordbook.getWordbookName());
            holder.checkBox.setChecked(itemDeleteWordbook.isChecked());
            holder.checkBox.setTag(itemDeleteWordbook);


            return convertView;

        }



    }

    public void checkBoxClick(){
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   ArrayList<String> deleteItems = new ArrayList<String>();


                    ArrayList<ItemDeleteWordbook> deleteWordbookList = adapterDeleteWordbook.wordbookList;      // 삭제될 단어장 리스트

                    for(int i=0 ; i<deleteWordbookList.size() ; i++){
                        ItemDeleteWordbook itemDeleteWordbook = deleteWordbookList.get(i);
                        if(itemDeleteWordbook.isChecked()){
                                 deleteItems.add(itemDeleteWordbook.getWordbookName());
                        }
                     }

                        // 단어장 삭제 작업 수행, 체크된 단어장의 개수 만큼
                        for(int i=0 ; i<deleteItems.size() ; i++){
                            dbHelper.deleteWordbook(deleteItems.get(i));
                        }

                // 다시 단어장 fragment로 전환
                FragmentTransaction fragementTransaction;
                fragementTransaction = getFragmentManager().beginTransaction();
                fragementTransaction.replace(R.id.frag_deletewordbook, new WordbookFragment(),"WordbookFragment");
             //   fragementTransaction.addToBackStack(null);
                fragementTransaction.commit();
                }
        });

   }




}
