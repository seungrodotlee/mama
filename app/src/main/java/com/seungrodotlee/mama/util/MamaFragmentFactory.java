package com.seungrodotlee.mama.util;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.view.calendar.CalendarFragment;
import com.seungrodotlee.mama.view.calendar.WholeTimelineFragment;
import com.seungrodotlee.mama.view.home.HomeFragment;
import com.seungrodotlee.mama.view.marker.MarkerFragment;
import com.seungrodotlee.mama.view.portfolio.PortfolioFragment;
import com.seungrodotlee.mama.view.setting.AccountSettingFragment;
import com.seungrodotlee.mama.view.setting.SettingFragment;

import java.util.ArrayList;

public class MamaFragmentFactory {
    public static String[] fragmentTitles = {"home", "calendar", "marker", "portfolio", "setting", "account_setting", "whole_timeline"};
    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_CALENDAR = 1;
    public static final int FRAGMENT_MARKER = 2;
    public static final int FRAGMENT_PORTFOLIO = 3;
    public static final int FRAGMENT_SETTING = 4;
    public static final int FRAGMENT_ACCOUNT = 5;
    public static final int FRAGMENT_WHOLE_TIMELINE = 6;

    public static final int USE_INSTANTLY = 0;
    public static final int USE_LATER = 1;
    private static MamaFragmentFactory instance;

    private FragmentManager fragmentManager;

    private ArrayList<MamaFragment> fragments = new ArrayList<MamaFragment>();
    private Fragment lastFragment;

    private Context context;

    int lastFragmentIndex = 0;

    private MamaFragmentFactory(Context context) {
        this.context = context;
    }

    public static synchronized MamaFragmentFactory getInstance(Context context) {
        if (null == instance) {
            instance = new MamaFragmentFactory(context);
        }
        return instance;
    }

    public static synchronized MamaFragmentFactory getInstance() {
        return instance;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        if (this.fragmentManager == null) {
            this.fragmentManager = fragmentManager;
        }
    }

    public FragmentManager getFragmentManage() {
        return fragmentManager;
    }

    public MamaFragment getFragment(int selector) {
        MamaFragment f;

        int layoutID = context.getResources().getIdentifier("fragment_" + fragmentTitles[selector], "layout", context.getPackageName());
        int menuID = context.getResources().getIdentifier("nav_menu_" + fragmentTitles[selector], "id", context.getPackageName());

        switch (selector) {
            case FRAGMENT_HOME:
                f = new HomeFragment(layoutID, menuID);
                break;
            case FRAGMENT_CALENDAR:
                f = new CalendarFragment(layoutID, menuID);
                break;
            case FRAGMENT_MARKER:
                f = new MarkerFragment(layoutID, menuID);
                break;
            case FRAGMENT_PORTFOLIO:
                f = new PortfolioFragment(layoutID, menuID);
                break;
            case FRAGMENT_SETTING:
                f = new SettingFragment(layoutID, menuID);
                break;
            case FRAGMENT_ACCOUNT:
                f = new AccountSettingFragment(layoutID, R.id.nav_menu_setting);
                break;
            case FRAGMENT_WHOLE_TIMELINE:
                f = new WholeTimelineFragment(layoutID, R.id.nav_menu_calendar);
                break;
            default:
                f = null;
        }

        return f;
    }

    public void addFragment(int selector) {
        fragments.add(getFragment(selector));
    }

    public int addFragment(MamaFragment f) {
        fragments.add(f);

        return fragments.size() - 1;
    }

    public void registryFragment(int index) {
        Fragment fr = fragments.get(index);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (lastFragment != null) {
            fragmentTransaction.hide(lastFragment);
        }
        fragmentTransaction.add(R.id.main_fragment_frame, fr);
        fragmentTransaction.commit();

        lastFragment = fr;
    }

    public void registryFragment(int index, int mode) {
        Fragment fr = fragments.get(index);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (lastFragment != null) {
            fragmentTransaction.hide(lastFragment);
        }
        fragmentTransaction.add(R.id.main_fragment_frame, fr);

        if (mode == USE_INSTANTLY) {
            fragmentTransaction.addToBackStack(null);
            lastFragmentIndex = index;
            lastFragment = fr;
        }

        fragmentTransaction.commit();

        lastFragment = fr;
    }

    public void changeFragment(int index) {
        Log.d("TAG", "체인지 호출");
        Log.d("TAG", "new = " + fragmentTitles[index]);
        Log.d("TAG", "new index = " + index);
        Log.d("TAG", "last index = " + lastFragmentIndex);
        Log.d("TAG", "last fragment = " + lastFragment.toString());
        if(index == lastFragmentIndex) {
            return;
        }

        Fragment fr = fragments.get(index);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (index > lastFragmentIndex) {
            fragmentTransaction.setCustomAnimations(R.anim.fragment_transition_in, R.anim.fragment_transition_out, R.anim.fragment_transition_back_in, R.anim.fragment_transition_back_out);
        }

        if (index < lastFragmentIndex) {
            fragmentTransaction.setCustomAnimations(R.anim.fragment_transition_back_in, R.anim.fragment_transition_back_out, R.anim.fragment_transition_in, R.anim.fragment_transition_out);
        }

        fragmentTransaction.hide(lastFragment);
        fragmentTransaction.show(fr);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        lastFragment = fr;

        lastFragmentIndex = index;
    }

    public void setLastFragment(int index) {
        lastFragment = fragments.get(index);
        lastFragmentIndex = index;
    }

    public int getFragmentSize() {
        return fragments.size();
    }
}
