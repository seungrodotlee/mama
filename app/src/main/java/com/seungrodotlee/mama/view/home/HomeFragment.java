package com.seungrodotlee.mama.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.data.Post;
import com.seungrodotlee.mama.data.ProjectData;
import com.seungrodotlee.mama.databinding.FragmentHomeBinding;
import com.seungrodotlee.mama.util.MamaFragmentFactory;

import java.util.ArrayList;

public class HomeFragment extends MamaFragment {
    private FragmentHomeBinding binding;
    private ProjectListAdapter adapter;
    private MamaFragmentFactory fragmentFactory;
    private ArrayList<ProjectData> data = new ArrayList<ProjectData>();
    private DatabaseReference ref;

    public HomeFragment(int layoutID, int menuItemID) {
        super(layoutID, menuItemID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutID, container, false);
        binding.setFragment(this);

        ref = global.getUserDatabaseReference();

        if(ref != null) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("TAG", "exist = " + dataSnapshot.exists());
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getProjects() != null) {
                        ArrayList<ProjectData> projects = post.getProjects();

                        binding.homeProjectList.setVisibility(View.VISIBLE);
                        binding.homeNoProjectLabel.setVisibility(View.GONE);
//                    global.setProjectDataList(projects);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        binding.homeProjectList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new ProjectListAdapter();
        binding.homeProjectList.setAdapter(adapter);

        return binding.getRoot();
    }

    public void openProjectCreator(View view) {
        Intent intent = new Intent(global.getContext(), ProjectCreatorActivity.class);

        startActivity(intent);
    }
}
