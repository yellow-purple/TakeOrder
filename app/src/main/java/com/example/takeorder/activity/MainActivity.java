package com.example.takeorder.activity;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.takeorder.R;
import com.example.takeorder.fragment.TakeOrderFragment;
import com.example.takeorder.fragment.eCountFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        Toolbar toolbar = findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);

        View root = tabs.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.lightGray));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(40);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
        private final Context mContext;


        public SectionsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;


        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TakeOrderFragment takeOrderFragment = new TakeOrderFragment();
                    return takeOrderFragment;
                case 1:
                    eCountFragment eCountFragment = new eCountFragment();
                    return eCountFragment;
                default:
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }


}