package com.vixir.finalproject.perfectday.activities;

import android.app.ActionBar;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.vixir.finalproject.perfectday.R;
import com.vixir.finalproject.perfectday.fragment.ListTasksFragment;
import com.vixir.finalproject.perfectday.fragment.MoreInfoFragment;
import com.vixir.finalproject.perfectday.fragment.TodayTasksFragment;
import com.vixir.finalproject.perfectday.utils.UpdateProgressUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.bottomBar)
    BottomBar mBottomBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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
        mBottomBar.setOnTabSelectListener(
                new OnTabSelectListener() {
                    @Override
                    public void onTabSelected(@IdRes int tabId) {
                        Fragment fragment;
                        switch (tabId) {
                            case R.id.bot_bar_today:
                                fragment = new TodayTasksFragment();
                                break;
                            case R.id.bot_bar_list:
                                fragment = new ListTasksFragment();
                                break;
                            case R.id.bot_bar_more:
                                fragment = new MoreInfoFragment();
                                break;
                            default:
                                fragment = new TodayTasksFragment();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.replace(R.id.flContent, fragment, fragment.getClass().getName());
                        ft.commit();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
