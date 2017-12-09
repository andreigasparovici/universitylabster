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
 * Created by andrei on 08.12.2017.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    private List<Event> eventList;

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        eventList = events;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Event course = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_course, parent, false);

        TextView courseName = convertView.findViewById(R.id.courseName);
        TextView courseTeacher = convertView.findViewById(R.id.courseTeacher);
        TextView courseDate = convertView.findViewById(R.id.courseDate);
        TextView courseLocation = convertView.findViewById(R.id.courseLocation);

        if (course == null) return convertView;

        courseName.setText(course.name);
        courseTeacher.setText(course.teacher);
        courseDate.setText(course.time);
        courseLocation.setText(course.location);

        if(course.up < 5)
            convertView.setBackgroundColor(Color.rgb(255,255,153));

        return convertView;
    }

    public Event getItem(int index) {
        return eventList.get(index);
    }
}
