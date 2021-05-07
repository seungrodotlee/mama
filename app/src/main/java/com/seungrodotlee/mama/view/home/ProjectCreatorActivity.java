package com.seungrodotlee.mama.view.home;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.seungrodotlee.mama.MamaActivity;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.adapter.HorizontalListAdapter;
import com.seungrodotlee.mama.data.ProjectData;
import com.seungrodotlee.mama.databinding.ActivityProjectCreatorBinding;
import com.seungrodotlee.mama.dialog.MamaDatePicker;
import com.seungrodotlee.mama.element.MamaCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class ProjectCreatorActivity extends MamaActivity {
    private ActivityProjectCreatorBinding binding;
    private MamaDatePicker picker;
    private HorizontalListAdapter adapter;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    private String[] hintAdj = {"엄청난", "비장의", "회심의", "최종병기", "비밀", "위험한", "어마어마한", "응큼한"};
    private ArrayList<String> currentTags = new ArrayList<String>();
    private String startDate, endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Random r = new Random();
        int random = r.nextInt(hintAdj.length - 1);

        Log.d("TAG", "Random = " + random);

        Calendar c = Calendar.getInstance();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_project_creator);

        binding.projectCreatorNameInput.setHint(hintAdj[random] + " 프로젝트");
        binding.projectCreatorOpenDatePickerBtn.setText(format.format(c.getTime()));

        binding.projectCreatorTagList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        adapter = new HorizontalListAdapter();
        binding.projectCreatorTagList.setAdapter(adapter);

        adapter.setOnItemClickListener(new HorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                currentTags.remove(position);
                adapter.removeItem(position);
                adapter.notifyDataSetChanged();

                if(adapter.getItemCount() == 0) {
                    binding.projectCreatorEmptyTagLabel.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.projectCreatorTagInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT) {
                    String tag = binding.projectCreatorTagInput.getText().toString();

                    if(adapter.getItemCount() == 0) {
                        binding.projectCreatorEmptyTagLabel.setVisibility(View.GONE);
                    }

                    currentTags.add(tag);
                    adapter.addItem(global.removeSeperator(tag));
                    binding.projectCreatorTagInput.setText("");
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        binding.setActivity(this);
        setLayoutPadding(binding.getRoot());
    }

    public void openDatePicker(View view) {
        picker = MamaDatePicker.newInstance(MamaCalendar.RANGE_SELECT);
        picker.setOnPositiveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MamaCalendar.SelectedDate date = picker.getCalendar().getSelectedDate();

                startDate = format.format(date.getStartDate().getTime());
                endDate = format.format(date.getEndDate().getTime());

                binding.projectCreatorOpenDatePickerBtn.setText(startDate + " - " + endDate);

                picker.dismiss();
            }
        });
        picker.show(getSupportFragmentManager(), picker.TAG);
    }

    public void createNewProject(View view) {
        int index = 0;

        if(global.getProjectDataList() != null) {
            index = global.getProjectDataList().size();
        }

        String name = binding.projectCreatorNameInput.getText().toString();
        String description = binding.projectCreatorExplainInput.getText().toString();

        if(description.equals("")) {
            description = "설명이 없습니다";
        }

        ProjectData data = new ProjectData(name, description, startDate, endDate,currentTags);

        DatabaseReference ref = global.getUserDatabaseReference();
        ref = ref.child("projects").child("" + index);

        ref.setValue(data);
        finish();
    }
}
