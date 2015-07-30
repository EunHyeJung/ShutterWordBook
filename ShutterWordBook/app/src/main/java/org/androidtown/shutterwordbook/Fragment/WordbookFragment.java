package org.androidtown.shutterwordbook.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.androidtown.shutterwordbook.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordbookFragment extends Fragment {


    private ListView listWordbook;  // 단어장 리스트


    public WordbookFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_wordbook, container, false);




        return rootView;
    }


}
