package com.kernelpanic.universitylabster;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @BindView(R.id.editEmail)
    EditText editEmail;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @OnClick(R.id.loginButton)
    void doLogin() {
        String email = editEmail.getText().toString(), password = editPassword.getText().toString();

        Log.d("DEBUG", "HERE");

        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .content("Vă rugăm aşteptaţi")
                .progress(true, 0)
                .show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Log.d("DEBUG", "HERE AGAIN");

                    dialog.dismiss();

                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Datele de autentificare sunt incorecte!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    public void goToRegister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }
    }
}
