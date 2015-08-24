package org.androidtown.shutterwordbook.Class;

import android.content.Context;

/**
 * Created by ehye on 2015-08-07.
 */
public class ItemWordbookContent {
    private String word;
    private String mean;


    public ItemWordbookContent(Context context, String word, String mean){
        this.word = word;
        this.mean = mean;
    }

    public String getWord() { return word; }
    public String getMean() { return mean; }
}
