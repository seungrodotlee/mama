package com.seungrodotlee.mama.view.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.databinding.FragmentPortfolioBinding;

public class PortfolioFragment extends MamaFragment {
    public PortfolioFragment(int layoutID, int menuItemID) {
        super(layoutID, menuItemID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentPortfolioBinding layout = DataBindingUtil.inflate(inflater, layoutID, container, false);
        layout.setFragment(this);

        return layout.getRoot();
    }
}
