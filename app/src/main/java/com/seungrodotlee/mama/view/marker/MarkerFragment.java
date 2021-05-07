package com.seungrodotlee.mama.view.marker;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.adapter.VerticalListAdapter;
import com.seungrodotlee.mama.data.MakerData;
import com.seungrodotlee.mama.data.ProjectData;
import com.seungrodotlee.mama.databinding.FragmentMarkerBinding;
import com.seungrodotlee.mama.util.DateComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MarkerFragment extends MamaFragment {
    private FragmentMarkerBinding binding;
    private VerticalListAdapter categorySelectorAdapter;
    private VerticalListAdapter projectSelectorAdapter;
    private MarkerListAdapter markerListAdapter;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    private String selectedCategory = "다가오는 날들의 목표";
    private String selectedProject = "프로젝트 없음 (일반 일정)";
    private boolean optionPanelOpened = false, categorySelectorOpened = false, projectSelectorOpened = false;

    public MarkerFragment(int layoutID, int menuItemID) {
        super(layoutID, menuItemID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutID, container, false);
        binding.setFragment(this);

        binding.markerListCategorySelectorSection.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        categorySelectorAdapter = new VerticalListAdapter();
        categorySelectorAdapter.setFirstItem("다가오는 날들의 목표");
        categorySelectorAdapter.addItem("앞으로의 일정");
        categorySelectorAdapter.addItem("지나간 일들");
        categorySelectorAdapter.addItem("모두 보기");

        binding.markerListCategorySelectorSection.setAdapter(categorySelectorAdapter);

        categorySelectorAdapter.setOnItemClickListener(new VerticalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedCategory = categorySelectorAdapter.getItem(position);
                binding.markerListCategoryBtn.setText(selectedCategory);

                selectList();
            }
        });

        binding.markerListProjectSelectorSection.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        projectSelectorAdapter = new VerticalListAdapter();
        projectSelectorAdapter.setFirstItem("프로젝트 없음 (일반 일정)");
        binding.markerListProjectSelectorSection.setAdapter(projectSelectorAdapter);

        for (int i = 0; i < global.getProjectDataList().size(); i++) {
            ProjectData item = global.getProjectDataList().get(i);

            projectSelectorAdapter.addItem(item.getName());
        }

        projectSelectorAdapter.setOnItemClickListener(new VerticalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedProject = projectSelectorAdapter.getItem(position);
                binding.markerListProjectBtn.setText(selectedProject);

                selectProject();
            }
        });

        binding.markerListListBox.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        markerListAdapter = new MarkerListAdapter();
        binding.markerListListBox.setAdapter(markerListAdapter);

        for (int i = 0; i < global.getGoalDataList().size(); i++) {
            markerListAdapter.addItem(global.getGoalDataList().get(i));
        }

        return binding.getRoot();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        markerListAdapter.resetItem();
        if (!hidden) {
            selectList();
            selectProject();
        }
    }

    public void toggleCategorySelectorSection(View view) {
        categorySelectorOpened = !categorySelectorOpened;

        ValueAnimator anim;
        if (categorySelectorOpened) {
            anim = ValueAnimator.ofInt(0, categorySelectorAdapter.getItemCount() * global.dpToPx(37));
        } else {
            anim = ValueAnimator.ofInt(categorySelectorAdapter.getItemCount() * global.dpToPx(37), 0);
        }

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = binding.markerListCategorySelectorSection.getLayoutParams();
                layoutParams.height = value;
                binding.markerListCategorySelectorSection.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(500);
        anim.start();
    }

    public void toggleProjectSelectorSection(View view) {
        projectSelectorOpened = !projectSelectorOpened;

        ValueAnimator anim;
        if (projectSelectorOpened) {
            anim = ValueAnimator.ofInt(0, projectSelectorAdapter.getItemCount() * global.dpToPx(37));
        } else {
            anim = ValueAnimator.ofInt(projectSelectorAdapter.getItemCount() * global.dpToPx(37), 0);
        }

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = binding.markerListProjectSelectorSection.getLayoutParams();
                layoutParams.height = value;
                binding.markerListProjectSelectorSection.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(500);
        anim.start();
    }

    public void toggleOptionPanel(View v) {
        optionPanelOpened = !optionPanelOpened;

        final ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = binding.markerListOptionPanel.getLayoutParams();
                layoutParams.height = value;
                binding.markerListOptionPanel.setLayoutParams(layoutParams);
            }
        };

        final ValueAnimator[] anim = new ValueAnimator[1];
        if (optionPanelOpened) {
            binding.markerListOptionArrow.setRotation(180);
            anim[0] = ValueAnimator.ofInt(0, global.dpToPx(global.getScreenHeight()));
            anim[0].addUpdateListener(listener);
            anim[0].setDuration(1500);
            anim[0].start();
        } else {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    binding.markerListOptionArrow.setRotation(0);
                    anim[0] = ValueAnimator.ofInt(global.dpToPx(126), 0);
                    anim[0].addUpdateListener(listener);
                    anim[0].setDuration(500);
                    anim[0].start();
                }
            };

            Handler handler = new Handler();

            Log.d("MARKER", "b = " + categorySelectorOpened + projectSelectorOpened);
            if (!categorySelectorOpened && !projectSelectorOpened) {
                handler.postDelayed(r, 0);
            } else {
                handler.postDelayed(r, 500);
            }

            if (categorySelectorOpened) {
                toggleCategorySelectorSection(binding.getRoot());
            }

            if (projectSelectorOpened) {
                toggleProjectSelectorSection(binding.getRoot());
            }
        }
    }

    public void selectList() {
        if (selectedCategory.equals("다가오는 날들의 목표")) {
            markerListAdapter.resetItem();
//            for (int i = 0; i < global.getGoalDataList().size(); i++) {
//                markerListAdapter.addItem(global.getGoalDataList().get(i));
//            }
            markerListAdapter.setData(getCommingMarkers(GOAL));
        } else if (selectedCategory.equals("앞으로의 일정")) {
            markerListAdapter.resetItem();
//            for (int i = 0; i < global.getScheduleDataList().size(); i++) {
//                markerListAdapter.addItem(global.getScheduleDataList().get(i));
//            }
            markerListAdapter.setData(getCommingMarkers(SCHEDULE));
        } else if (selectedCategory.equals("지나간 일들")) {
            markerListAdapter.resetItem();
            markerListAdapter.setData(getPastMarkers(ALL));
        } else if (selectedCategory.equals("모두 보기")) {
            markerListAdapter.resetItem();
            ArrayList<MakerData> list = getCommingMarkers(ALL);
            //list.addAll(global.getGoalDataList());

//            for (int i = 0; i < list.size(); i++) {
//                markerListAdapter.addItem(list.get(i));
//            }
            list.addAll(getPastMarkers(ALL));
            markerListAdapter.setData(list);
        }
    }

    public static final int SCHEDULE = 0;
    public static final int GOAL = 1;
    public static final int ALL = 2;

    public ArrayList<MakerData> getPastMarkers(int mode) {
        ArrayList<MakerData> result = new ArrayList<MakerData>();
        ArrayList<MakerData> list;

        switch (mode) {
            case SCHEDULE:
                list = (ArrayList<MakerData>) global.getScheduleDataList().clone();
                break;
            case GOAL:
                list = (ArrayList<MakerData>) global.getGoalDataList().clone();
                break;
            default:
                list = (ArrayList<MakerData>) global.getScheduleDataList().clone();
                list.addAll(global.getGoalDataList());
        }


        for (int i = 0; i < list.size(); i++) {
            try {
                Calendar calendar = Calendar.getInstance();
                Calendar target = Calendar.getInstance();
                target.setTime(format.parse(list.get(i).getDate()));

                if (calendar.compareTo(target) == 1 && !list.get(i).getDate().equals(format.format(calendar.getTime()))) {
                    result.add(list.get(i));
                }
            } catch (ParseException e) {

            }
        }

        Collections.sort(result, new DateComparator());
        return result;
    }

    public ArrayList<MakerData> getCommingMarkers(int mode) {
        ArrayList<MakerData> result = new ArrayList<MakerData>();
        ArrayList<MakerData> list;

        switch (mode) {
            case SCHEDULE:
                list = (ArrayList<MakerData>) global.getScheduleDataList().clone();
                break;
            case GOAL:
                list = (ArrayList<MakerData>) global.getGoalDataList().clone();
                break;
            default:
                list = (ArrayList<MakerData>) global.getScheduleDataList().clone();
                list.addAll(global.getGoalDataList());
        }

        for (int i = 0; i < list.size(); i++) {
            try {
                Calendar calendar = Calendar.getInstance();
                Calendar target = Calendar.getInstance();
                target.setTime(format.parse(list.get(i).getDate()));

                if (calendar.compareTo(target) <= 0 || list.get(i).getDate().equals(format.format(calendar.getTime()))) {
                    result.add(list.get(i));
                }
            } catch (ParseException e) {

            }
        }

        Collections.sort(result, new DateComparator());
        return result;
    }

    public void selectProject() {
        selectList();
        ArrayList<MakerData> result = new ArrayList<MakerData>();
        ArrayList<MakerData> list = markerListAdapter.getData();

        Log.d("SELECT-P", "size = " + list.size());
        for(int i = 0; i < list.size(); i++) {
            Log.d("SELECT-P", "dependecy = " + list.get(i).getDependency());
            if(list.get(i).getDependency().equals(selectedProject)) {
                result.add(list.get(i));
            }
        }

        markerListAdapter.setData(result);
    }
}
