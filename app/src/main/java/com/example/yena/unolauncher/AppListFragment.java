package com.example.yena.unolauncher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class AppListFragment extends Fragment {

    private List<ResolveInfo> apps;
    private int iconSize;
    private int columnNumber;
    private int rowNumber;
    private int viewPagerWidth;
    private int viewPagerHeight;
    private boolean deleteBadgeViewVisibility;
    private GridView gridView;

    public AppListFragment() {

    }

    @SuppressLint("ValidFragment")
    public AppListFragment(List<ResolveInfo> apps, int iconSize, int columnNumber) {
        this.apps = apps;
        this.iconSize = iconSize;
        this.columnNumber = columnNumber;
    }

    @SuppressLint("ValidFragment")
    public AppListFragment(List<ResolveInfo> apps, int iconSize, int columnNumber, int rowNumber, int viewPagerWidth, int viewPagerHeight) {
        this.apps = apps;
        this.iconSize = iconSize;
        this.columnNumber = columnNumber;
        this.rowNumber = rowNumber;
        this.viewPagerWidth = viewPagerWidth;
        this.viewPagerHeight = viewPagerHeight;
        this.deleteBadgeViewVisibility = false;
    }

    @SuppressLint("ValidFragment")
    public AppListFragment(List<ResolveInfo> apps, int iconSize, int columnNumber, int rowNumber, int viewPagerWidth, int viewPagerHeight, boolean deleteBadgeViewVisibility){
        this.apps = apps;
        this.iconSize = iconSize;
        this.columnNumber = columnNumber;
        this.rowNumber = rowNumber;
        this.viewPagerWidth = viewPagerWidth;
        this.viewPagerHeight = viewPagerHeight;
        this.deleteBadgeViewVisibility = deleteBadgeViewVisibility;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        gridView = (GridView) view.findViewById(R.id.gv_app_list);
        gridView.setColumnWidth(iconSize);
        gridView.setNumColumns(columnNumber);
        gridView.setAdapter(new MainBaseAdapter(getActivity().getApplicationContext(), apps));
        gridView.setOnItemClickListener(onItemClickListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ResolveInfo clickedResolveInfo = (ResolveInfo) parent.getItemAtPosition(position);
            ActivityInfo clickedActivityInfo = clickedResolveInfo.activityInfo;

            if(deleteBadgeViewVisibility){
                Uri packageURI = Uri.parse("package:" + clickedResolveInfo.resolvePackageName);
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(uninstallIntent);
            } else{
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName(clickedActivityInfo.applicationInfo.packageName, clickedActivityInfo.name);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }

        }
    };

    public class MainBaseAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private Context context;
        private List<ResolveInfo> appList;

        MainBaseAdapter(Context context, List<ResolveInfo> appList) {
            this.context = context;
            this.appList = appList;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int position) {
            return appList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ResolveInfo resolveInfo = appList.get(position);
            View view;

            if (convertView == null) {
                view = inflater.inflate(R.layout.item, parent, false);
                Log.d("AppListFragment", "convertView is null");
            } else {
                view = convertView;
            }
            RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.ll_item);
            ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) relativeLayout.getLayoutParams();
            lp.width = (int) (viewPagerWidth / columnNumber);
            lp.height = (int) (viewPagerHeight / rowNumber);
            Log.d("lp","width : "+lp.width);
            Log.d("lp","height : "+lp.height);
            relativeLayout.setLayoutParams(lp);

            ImageView imageView = (ImageView) view.findViewById(R.id.iv_item);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) imageView.getLayoutParams();
            params.width = iconSize;
            params.height = iconSize;
            imageView.setLayoutParams(params);
            TextView textView = (TextView) view.findViewById(R.id.tv_item);
            imageView.setImageDrawable(resolveInfo.loadIcon(getActivity().getPackageManager()));
            textView.setText(resolveInfo.loadLabel(getActivity().getPackageManager()).toString());

            if(deleteBadgeViewVisibility){
                BadgeView badgeView = new BadgeView(getActivity().getApplicationContext(), imageView);
                badgeView.setText("x");
                badgeView.show();
            }

            return view;
        }

        @Override
        public int getCount() {
            return appList.size();
        }
    }
}