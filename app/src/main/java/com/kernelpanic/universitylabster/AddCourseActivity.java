package com.kernelpanic.universitylabster;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCourseActivity extends AppCompatActivity {

    int dayIndex = -1;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @BindView(R.id.editName)
    EditText editName;

    @BindView(R.id.editLocation)
    EditText editLocation;

    @BindView(R.id.editTeacher)
    EditText editTeacher;

    @BindView(R.id.viewStart)
    TextView viewStart;

    @BindView(R.id.viewStop)
    TextView viewStop;

    @OnClick(R.id.startPicker)
    void pickStart() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    viewStart.setText(hourOfDay + ":" + minute);
                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddCourseActivity.this, myTimeListener, hour, minute, true);
        timePickerDialog.show();
    }

    @OnClick(R.id.stopPicker)
    void pickStop() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    viewStop.setText(hourOfDay + ":" + minute);

                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddCourseActivity.this, myTimeListener, hour, minute, true);
        timePickerDialog.show();
    }

    @OnClick(R.id.addCourseButton)
    void addCourse() {
        int randomShit = ThreadLocalRandom.current().nextInt(0, 10000000);

        Map<String, Object> data = new HashMap<>();
        data.put("teacher", editTeacher.getText().toString());
        data.put("location", editLocation.getText().toString());
        data.put("name", editName.getText().toString());
        data.put("time", viewStart.getText().toString() + "-" + viewStart.getText().toString());
        data.put("day", dayIndex);
        data.put("up", 0);


        databaseReference.child(String.valueOf(randomShit)).setValue(data);

        // Add notification

        Map<String, Object> notification = new HashMap<>();
        notification.put("teacher", editTeacher.getText().toString());
        notification.put("location", editLocation.getText().toString());
        notification.put("name", editName.getText().toString());
        notification.put("time", viewStart.getText().toString() + "-" + viewStart.getText().toString());
        notification.put("id", String.valueOf(randomShit));
        firebaseDatabase.getReference("notifications").child(String.valueOf(randomShit)).setValue(notification);


        Toast.makeText(AddCourseActivity.this, "Cursul a fost adăugat!", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();

        if(b != null)
            dayIndex = b.getInt("day");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("courses");
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
