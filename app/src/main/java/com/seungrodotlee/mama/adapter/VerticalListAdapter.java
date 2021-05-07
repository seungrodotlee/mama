package com.seungrodotlee.mama.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.R;

import java.util.ArrayList;

public class VerticalListAdapter extends RecyclerView.Adapter<VerticalListAdapter.ViewHolder> {
    private ArrayList<String> data = new ArrayList<String>();
    private LayoutInflater inflater;
    private OnItemClickListener listener;

    private String lastSelectedItemTitle = "";
    private int lastSelectedItemIndex = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public VerticalListAdapter() { }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        LinearLayout divider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.vertical_list_item_title);
            divider = itemView.findViewById(R.id.vertical_list_item_divider);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.item_vertical_list, parent, false);
        VerticalListAdapter.ViewHolder vh = new VerticalListAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.label.setText(data.get(position));
        holder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onItemClick(v, position);
                }

                if(lastSelectedItemIndex != -1) {
                    data.add(lastSelectedItemIndex, lastSelectedItemTitle);
                }

                if(lastSelectedItemIndex <= position) {
                    lastSelectedItemIndex = position;
                    lastSelectedItemTitle = data.get(position + 1);
                    Log.d("VLA", "selected = " + lastSelectedItemTitle);
                    data.remove(position + 1);
                } else {
                    lastSelectedItemIndex = position;
                    lastSelectedItemTitle = data.get(position);
                    Log.d("VLA", "selected = " + lastSelectedItemTitle);
                    data.remove(position);
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getItem(int position) {
        return data.get(position);
    }

    public void addItem(String text) {
        data.add(text);

        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setFirstItem(String text) {
        lastSelectedItemTitle = text;
    }
}
