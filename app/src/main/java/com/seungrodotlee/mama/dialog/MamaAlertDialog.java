package com.seungrodotlee.mama.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.databinding.DialogAlertBinding;

public class MamaAlertDialog extends DialogFragment {
    private DialogAlertBinding binding;
    private AlertDialog.Builder builder;
    private MamaAlertDialog me;
    private View.OnClickListener listener;
    public static final String TAG = "alert_dialog";
    private String message;

    private MamaAlertDialog(String message) { this.message = message; }

    public static MamaAlertDialog newInstance(String message) {
        MamaAlertDialog fragment = new MamaAlertDialog(message);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_alert, null, false);
        binding.alertDialogLabel.setText(message);

        builder = new AlertDialog.Builder(getActivity());

        builder.setView(binding.getRoot());

        me = this;

        binding.alertDialogNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.dismiss();
            }
        });
        binding.alertDialogPositiveBtn.setOnClickListener(listener);

        return builder.setView(binding.getRoot()).create();
    }

    public void setOnPositiveClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
