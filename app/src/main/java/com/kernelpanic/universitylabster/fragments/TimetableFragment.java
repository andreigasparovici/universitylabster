package com.kernelpanic.universitylabster.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.utilities.CalendarOperations;

import butterknife.ButterKnife;

/**
 * Created by andrei on 08.12.2017.
 */

public class TimetableFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timetable_fragment, container, false);

        ButterKnife.bind(this, view);

        return view;
    }
}
