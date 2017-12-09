package com.kernelpanic.universitylabster;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @BindView(R.id.editSection)
    EditText editSection;

    @BindView(R.id.editGroup)
    EditText editGroup;

    @BindView(R.id.editSubGroup)
    EditText editSubGroup;

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
            section = editSection.getText().toString(),
            group = editGroup.getText().toString(),
            subGroup = editSubGroup.getText().toString();

        Boolean ok=true;
        if(email.length()<5){ok=false;editEmail.setError("Invalid");}
        if(password.length()<5){ok=false;editPassword.setError("Invalid");}
        if(name.length()<5){ok=false;editName.setError("Invalid");}
        if(faculty.length()<5){ok=false;editFaculty.setError("Invalid");}
        if(year.length()>1){ok=false;editYear.setError("Invalid");}
        if(section.length()<5){ok=false;editSection.setError("Invalid");}
        if(group.length()<5){ok=false;editGroup.setError("Invalid");}
        if(subGroup.length()<5){ok=false;editSubGroup.setError("Invalid");}
        if(!ok)return;

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
            .content("Vă rugăm aşteptaţi")
            .progress(true, 0)
            .show();


        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates =
                        new UserProfileChangeRequest.Builder()
                        .setDisplayName(name).build();

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Map<String, String> data = new HashMap<>();
                                data.put("faculty", faculty);
                                data.put("year", year);
                                data.put("section", section);
                                data.put("group", group);
                                data.put("subGroup", subGroup);

                                reference.child(user.getUid()).setValue(data);
                                dialog.dismiss();
                            } else {
                                dialog.dismiss();
                            }
                            }
                        });
                } else {
                    dialog.dismiss();
                }
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

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
