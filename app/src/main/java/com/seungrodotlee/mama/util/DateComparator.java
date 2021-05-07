package com.seungrodotlee.mama.util;

import com.seungrodotlee.mama.data.MakerData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateComparator implements Comparator<MakerData>
{
    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
    public int compare(MakerData one, MakerData another) {
        int result = 1;

        try {
            result = format.parse(one.getDate()).compareTo(format.parse(another.getDate()));
            if(result == 0) {
                result = 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}