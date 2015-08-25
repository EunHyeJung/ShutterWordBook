package org.androidtown.shutterwordbook.Activity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TabHost;

import org.androidtown.shutterwordbook.Fragment.DeleteWordbookFragment;
import org.androidtown.shutterwordbook.Fragment.DictionaryFragment;
import org.androidtown.shutterwordbook.Fragment.SettingFragment;

import org.androidtown.shutterwordbook.Fragment.WordbookFragment;
import org.androidtown.shutterwordbook.R;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, WordbookFragment.AccidentListener , DialogInterface.OnDismissListener
                                                                           {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public ViewPager mViewPager;

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    String  newWordbookName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);                // 상단 타이틀바 없앰.

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
                Fragment fragment = ((SectionsPagerAdapter) mViewPager.getAdapter()).getFragment(position);
                if (position == 1 && fragment != null) {
                    fragment.onResume();

                }

            }
        });

        actionBar.addTab(
                actionBar.newTab()
                        .setText(mSectionsPagerAdapter.getPageTitle(0))
                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(mSectionsPagerAdapter.getPageTitle(1))

                        .setTabListener(this));
        actionBar.addTab(
                actionBar.newTab()
                        .setText(mSectionsPagerAdapter.getPageTitle(2))
                        .setTabListener(this));
     /*   //
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

<<<<<<< HEAD
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
=======
        //*/
    }



    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    // 다이얼로그가 dismiss 될때 호출됨.
    @Override
    public void onDismiss(DialogInterface dialog) {
        Fragment fragment = ((SectionsPagerAdapter )mViewPager.getAdapter()).getFragment(1);

        System.out.println("다이얼로그 dismiss 호출 확인");
        if(fragment != null){
            fragment.onResume();
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            mFragmentManager = fm;
            mFragmentTags = new HashMap<Integer, String>();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
         //   return PlaceholderFragment.newInstance(position + 1);

            Fragment currentFragment = null;

            switch(position)
            {
                case 0:
                    currentFragment = new DictionaryFragment();
                    break;
                case 1:
                        currentFragment = new WordbookFragment();
                    break;
                case 2:
                    currentFragment = new SettingFragment();
                    break;

            }

            return currentFragment;
      }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position){
            Object obj = super.instantiateItem(container, position);
                // fragment tag를 여기 기록
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            return obj;
        }



        public Fragment getFragment(int position){
            String tag = mFragmentTags.get(position);

            if(tag == null)
                return null;

            return mFragmentManager.findFragmentByTag(tag);
        }




        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
/*
         WordbookFragment와 통신
    *      ContentActivity에 보고자 하는 단어장의 이름을 전달하여
    *      ContentActivity에서 해당하는 단어장을 열어서 출력하도록 한다.
*/
    @Override
    public void showWordbook(String wordbookName){
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("wordbookName", wordbookName);
        startActivity(intent);
    }
}