package com.seungrodotlee.mama.view.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.databinding.FragmentCalendarBinding;
import com.seungrodotlee.mama.element.MamaCalendar;

public class  CalendarFragment extends MamaFragment {
    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;

    public CalendarFragment(int layoutID, int menuItemID) {
        super(layoutID, menuItemID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutID, container, false);
        binding.setFragment(this);

        binding.calendarCalendarBox.setOnItemClickListener(new MamaCalendar.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("CF", "click on " + position);

                Log.d("CF", "date = " + binding.calendarCalendarBox.getDate(position));
                viewModel.setSelectedDateInCalendar(binding.calendarCalendarBox.getDate(position));
                fragmentFactory.changeFragment(fragmentFactory.FRAGMENT_WHOLE_TIMELINE);
            }
        });
        binding.calendarCalendarBox.show();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CalendarViewModel.class);
        viewModel.getSelectedDateFromTimeline().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.calendarCalendarBox.setDate(viewModel.getSelectedDateFromTimeline().getValue());
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            binding.calendarCalendarBox.show();
        }

    }
}
