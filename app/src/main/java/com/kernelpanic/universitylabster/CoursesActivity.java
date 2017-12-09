package com.kernelpanic.universitylabster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.adapters.EventAdapter;
import com.kernelpanic.universitylabster.adapters.WeekAdapter;
import com.kernelpanic.universitylabster.models.Event;
import com.kernelpanic.universitylabster.viewmodels.DetailsViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CoursesActivity extends AppCompatActivity {

    @BindView(R.id.courseList)
    ListView courseList;

    @BindView(R.id.title)
    TextView title;

    String day = "";

    @OnClick(R.id.addCourseButton)
    void addCourse() {
        Intent intent = new Intent(CoursesActivity.this, AddEventActivity.class);
        Bundle b = new Bundle();
        b.putInt("day", getIndex(day));
        intent.putExtras(b);
        startActivity(intent);
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
   // DatabaseReference reference = database.getReference("courses");
    DetailsViewModel viewModel;

    int getIndex(String day) {
        List<String> d = Arrays.asList(WeekAdapter.days);
        return d.indexOf(day) + 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        //viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        if(b != null)
            day = b.getString("day");

        title.setText("Vezi cursurile de " + day.toLowerCase() + ":");

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Se încarcă datele")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        DatabaseReference reference = database.getReference("courses");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dialog.isCancelled())
                    dialog.cancel();

                if (dataSnapshot.exists()) {
                    ArrayList<Event> courses = new ArrayList<>();

                    for (DataSnapshot course : dataSnapshot.getChildren()) {

                        Event c = course.getValue(Event.class);
                        if(c == null) continue;

                        c.id = Integer.valueOf(course.getKey());
                        Log.e("DEBUG", String.valueOf(c.id));
                        if (c.day == getIndex(day))
                            courses.add(c);
                    }

                    EventAdapter courseAdapter = new EventAdapter(CoursesActivity.this, courses);
                    courseList.setAdapter(courseAdapter);

                    if(courses.size() == 0) {
                        Toast.makeText(CoursesActivity.this, "Nu există cursuri!", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "ZEROO");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapter, View v, int position, long x){
                Event item = (Event)adapter.getItemAtPosition(position);

                Intent intent = new Intent(CoursesActivity.this, DetailsActivity.class);
                //Bundle b = new Bundle();

                viewModel.course = item;
                viewModel.courseId = item.id;
                //DetailsViewModel.courseId = item.id;

                b.putBoolean("enabled", item.up >= 5);

                //intent.putExtras(b);
                startActivity(intent);
            }
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

    @Override
    protected void onRestart() {
        super.onRestart();
        DatabaseReference reference = database.getReference("courses");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Event> courses = new ArrayList<>();

                    for (DataSnapshot course : dataSnapshot.getChildren()) {

                        Event c = course.getValue(Event.class);
                        c.id = Integer.valueOf(course.getKey());
                        Log.e("DEBUG", String.valueOf(c.id));
                        if (c.day == getIndex(day))
                            courses.add(c);
                    }

                    EventAdapter courseAdapter = new EventAdapter(CoursesActivity.this, courses);
                    courseList.setAdapter(courseAdapter);

                    if(courses.size() == 0) {
                        Toast.makeText(CoursesActivity.this, "Nu există cursuri!", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "ZEROO");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
