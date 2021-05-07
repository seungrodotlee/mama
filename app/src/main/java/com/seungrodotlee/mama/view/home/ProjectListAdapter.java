package com.seungrodotlee.mama.view.home;

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
import java.util.Calendar;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private MamaGlobal global;

    public ProjectListAdapter() {
        global = MamaGlobal.getInstance();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        TextView titleLabel, descriptionLabel, statusLabel, startDayLabel, endDayLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.project_item_layout);
            titleLabel = (TextView) itemView.findViewById(R.id.project_item_title);
            descriptionLabel = (TextView) itemView.findViewById(R.id.project_item_description);
            statusLabel = (TextView) itemView.findViewById(R.id.project_item_status);
            startDayLabel = (TextView) itemView.findViewById(R.id.project_item_start_label);
            endDayLabel = (TextView) itemView.findViewById(R.id.project_item_end_label);
        }
    }

    @NonNull
    @Override
    public ProjectListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.item_project_list, parent, false);
        ProjectListAdapter.ViewHolder vh = new ProjectListAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectData item = global.getProjectDataList().get(position);
        Log.d("PLA", "item = " + item.getName());

        holder.titleLabel.setText(item.getName());
        holder.descriptionLabel.setText(item.getDescription());

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        Calendar cal = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        String startPoint = item.getStartpoint();
        String endPoint = item.getEndpoint();

        holder.startDayLabel.setText(startPoint);
        holder.endDayLabel.setText(endPoint);

        try {
            startCal.setTime(format.parse(startPoint));
            endCal.setTime(format.parse(endPoint));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("CP", "cal " + format.format(cal.getTime()));
        Log.d("CP", "end " + format.format(endCal.getTime()));
        Log.d("CP", "compare " + cal.compareTo(endCal));

        if(cal.compareTo(endCal) == 1) {
            holder.statusLabel.setText("프로젝트 끝!");
            holder.layout.setAlpha(0.4f);
            holder.layout.setClickable(false);
        } else if(cal.compareTo(startCal) == 1) {
            long left = endCal.getTime().getTime() - cal.getTime().getTime();
            holder.statusLabel.setText(((left / (24*60*60*1000)) + 1) + "일 남음");
        } else {
            if(format.format(cal.getTime()).equals(startPoint)) {
                long left = endCal.getTime().getTime() - cal.getTime().getTime();
                holder.statusLabel.setText(((left / (24*60*60*1000)) + 1) + "일 남음");
            } if(format.format(cal.getTime()).equals(endPoint)) {
                holder.statusLabel.setText("마지막 날...");
            } else {
                holder.statusLabel.setText("시작 전... 준비하세요");
            }
        }
    }

    @Override
    public int getItemCount() {
        return global.getProjectDataList().size();
    }
}
