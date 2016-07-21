package com.example.yena.unolauncher;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final int LAYOUT_TITLE_WEIGHT = 1;
    private static final int LAYOUT_VIEWPAGER_WEIGHT = 8;
    private static final int LAYOUT_DOTS_WEIGHT = 1;

    private static final int MAIN_MODE = 0, DELETE_MODE = 1;
    public static final int IS_IN_FOREGROUND = 0, IS_IN_BACKGROUND = 1;

    private SharedPreferences pref;

    private ViewPager viewPager;
    private AppListPagerAdapter pagerAdapter;
    private LinearLayout llMain;
    private RelativeLayout rlTitle;
    private LinearLayout llPageIndicator;
    private ToggleButton tbMenu;
    private List<AppListFragment> fragments = new ArrayList<AppListFragment>();

    private int dotsCount;
    private ImageView[] dots;

    private int displayWidth, displayHeight, iconSize;
    private int viewPagerWidth, viewPagerHeight;
    private int columnNumber, rowNumber, maxAppNumPerPage, pageCount;

    private int gridValue, themeValue;
    private int selectedGrid, selectedTheme;

    private int mode;
    private boolean isInForeground;

    private List<ResolveInfo> pkgAppsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        resetAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetAdapter();
        isInForeground = true;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(UNOSharedPreferences.IS_FOREGROUND, isInForeground());
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(UNOSharedPreferences.IS_FOREGROUND,isInForeground());
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mode == DELETE_MODE) {
                    mode = MAIN_MODE;
                    resetAdapter();
                }
                break;
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            if (event.getAction() == KeyEvent.ACTION_UP){

                tbMenu.performClick();
                return true;
            }}
        return super.dispatchKeyEvent(event);
    };

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    private void setUIPageViewController() {
        llPageIndicator.removeAllViews();
        dotsCount = pagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins((int) (0.01 * displayWidth), 0, (int) (0.01 * displayWidth), 0);

            llPageIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    private void init() {

        calculateSize();

        pref = this.getSharedPreferences(UNOSharedPreferences.NAME, 0);
        gridValue = pref.getInt(UNOSharedPreferences.GRID_SETTING, GridSetting.GRID1);
        themeValue = pref.getInt(UNOSharedPreferences.THEME_SETTING,ThemeSetting.THEME1);

        GridSetting gridSetting = new GridSetting(getApplicationContext(),gridValue);
        columnNumber = gridSetting.numColumn;
        rowNumber = gridSetting.numRow;

        mode = MAIN_MODE;

        llMain = (LinearLayout) findViewById(R.id.ll_main);
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        viewPager = (ViewPager) findViewById(R.id.vp_main);
        llPageIndicator = (LinearLayout) findViewById(R.id.ll_count_dots);
        tbMenu = (ToggleButton)findViewById(R.id.tb_menu);

        viewPager.addOnPageChangeListener(this);

        setIconSize();
        setLayoutWeight();
        setListener();
        setTheme();
    }

    private void setListener() {
        tbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog();
            }
        });
    }

    private void gridChange() {
        final String items[] = {getString(R.string.grid1), getString(R.string.grid2),
                getString(R.string.grid3), getString(R.string.grid4)};
        selectedGrid = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.grid_dialog_title));
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectedGrid = whichButton;
                    }
                }).setPositiveButton(getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(gridValue != selectedGrid){
                            gridValue = selectedGrid;
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt(UNOSharedPreferences.GRID_SETTING, gridValue);
                            editor.commit();
                            GridSetting gridSetting = new GridSetting(getApplicationContext(),gridValue);
                            gridSetting.setColumnRow(gridValue);
                            columnNumber = gridSetting.numColumn;
                            rowNumber = gridSetting.numRow;
                            setIconSize();
                            resetAdapter();
                        }
                        dialog.cancel();
                    }
                }).setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void themeChange() {
        final String items[] = {getString(R.string.theme1), getString(R.string.theme2),
                getString(R.string.theme3)};
        selectedTheme = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.theme_dialog_title));
        builder.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        selectedTheme = whichButton;
                    }
                }).setPositiveButton(getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(themeValue != selectedTheme){
                            themeValue = selectedTheme;
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt(UNOSharedPreferences.THEME_SETTING, themeValue);
                            editor.commit();
                            setTheme();
                        }
                        dialog.cancel();
                    }
                }).setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    private void setTheme(){
        ThemeSetting themeSetting = new ThemeSetting(getApplicationContext());
        llMain.setBackgroundResource(themeSetting.getThemeResource(themeValue));
    }

    private void showMenuDialog(){
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent_white);
        dialog.setContentView(R.layout.dialog_menu);

        ImageButton ibDelete = (ImageButton)dialog.findViewById(R.id.ib_menu_delete);
        ImageButton ibTheme = (ImageButton)dialog.findViewById(R.id.ib_menu_background);
        ImageButton ibGrid = (ImageButton)dialog.findViewById(R.id.ib_menu_grid);

        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == MAIN_MODE) {
                    mode = DELETE_MODE;
                } else if (mode == DELETE_MODE) {
                    mode = MAIN_MODE;
                }
                resetAdapter();
            }
        });

        ibTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themeChange();
            }
        });

        ibGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridChange();
            }
        });

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int)(displayWidth / 6);
        params.height = (int)((params.width / 3) *1.2);
        params.x = tbMenu.getLeft();
        params.y = rlTitle.getHeight();
        params.gravity = Gravity.TOP | Gravity.LEFT;
        dialog.getWindow().setAttributes(params);
        dialog.show();
    }

    private void setLayoutWeight() {
        llMain.setWeightSum(LAYOUT_TITLE_WEIGHT + LAYOUT_VIEWPAGER_WEIGHT + LAYOUT_DOTS_WEIGHT);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, LAYOUT_TITLE_WEIGHT);
//        layoutParams.setMargins((int) (0.02 * displayWidth), 0, (int) (0.02 * displayWidth), 0);
        rlTitle.setLayoutParams(layoutParams);
        ViewGroup.MarginLayoutParams menuMarginParams = new ViewGroup.MarginLayoutParams(tbMenu.getLayoutParams());
        menuMarginParams.setMargins((int)(0.02*displayWidth), 0, 0, 0);
        tbMenu.setLayoutParams(new RelativeLayout.LayoutParams(menuMarginParams));
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, LAYOUT_VIEWPAGER_WEIGHT));
        llPageIndicator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, LAYOUT_DOTS_WEIGHT));

    }

    private void resetAdapter() {
        maxAppNumPerPage = columnNumber * rowNumber;

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        createFragments(mode, getPackageManager().queryIntentActivities(intent, 0));

        pagerAdapter = new AppListPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        setUIPageViewController();

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fade);
        llMain.startAnimation(animation);
    }

    private void setIconSize(){
        if (displayWidth >= displayHeight) {
            iconSize = (int) (0.6 * viewPagerHeight / (rowNumber));
            Log.d("display calculate", "displayWidth > displayHeight");
        } else {
            iconSize = viewPagerWidth / (columnNumber);
            Log.d("display calculate", "displayHeight > displayWidth");
        }
    }

    private void calculateSize() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        displayHeight = displaymetrics.heightPixels / (int) getResources().getDisplayMetrics().density;
        displayWidth = displaymetrics.widthPixels / (int) getResources().getDisplayMetrics().density;

        double rate = (double) LAYOUT_VIEWPAGER_WEIGHT / (double) (LAYOUT_TITLE_WEIGHT + LAYOUT_VIEWPAGER_WEIGHT + LAYOUT_DOTS_WEIGHT);
        viewPagerWidth = displayWidth;
        viewPagerHeight = (int) (displayHeight * rate);
        Log.d("rate", rate + "");
        Log.d("displayWidth", displayWidth + "");
        Log.d("displayHeight", displayHeight + "");
        Log.d("columnNumber", columnNumber + "");
    }

    private int isInForeground(){
        if(isInForeground){
            return IS_IN_FOREGROUND;
        } else{
            return IS_IN_BACKGROUND;
        }
    }

    private boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private void createFragments(int mode, List<ResolveInfo> allPkgAppsList) {
        pkgAppsList.clear();
        fragments.clear();

        if (mode == MAIN_MODE) {
            pkgAppsList = allPkgAppsList;
        } else if (mode == DELETE_MODE) {
            for (int i = 0; i < allPkgAppsList.size(); i++) {
                if (!isSystemPackage(allPkgAppsList.get(i))) {
                    pkgAppsList.add(allPkgAppsList.get(i));
                }
            }
        } else {
            Log.d("createFragments", "mode is not correct");
        }

        for (int i = 0; i < pkgAppsList.size(); i++) {
            if (pkgAppsList.get(i).loadLabel(getPackageManager()).toString().equals(getResources().getString(R.string.app_name))) {
                pkgAppsList.remove(i);
            }
        }

        if (pkgAppsList.size() % maxAppNumPerPage == 0) {
            pageCount = (pkgAppsList.size() / maxAppNumPerPage);
        } else {
            pageCount = (pkgAppsList.size() / maxAppNumPerPage) + 1;
        }

        for (int i = 0; i < pageCount; i++) {
            List<ResolveInfo> apps;
            if (i == pageCount - 1) {
                apps = pkgAppsList.subList(i * maxAppNumPerPage, pkgAppsList.size());
            } else {
                apps = pkgAppsList.subList(i * maxAppNumPerPage, (i + 1) * maxAppNumPerPage);
            }
            if (mode == MAIN_MODE) {
                fragments.add(new AppListFragment(apps, iconSize, columnNumber, rowNumber, viewPagerWidth, viewPagerHeight));
            } else if (mode == DELETE_MODE) {
                fragments.add(new AppListFragment(apps, iconSize, columnNumber, rowNumber, viewPagerWidth, viewPagerHeight, true));
            }
        }
    }

}