package com.kernelpanic.universitylabster;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.fragments.SettingsFragment;
import com.kernelpanic.universitylabster.fragments.TimetableFragment;
import com.kernelpanic.universitylabster.fragments.WeekFragment;
import com.kernelpanic.universitylabster.models.Course;
import com.kernelpanic.universitylabster.models.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DashboardActivity extends AppCompatActivity {


    class NotificationComparator implements Comparator<Notification> {
        @Override
        public int compare(Notification n1, Notification n2) {
            return (int)(n1.date - n2.date) % Integer.MAX_VALUE;
        }
    }

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference notificationReference;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private WeekFragment weekFragment;
    private SettingsFragment settingsFragment;
    private TimetableFragment timetableFragment;

    static Context context;

    private NotificationCompat.Builder mBuilder;

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

        context = this;

        firebaseDatabase = FirebaseDatabase.getInstance();

        notificationReference = firebaseDatabase.getReference("notifications");

        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Notification> children = new ArrayList<>();

                for(DataSnapshot child: dataSnapshot.getChildren())
                    children.add(child.getValue(Notification.class));

                Collections.sort(children, new NotificationComparator());
                Collections.reverse(children);

                Notification last = children.size() >= 1 ? children.get(children.size() - 1) : null;

                if(last == null) return;
                if(last.user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) return;


                SharedPreferences prefs = getSharedPreferences("last_notification", MODE_PRIVATE);
                String lastNotifiedId = prefs.getString("last_id", null);

                if(last.id.equals(lastNotifiedId)) return;

                Intent acceptIntent = new Intent(DashboardActivity.this, ActionReciver.class);
                Intent declineIntent = new Intent(DashboardActivity.this, ActionReciver.class);

                acceptIntent.putExtra("action","1");
                acceptIntent.putExtra("id", String.valueOf(last.id));
                declineIntent.putExtra("action","decline");

                final PendingIntent piAccept = PendingIntent.getBroadcast(DashboardActivity.this, 1, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                final PendingIntent piDecline = PendingIntent.getBroadcast(DashboardActivity.this, 0, declineIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                mBuilder = new NotificationCompat.Builder(DashboardActivity.this);
                mBuilder.setSmallIcon(R.drawable.man_thinking);
                mBuilder.setContentTitle("Aproba/Refuza curs");
                mBuilder.setOngoing(false);
                mBuilder.setAutoCancel(true);
                mBuilder.setPriority(android.app.Notification.PRIORITY_DEFAULT);
                mBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
                mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(last.name + " " + last.location + "(" + last.location + ")"));
                mBuilder.addAction(R.drawable.ic_launcher_foreground,
                        "Accept", piAccept);
                mBuilder.addAction(R.drawable.ic_launcher_foreground,
                        "Decline", piDecline);

                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyMgr.notify(Integer.parseInt(last.id), mBuilder.build());
                Log.e("NOTIFY", last.id);

                SharedPreferences.Editor editor = getSharedPreferences("last_notification", MODE_PRIVATE).edit();
                editor.putString("last_id", last.id);
                editor.apply();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

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

    public static void cancelNotification(String id)
    {
        int idNotificare = Integer.valueOf(id);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(idNotificare);
    }
}
