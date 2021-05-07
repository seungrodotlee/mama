package com.seungrodotlee.mama;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.seungrodotlee.mama.activity.MainActivity;
import com.seungrodotlee.mama.util.MamaFragmentFactory;

public class MamaFragment extends Fragment {
    MenuItem currentMenuItem;
    MenuItem lastMenuItem;
    int selector;
    protected int layoutID;
    protected int menuItemID = -1;
    protected int index;
    protected MamaGlobal global;
    protected MamaFragmentFactory fragmentFactory = MamaFragmentFactory.getInstance();

    public MamaFragment(int layoutID, int menuItemID) {
        this.layoutID = layoutID;
        this.menuItemID = menuItemID;
        this.index = fragmentFactory.getFragmentSize();
    }

    public MamaFragment(int layoutID) {
        this.layoutID = layoutID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        global = MamaGlobal.getInstance();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(menuItemID != -1) {
            if(!hidden) {
                MainActivity activity = (MainActivity) getActivity();

                currentMenuItem = activity.getNavigation().getMenu().findItem(menuItemID);

                if(lastMenuItem != null) {
                    lastMenuItem.setChecked(false);
                }

                if(currentMenuItem != null) {
                    currentMenuItem.setChecked(true);
                    lastMenuItem = currentMenuItem;
                }

                fragmentFactory.setLastFragment(index);
            }
        }
    }

    public void startActivity(Intent intent) {
        Bundle bundle = ActivityOptions.makeCustomAnimation(global.getContext(), R.anim.activity_transition_in, R.anim.activity_transition_out).toBundle();

        super.startActivity(intent, bundle);
    }
}
