package com.kernelpanic.universitylabster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.models.Course;
import com.kernelpanic.universitylabster.models.Note;

import java.util.ArrayList;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @BindView(R.id.userList)
    ListView userList;

    @BindView(R.id.checkInButton)
    Button checkInButton;

    @OnClick(R.id.checkInButton)
    void checkIn() {
        FirebaseDatabase.getInstance().getReference("attendance").child(String.valueOf(courseId))
                .child(firebaseUser.getUid())
                .setValue(firebaseUser.getDisplayName());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> result = new ArrayList<>();
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        result.add(user.getValue(String.class));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter(DetailsActivity.this, android.R.layout.simple_list_item_1, result);
                    userList.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    int courseId = -1;
    boolean enabled = true;

    @BindView(R.id.notesButton)
    Button notesButton;

    @OnClick(R.id.notesButton)
    void gotToNotes() {
        Intent intent = new Intent(DetailsActivity.this, NotesActivity.class);
        Bundle b = new Bundle();
        b.putString("course_id", String.valueOf(courseId));

        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            courseId = b.getInt("course");
            enabled = b.getBoolean("enabled");
        }

        if(!enabled) {
            notesButton.setEnabled(false);
            checkInButton.setEnabled(false);
        }



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("attendance").child(String.valueOf(courseId));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ArrayList<String> result = new ArrayList<>();
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        result.add(user.getValue(String.class));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter(DetailsActivity.this, android.R.layout.simple_list_item_1, result);
                    userList.setAdapter(adapter);
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
