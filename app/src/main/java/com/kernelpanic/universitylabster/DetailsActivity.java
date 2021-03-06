package com.kernelpanic.universitylabster;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.fragments.AttendanceFragment;
import com.kernelpanic.universitylabster.fragments.DetailsFragment;
import com.kernelpanic.universitylabster.fragments.MaterialsFragment;
import com.kernelpanic.universitylabster.models.Event;
import com.kernelpanic.universitylabster.utilities.ViewPagerAdapter;
import com.kernelpanic.universitylabster.viewmodels.DetailsViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private DetailsViewModel viewModel;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailsFragment(), "Detalii");
        adapter.addFragment(new AttendanceFragment(), "Prezenţă");
        adapter.addFragment(new MaterialsFragment(), "Mesaje");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        //viewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            //courseId = bundle.getInt("course");
            //viewModel.courseId = courseId;
            Query query = reference.child("courses").orderByKey().equalTo(String.valueOf(viewModel.courseId));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int id = -1;
                        for (DataSnapshot course : dataSnapshot.getChildren()) {
                            if(course != null) {
                                viewModel.course = course.getValue(Event.class);
                                id = course.getValue(Event.class).id;
                                break;
                            }
                        }

                        Query query2 = reference.child("attendance").child(String.valueOf(id));//.orderByKey().equalTo(String.valueOf(item.id));
                        query2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    List<String> users = new ArrayList<>();
                                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                                        users.add(user.getKey());
                                    }
                                    viewModel.attendance  = users;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
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
