package com.seungrodotlee.mama.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.R;

import java.util.ArrayList;

public class HorizontalListAdapter extends RecyclerView.Adapter<HorizontalListAdapter.ViewHolder> {
    private ArrayList<String> data = new ArrayList<String>();
    private LayoutInflater inflater;

    public HorizontalListAdapter() { }

    private HorizontalListAdapter.OnItemClickListener listener = null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView label, emptyTagLabel;
        ImageButton clearBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.horizon_list_cell_label);
            clearBtn = itemView.findViewById(R.id.horizon_list_cell_clear_btn);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        if(listener != null) {
                            listener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(R.layout.item_horizon_list, parent, false);
        HorizontalListAdapter.ViewHolder vh = new HorizontalListAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.label.setText(data.get(position));
    }

    public void setOnItemClickListener(HorizontalListAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(String text) {
        data.add(text);
    }

    public void removeItem(int position) {
        data.remove(position);
    }
}
