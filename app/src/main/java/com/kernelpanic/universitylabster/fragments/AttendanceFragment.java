package com.kernelpanic.universitylabster.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.R;
import com.kernelpanic.universitylabster.models.Event;
import com.kernelpanic.universitylabster.viewmodels.DetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andrei on 09.12.2017.
 */

public class AttendanceFragment extends Fragment {
    private DetailsViewModel viewModel;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @BindView(R.id.attendanceList)
    ListView attendanceList;

    @BindView(R.id.checkInButton)
    Button checkInButton;

    @OnClick(R.id.checkInButton)
    void checkIn() {
        reference.child(String.valueOf(viewModel.course.id)).child(user.getUid()).setValue(user.getDisplayName())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                reference.child(String.valueOf(viewModel.course.id)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> users = new ArrayList<>();
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            users.add(user.getValue(String.class));
                        }
                        viewModel.attendance  = users;
                        populateData();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    void populateData() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        viewModel.attendance.toArray(new String[viewModel.attendance.size()]));
        attendanceList.setAdapter(adapter);

        Event item = viewModel.course;
        if(item.up >= 5)
            checkInButton.setEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendance_fragment, container, false);

        ButterKnife.bind(this, view);

        populateData();

        return view;
    }
}
