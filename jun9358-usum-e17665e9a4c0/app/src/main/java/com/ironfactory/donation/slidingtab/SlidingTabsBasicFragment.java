package com.ironfactory.donation.slidingtab;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.LoginActivity;

import java.util.ArrayList;

public class SlidingTabsBasicFragment extends Fragment {
    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    private ArrayList<SlidingBaseFragment> mTabFragments;
    private ArrayList<String> mTabTitles;

    public void setTabFragments(ArrayList<SlidingBaseFragment> tabFragments) {
        mTabFragments = tabFragments;
    }

    public void setTabTitles(ArrayList<String> tabTitles) {
        mTabTitles = tabTitles;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sliding_tabs_basic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SlidingFragmentPagerAdapter(getFragmentManager(), mTabFragments));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.parseColor("#1abc9c"));
        mSlidingTabLayout.setBackgroundColor(Color.parseColor("#34495e"));
        mSlidingTabLayout.setDividerColors(Color.parseColor("#527394"));
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTabFragments.get(position).onPageSelected();
            }

            @Override
            public void onPageSelected(int position) {
                mTabFragments.get(position).onPageSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SlidingFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<SlidingBaseFragment> fragments;

        public SlidingFragmentPagerAdapter(FragmentManager fm, ArrayList<SlidingBaseFragment> fragments) {
            super(fm);

            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            if (fragments == null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return 0;
            } else {
                return fragments.size();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }
}