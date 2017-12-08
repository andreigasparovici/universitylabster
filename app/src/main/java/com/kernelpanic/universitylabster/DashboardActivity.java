package com.kernelpanic.universitylabster;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kernelpanic.universitylabster.fragments.SettingsFragment;
import com.kernelpanic.universitylabster.fragments.TimetableFragment;
import com.kernelpanic.universitylabster.fragments.WeekFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private WeekFragment weekFragment;
    private SettingsFragment settingsFragment;
    private TimetableFragment timetableFragment;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        weekFragment = new WeekFragment();
        timetableFragment = new TimetableFragment();
        settingsFragment = new SettingsFragment();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) return;

            fragmentManager.beginTransaction()
                .add(R.id.fragment_container, weekFragment)
                .commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_courses:
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, weekFragment).commit();
                        break;

                    case R.id.action_timetable:
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, timetableFragment).commit();
                        break;

                    case R.id.action_settings:
                        fragmentManager.beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
                        break;

                }
                return true;
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_item:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
