package com.seungrodotlee.mama.adapter;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.element.MamaSwitch;

import java.util.ArrayList;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SettingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int SETTING_WITH_INPUT = 0;
    public static final int SETTING_WITH_NEW_VIEW = 1;
    public static final int SETTING_WITH_SWITCH = 2;
    public static final int SETTING_WITH_ITSELF = 3;

    private MamaGlobal global;

    LayoutInflater inflater = null;
    private ArrayList<SettingListItem> data = new ArrayList<SettingListItem>();
    private int count = 0;

    public interface OnItemEventListener {
        void onItemEvent(View v, int position);
    }

    public SettingListAdapter() {
    }

    public class InputTypeViewHolder extends RecyclerView.ViewHolder {
        TextView titleLabel;
        EditText input;

        public InputTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleLabel = (TextView) itemView.findViewById(R.id.list_item_i_title);
            input = (EditText) itemView.findViewById(R.id.list_item_i_input);
        }
    }

    public class NewViewTypeViewHolder extends RecyclerView.ViewHolder {
        TextView titleLabel;
        LinearLayout layout;

        public NewViewTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleLabel = (TextView) itemView.findViewById(R.id.list_item_n_title);
            layout = (LinearLayout) itemView.findViewById(R.id.list_item_n_layout);
        }
    }

    public class SwitchTypeViewHolder extends RecyclerView.ViewHolder {
        TextView titleLabel;
        MamaSwitch toggler;

        public SwitchTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleLabel = (TextView) itemView.findViewById(R.id.list_item_s_title);
            toggler = (MamaSwitch) itemView.findViewById(R.id.list_item_s_switch);
        }
    }

    public class ItselfTypeViewHolder extends RecyclerView.ViewHolder {
        TextView titleLabel;
        LinearLayout layout;

        public ItselfTypeViewHolder(@NonNull View itemView) {
            super(itemView);

            titleLabel = (TextView) itemView.findViewById(R.id.list_item_is_title);
            layout = (LinearLayout) itemView.findViewById(R.id.list_item_is_layout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view;

        switch (viewType) {
            case SETTING_WITH_INPUT:
                view = inflater.inflate(R.layout.item_setting_list_with_input, parent, false);
                return new InputTypeViewHolder(view);
            case SETTING_WITH_NEW_VIEW:
                view = inflater.inflate(R.layout.item_setting_list_with_new_view, parent, false);
                return new NewViewTypeViewHolder(view);
            case SETTING_WITH_SWITCH:
                view = inflater.inflate(R.layout.item_setting_list_with_switch, parent, false);
                return new SwitchTypeViewHolder(view);
            case SETTING_WITH_ITSELF:
                view = inflater.inflate(R.layout.item_setting_list_with_itself, parent, false);
                return new ItselfTypeViewHolder(view);
            default:
                view = inflater.inflate(R.layout.item_setting_list_with_itself, parent, false);
                return new ItselfTypeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            global = MamaGlobal.getInstance();
            final Context context = global.getContext();
            int viewType = getItemViewType(position);

            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            TextView titleLabel;

            final SettingListItem item = data.get(position);

            if(holder instanceof InputTypeViewHolder) {
                final InputTypeViewHolder inputHolder = (InputTypeViewHolder) holder;
                inputHolder.titleLabel.setText(item.getTitle());

                inputHolder.input.setText(item.getCurrent());
                inputHolder.input.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        Log.d("TAG", "키 " + keyCode);
                        if(keyCode == KeyEvent.KEYCODE_ENTER) {
                            item.setCurrent(inputHolder.input.getText().toString());

                            Log.d("TAG", "엔터 입력");
                            InputMethodManager imm = (InputMethodManager) global.getContext().getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(inputHolder.input.getWindowToken(), 0);
                            if (data.get(position).getListener() != null) {
                                data.get(position).getListener().onItemEvent(v, position);
                            }

                            notifyDataSetChanged();
                            return true;
                        }

                        return false;
                    }
                });
            }

            if(holder instanceof NewViewTypeViewHolder) {
                NewViewTypeViewHolder newViewHolder = (NewViewTypeViewHolder) holder;
                newViewHolder.titleLabel.setText(item.getTitle());

                newViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.get(position).getListener() != null) {
                            data.get(position).getListener().onItemEvent(v, position);
                        }
                    }
                });
            }

            if(holder instanceof  SwitchTypeViewHolder) {
                final SwitchTypeViewHolder switchHolder = (SwitchTypeViewHolder) holder;
                switchHolder.titleLabel.setText(item.getTitle());

                switchHolder.toggler.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchHolder.toggler.onClick(v);

                        if (data.get(position).getListener() != null) {
                            data.get(position).getListener().onItemEvent(v, position);
                        }
                    }
                });

                if (data.get(position).getCurrent().equals("true")) {
                    switchHolder.toggler.setChecked(true);
                } else {
                    switchHolder.toggler.setChecked(false);
                }
            }

        if(holder instanceof ItselfTypeViewHolder) {
            ItselfTypeViewHolder itselfHolder = (ItselfTypeViewHolder) holder;
            itselfHolder.titleLabel.setText(item.getTitle());

            itselfHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.get(position).getListener() != null) {
                        data.get(position).getListener().onItemEvent(v, position);
                    }
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount()  {
        return data.size();
    }

    public SettingListItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getType();
    }

    public String getItemTitle(int position) {
        return data.get(position).getTitle();
    }

    public void addItem(String title, String current, int type, OnItemEventListener listener) {
        SettingListItem item = new SettingListItem();

        item.setTitle(title);

        if (type == SETTING_WITH_SWITCH) {
            if (!current.equals("true") && !current.equals("false")) {
                item.setCurrent("false");
            } else {
                item.setCurrent(current);
            }
        } else {
            item.setCurrent(current);
        }

        item.setType(type);
        item.setListener(listener);

        data.add(item);
    }

    public void addItem(String title, int type) {
        SettingListItem item = new SettingListItem();

        item.setTitle(title);
        item.setType(type);

        data.add(item);
    }

    public void addItem(String title, int type, OnItemEventListener listener) {
        SettingListItem item = new SettingListItem();

        item.setTitle(title);
        item.setType(type);
        item.setListener(listener);

        data.add(item);
    }

    public void updateItem(int position, SettingListItem item) {
        data.set(position, item);
    }
}