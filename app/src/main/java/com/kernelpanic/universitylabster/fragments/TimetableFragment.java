package com.kernelpanic.universitylabster.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.adapters.TimetableAdapter;
import com.kernelpanic.universitylabster.adapters.WeekAdapter;
import com.kernelpanic.universitylabster.utilities.CalendarOperations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by andrei on 08.12.2017.
 */

public class TimetableFragment extends Fragment {

    @BindView(R.id.timeTable)
    ListView timeTable;

    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();
    public static ArrayList<String> location = new ArrayList<String>();

    public static ArrayList<String> readCalendarEvent(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        location.clear();
        for (int i = 0; i < CNames.length; i++) {

            String name = cursor.getString(1);

            if(name.startsWith("Curs ") || name.startsWith("Laborator")) {
                nameOfEvent.add(cursor.getString(1));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));
                location.add(cursor.getString(5));
                CNames[i] = cursor.getString(1);

            }

            cursor.moveToNext();
        }
        return nameOfEvent;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timetable_fragment, container, false);

        ButterKnife.bind(this, view);

        readCalendarEvent(TimetableFragment.this.getContext());

        final TimetableAdapter adapter = new TimetableAdapter(TimetableFragment.this.getContext(), nameOfEvent, startDates, endDates, descriptions, location);
        timeTable.setAdapter(adapter);

        return view;
    }
}
