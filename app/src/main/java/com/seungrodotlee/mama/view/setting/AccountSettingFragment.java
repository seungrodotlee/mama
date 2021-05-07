package com.seungrodotlee.mama.view.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.adapter.SettingListAdapter;
import com.seungrodotlee.mama.databinding.FragmentAccountSettingBinding;
import com.seungrodotlee.mama.dialog.MamaAlertDialog;

public class AccountSettingFragment extends MamaFragment {
    private FragmentAccountSettingBinding binding;
    private SettingListAdapter adapter;

    public AccountSettingFragment(int layoutID, int menuID) {
        super(layoutID, menuID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutID, container, false);
        binding.setFragment(this);

        global = MamaGlobal.getInstance();

        final RecyclerView list = binding.accountSettingListView;
        list.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        list.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        adapter = new SettingListAdapter();

        adapter.addItem("로그아웃", SettingListAdapter.SETTING_WITH_ITSELF, new SettingListAdapter.OnItemEventListener() {
            @Override
            public void onItemEvent(View v, int position) {
                final MamaAlertDialog dialog = MamaAlertDialog.newInstance("정말 로그아웃 하실거예요?");
                dialog.setOnPositiveClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        global.getAuth().signOut();
                        dialog.dismiss();
                    }
                });
                dialog.show(getFragmentManager(), MamaAlertDialog.TAG);
            }
        });

        list.post(new Runnable() {
            @Override
            public void run() {
                list.setAdapter(adapter);
            }
        });

        return binding.getRoot();
    }
}
