package com.seungrodotlee.mama.view.calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.data.ProjectData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SimpleProjectListAdapter extends RecyclerView.Adapter<com.seungrodotlee.mama.view.calendar.SimpleProjectListAdapter.ViewHolder> {
    public static final int PROJECT = 0;
    public static final int SCHEDULE = 1;
    public static final int GOAL = 2;

    private LayoutInflater inflater;
    private MamaGlobal global;
    private String date;
    private int mode;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    ;
    private ArrayList<ProjectData> projectDataList = new ArrayList<ProjectData>();

    public SimpleProjectListAdapter() {
        global = MamaGlobal.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout, dateSection;
        TextView titleLabel, descriptionLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.project_item_layout);
            titleLabel = (TextView) itemView.findViewById(R.id.project_item_title);
            titleLabel.setTextSize(16);
            descriptionLabel = (TextView) itemView.findViewById(R.id.project_item_description);
            descriptionLabel.setVisibility(View.GONE);
            dateSection = (LinearLayout) itemView.findViewById(R.id.project_item_date_section);
            dateSection.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public com.seungrodotlee.mama.view.calendar.SimpleProjectListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.item_project_list, parent, false);
        com.seungrodotlee.mama.view.calendar.SimpleProjectListAdapter.ViewHolder vh = new com.seungrodotlee.mama.view.calendar.SimpleProjectListAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectData item = (ProjectData) projectDataList.get(position);

        Log.d("PLA", "item = " + item.getName());

        holder.titleLabel.setText(item.getName());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setLayoutDirection(LinearLayout.HORIZONTAL);
        params.setMargins(0, 0, 0, global.dpToPx(10));

        if (position % 2 == 0) {
            params.setMarginEnd(global.dpToPx(5));
            holder.layout.setLayoutParams(params);
        } else {
            params.setMarginStart(global.dpToPx(5));
            holder.layout.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return projectDataList.size();
    }

    public void setDate(String date) {
        this.date = date;

        Calendar cal = Calendar.getInstance();

        projectDataList.clear();

        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        ArrayList<ProjectData> projectTemp = global.getProjectDataList();

        for (int i = 0; i < projectTemp.size(); i++) {
            ProjectData item = projectTemp.get(i);

            try {
                cal.setTime(format.parse(date));
                startCal.setTime(format.parse(item.getStartpoint()));
                endCal.setTime(format.parse(item.getEndpoint()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if ((cal.compareTo(startCal) >= 0) && (cal.compareTo(endCal) <= 0)) {
                projectDataList.add(item);
            } else {
                if (date.equals(item.startpoint) || date.equals(item.endpoint)) {
                    projectDataList.add(item);
                }
            }
        }


        notifyDataSetChanged();
    }
}
