package com.gob.etisbewcommunicator;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity {

    CollectionPagerAdapter collectionPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.mainPager);
        viewPager.setAdapter(collectionPagerAdapter);
    }
}
