package com.easyschools.teacher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainTabsActivity extends AppCompatActivity {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;
    private Boolean exit = false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabs);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentManager = getSupportFragmentManager();
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        fragment = new HomePage();
                        break;
                    case R.id.nav_messages:
                        fragment = new Messages();
                        break;
                    case R.id.nav_notify:
                        fragment = new Notification();
                        break;
                    case R.id.nav_my_profile:
                        fragment = new MyProfile();
                        break;
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction().addToBackStack(null);
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
        navigation.setSelectedItemId(R.id.nav_home);
        BottomNavigationViewHelper.removeShiftMode(navigation);
    }

    @Override
    public void onBackPressed() {
        FrameLayout drawer = (FrameLayout) findViewById(R.id.main_container);
        int backstack = getSupportFragmentManager().getBackStackEntryCount();
        HomePage homePage = new HomePage();
        if (backstack > 0) {
            for (int i = 0; i < backstack; i++) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, homePage).commit();
            }
        } else {
            this.finish();
        }


    }
}
