package com.kernelpanic.universitylabster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.viewmodels.DetailsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyEventActivity extends AppCompatActivity {

    @BindView(R.id.eventName)
    EditText eventName;

    @BindView(R.id.teacherName)
    EditText teacherName;

    @BindView(R.id.location)
    EditText location;

    @BindView(R.id.year)
    EditText year;

    @BindView(R.id.day)
    EditText day;

    @BindView(R.id.begin)
    Button begin;

    @BindView(R.id.end)
    Button end;

    @OnClick(R.id.modifyButton)
    void modify() {
        reference.child("location").setValue(location.getText().toString());
        reference.child("name").setValue(eventName.getText().toString());
        reference.child("teacher").setValue(teacherName.getText().toString());
        reference.child("year").setValue(Integer.valueOf(year.getText().toString()));
        reference.child("day").setValue(Integer.valueOf(year.getText().toString()));
        Toast.makeText(ModifyEventActivity.this, "Modificat", Toast.LENGTH_SHORT).show();
    }

    int eventId;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_event);

        ButterKnife.bind(this);

        eventId = DetailsViewModel.courseId;

        reference = database.getReference("courses").child(String.valueOf(eventId));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String locationValue = dataSnapshot.child("location").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);
                String teacher = dataSnapshot.child("teacher").getValue(String.class);
                Long yearValue = dataSnapshot.child("year").getValue(Long.class);
                Integer dayValue =  dataSnapshot.child("day").getValue(Integer.class);

                location.setText(locationValue);
                eventName.setText(name);
                teacherName.setText(teacher);
                year.setText(String.valueOf(yearValue));
                day.setText(String.valueOf(dayValue));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {  }
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
