package org.androidtown.shutterwordbook.Class;

import android.content.Context;

/**
 * Created by ehye on 2015-08-16.
 */
public class ItemWordbook {
    private String wordbookName;

    public ItemWordbook(String wordbookName) {
        this.wordbookName = wordbookName;

    }

    public void setWordbookName(String wordbookName){
        this.wordbookName = wordbookName;
    }

      public String getWordbookName(){
        return wordbookName;
    }



}
