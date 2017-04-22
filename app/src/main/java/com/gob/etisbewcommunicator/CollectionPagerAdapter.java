package com.gob.etisbewcommunicator;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CollectionPagerAdapter extends FragmentStatePagerAdapter {

    private static final int numberOfFragments = 4;
    private Fragment[] fragments;
    private String[] pageTitles;

    public CollectionPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);

        fragments = new Fragment[numberOfFragments];
        pageTitles = new String[numberOfFragments];

        fragments[0] = new CreateSensor();
        pageTitles[0] = "Create Sensor";

        fragments[1] = new SensorReadingGenerator();
        pageTitles[1] = "Reading Generator";

        for (int i = 2; i < numberOfFragments; i++) {
            fragments[i] = new ObjectFragment();
            Bundle args = new Bundle();
            args.putInt(ObjectFragment.ARG_OBJECT, i + 1);
            fragments[i].setArguments((args));
            pageTitles[i] = "Object" + i;
        }
    }

    @Override
    public Fragment getItem(int i) {
        return fragments[i];
    }

    @Override
    public int getCount() {
        return numberOfFragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
