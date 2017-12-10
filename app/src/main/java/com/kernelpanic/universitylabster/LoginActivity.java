package com.kernelpanic.universitylabster;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.WRITE_CALENDAR;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener  {

    private FirebaseAuth firebaseAuth;
    public static Context context;
    private FirebaseUser firebaseUser;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("users");

    @BindView(R.id.status)
    TextView mStatusTextView;

    @BindView(R.id.sign_out_button)
    Button signOutButton;
    @BindView(R.id.disconnect_button)
    Button disconectButton;

    @BindView(R.id.editEmail)
    EditText editEmail;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @BindView(R.id.loginButton)
    Button signIn;

    @OnClick(R.id.loginButton)
    void doLogin() {
        String email = editEmail.getText().toString(), password = editPassword.getText().toString();

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Vă rugăm aşteptaţi")
                .progress(true, 0)
                .show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, (task) -> {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Datele de autentificare sunt incorecte!", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context=this;

        ButterKnife.bind(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        if(firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }
    }

    public void goToRegister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        //finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            signIn.setVisibility(View.VISIBLE);
            // Signed in successfully, show authenticated UI.
            //registerUser(account);
            new MultiInputMaterialDialogBuilder(this)
                    .addInput("","facultate")
                    .addInput("","an de studiu")
                    .addInput("","sectiune")
                    .addInput("","grup")
                    .addInput("","subgrup")
                    .addInput("","contact")
                    .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                        @Override
                        public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                            registerUser(account, inputs.get(0).toString(),inputs.get(1).toString(),
                                    inputs.get(2).toString(), inputs.get(3).toString(), inputs.get(4).toString(),
                                    inputs.get(5).toString());
                            signOut();
                            Intent intent = new Intent(LoginActivity.context, DashboardActivity.class);
                            startActivity(intent);
                        }
                    })
                    .positiveText("Terminat")
                    .negativeText("Anuleaza")
                    .title("Mici Detalii")
                    .build().show();
                signOut();
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    public void registerUser(GoogleSignInAccount account, String faculty, String year, String section, String group, String subGroup, String contact){
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Vă rugăm aşteptaţi")
                .progress(true, 0)
                .show();
        firebaseAuth.createUserWithEmailAndPassword(account.getEmail(), account.getEmail())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(account.getDisplayName()).build();

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
                                                data.put("contact", contact);

                                                reference.child(user.getUid()).setValue(data);

                                                createCalendar(user.getEmail(), "calendar_"+user.getDisplayName());

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
    protected void onStart() {

        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUI(account);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Button signIn = findViewById(R.id.loginButton);
                        signIn.setVisibility(View.VISIBLE);
                        signOutButton.setVisibility(View.INVISIBLE);
                        disconectButton.setVisibility(View.INVISIBLE);
                        updateUI(null);
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        Button signIn = findViewById(R.id.loginButton);
                        signIn.setVisibility(View.VISIBLE);
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
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

}
