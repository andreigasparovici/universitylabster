package com.kernelpanic.universitylabster;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEventActivity extends AppCompatActivity {

    FirebaseUser user;

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

    @BindView(R.id.isLaboratory)
    RadioButton isLaboratory;

    @BindView(R.id.isCourse)
    RadioButton isCourse;

    @BindView(R.id.editTextYear)
    EditText year;

    @BindView(R.id.editTextSection)
    EditText section;

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
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, myTimeListener, hour, minute, true);
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, myTimeListener, hour, minute, true);
        timePickerDialog.show();
    }

    @OnClick(R.id.addCourseButton)
    void addCourse() {
        int randomShit = ThreadLocalRandom.current().nextInt(0, 10000000);
        Log.e("RANDOM", String.valueOf(randomShit));

        long date = System.currentTimeMillis();

        final String
                teacher = editTeacher.getText().toString();
        final String location = editLocation.getText().toString();
        String name = editName.getText().toString();

        boolean ok = true;
        if(name.length()<5){ok=false;editName.setError("Invalid");}
        Map<String, Object> data = new HashMap<>();
        data.put("teacher", teacher);
        data.put("location", location);

        if(isCourse.isChecked()) {
            name = "Curs " + name;
        } else {
            name = "Laborator " + name;
        }
        data.put("name", name);
        data.put("time", viewStart.getText().toString() + "-" + viewStop.getText().toString());
        data.put("day", dayIndex);
        data.put("up", 0);
        data.put("date", date);
        data.put("year", Integer.parseInt(year.getText().toString()));
        data.put("section", section.getText().toString());

        databaseReference.child(String.valueOf(randomShit)).setValue(data);


        Map<String, Object> notification = new HashMap<>();
        notification.put("teacher", editTeacher.getText().toString());
        notification.put("location", editLocation.getText().toString());
        notification.put("name", name);
        notification.put("time", viewStart.getText().toString() + "-" + viewStart.getText().toString());
        notification.put("id", String.valueOf(randomShit));
        notification.put("user_id",  user.getUid());
        notification.put("date", date);
        notification.put("year", Integer.parseInt(year.getText().toString()));
        notification.put("section", section.getText().toString());

        firebaseDatabase.getReference("notifications").child(String.valueOf(randomShit)).setValue(notification);

        Toast.makeText(AddEventActivity.this, "Cursul a fost adăugat!", Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        user = FirebaseAuth.getInstance().getCurrentUser();

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
