package com.kernelpanic.universitylabster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.fragments.WeekFragment;
import com.kernelpanic.universitylabster.models.Course;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoursesActivity extends AppCompatActivity {

    @BindView(R.id.courseList)
    ListView courseList;

    @BindView(R.id.title)
    TextView title;

    String day = "";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("courses");

    int getIndex(String day) {
        List<String> d = Arrays.asList(WeekAdapter.days);
        return d.indexOf(day) + 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        if(b != null)
            day = b.getString("day");

        title.setText("Vezi cursurile de " + day.toLowerCase() + ":");

        Log.e("DEBUG", String.valueOf(getIndex(day)));

//        final MaterialDialog dialog = new MaterialDialog.Builder(this)
//                .content("Se încarcă datele")
//                .progress(true, 0)
//                .show();

        DatabaseReference reference = database.getReference("courses");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               // if(!dialog.isCancelled())
                ///    dialog.cancel();

                if (dataSnapshot.exists()) {
                    ArrayList<Course> courses = new ArrayList<>();

                    for (DataSnapshot course : dataSnapshot.getChildren()) {
                        Course c = course.getValue(Course.class);
                        if (c.day == getIndex(day))
                            courses.add(course.getValue(Course.class));
                    }

                    CourseAdapter courseAdapter = new CourseAdapter(CoursesActivity.this, courses);
                    courseList.setAdapter(courseAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
