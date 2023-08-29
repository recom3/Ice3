package com.recom3.snow3;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.recom3.snow3.activity.buddy.BuddiesTabFragment;

/**
 * Created by Recom3 on 05/07/2022.
 */

public class SectionsPagerAdapter extends FragmentStatePagerAdapter implements FirstPageFragmentListener {

    Drawable myDrawable;
    String title;
    Context mContext;
    private boolean mBuddyList = false;

    public static FragmentManager mFragmentManager;
    Fragment mFragmentAtPos0 = null;

    private BuddyEnableCallback mBuddyActivateCallback;

    private int[] imageResId = {
            R.drawable.tab_icon_buddies_active,
            R.drawable.tab_icon_trips_active,
            R.drawable.tab_icon_pairing_active
    };

    public SectionsPagerAdapter(FragmentManager fm, Context context, BuddyEnableCallback buddyActivateCallback) {
        super(fm);
        mFragmentManager = fm;
        mContext = context;
        mBuddyActivateCallback = buddyActivateCallback;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 1:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return new GreenFragment();

            case 2:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return new BlueFragment();

            default:
                // The other sections of the app are dummy placeholders.
                /*
                if (mFragmentAtPos0 == null)
                {
                    mFragmentAtPos0 = BuddiesListFragment.instantiate(new RedFragment(mBuddyActivateCallback)
                    {
                        public void onSwitchToNextFragment()
                        {
                            mFragmentManager.beginTransaction().remove(mFragmentAtPos0).commit();
                            mFragmentAtPos0 = NextFragment.newInstance();
                            notifyDataSetChanged();
                        }
                    });
                }
                */
                if (mFragmentAtPos0 == null) {
                    mFragmentAtPos0 = new RedFragment(mBuddyActivateCallback);
                }
                return mFragmentAtPos0;        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        /*
        switch (position) {
            case 0: return "First view";//getString(R.string.title_section1).toUpperCase();
            case 1: return "Second view";//getString(R.string.title_section1).toUpperCase();
            case 2: return "Third view";//getString(R.string.title_section1).toUpperCase();
        }
        */

        //https://stackoverflow.com/questions/30892545/tablayout-with-icons-only
        Drawable image = mContext.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
        //return null;
    }

    //https://stackoverflow.com/questions/7723964/replace-fragment-inside-a-viewpager
    @Override
    public int getItemPosition(Object object)
    {
        //if(mBuddyList) {
        //    if (object instanceof RedFragment)
        //        return POSITION_NONE;
        //}
        if (object instanceof RedFragment && mFragmentAtPos0 instanceof BuddiesTabFragment)
            return POSITION_NONE;
            //((RedFragment) object).notify();
        //return POSITION_UNCHANGED;
        //don't return POSITION_NONE, avoid fragment recreation.
        return super.getItemPosition(object);
    }

    /**
     * Trying to load trip details
     * @param activate
     * @return
     */
    public boolean setBuddiesListFragment(boolean activate)
    {
        if(activate)
        {
            mFragmentManager.beginTransaction().remove(mFragmentAtPos0).commit();
            mFragmentAtPos0 = new BuddiesTabFragment();
            notifyDataSetChanged();
        }

        return mBuddyList;
    }

    public boolean setTripDetailFragment(boolean activate)
    {
        if(activate)
        {
            mFragmentManager.beginTransaction().remove(mFragmentAtPos0).commit();
            mFragmentAtPos0 = new BuddiesTabFragment();
            notifyDataSetChanged();
        }

        return activate;
    }

    @Override
    public void onSwitchToNextFragment() {
        mFragmentManager.beginTransaction().remove(mFragmentAtPos0).commit();
        mFragmentAtPos0 = new BuddiesTabFragment();
        notifyDataSetChanged();
    }
}
