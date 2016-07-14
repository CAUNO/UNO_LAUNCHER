package com.example.yena.unolauncher;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.widget.GridView;

/**
 * Created by yena on 2016-07-14.
 */
public class GridSetting {

    public static final int GRID1 = 0;
    public static final int GRID2 = 1;
    public static final int GRID3 = 2;
    public static final int GRID4 = 3;

    int numColumn, numRow;
    Context context;

    public GridSetting(){
    }

    public GridSetting(Context context, int gridValue){
        this.context = context;
        setColumnRow(gridValue);
    }

    void setColumnRow(int gridValue) {
        switch (gridValue) {
            case GRID1:
                numColumn = 8;
                numRow = 4;
                break;
            case GRID2:
                numColumn = 4;
                numRow = 3;
                break;
            case GRID3:
                numColumn = 10;
                numRow = 4;
                break;
            case GRID4:
                numColumn = 5;
                numRow = 4;
                break;
            default:
                numColumn = 8;
                numRow = 4;
        }
    }
}
