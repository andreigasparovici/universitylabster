package com.kernelpanic.universitylabster;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kernelpanic.universitylabster.fragments.SettingsFragment;
import com.kernelpanic.universitylabster.fragments.SocialFragment;
import com.kernelpanic.universitylabster.fragments.TimetableFragment;
import com.kernelpanic.universitylabster.fragments.WeekFragment;
import com.kernelpanic.universitylabster.models.Event;
import com.kernelpanic.universitylabster.models.NewEventNotification;
import com.kernelpanic.universitylabster.utilities.ActionReciver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.WRITE_CALENDAR;


public class DashboardActivity extends AppCompatActivity {

    class NotificationComparator implements Comparator<NewEventNotification> {
        @Override
        public int compare(NewEventNotification n1, NewEventNotification n2) {
            return (int)(n1.date - n2.date) % Integer.MAX_VALUE;
        }
    }

    public static Context context;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;

    DatabaseReference notificationReference, courseReference;

    FragmentManager fragmentManager = getSupportFragmentManager();

    WeekFragment weekFragment;
    SettingsFragment settingsFragment;
    TimetableFragment timetableFragment;
    SocialFragment socialFragment;

    NotificationCompat.Builder mBuilder;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }

    void firebaseInit() {
        firebaseDatabase = FirebaseDatabase.getInstance();

        notificationReference = firebaseDatabase.getReference("notifications");
        courseReference = firebaseDatabase.getReference("courses");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    void uiInit() {
        weekFragment = new WeekFragment();
        timetableFragment = new TimetableFragment();
        settingsFragment = new SettingsFragment();
        socialFragment = new SocialFragment();

        if (findViewById(R.id.fragment_container) != null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, weekFragment)
                    .commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
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

                case R.id.action_others:
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, socialFragment).commit();

            }
            return true;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        firebaseInit();
        uiInit();

        context = this;

        notificationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<NewEventNotification> children = new ArrayList<>();

                for(DataSnapshot child: dataSnapshot.getChildren())
                    children.add(child.getValue(NewEventNotification.class));

                Collections.sort(children, new NotificationComparator());
                Collections.reverse(children);

                NewEventNotification last = children.size() >= 1 ? children.get(0) : null;

               if(last == null) return;

                SharedPreferences prefs = getSharedPreferences("last_notification", MODE_PRIVATE);
                String lastNotifiedId = prefs.getString("last_id", null);

                if(last.id.equals(lastNotifiedId)) return;

                if(last.user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) return;

                Intent acceptIntent = new Intent(DashboardActivity.this, ActionReciver.class);
                Intent declineIntent = new Intent(DashboardActivity.this, ActionReciver.class);

                acceptIntent.putExtra("action","1");
                acceptIntent.putExtra("id", String.valueOf(last.id));
                declineIntent.putExtra("action","decline");
                declineIntent.putExtra("id", String.valueOf(last.id));

                final PendingIntent piAccept = PendingIntent.getBroadcast(DashboardActivity.this, 1, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                final PendingIntent piDecline = PendingIntent.getBroadcast(DashboardActivity.this, 0, declineIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                mBuilder = new NotificationCompat.Builder(DashboardActivity.this);
                mBuilder.setSmallIcon(R.drawable.man_thinking);
                mBuilder.setContentTitle("Aproba/Ignora laborator/curs");
                mBuilder.setOngoing(false);
                mBuilder.setAutoCancel(true);
                mBuilder.setPriority(android.app.Notification.PRIORITY_DEFAULT);
                mBuilder.setDefaults(android.app.Notification.DEFAULT_ALL);
                mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(last.name + " " + last.location + "(" + last.location + ")"));
                mBuilder.addAction(R.drawable.ic_launcher_foreground,
                        "Aprob", piAccept);
                mBuilder.addAction(R.drawable.ic_launcher_foreground,
                        "Ignore", piDecline).setVisibility(View.GONE);

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

        courseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> courses = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event c = snapshot.getValue(Event.class);
                    if (c == null) continue;
                    if(c.up >= 5)
                    courses.add(c);
                }

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(DashboardActivity.this);

                for(Event event : courses) {
                    int startHour, startMinute, endHour, endMinute;

                    StringTokenizer strtok = new StringTokenizer(event.time, "-");
                    String startTime = strtok.nextToken();
                    String endTime = strtok.nextToken();

                    strtok = new StringTokenizer(startTime, ":");
                    startHour = Integer.valueOf(strtok.nextToken());
                    startMinute = Integer.valueOf(strtok.nextToken());

                    strtok = new StringTokenizer(endTime, ":");
                    endHour = Integer.valueOf(strtok.nextToken());
                    endMinute = Integer.valueOf(strtok.nextToken());

                    if (!settings.contains(event.name)) {
                        Long insertedId = addEvent(
                            new GregorianCalendar(new Date().getYear(), new Date().getMonth() - 1, new Date().getDate()),
                            firebaseUser.getEmail(),
                            event.location,
                            event.name,
                            event.teacher,
                            startHour, startMinute, endHour, endMinute);



                        SharedPreferences.Editor edit = settings.edit();
                        edit.putString(event.name, String.valueOf(insertedId));
                        edit.apply();
                    }
                }

                /*AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (Course course : courses) {
                            Event event = db.eventDao().getById(course.id);
                            if (event == null) {
                                notifications.add(course);
                            }
                        }

                        for (Course notification : notifications) {
                            NotificationManager mNotificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            Log.e("DEBUG", notification.name);

                            int startHour, startMinute, endHour, endMinute;

                            StringTokenizer strtok = new StringTokenizer(notification.time, "-");
                            String startTime = strtok.nextToken();
                            String endTime = strtok.nextToken();

                            strtok = new StringTokenizer(startTime, ":");
                            startHour = Integer.valueOf(strtok.nextToken());
                            startMinute = Integer.valueOf(strtok.nextToken());

                            strtok = new StringTokenizer(endTime, ":");
                            endHour = Integer.valueOf(strtok.nextToken());
                            endMinute = Integer.valueOf(strtok.nextToken());

                            CalendarOperations.getInstance().addEvent(
                                    DashboardActivity.this,
                                    DashboardActivity.context,
                                    new GregorianCalendar(2017, 11, 10),
                                    firebaseUser.getEmail(),
                                    notification.location,
                                    notification.name,
                                    notification.teacher,
                                    startHour, startMinute, endHour, endMinute);

                            Toast.makeText(DashboardActivity.this, notification.name, Toast.LENGTH_SHORT).show();

                            try {
                                db.eventDao().insertOne(new Event(notification.id, notification.day, notification.location, notification.up));
                            }catch (Exception ex) {
                                Log.e("SQL ERROR", ex.getMessage());
                            }
                        }
                    }
                });*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
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

    public static void cancelNotification(String id) {
        int idNotificare = Integer.valueOf(id);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(idNotificare);
    }

    public long addEvent(Calendar cal, String account, String location, String title, String description, Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {


        long calId = getCalendarId( account);
        if (calId == -1) {
            Log.e("shit", "nu se face");
            // no calendar account; react meaningfully
            return -1;
        }
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.HOUR, startHour - 2);
        cal.set(Calendar.MINUTE, startMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.HOUR, endHour - 2);
        cal.set(Calendar.MINUTE, endMinute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long end = cal.getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, start);
        values.put(CalendarContract.Events.DTEND, end);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Romania/Bucharest");
        values.put(CalendarContract.Events.DESCRIPTION,
                description);
// reasonable defaults exist:
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS,
                CalendarContract.Events.STATUS_CONFIRMED);
        values.put(CalendarContract.Events.ALL_DAY, 0);
        values.put(CalendarContract.Events.ORGANIZER, account);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, 1);
        values.put(CalendarContract.Events.GUESTS_CAN_MODIFY, 1);
        values.put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        if (ActivityCompat.checkSelfPermission(this, WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        Uri uri =
                getContentResolver().
                        insert(CalendarContract.Events.CONTENT_URI, values);
        //Log.e("event", "created");
        long eventId = new Long(uri.getLastPathSegment());

        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
        values = new ContentValues();
        values.put("event_id", eventId);
        values.put("method", 1);
        values.put("minutes", 60);
        getContentResolver().insert(REMINDERS_URI, values);
        REMINDERS_URI = Uri.parse(getCalendarUriBase() + "reminders");
        values = new ContentValues();
        values.put("event_id", eventId);
        values.put("method", 1);
        values.put("minutes", 1440);
        getContentResolver().insert(REMINDERS_URI, values);
        return eventId;
    }

    private String getCalendarUriBase() {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = managedQuery(calendars, null, null, null, null);
            } catch (Exception e) { }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }

    private long getCalendarId(String account) {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";
        // use the same values as above:
        String[] selArgs =
                new String[]{
                        account,
                        CalendarContract.ACCOUNT_TYPE_LOCAL};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        Cursor cursor =
                context.getContentResolver().
                        query(
                                CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                selection,
                                selArgs,
                                null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }

}
