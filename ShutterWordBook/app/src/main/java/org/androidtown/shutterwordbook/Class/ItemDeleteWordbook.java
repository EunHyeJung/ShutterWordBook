package org.androidtown.shutterwordbook.Class;

/**
 * Created by ehye on 2015-08-23.
 */
public class ItemDeleteWordbook {
    String wordbookName=null;
    boolean checked = false;

    public ItemDeleteWordbook(String wordbookName, boolean checked){
        super();
        this.wordbookName = wordbookName;
        this.checked = checked;
    }
    public String getWordbookName(){
        return wordbookName;
    }
    public void setWordbookName(String wordbookName){
        this.wordbookName = wordbookName;
    }
    public boolean isChecked(){
        return checked;
    }
    public void setChecked(boolean checked){
        this.checked = checked;
    }


}
