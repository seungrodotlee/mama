package com.seungrodotlee.mama.view.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.seungrodotlee.mama.activity.LoginActivity;
import com.seungrodotlee.mama.adapter.SettingListAdapter;
import com.seungrodotlee.mama.adapter.SettingListItem;
import com.seungrodotlee.mama.databinding.FragmentSettingBinding;
import com.seungrodotlee.mama.util.MamaFragmentFactory;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SettingFragment extends MamaFragment {
    private FragmentSettingBinding binding;
    private SettingListAdapter adapter;
    private boolean loginStatus;
    private String userName;

    public SettingFragment(int layoutID, int menuItemID) {
        super(layoutID, menuItemID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutID, container, false);
        binding.setFragment(this);

        userName = global.getUserName();
        global = MamaGlobal.getInstance();

        final RecyclerView list = binding.settingListView;
        list.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        list.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        adapter = new SettingListAdapter();

        Log.d(TAG, "list view = " + list);

        adapter.addItem("이름", userName, SettingListAdapter.SETTING_WITH_INPUT, new SettingListAdapter.OnItemEventListener() {
            @Override
            public void onItemEvent(View v, int position) {
                Log.d("TAG", "이벤트 넘어옴");
                global.updateUserName(global.removeSeperator(adapter.getItem(0).getCurrent()));
            }
        });

        if(global.getUser() != null) {
            loginStatus = true;
        } else {
            loginStatus = false;
        }

        adapter.addItem(loginStatus ? "계정" : "로그인", loginStatus ? SettingListAdapter.SETTING_WITH_NEW_VIEW : SettingListAdapter.SETTING_WITH_ITSELF, new SettingListAdapter.OnItemEventListener() {
            @Override
            public void onItemEvent(View v, int position) {
                if(loginStatus) {
                    fragmentFactory.changeFragment(MamaFragmentFactory.FRAGMENT_ACCOUNT);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        adapter.addItem("다크모드", "true", SettingListAdapter.SETTING_WITH_SWITCH, new SettingListAdapter.OnItemEventListener() {
            @Override
            public void onItemEvent(View v, int position) {

            }
        });

        adapter.addItem("테스트", SettingListAdapter.SETTING_WITH_NEW_VIEW, new SettingListAdapter.OnItemEventListener() {
            @Override
            public void onItemEvent(View v, int position) {

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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Log.d("TAG", "설정 화면");
        Log.d("TAG", "" + global.isUserLogin());
        Log.d("TAG", "" + loginStatus);

        if(global.isUserLogin() != loginStatus) {
            loginStatus = global.isUserLogin();

            SettingListItem item = adapter.getItem(1);
            item.setTitle(loginStatus ? "계정" : "로그인");
            item.setType(loginStatus ? SettingListAdapter.SETTING_WITH_NEW_VIEW : SettingListAdapter.SETTING_WITH_ITSELF);
            adapter.updateItem(1, item);
            adapter.notifyDataSetChanged();
        }

        if(global.getUserName() != userName) {
            userName = global.getUserName();

            SettingListItem item = adapter.getItem(0);
            item.setCurrent(userName);
            adapter.updateItem(0, item);
            adapter.notifyDataSetChanged();
        }
    }
}
