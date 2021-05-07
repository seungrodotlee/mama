package com.seungrodotlee.mama.view.marker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.data.MakerData;
import com.seungrodotlee.mama.util.DateComparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MarkerListAdapter extends RecyclerView.Adapter<MarkerListAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    private ArrayList<MakerData> data = new ArrayList<MakerData>();
    public MarkerListAdapter() {}

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout, endSection;
        TextView titleLabel, descriptionLabel, statusLabel, dayLabel, startSub;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = (LinearLayout) itemView.findViewById(R.id.project_item_layout);
            titleLabel = (TextView) itemView.findViewById(R.id.project_item_title);
            titleLabel.setTextSize(16);
            descriptionLabel = (TextView) itemView.findViewById(R.id.project_item_description);
            statusLabel = (TextView) itemView.findViewById(R.id.project_item_status);
            dayLabel = (TextView) itemView.findViewById(R.id.project_item_start_label);
            startSub = (TextView) itemView.findViewById(R.id.project_item_start_sub);
            startSub.setVisibility(View.GONE);
            endSection = (LinearLayout) itemView.findViewById(R.id.project_item_end_section);
            endSection.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.item_project_list, parent, false);
        MarkerListAdapter.ViewHolder vh = new MarkerListAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MakerData item = data.get(position);

        Calendar calendar = Calendar.getInstance();
        Calendar targetCal = Calendar.getInstance();

        try {
            targetCal.setTime(format.parse(item.getDate()));
        } catch (ParseException e) {

        }

        holder.layout.setAlpha(1f);

        if(format.format(calendar.getTime()).equals(item.getDate())) {
            holder.statusLabel.setText("마지막 날...");
        } else if(calendar.compareTo(targetCal) == 1 && !data.get(position).getDate().equals(format.format(calendar.getTime()))) {
            holder.statusLabel.setText("지나간 일정");
            holder.layout.setAlpha(0.4f);
        } else if(calendar.compareTo(targetCal) == -1) {
            long left = targetCal.getTime().getTime() - calendar.getTime().getTime();
            holder.statusLabel.setText(((left / (24*60*60*1000)) + 1) + "일 남음");
        }

        holder.titleLabel.setText(item.getName());
        holder.descriptionLabel.setText(item.getDescription());
        holder.dayLabel.setText(item.getDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<MakerData> getData() {
        return data;
    }

    public void addItem(MakerData item) {
        data.add(item);

        Collections.sort(data, new DateComparator());

        notifyDataSetChanged();
    }

    public void resetItem() {
        data.clear();
    }

    public void setData(ArrayList<MakerData> list) {
        data = (ArrayList<MakerData>) list.clone();

        notifyDataSetChanged();
    }
}
