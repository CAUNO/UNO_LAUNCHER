package com.example.yena.unolauncher;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final int TABLET_COLUMN_NUMBER = 8;
    private static final int PHONE_COLUMN_NUMBER = 4;
    private static final int TABLET_ROW_NUMBER = 4;
    private static final int PHONE_ROW_NUMBER = 5;

    private static final int LAYOUT_TITLE_WEIGHT = 1;
    private static final int LAYOUT_VIEWPAGER_WEIGHT = 8;
    private static final int LAYOUT_DOTS_WEIGHT = 1;

    private static final int MAIN_MODE = 0, DELETE_MODE = 1;

    private SharedPreferences pref;

    private ViewPager viewPager;
    private AppListPagerAdapter pagerAdapter;
    private LinearLayout llMain;
    private RelativeLayout rlTitle;
    private LinearLayout llPageIndicator;
    private ImageButton ibDelete;
    private List<AppListFragment> fragments = new ArrayList<AppListFragment>();

    private int dotsCount;
    private ImageView[] dots;

    private int displayWidth, displayHeight, iconSize;
    private int viewPagerWidth, viewPagerHeight;
    private int columnNumber, rowNumber, maxAppNumPerPage, pageCount;

    private int gridValue;

    private int mode;

    private List<ResolveInfo> pkgAppsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetAdapter();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mode == DELETE_MODE) {
                    mode = MAIN_MODE;
                    resetAdapter();
                }
        }
        return false;
    }

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

        GridSetting gridSetting = new GridSetting(getApplicationContext(),gridValue);
        columnNumber = gridSetting.numColumn;
        rowNumber = gridSetting.numRow;

        mode = MAIN_MODE;

        llMain = (LinearLayout) findViewById(R.id.ll_main);
        rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        viewPager = (ViewPager) findViewById(R.id.vp_main);
        llPageIndicator = (LinearLayout) findViewById(R.id.ll_count_dots);
        ibDelete = (ImageButton) findViewById(R.id.ib_delete);

        viewPager.addOnPageChangeListener(this);

        setLayoutWeight();
        setListener();
    }

    private void setListener() {
        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete:
                                if (mode == MAIN_MODE) {
                                    mode = DELETE_MODE;
                                } else if (mode == DELETE_MODE) {
                                    mode = MAIN_MODE;
                                }
                                resetAdapter();
                                return true;
                            case R.id.menu_item_background:

                                return true;
                            case R.id.menu_item_grid:
                                gridChange();
                                return true;
                        }
                        return false;
                    }
                });

                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
            }
        });
    }

    private void gridChange() {
        GridSetting gridSetting = new GridSetting(getApplicationContext(),gridValue);
        gridSetting.gridSelectDialog(MainActivity.this);
        if(gridValue != pref.getInt(UNOSharedPreferences.GRID_SETTING,GridSetting.GRID1)){
            Log.d("ㅁㄴㅇㄹ","여기");
            gridValue = pref.getInt(UNOSharedPreferences.GRID_SETTING,GridSetting.GRID1);
            gridSetting.setColumnRow(gridValue);
            columnNumber = gridSetting.numColumn;
            rowNumber = gridSetting.numRow;
            resetAdapter();
            Log.d("gridchange","여기");
        }
    }

    private void setLayoutWeight() {
        llMain.setWeightSum(LAYOUT_TITLE_WEIGHT + LAYOUT_VIEWPAGER_WEIGHT + LAYOUT_DOTS_WEIGHT);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, LAYOUT_TITLE_WEIGHT);
        layoutParams.setMargins((int) (0.05 * displayWidth), 0, (int) (0.05 * displayWidth), 0);
        rlTitle.setLayoutParams(layoutParams);
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
    }

    private void calculateSize() {
        if (isTablet()) {
            columnNumber = TABLET_COLUMN_NUMBER;
            rowNumber = TABLET_ROW_NUMBER;
        } else {
            columnNumber = PHONE_COLUMN_NUMBER;
            rowNumber = PHONE_ROW_NUMBER;
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        displayHeight = displaymetrics.heightPixels / (int) getResources().getDisplayMetrics().density;
        displayWidth = displaymetrics.widthPixels / (int) getResources().getDisplayMetrics().density;

        double rate = (double) LAYOUT_VIEWPAGER_WEIGHT / (double) (LAYOUT_TITLE_WEIGHT + LAYOUT_VIEWPAGER_WEIGHT + LAYOUT_DOTS_WEIGHT);
        viewPagerWidth = displayWidth;
        viewPagerHeight = (int) (displayHeight * rate);
        Log.d("rate", rate + "");
        if (displayWidth >= displayHeight) {
            iconSize = (int) (0.6 * viewPagerHeight / (rowNumber));
            Log.d("display calculate", "displayWidth > displayHeight");
        } else {
            iconSize = viewPagerWidth / (columnNumber);
            Log.d("display calculate", "displayHeight > displayWidth");
        }
        Log.d("displayWidth", displayWidth + "");
        Log.d("displayHeight", displayHeight + "");
        Log.d("columnNumber", columnNumber + "");
    }

    private boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private boolean isTablet() {
        int portrait_width_pixel = Math.min(this.getResources().getDisplayMetrics().widthPixels, this.getResources().getDisplayMetrics().heightPixels);
        int dots_per_virtual_inch = this.getResources().getDisplayMetrics().densityDpi;
        float virutal_width_inch = portrait_width_pixel / dots_per_virtual_inch;
        if (virutal_width_inch <= 2) {
            //is phone
            Log.d("Device", "Phone");
            return false;
        } else {
            //is tablet
            Log.d("Device", "Tablet");
            return true;
        }
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