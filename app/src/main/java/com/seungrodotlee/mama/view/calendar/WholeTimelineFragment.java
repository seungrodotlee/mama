package com.seungrodotlee.mama.view.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.databinding.FragmentWholeTimelineBinding;
import com.seungrodotlee.mama.dialog.NewTodoItemDialog;
import com.seungrodotlee.mama.element.MamaCalendar;

public class WholeTimelineFragment extends MamaFragment {
    private FragmentWholeTimelineBinding binding;
    private SimpleProjectListAdapter projectListAdapter;
    private SimpleMarkerListAdapter scheduleListAdapter;
    private SimpleMarkerListAdapter goalListAdapter;
    private CalendarViewModel viewModel;
    private NewTodoItemDialog dialog;

    private String selectedDate;

    public WholeTimelineFragment(int layoutID, int menuItemID) {
        super(layoutID, menuItemID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutID, container, false);
        binding.setFragment(this);

        binding.wholeTimelineCalendar.setLayoutManager(MamaCalendar.LINEAR_VIEW);
        binding.wholeTimelineCalendar.show();
        binding.wholeTimelineCalendar.registryScrollListener(this);
        binding.wholeTimelineCalendar.setOnItemClickListener(new MamaCalendar.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selectedDate = binding.wholeTimelineCalendar.getDate(position);
                viewModel.setSelectedDateInTimeline(selectedDate);
                projectListAdapter.setDate(selectedDate);
                scheduleListAdapter.setDate(selectedDate);
                goalListAdapter.setDate(selectedDate);
            }
        });

        binding.wholeTimelineProjectList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        projectListAdapter = new SimpleProjectListAdapter();
        binding.wholeTimelineProjectList.setAdapter(projectListAdapter);

        binding.wholeTimelineScheduleList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        scheduleListAdapter = new SimpleMarkerListAdapter(SimpleMarkerListAdapter.SCHEDULE);
        binding.wholeTimelineScheduleList.setAdapter(scheduleListAdapter);

        binding.wholeTimelineGoalList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        goalListAdapter = new SimpleMarkerListAdapter(SimpleMarkerListAdapter.GOAL);
        binding.wholeTimelineGoalList.setAdapter(goalListAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(CalendarViewModel.class);
        viewModel.getSelectedDateFromCalendar().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                selectedDate = viewModel.getSelectedDateFromCalendar().getValue();
                binding.wholeTimelineCalendar.setDate(selectedDate);
                projectListAdapter.setDate(selectedDate);
                scheduleListAdapter.setDate(selectedDate);
                goalListAdapter.setDate(selectedDate);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden) {
            binding.wholeTimelineCalendar.show();
        }
    }

    public void openNewTodoDialog(final View view) {
        dialog = NewTodoItemDialog.newInstance(NewTodoItemDialog.SCHEDULE, selectedDate);
        dialog.show(getFragmentManager(), dialog.TAG);
        dialog.setOnPositiveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view.getId() == R.id.whole_timeline_new_schedule_btn) {
                    dialog.setMode(NewTodoItemDialog.SCHEDULE);
                    scheduleListAdapter.addItem(dialog.getData());
                }

                if(view.getId() == R.id.whole_timeline_new_goal_btn) {
                    dialog.setMode(NewTodoItemDialog.GOAL);
                    goalListAdapter.addItem(dialog.getData());
                }
            }
        });
    }
}
