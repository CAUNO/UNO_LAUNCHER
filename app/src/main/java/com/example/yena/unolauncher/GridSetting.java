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

    private int selectedGrid;

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

    void gridSelectDialog(final Context context) {
        final String items[] = {context.getString(R.string.grid1), context.getString(R.string.grid2),
                context.getString(R.string.grid3), context.getString(R.string.grid4)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.grid_dialog_title));
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectedGrid = whichButton;
                    }

                }).setPositiveButton(context.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences pref;
                        pref = context.getSharedPreferences(UNOSharedPreferences.NAME,0);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt(UNOSharedPreferences.GRID_SETTING,selectedGrid);
                        editor.commit();
                        dialog.cancel();
                    }
                }).setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    void setGrid(int radioId) {
        switch (radioId) {
            case R.id.rb_grid1:
                numColumn = 8;
                numRow = 4;
                break;
            case R.id.rb_grid2:
                numColumn = 4;
                numRow = 3;
                break;
            case R.id.rb_grid3:
                numColumn = 10;
                numRow = 4;
                break;
            case R.id.rb_grid4:
                numColumn = 5;
                numRow = 4;
                break;
            default:
                numColumn = 8;
                numRow = 4;
        }
    }
}
