package org.androidtown.shutterwordbook.Class;

import android.content.Context;

/**
 * Created by ehye on 2015-08-16.
 */
public class WordbookListView {
    private String wordbookName;
    private boolean check;

    public WordbookListView(Context context, String wordbookName, boolean check){
        this.wordbookName = wordbookName;
        this.check = check;
    }

    public boolean getCheck(){
        return check;
    }
    public String getWordbookName(){
        return wordbookName;
    }



}
