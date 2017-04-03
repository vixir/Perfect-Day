package com.vixir.finalproject.perfectday;

import android.app.ActionBar;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if (null != actionBar) actionBar.hide();
        UpdateProgressUtilities.scheduleUpdateProgressReminder(this);
        mBottomBar.setOnTabSelectListener(
                new OnTabSelectListener() {
                    @Override
                    public void onTabSelected(@IdRes int tabId) {
                        Fragment fragment = null;
                        Class fragmentClass;
                        switch (tabId) {
                            case R.id.bot_bar_today:
                                fragmentClass = TodayTasksFragment.class;
                                break;
                            case R.id.bot_bar_list:
                                fragmentClass = ListTasksFragment.class;
                                break;
                            case R.id.bot_bar_more:
                                fragmentClass = MoreInfoFragment.class;
                                break;
                            default:
                                fragmentClass = TodayTasksFragment.class;
                        }
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
