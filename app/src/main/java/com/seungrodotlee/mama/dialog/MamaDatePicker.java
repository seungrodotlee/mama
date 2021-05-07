package com.seungrodotlee.mama.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.databinding.DialogDatePickerBinding;
import com.seungrodotlee.mama.element.MamaCalendar;

public class MamaDatePicker extends DialogFragment {
    public static String TAG = "dialog_date_picker";
    private MamaDatePicker me;
    private DialogDatePickerBinding binding;
    private AlertDialog.Builder builder;
    private Button positiveBtn, negativeBtn;
    private View.OnClickListener listener;

    private int mode;

    private MamaDatePicker(int mode) {
        this.mode = mode;
    }

    public static MamaDatePicker newInstance(int mode) {
        MamaDatePicker fragment = new MamaDatePicker(mode);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_date_picker, null, false);

        builder = new AlertDialog.Builder(getActivity());

        binding.datePickerCalendar.setSelectMode(mode);
        binding.datePickerCalendar.show();

        builder.setView(binding.getRoot());

        me = this;

        binding.datePickerNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.dismiss();
            }
        });
        binding.datePickerPositiveBtn.setOnClickListener(listener);

        return builder.setView(binding.getRoot()).create();
    }

    public void setOnPositiveClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public MamaCalendar getCalendar() {
        return binding.datePickerCalendar;
    }
}
