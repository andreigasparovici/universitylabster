package com.kernelpanic.universitylabster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kernelpanic.universitylabster.fragments.WeekFragment;
import com.kernelpanic.universitylabster.models.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 08.12.2017.
 */

public class CourseAdapter extends ArrayAdapter<Course> {
    List<Course> courses;

    public CourseAdapter(Context context, ArrayList<Course> courses) {
        super(context, 0, courses);
        this.courses = courses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Course course = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_course, parent, false);
        }
        TextView courseName = convertView.findViewById(R.id.courseName);
        TextView courseTeacher = convertView.findViewById(R.id.courseTeacher);
        TextView courseDate = convertView.findViewById(R.id.courseDate);
        TextView courseLocation = convertView.findViewById(R.id.courseLocation);

        courseName.setText(course.name);
        courseTeacher.setText(course.teacher);
        courseDate.setText(course.time);
        courseLocation.setText(course.location);

        return convertView;
    }

    public Course getItem(int x) {
        Log.e("DEBUUUUUUUG", String.valueOf(x));
        return courses.get(x);
    }
}
