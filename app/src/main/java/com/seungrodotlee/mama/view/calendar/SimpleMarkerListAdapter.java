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
import com.seungrodotlee.mama.data.MakerData;

import java.util.ArrayList;

public class SimpleMarkerListAdapter extends RecyclerView.Adapter<SimpleMarkerListAdapter.ViewHolder> {
    public static final int SCHEDULE = 0;
    public static final int GOAL = 1;

    private LayoutInflater inflater;
    private MamaGlobal global;
    private String date;
    private int mode;
    ;
    private ArrayList<MakerData> markerDataList = new ArrayList<MakerData>();

    public SimpleMarkerListAdapter(int mode) {
        this.mode = mode;
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
    public SimpleMarkerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.item_project_list, parent, false);
        SimpleMarkerListAdapter.ViewHolder vh = new SimpleMarkerListAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MakerData item = markerDataList.get(position);

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
        return markerDataList.size();
    }

    public void setDate(String date) {
        this.date = date;

        markerDataList.clear();

        ArrayList<MakerData> markerTemp;
        if(mode == SCHEDULE) {
            markerTemp = global.getScheduleDataList();
        } else {
            markerTemp = global.getGoalDataList();
        }

        if(markerTemp == null) {
            return;
        }

        for (int i = 0; i < markerTemp.size(); i++) {
            MakerData item = markerTemp.get(i);
            Log.d("MARKER", "marker " + i + " = " + item.getName());

                if (date.equals(item.getDate())) {
                    markerDataList.add(item);
                }
        }

        notifyDataSetChanged();
    }

    public void addItem(MakerData data) {
        markerDataList.add(data);

        notifyDataSetChanged();
    }
}
