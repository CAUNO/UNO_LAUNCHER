package com.example.yena.unolauncher;

import android.content.Context;

/**
 * Created by yena on 2016-07-15.
 */
public class ThemeSetting {
    public static final int THEME1 = 0;
    public static final int THEME2 = 1;
    public static final int THEME3 = 2;

    private Context context;

    public ThemeSetting(){
    }

    public ThemeSetting(Context context){
        this.context = context;
    }

    int getThemeResource(int themeValue){
        switch (themeValue){
            case THEME1 :
                return R.drawable.theme_orange;
            case THEME2 :
                return R.drawable.theme_blue;
            case THEME3 :
                return R.drawable.theme_green;
            default:
                return R.drawable.theme_orange;
        }
    }
}
