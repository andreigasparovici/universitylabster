package com.kernelpanic.universitylabster;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.WRITE_CALENDAR;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("users");

    @BindView(R.id.editEmail)
    EditText editEmail;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @BindView(R.id.editName)
    EditText editName;

    @BindView(R.id.editFaculty)
    EditText editFaculty;

    @BindView(R.id.editYear)
    EditText editYear;

    @BindView(R.id.editContact)
    EditText editContact;



    @OnClick(R.id.registerButton)
    void doRegister() {
        final String
            email = editEmail.getText().toString(),
            password = editPassword.getText().toString(),
            name = editName.getText().toString(),
            faculty = editFaculty.getText().toString(),
            year = editYear.getText().toString(),
            contact = editContact.getText().toString();

        Boolean ok=true;
        if(email.length()<5){ok=false;editEmail.setError("Invalid");}
        if(password.length()<5){ok=false;editPassword.setError("Invalid");}
        if(name.length()<5){ok=false;editName.setError("Invalid");}
        if(faculty.length()<5){ok=false;editFaculty.setError("Invalid");}
        if(year.length()>1){ok=false;editYear.setError("Invalid");}
        if(!ok)return;

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
            .content("Vă rugăm aşteptaţi")
            .progress(true, 0)
            .show();


        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, (task) -> {
                if (task.isSuccessful()) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build();

                    if (user == null) return;

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Map<String, String> data = new HashMap<>();
                                    data.put("faculty", faculty);
                                    data.put("year", year);
                                   /* data.put("section", section);
                                    data.put("group", group);
                                    data.put("subGroup", subGroup);*/
                                    data.put("name", user.getDisplayName());
                                    data.put("contact", contact);

                                    reference.child(user.getUid()).setValue(data);

                                    createCalendar(user.getEmail(), "calendar_"+user.getDisplayName());
                                    ///Log.e("user", user.getEmail()+" "+user.getDisplayName());
                                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                                    finish();

                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                }
                            }
                        });
                } else {
                    dialog.dismiss();
                }
            });
    }

    public void createCalendar(String account, String calendar_name) {
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
                "com"+account);
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        if (ActivityCompat.checkSelfPermission(this, WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Uri uri = getContentResolver().insert(builder.build(), values);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.READ_CALENDAR,
                        WRITE_CALENDAR
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        ButterKnife.bind(this);
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
