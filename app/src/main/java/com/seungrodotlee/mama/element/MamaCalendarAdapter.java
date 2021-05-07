package com.seungrodotlee.mama.element;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MamaCalendarAdapter extends RecyclerView.Adapter<MamaCalendarAdapter.ViewHolder> {
    private ArrayList<CalendarItem> data = new ArrayList<CalendarItem>();
    private LayoutInflater inflater;

    private MamaGlobal global;
    private Context context;
    private int calendarSize;
    private int itemSize;
    private int textSize = 24;
    private int markerSize = 6;

    private int backgroundR = 121;
    private int backgroundG = 143;
    private int backgroundB = 131;

    private int year, month;

    private OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout cell;
        TextView dayLabel;
        ImageView marker, selectionBG;

        ViewHolder(View view) {
            super(view);

            cell = (RelativeLayout) view.findViewById(R.id.calendar_mama_cell_bg);
            dayLabel = (TextView) view.findViewById(R.id.calendar_mama_cell_day_label);
            marker = (ImageView) view.findViewById(R.id.calendar_mama_cell_marker);

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(data.get(getAdapterPosition()).getDay() == -1) {
                        return;
                    }

                    int pos = getAdapterPosition();
                    Log.d("holder", "click on " + pos);
                    if(pos != RecyclerView.NO_POSITION) {
                        if(listener != null) {
                            listener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }

    public MamaCalendarAdapter() {
        global = MamaGlobal.getInstance();
        context = global.getContext();

        Calendar c = Calendar.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.item_calendar_mama, parent, false);
        MamaCalendarAdapter.ViewHolder vh = new MamaCalendarAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalendarItem item = data.get(position);
        String day = "" + data.get(position).getDay();
        int event = item.getEvent();

        holder.cell.getLayoutParams().width = itemSize;
        holder.dayLabel.setTextSize(textSize);
        holder.dayLabel.setText("");
        holder.marker.getLayoutParams().width = global.dpToPx(markerSize);
        holder.marker.getLayoutParams().height = global.dpToPx(markerSize);
        holder.dayLabel.setTextColor(global.getContext().getResources().getColor(R.color.white));
        holder.cell.setBackgroundColor(global.getContext().getResources().getColor(android.R.color.transparent));

        if(!day.equals("-1")) {


            holder.dayLabel.setText(day);

            switch (event) {
                case CalendarItem.PROJECT_PERIOD:
                    holder.cell.setBackgroundColor(item.getBackground());
                    break;
                case CalendarItem.IMPORTANT_DAY:
                    holder.marker.setVisibility(View.VISIBLE);
                    break;
                case CalendarItem.IMPORTANT_DAY_IN_PROJECT:
                    holder.marker.setVisibility(View.VISIBLE);
                    holder.cell.setBackgroundColor(item.getBackground());
                    break;
            }
        }

        if(event != CalendarItem.PROJECT_PERIOD && event != CalendarItem.IMPORTANT_DAY_IN_PROJECT) {
            if(data.get(position).getDate() == 7) {
                holder.dayLabel.setTextColor(global.getContext().getResources().getColor(R.color.blue));
            }

            if(data.get(position).getDate() == 1) {
                holder.dayLabel.setTextColor(global.getContext().getResources().getColor(R.color.exlusive));
            }
        }


        if(data.get(position).isSelection()) {
            holder.cell.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.dayLabel.setTextColor(context.getResources().getColor(R.color.black));
        }

        String msg = data.get(position).getMsg();
        if(!msg.equals("")) {
            holder.dayLabel.setText(msg);
            holder.dayLabel.setAlpha(0.6f);
            holder.cell.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.dayLabel.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) { return (long) position; }

    @Override
    public int getItemCount() { return data.size(); }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public int getItemSize() {
        return itemSize;
    }

    public void setBackgroundOpacity(int position, double ratio) {
        String opacity = String.format("%02X%n", ratio);

        CalendarItem item = data.get(position);
        item.setBackground(Color.argb((int) (256 * ratio), backgroundR, backgroundG, backgroundB));

        data.set(position, item);
    }

    public CalendarItem getItem(int position) {
        return data.get(position);
    }

    public void addItem(int day, int event) {
        CalendarItem item = new CalendarItem();

        item.setYear(year);
        item.setMonth(month);
        item.setDay(day);
        item.setEvent(event);

        data.add(item);
    }

    public void addItemIn(int position, CalendarItem item) {
        data.add(position, item);
    }

    public void addItem(CalendarItem item) {
        data.add(item);
    }

    public void addItem(int day, int event, double ratio) {
        CalendarItem item = new CalendarItem();

        item.setYear(year);
        item.setMonth(month);
        item.setDay(day);
        item.setEvent(event);
        item.setBackground(Color.argb((int) (255 * ratio), backgroundR, backgroundG, backgroundB));

        data.add(item);
    }

    public int getBackgroundColor(double ratio) {
        return Color.argb((int) (255 * ratio), backgroundR, backgroundG, backgroundB);
    }

    public void updateItem(int position, CalendarItem item) {
        data.set(position, item);
    }

    public void clearItem() {
        data.clear();
    }

    public void setSize(float ratio) {
        Log.d("TAG", "cal size = " + (int) (global.getScreenWidth() * ratio));

        this.textSize = (int) (textSize * ratio);
        this.markerSize = (int) (markerSize * ratio);
        calendarSize = (int) (global.getScreenWidth() * ratio);
        itemSize = (int) ((double) calendarSize / (double) 7);

        Log.d("TAG", "cal size = " + calendarSize);
        Log.d("TAG", "item size = " + itemSize);

        notifyDataSetChanged();
    }
}