package com.kernelpanic.universitylabster.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kernelpanic.universitylabster.R;

import java.util.concurrent.CompletableFuture;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by andrei on 08.12.2017.
 */

public class SettingsFragment extends Fragment {

    @BindView(R.id.userName)
    TextView userName;

    @BindView(R.id.userEmail)
    TextView userEmail;

    @BindView(R.id.userFaculty)
    EditText userFaculty;

    @BindView(R.id.userYear)
    EditText userYear;

    @BindView(R.id.userSection)
    EditText userSection;

    void populateUI(String faculty, String section, String year) {
        userFaculty.setText(faculty);
        userYear.setText(year);
        userSection.setText(section);
    }

    @OnClick(R.id.changeData)
    void changeData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(user.getUid())
                .child("faculty").setValue(userFaculty.getText().toString());
        reference.child(user.getUid())
                .child("section").setValue(userSection.getText().toString());
        reference.child(user.getUid())
                .child("year").setValue(Integer.valueOf(userYear.getText().toString()));
        new MaterialDialog.Builder(SettingsFragment.this.getContext())
                .title("Success")
                .positiveText("OK")
                .show();
    }

    @OnClick(R.id.changeEmail)
    void changeEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new MaterialDialog.Builder(SettingsFragment.this.getContext())
            .title("Parola:")
            .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
            .input("", "", (dialog, password) -> {
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), password.toString());
                user.reauthenticate(credential)
                    .addOnCompleteListener((task -> {
                        if(task.isSuccessful()) {
                            new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                    .title("Noua adresă de e-mail:")
                                    .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                    .input("Email", "", (dialog1, input) -> {
                                        user.updateEmail(input.toString())
                                            .addOnCompleteListener((task1) -> {
                                                if (task1.isSuccessful()) {
                                                    new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                                            .title("Success")
                                                            .positiveText("OK")
                                                            .show();
                                                } else {
                                                    new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                                            .title("Failed")
                                                            .content(task1.getException().getMessage())
                                                            .positiveText("OK")
                                                            .show();
                                                }
                                            });
                                    }).show();
                        } else {
                            new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                    .title("Failed")
                                    .content(task.getException().getMessage())
                                    .positiveText("OK")
                                    .show();
                        }
                    }));
            }).show();
    }

    @OnClick(R.id.changePassword)
    void changePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new MaterialDialog.Builder(SettingsFragment.this.getContext())
                .title("Parola:")
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("", "", (dialog, password) -> {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), password.toString());
                    user.reauthenticate(credential)
                            .addOnCompleteListener((task -> {
                                if (task.isSuccessful()) {
                                    new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                            .title("Parola nouă:")
                                            .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                                            .input("", "", (dialog1, newPassword) -> {
                                                user.updatePassword(newPassword.toString())
                                                        .addOnCompleteListener((task1) -> {
                                                            if (task1.isSuccessful()) {
                                                                new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                                                        .title("Success")
                                                                        .positiveText("OK")
                                                                        .show();
                                                            } else {
                                                                new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                                                        .title("Failed")
                                                                        .content(task1.getException().getMessage())
                                                                        .positiveText("OK")
                                                                        .show();
                                                            }
                                                        });
                                            }).show();
                                } else {
                                    new MaterialDialog.Builder(SettingsFragment.this.getContext())
                                            .title("Failed")
                                            .content(task.getException().getMessage())
                                            .positiveText("OK")
                                            .show();
                                }
                            }));
                }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ButterKnife.bind(this, view);

        userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String faculty = dataSnapshot.child("faculty").getValue(String.class);
                String section = dataSnapshot.child("section").getValue(String.class);
                String year = dataSnapshot.child("year").getValue(String.class);

                populateUI(faculty, section, year);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        return view;
    }
}
