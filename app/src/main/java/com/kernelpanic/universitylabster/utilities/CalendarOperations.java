package com.kernelpanic.universitylabster.utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.Manifest;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.TimeZone;

import static android.Manifest.permission.WRITE_CALENDAR;

/**
 * Created by andrei on 09.12.2017.
 */

public class CalendarOperations {
    private static CalendarOperations instance;

    public static CalendarOperations getInstance() {
        return instance == null ? new CalendarOperations() : instance;
    }

    public void createCalendar(Context context, String account, String calendar_name) {
        if(verifyCalendar(context, "calendar_"+ account))
            return;

        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME,
                account);
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME,
                calendar_name);
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                calendar_name);
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0xffff0000);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                account);
        TimeZone timeZone = TimeZone.getTimeZone("Romania");
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                "Romania/Bucharest");
        values.put(
                CalendarContract.Calendars.SYNC_EVENTS,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                "com" + account);
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        if (ActivityCompat.checkSelfPermission(context, WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Uri uri = context.getContentResolver().insert(builder.build(), values);
    }

    private long getCalendarId(Context context, String account) {
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
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

    public long addEvent(Activity activity, Context context, Calendar cal, String account, String location, String title, String description, Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {


        long calId = getCalendarId(context, account);
        if (calId == -1) {
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
        if (ActivityCompat.checkSelfPermission(context, WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        Uri uri =
                context.getContentResolver().
                        insert(CalendarContract.Events.CONTENT_URI, values);
        //Log.e("event", "created");
        long eventId = new Long(uri.getLastPathSegment());

        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(activity) + "reminders");
        values = new ContentValues();
        values.put("event_id", eventId);
        values.put("method", 1);
        values.put("minutes", 60);
        context.getContentResolver().insert(REMINDERS_URI, values);
        REMINDERS_URI = Uri.parse(getCalendarUriBase(activity) + "reminders");
        values = new ContentValues();
        values.put("event_id", eventId);
        values.put("method", 1);
        values.put("minutes", 1440);
        context.getContentResolver().insert(REMINDERS_URI, values);
        return eventId;
    }

    public static boolean verifyCalendar(Context context, String calendarName) {
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Cursor calCursor =
                context.getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                CalendarContract.Calendars.VISIBLE + " = 1",
                                null,
                                CalendarContract.Calendars._ID + " ASC");
        if (calCursor.moveToFirst()) {
            do {
                long id = calCursor.getLong(0);
                String displayName = calCursor.getString(1);
                if(displayName.equals(calendarName))
                    return true;
            } while (calCursor.moveToNext());
            return true;
        }
        return false;
    }

    private String getCalendarUriBase(Activity act) {

        String calendarUriBase = null;
        Uri calendars = Uri.parse("content://calendar/calendars");
        Cursor managedCursor = null;
        try {
            managedCursor = act.managedQuery(calendars, null, null, null, null);
        } catch (Exception e) {
        }
        if (managedCursor != null) {
            calendarUriBase = "content://calendar/";
        } else {
            calendars = Uri.parse("content://com.android.calendar/calendars");
            try {
                managedCursor = act.managedQuery(calendars, null, null, null, null);
            } catch (Exception e) {
            }
            if (managedCursor != null) {
                calendarUriBase = "content://com.android.calendar/";
            }
        }
        return calendarUriBase;
    }

    public void deleteEvent(Context context, Long eventId){
        if (ActivityCompat.checkSelfPermission(context, WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Uri uri = CalendarContract.Reminders.CONTENT_URI;
        String mSelectionClause = CalendarContract.Reminders.EVENT_ID+ " = ?";
        String[] mSelectionArgs = new String[]{String.valueOf(eventId)};

        int updCount = context.getContentResolver().delete(uri, mSelectionClause, mSelectionArgs);

        String[] selArgs =
                new String[]{Long.toString(eventId)};
        int deleted =
                context.getContentResolver().
                        delete(
                                CalendarContract.Events.CONTENT_URI,
                                CalendarContract.Events._ID + " =? ",
                                selArgs);
    }
}
