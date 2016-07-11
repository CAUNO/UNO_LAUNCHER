package com.example.yena.unolauncher;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

    private static final double PADDING_RATE = 0.05;

    private ViewPager viewPager;
    private AppListPagerAdapter pagerAdapter;
    private LinearLayout llMain;
    private RelativeLayout rlTitle;
    private LinearLayout llPageIndicator;
    private List<AppListFragment> fragments = new ArrayList<AppListFragment>();

    private int dotsCount;
    private ImageView[] dots;

    private int layoutWidth, layoutHeight, iconSize;
    private int columnNumber, rowNumber, maxAppNumPerPage, pageCount;

    private List<ResolveInfo>pkgAppsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        calculateSize();
//        linearLayout.setPadding(calculatePadding(layoutWidth),calculatePadding(layoutHeight),
//                calculatePadding(layoutWidth),calculatePadding(layoutHeight));

        if(isTablet()){
            columnNumber = TABLET_COLUMN_NUMBER;
            rowNumber = TABLET_ROW_NUMBER;
        } else{
            columnNumber = PHONE_COLUMN_NUMBER;
            rowNumber = PHONE_ROW_NUMBER;
        }

        maxAppNumPerPage = columnNumber * rowNumber;

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);

        if(pkgAppsList.size() % maxAppNumPerPage == 0){
            pageCount = (pkgAppsList.size() / maxAppNumPerPage);
        } else{
            pageCount = (pkgAppsList.size() / maxAppNumPerPage) + 1;
        }
        createFragments();


        viewPager.addOnPageChangeListener(this);
        pagerAdapter = new AppListPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        setUIPageViewController();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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

        dotsCount = pagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            llPageIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    private void init(){
        llMain = (LinearLayout)findViewById(R.id.ll_main);
        rlTitle = (RelativeLayout)findViewById(R.id.rl_title);
        viewPager = (ViewPager) findViewById(R.id.vp_main);
        llPageIndicator = (LinearLayout)findViewById(R.id.ll_count_dots);

        setLayoutWeight();
    }

    private void setLayoutWeight(){
        llMain.setWeightSum(LAYOUT_TITLE_WEIGHT + LAYOUT_VIEWPAGER_WEIGHT + LAYOUT_DOTS_WEIGHT);

        rlTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, LAYOUT_TITLE_WEIGHT));
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, LAYOUT_VIEWPAGER_WEIGHT));
        llPageIndicator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, LAYOUT_DOTS_WEIGHT));

    }

    private void calculateSize(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        layoutHeight = displaymetrics.heightPixels / (int)getResources().getDisplayMetrics().density ;
        layoutWidth = displaymetrics.widthPixels / (int)getResources().getDisplayMetrics().density ;
        double rate = (double)LAYOUT_VIEWPAGER_WEIGHT / (double)(LAYOUT_TITLE_WEIGHT+LAYOUT_VIEWPAGER_WEIGHT+LAYOUT_DOTS_WEIGHT);
        Log.d("rate",rate+"");
        if(layoutWidth >= layoutHeight){
            iconSize = (int)(rate * layoutHeight / (columnNumber+1));
        } else{
            iconSize = (int)(rate * layoutWidth / (columnNumber+1));
        }
    }

    private boolean isTablet(){
        int portrait_width_pixel=Math.min(this.getResources().getDisplayMetrics().widthPixels, this.getResources().getDisplayMetrics().heightPixels);
        int dots_per_virtual_inch=this.getResources().getDisplayMetrics().densityDpi;
        float virutal_width_inch=portrait_width_pixel/dots_per_virtual_inch;
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

    private void createFragments(){
        Log.d("createFragments","시작");
        for(int i=0; i<pageCount; i++){
            List<ResolveInfo> apps;
            if(i == pageCount - 1){
                apps = pkgAppsList.subList(i*maxAppNumPerPage,pkgAppsList.size());
            }else{
                apps = pkgAppsList.subList(i*maxAppNumPerPage,(i+1)*maxAppNumPerPage);
            }
            fragments.add(new AppListFragment(apps, iconSize, columnNumber));
        }
    }

    private int calculatePadding(int length){
        int padding = (int)(PADDING_RATE * length);
        return padding;
    }
}
