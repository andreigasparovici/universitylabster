package com.kernelpanic.universitylabster.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.models.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 10.12.2017.
 */

public class TimetableAdapter extends ArrayAdapter<String>{

    private List<String> nameOfEvent, startDates, endDates, descriptions, location;

    public TimetableAdapter(Context context,
                            ArrayList<String> nameOfEvent,
                            ArrayList<String> startDates,
                            ArrayList<String> endDates,
                            ArrayList<String> descriptions,
                            ArrayList<String> location) {
        super(context, 0, nameOfEvent);

        this.nameOfEvent = nameOfEvent.size() < 20 ? nameOfEvent : nameOfEvent.subList(0, 20);
        this.startDates = startDates.size() < 20 ? startDates : startDates.subList(0, 20);
        this.endDates = endDates.size() < 20 ? endDates : endDates.subList(0, 20);
        this.descriptions = descriptions.size() < 20 ? descriptions : descriptions.subList(0, 20);
        this.location = location.size() < 20 ? location : location.subList(0, 20);
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        String event = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_timetable, parent, false);

        TextView name, start, end, desc, loc;

        name = convertView.findViewById(R.id.nameOfEvent);
        start = convertView.findViewById(R.id.startDate);
        end = convertView.findViewById(R.id.endDate);
        desc = convertView.findViewById(R.id.description);
        loc = convertView.findViewById(R.id.location);

        /*TextView courseName = convertView.findViewById(R.id.courseName);
        TextView courseTeacher = convertView.findViewById(R.id.courseTeacher);
        TextView courseDate = convertView.findViewById(R.id.courseDate);
        TextView courseLocation = convertView.findViewById(R.id.courseLocation);

        if (event == null) return convertView;

        courseName.setText(course.name);
        courseTeacher.setText(course.teacher);
        courseDate.setText(course.time);
        courseLocation.setText(course.location);*/

        name.setText(nameOfEvent.get(position));
        start.setText(startDates.get(position));
        end.setText(endDates.get(position));
        desc.setText(descriptions.get(position));
        loc.setText(location.get(position));


        return convertView;
    }

    public String getItem(int index) {
        return nameOfEvent.get(index);
    }

}
