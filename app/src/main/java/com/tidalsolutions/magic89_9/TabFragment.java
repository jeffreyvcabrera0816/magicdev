package com.tidalsolutions.magic89_9;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 5 ;
    public boolean isZero = false;
    android.support.v7.widget.Toolbar toolbar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);
        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);


//  viewPager.setOffscreenPageLimit(4);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        final int[] ICONS = new int[] {
                R.mipmap.icn512_radio_gray,
                R.mipmap.icn512_activities_gray,
                R.mipmap.icn512_forums_gray,
                R.mipmap.icn512_poll_gray,
                R.mipmap.icn512_bio_gray,
        };

        final int[] ICONS_SELECTED = new int[] {
                R.mipmap.icn512_radio,
                R.mipmap.icn512_activities,
                R.mipmap.icn512_forums,
                R.mipmap.icn512_poll,
                R.mipmap.icn512_bio,
        };

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    tabLayout.getTabAt(i).setIcon(ICONS[i]);
                }
                if (tabLayout.getTabCount() > 0) {
                    tabLayout.getTabAt(0).setIcon(ICONS_SELECTED[0]);
                }
            }
        });
        toolbar.setTitle("Radio");

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    toolbar.setTitle("Radio");
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(position).setIcon(ICONS_SELECTED[position]);
                    }
                } else {
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(0).setIcon(ICONS[0]);
                    }
                }

                if (position == 1) {
                    toolbar.setTitle("Activities");
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(position).setIcon(ICONS_SELECTED[position]);
                    }
                } else {
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(1).setIcon(ICONS[1]);
                    }
                }

                if (position == 2) {
                    toolbar.setTitle("Forums");
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(position).setIcon(ICONS_SELECTED[position]);
                    }
                } else {
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(2).setIcon(ICONS[2]);
                    }
                }

                if (position == 3) {
                    toolbar.setTitle("Polls");
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(position).setIcon(ICONS_SELECTED[position]);
                    }
                } else {
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(3).setIcon(ICONS[3]);
                    }
                }

                if (position == 4) {
                    toolbar.setTitle("Bio");
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(position).setIcon(ICONS_SELECTED[position]);
                    }
                } else {
                    if (tabLayout.getTabCount() > 0) {
                        tabLayout.getTabAt(4).setIcon(ICONS[4]);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        return x;


    }
    //OR FragmentPagerAdapter
    class MyAdapter extends FragmentStatePagerAdapter {
        ProgressDialog pd;
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new FavoritesFragment();
                case 1 : return new HomeFragment();
                case 2 : return new ForumFragment();
                case 3 : return new PollsFragment();
                case 4 : return new BioFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            return null;
        }
    }

}

