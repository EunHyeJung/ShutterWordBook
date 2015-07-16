package org.androidtown.shutterwordbook.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidtown.shutterwordbook.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordmeanFragment extends Fragment {

    private TextView textMean;  // 사전의미
    private TextView textWord;  // 사전에서 보여주는 단어

    String word;
    String mean;

    public WordmeanFragment() {
        // Required empty public constructor
    }
    public WordmeanFragment(String textWord, String textMean) {
        // Required empty public constructor
        this.word = textWord;
        this.mean = textMean;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_wordmean, container, false);

        textWord = (TextView)rootView.findViewById(R.id.textView_word);
        textMean = (TextView)rootView.findViewById(R.id.textView_meaning);

        textWord.setText(word);
        textMean.setText(mean);

        return rootView;
    }


}