package com.seungrodotlee.mama.element;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.seungrodotlee.mama.MamaFragment;
import com.seungrodotlee.mama.MamaGlobal;
import com.seungrodotlee.mama.R;
import com.seungrodotlee.mama.data.ProjectData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MamaCalendar extends LinearLayout {
    public static final int ONE_DAY_SELECT = 0;
    public static final int RANGE_SELECT = 1;
    public static final int IMMEDIATE_ONE_DAY_SELECT = 2;

    public static final int GRID_VIEW = 0;
    public static final int LINEAR_VIEW = 1;

    private MamaGlobal global;
    private ImageButton prevMonthBtn, nextMonthBtn;
    private TextView currentYearLabel, currentMonthLabel;
    private LinearLayout dateBox;
    private RecyclerView calendarBox;

    private Calendar calendar, selection, startPoint;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    private ArrayList<CalendarItem> selectedList = new ArrayList<CalendarItem>();
    private ArrayList<Integer> selectedListIndex = new ArrayList<Integer>();
    private int year, month, currentMonth, day, startDay, lastDay, selectMode, lastSelection = -1, startPointPosition = -1;
    private int viewMode = 0;
    private int scrollPosition = 0;
    private boolean loading = false;

    RecyclerView.SmoothScroller smoothScroller;

    private MamaCalendarAdapter adapter;

    OnItemClickListener listener;

    private MutableLiveData<Integer> currnetLastVisibleItem = new MutableLiveData<Integer>();

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }

    public class SelectedDate {
        private Calendar startDate, endDate;

        public SelectedDate(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Calendar getStartDate() {
            return startDate;
        }

        public Calendar getEndDate() {
            return endDate;
        }
    }

    public MamaCalendar(Context context) {
        this(context, null);
    }

    public MamaCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);

        global = MamaGlobal.getInstance();
        smoothScroller = new LinearSmoothScroller(context) {
            @Override
            protected int getHorizontalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 100f / displayMetrics.densityDpi;
            }
        };

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calendar_mama, this, true);

        selection = Calendar.getInstance();
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        currentMonth = month;
        day = calendar.get(Calendar.DATE);

        prevMonthBtn = (ImageButton) findViewById(R.id.calendar_mama_prev_month_btn);
        nextMonthBtn = (ImageButton) findViewById(R.id.calendar_mama_next_month_btn);

        currentYearLabel = (TextView) findViewById(R.id.calendar_mama_year_label);
        currentMonthLabel = (TextView) findViewById(R.id.calendar_mama_month_label);

        dateBox = (LinearLayout) findViewById(R.id.calendar_mama_date_box);
        calendarBox = (RecyclerView) findViewById(R.id.calendar_mama_calendar_box);
        calendarBox.setLayoutManager(new GridLayoutManager(global.getContext(), 7));

        adapter = new MamaCalendarAdapter();
        adapter.setYear(year);
        adapter.setMonth(month - 1);
        adapter.setOnItemClickListener(new MamaCalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (listener != null) {
                    listener.onItemClick(v, position);
                }

                CalendarItem item = adapter.getItem(position);

                if (item.getDay() == -1) {
                    return;
                }

                Log.d("TAG", "pos = " + position);
                Log.d("TAG", "last selection = " + lastSelection);

                selection.set(item.getYear(), item.getMonth(), item.getDay());

                Log.d("MC", "selection = " + format.format(selection.getTime()));

                switch (selectMode) {
                    case MamaCalendar.ONE_DAY_SELECT:
                        item.setSelection(true);
                        adapter.updateItem(position, item);

                        if (lastSelection != -1) {
                            item = adapter.getItem(lastSelection);
                            item.setSelection(false);
                            adapter.updateItem(lastSelection, item);
                        }

                        lastSelection = position;
                        break;
                    case MamaCalendar.RANGE_SELECT:
                        if ((startPointPosition == -1) || (selection.compareTo(startPoint) == -1)) {
                            if (selectedList.size() > 0) {
                                for (int i = 0; i < selectedList.size(); i++) {
                                    Log.d("TAG", "index = " + selectedListIndex.get(i));
                                    Log.d("TAG", "item = " + selectedList.get(i).getDay());
                                    CalendarItem checked = selectedList.get(i);
                                    checked.setSelection(false);
                                    if (!checked.getMsg().equals("")) {
                                        checked.setMsg("");
                                    }

                                    adapter.updateItem(selectedListIndex.get(i), checked);
                                }

                                selectedList.clear();
                                selectedListIndex.clear();
                            }

                            if (startPointPosition != -1) {
                                CalendarItem temp = adapter.getItem(startPointPosition);
                                temp.setSelection(false);
                                adapter.updateItem(startPointPosition, temp);
                            }

                            startPointPosition = position;
                            startPoint = (Calendar) selection.clone();
                            item.setSelection(true);
                            adapter.updateItem(position, item);
                        } else if (selection.compareTo(startPoint) == 1) {
                            Calendar c = Calendar.getInstance();

                            for (int i = startPointPosition; i <= position; i++) {
                                item = adapter.getItem(i);
                                item.setSelection(true);
                                adapter.updateItem(i, item);

                                selectedList.add(item);
                                selectedListIndex.add(i);
                            }

                            startPointPosition = -1;
                        }
                        break;
                    case MamaCalendar.IMMEDIATE_ONE_DAY_SELECT:
                        break;
                }

                adapter.notifyDataSetChanged();
            }
        });

        prevMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectMode == MamaCalendar.RANGE_SELECT) {
                    if ((startPointPosition != -1) || selection.compareTo(startPoint) == 1) {
                        Log.d("TAG", "셀렉션 데이터 초기화");
                        if (selectedList.size() > 0) {
                            for (int i = 0; i < selectedList.size(); i++) {
                                Log.d("TAG", "index = " + selectedListIndex.get(i));
                                Log.d("TAG", "item = " + selectedList.get(i).getDay());
                                CalendarItem checked = selectedList.get(i);
                                checked.setSelection(false);
                                adapter.updateItem(selectedListIndex.get(i), checked);
                            }
                        }

                        selectedList.clear();
                        selectedListIndex.clear();

                        startPointPosition = -1;
                        startPoint = Calendar.getInstance();
                    }
                }

                lastSelection = -1;
                Log.d("TAG", String.valueOf(calendarBox.getWidth()));
                if (month > 1) {
                    month--;
                    currentMonthLabel.setText("" + month);
                } else {
                    year--;
                    month = 12;
                    currentYearLabel.setText("" + year);
                    currentMonthLabel.setText("" + month);
                    adapter.setYear(year);
                }

                adapter.setMonth(month - 1);

                drawCalendar();
            }
        });

        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelection = -1;
                if (month < 12) {
                    month++;
                    currentMonthLabel.setText("" + month);
                } else {
                    year++;
                    month = 1;
                    currentYearLabel.setText("" + year);
                    currentMonthLabel.setText("" + month);
                    adapter.setYear(year);
                }

                adapter.setMonth(month - 1);

                drawCalendar();

                if (selectMode == MamaCalendar.RANGE_SELECT) {
                    Log.d("TAG", "compare = " + selection.compareTo(startPoint));
                    Log.d("TAG", "start point = " + startPoint);
                    Log.d("TAG", "spp = " + startPointPosition);

                    if (selectedList.size() > 0) {
                        Log.d("TAG", "셀렉션 데이터 초기화");
                        for (int i = 0; i < selectedList.size(); i++) {
                            Log.d("TAG", "index = " + selectedListIndex.get(i));
                            Log.d("TAG", "item = " + selectedList.get(i).getDay());
                            CalendarItem checked = selectedList.get(i);
                            checked.setSelection(false);
                            adapter.updateItem(selectedListIndex.get(i), checked);

                            selectedList.clear();
                            selectedListIndex.clear();
                        }

                        startPointPosition = -1;
                        startPoint = Calendar.getInstance();
                    } else {
                        Log.d("MC", "start point = " + format.format(startPoint.getTime()));
                        if (startPointPosition == -1) {
                            return;
                        }

                        startPointPosition = 0;

                        int sum = adapter.getItem(0).getDay() + adapter.getItem(1).getDay() + adapter.getItem(2).getDay();
                        CalendarItem item;

                        switch (sum) {
                            case 3:
                                break;
                            case 1:
                            case -1:
                                item = adapter.getItem(0);
                                item.setMsg("~");
                                adapter.updateItem(0, item);
                                break;
                            default:
                                Log.d("TAG", "여기");
                                item = adapter.getItem(0);
                                item.setMsg((startPoint.get(Calendar.MONTH) + 1) + ".");
                                adapter.updateItem(0, item);
                                item = adapter.getItem(1);
                                item.setMsg("" + startPoint.get(Calendar.DATE));
                                adapter.updateItem(1, item);
                                item = adapter.getItem(2);
                                item.setMsg("~");
                                adapter.updateItem(2, item);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w != oldw) {
            float ratio = (float) this.getWidth() / global.getScreenWidth();

            currentYearLabel.setTextSize(24 * ratio);
            currentMonthLabel.setTextSize(24 * ratio);
            for(int i = 0; i < dateBox.getChildCount(); i++) {
               ((TextView) dateBox.getChildAt(i)).setTextSize(16 * ratio);
            }

            adapter.setSize(ratio);
            show();

            this.post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLayoutManager(int viewMode) {
        this.viewMode = viewMode;

        if (viewMode == GRID_VIEW) {
            calendarBox.setLayoutManager(new GridLayoutManager(global.getContext(), 7));
            prevMonthBtn.setVisibility(VISIBLE);
            nextMonthBtn.setVisibility(VISIBLE);
        } else {
            calendarBox.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            prevMonthBtn.setVisibility(GONE);
            nextMonthBtn.setVisibility(GONE);
            dateBox.setVisibility(GONE);
        }
    }

    private int loadMonthCall = 0;

    private void loadPrevMonth() {
        final Calendar c = Calendar.getInstance();

        if(month == 0) {
            year--;
            c.set(Calendar.YEAR, year);
            month = 12;
        }

        c.set(Calendar.MONTH, month - 1);

        Log.d("REG", "month = " + (month));

        for (int i = 0; i < c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            registDay(i, i + 1);
        }

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                calendarBox.scrollToPosition(c.getActualMaximum(Calendar.DATE) + 6);
            }
        }, 10);
    }

    private void loadNextMonth() {
        Calendar c = Calendar.getInstance();

        month++;

        if(month == 13) {
            year++;
            c.set(Calendar.YEAR, year);
            month = 1;
        }

        Log.d("REG", "month = " + (month));

        c.set(Calendar.MONTH, month - 1);

        for (int i = 0; i < c.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            registDay(i + 1);
        }

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        }, 10);
    }

    public void registryScrollListener(MamaFragment f) {
        calendarBox.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) calendarBox.getLayoutManager();

                if (dx > 0) {
                    month = adapter.getItem(layoutManager.findLastCompletelyVisibleItemPosition()).getMonth() + 1;

                    if (month < 10) {
                        currentMonthLabel.setText("0" + (month));
                    } else {
                        currentMonthLabel.setText("" + (month));
                    }

                    year = adapter.getItem(layoutManager.findLastCompletelyVisibleItemPosition()).getYear();
                    currentYearLabel.setText("" + year);
                } else {
                    month = adapter.getItem(layoutManager.findFirstVisibleItemPosition()).getMonth();

                    if (month + 1 < 10) {
                        currentMonthLabel.setText("0" + (month + 1));
                    } else {
                        currentMonthLabel.setText("" + (month + 1));
                    }

                    year = adapter.getItem(layoutManager.findFirstCompletelyVisibleItemPosition()).getYear();
                    currentYearLabel.setText("" + year);
                }

                Log.d("SCROLL", "year = " + year + " month = " + month);

                if (!loading) {
                    if ((dx > 0) && (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1)) {
                        loading = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loading = false;
                            }
                        }, 3000);

                        Log.d("SCROLL", "끝");

                        loadNextMonth();
                    } else if ((dx < 0) && (layoutManager.findFirstCompletelyVisibleItemPosition() == 0)) {
                        loading = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loading = false;
                            }
                        }, 3000);

                        Log.d("SCROLL", "시");
                        loadPrevMonth();
                    }
                }

            }
        });
    }

    public void setSelectMode(int mode) {
        selectMode = mode;
    }

    public String getDate(int position) {
        CalendarItem item = adapter.getItem(position);
        String month, day;

        if(item.getMonth() < 9) {
            month = "0" + (item.getMonth() + 1);
        } else {
            month = "" + (item.getMonth() + 1);
        }

        if(item.getDay() < 9) {
            day = "0" + item.getDay();
        } else {
            day = "" + item.getDay();
        }

        return item.getYear() + "." + month + "." + day;
    }

    public SelectedDate getSelectedDate() {
        if (selectMode == ONE_DAY_SELECT) {
            return new SelectedDate(selection, selection);
        }

        return new SelectedDate(startPoint, selection);
    }

    private void drawCalendar() {
        currentYearLabel.setText("" + year);

        if (month >= 10) {
            currentMonthLabel.setText("" + month);
        } else {
            currentMonthLabel.setText("0" + month);
        }

        adapter.clearItem();

        calendar.set(year, month - 1, 1);
        startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (viewMode == LINEAR_VIEW) {
            startDay = 0;
        }

        if (startDay != 0) {
            for (int i = 0; i < startDay; i++) {
                adapter.addItem(-1, CalendarItem.NORMAL);
            }
        }

        for (int i = startDay; i < startDay + lastDay; i++) {
            registDay(i - startDay + 1);
        }

        if (viewMode == GRID_VIEW) {
            for (int i = startDay + lastDay; i < 42; i++) {
                adapter.addItem(-1, CalendarItem.NORMAL);
            }
        }

        if ((viewMode == GRID_VIEW) && (selectMode == ONE_DAY_SELECT) && (month == currentMonth)) {
            selectToday();
        }

        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                calendarBox.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }, 1);
    }

    public void registDay(int today) {
        registDay(-1, today);
    }

    public void registDay(int position, int today) {
        CalendarItem item = new CalendarItem();

        item.setYear(year);
        item.setMonth(month - 1);
        item.setDay(today);

        ArrayList<ProjectData> projectData = global.getProjectDataList();

        calendar.set(year, month - 1, today);

        boolean projectPeriod = false;

        int layer = 0;
        for (int j = 0; j < projectData.size(); j++) {
            String projectStart = projectData.get(j).startpoint;
            String projectEnd = projectData.get(j).endpoint;

            try {
                if ((calendar.getTime().compareTo(format.parse(projectStart)) >= 0) && (calendar.getTime().compareTo(format.parse(projectEnd)) <= 0)) {
                    projectPeriod = true;
                    layer++;
                } else if ((calendar.getTime().compareTo(format.parse(projectEnd)) == 1) && format.format(calendar.getTime()).equals(projectEnd)) {
                    projectPeriod = true;
                    layer++;
                } else if ((calendar.getTime().compareTo(format.parse(projectStart)) == -1) && format.format(calendar.getTime()).equals(projectStart)) {
                    projectPeriod = true;
                    layer++;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (projectPeriod) {
            double divParent;

            if (projectData.size() > 6) {
                divParent = 6;
            } else {
                divParent = projectData.size();
            }

            double ratio = (double) layer / divParent;
            item.setBackground(adapter.getBackgroundColor(ratio));

            if (today == 1) {
                item.setEvent(CalendarItem.IMPORTANT_DAY_IN_PROJECT);
            } else {
                item.setEvent(CalendarItem.PROJECT_PERIOD);
            }
        }

        item.setDate(calendar.get(Calendar.DAY_OF_WEEK));

        if (position != -1) {
            adapter.addItemIn(position, item);
        } else {
            adapter.addItem(item);
        }
    }

    public void show() {
        drawCalendar();

        if (selectMode == RANGE_SELECT) {
            selectToday();
        }
    }

    private void selectToday() {
        int position = startDay + day - 1;
        selection.set(year, month - 1, day);
        CalendarItem item = adapter.getItem(position);
        item.setSelection(true);
        adapter.updateItem(position, item);

        lastSelection = position;

        startPointPosition = position;
        startPoint = (Calendar) selection.clone();

        adapter.notifyDataSetChanged();
    }

    public void setDate(final String date) {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                CalendarItem item = null;
                int position = -1;

                try {
                    selection.setTime(format.parse(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("SETDATE", "original string = " + date);
                Log.d("SETDATE", "set time " + format.format(selection.getTime()));
                Log.d("SETDATE", "c get = " + selection.get(Calendar.MONTH));
                Log.d("SETDATE", "month = " + month);
                if ((selection.get(Calendar.YEAR) == year) && (selection.get(Calendar.MONTH) + 1 == month)) {

                } else {
                    year = selection.get(Calendar.YEAR);
                    month = selection.get(Calendar.MONTH) + 1;
                    drawCalendar();
                }

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (getDate(i).equals(date)) {
                        item = adapter.getItem(i);
                        position = i;
                        break;
                    }
                }

                Log.d("MC", "selection = " + format.format(selection.getTime()));

                if (item == null) {
                    Log.d("MC", "오류남");
                    return;
                }


                item.setSelection(true);
                adapter.updateItem(position, item);

                if (lastSelection != -1) {
                    item = adapter.getItem(lastSelection);
                    item.setSelection(false);
                    adapter.updateItem(lastSelection, item);
                }

                lastSelection = position;
                adapter.notifyDataSetChanged();


                if (viewMode == LINEAR_VIEW) {
                    scrollPosition = position - 3;

                    smoothScroller.setTargetPosition(scrollPosition);
                    calendarBox.getLayoutManager().startSmoothScroll(smoothScroller);
                }
            }
        }, 100);
    }
}