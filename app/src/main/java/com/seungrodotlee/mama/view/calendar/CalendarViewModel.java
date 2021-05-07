package com.seungrodotlee.mama.view.calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CalendarViewModel extends ViewModel {
    private MutableLiveData<String> selectedDateInTimeline = new MutableLiveData<>();
    private MutableLiveData<String> selectedDateInCalendar = new MutableLiveData<>();

    public LiveData<String> getSelectedDateFromCalendar() {
        return selectedDateInCalendar;
    }

    public void setSelectedDateInCalendar(String data) {
        this.selectedDateInCalendar.setValue(data);
    }

    public LiveData<String> getSelectedDateFromTimeline() {
        return selectedDateInTimeline;
    }

    public void setSelectedDateInTimeline(String data) {
        this.selectedDateInTimeline.setValue(data);
    }
}
