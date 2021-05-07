package com.seungrodotlee.mama.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.adapter.HorizontalListAdapter;
import com.seungrodotlee.mama.data.MakerData;
import com.seungrodotlee.mama.data.ProjectData;
import com.seungrodotlee.mama.databinding.DialogCreateNewTodoItemBinding;
import com.seungrodotlee.mama.element.MamaCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class NewTodoItemDialog extends DialogFragment {
    public static String SCHEDULE = "schedules";
    public static String GOAL = "goals";

    public static String TAG = "new_todo_dialog";
    private DialogCreateNewTodoItemBinding binding;
    private AlertDialog.Builder builder;
    private MamaGlobal global;
    private MamaDatePicker picker;
    private PopupMenu popup;
    private HorizontalListAdapter adapter;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

    private View.OnClickListener listener;

    private String mode;
    private String title;
    private String description = "설명이 없습니다";
    private String date;
    private String dependencyProject = "프로젝트 없음 (일반 일정)";
    private ArrayList<String> currentTags = new ArrayList<String>();

    private MakerData data;

    private NewTodoItemDialog(String mode, String date) {
        this.mode = mode;
        this.date = date;
    }

    public static NewTodoItemDialog newInstance(String mode, String date) {
        NewTodoItemDialog fragment = new NewTodoItemDialog(mode, date);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_create_new_todo_item, null, false);
        binding.setFragment(this);

        global = MamaGlobal.getInstance();

        builder = new AlertDialog.Builder(getActivity());

        builder.setView(binding.getRoot());

        Context wrapper = new ContextThemeWrapper(getContext(), R.style.mamaPopupStyle);
        popup = new PopupMenu(wrapper, binding.dialogCreateNewTodoSelectProjectBtn);
        Menu menu = popup.getMenu();
        menu.add("프로젝트 없음 (일반 일정)");

        ArrayList<ProjectData> projectData = global.getProjectDataList();

        Calendar calendar = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        try {
            calendar.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < projectData.size(); i++) {
            String startPoint = projectData.get(i).getStartpoint();
            String endPoint = projectData.get(i).getEndpoint();

            try {
                startCal.setTime(format.parse(startPoint));
                endCal.setTime(format.parse(endPoint));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(calendar.compareTo(startCal) >= 0 && calendar.compareTo(endCal) <= 0) {
                menu.add(projectData.get(i).getName());
            } else if(date.equals(startPoint) || date.equals(endPoint)) {
                menu.add(projectData.get(i).getName());
            }
        }

        binding.dialogCreateNewTodoTitleInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("TAG", "키 " + keyCode);
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d("TAG", "엔터 입력");
                    InputMethodManager imm = (InputMethodManager) global.getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.dialogCreateNewTodoTitleInput.getWindowToken(), 0);
                    binding.dialogCreateNewTodoTitleInput.setText( binding.dialogCreateNewTodoTitleInput.getText().toString().replace("\n", ""));

                    return true;
                }

                return false;
            }
        });

        binding.dialogCreateNewTodoDatePickerBtn.setText(date);

        binding.dialogCreateNewTodoDescriptionInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("TAG", "키 " + keyCode);
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d("TAG", "엔터 입력");
                    InputMethodManager imm = (InputMethodManager) global.getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.dialogCreateNewTodoDescriptionInput.getWindowToken(), 0);
                    binding.dialogCreateNewTodoDescriptionInput.setText(binding.dialogCreateNewTodoDescriptionInput.getText().toString().replace("\n", ""));

                    return true;
                }

                return false;
            }
        });

        binding.dialogCreateNewTodoTagList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapter = new HorizontalListAdapter();
        binding.dialogCreateNewTodoTagList.setAdapter(adapter);

        adapter.setOnItemClickListener(new HorizontalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                currentTags.remove(position);
                adapter.removeItem(position);
                adapter.notifyDataSetChanged();

                if(adapter.getItemCount() == 0) {
                    binding.dialogCreateNewTodoEmptyTagLabel.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.dialogCreateNewTodoTagInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT) {
                    String tag = binding.dialogCreateNewTodoTagInput.getText().toString();

                    if(adapter.getItemCount() == 0) {
                        binding.dialogCreateNewTodoEmptyTagLabel.setVisibility(View.GONE);
                    }

                    currentTags.add(tag);
                    adapter.addItem(global.removeSeperator(tag));
                    binding.dialogCreateNewTodoTagInput.setText("");
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        binding.dialogCreateNewTodoNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.dialogCreateNewTodoPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = binding.dialogCreateNewTodoTitleInput.getText().toString();
                description = binding.dialogCreateNewTodoDescriptionInput.getText().toString();

                if(description.equals("")) {
                    description = "설명이 없습니다";
                }

                data = new MakerData(title, description, dependencyProject, date, currentTags);

                if(listener != null) {
                    listener.onClick(v);
                }

                int index = 0;

                if(mode == SCHEDULE || global.getScheduleDataList() != null) {
                    index = global.getScheduleDataList().size();
                }
                if(mode == GOAL || global.getGoalDataList() != null) {
                    index = global.getGoalDataList().size();
                }

                DatabaseReference ref = global.getUserDatabaseReference();
                ref = ref.child(mode).child("" + index);

                ref.setValue(data);
                dismiss();
            }
        });

        return builder.setView(binding.getRoot()).create();
    }

    public void setOnPositiveClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void openDatePicker(View view) {
        picker = MamaDatePicker.newInstance(MamaCalendar.ONE_DAY_SELECT);
        picker.setOnPositiveClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MamaCalendar.SelectedDate selected = picker.getCalendar().getSelectedDate();

                date = format.format(selected.getStartDate().getTime());

                binding.dialogCreateNewTodoDatePickerBtn.setText(date);

                picker.dismiss();
            }
        });
        picker.show(getFragmentManager(), picker.TAG);
    }

    public void selectProject(View view) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                dependencyProject = item.getTitle().toString();
                binding.dialogCreateNewTodoSelectProjectBtn.setText(dependencyProject);

                return false;
            }
        });
        popup.show();
    }

    public MakerData getData() {
        return data;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
