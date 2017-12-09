package com.kernelpanic.universitylabster.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.kernelpanic.universitylabster.models.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 09.12.2017.
 */

public class DetailsViewModel{
    public static Course course = new Course();
    public static List<String> attendance = new ArrayList<>();
    public static int courseId;
}
