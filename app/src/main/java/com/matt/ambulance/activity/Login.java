package com.matt.ambulance.activity;

import static com.matt.ambulance.util.Strings.empty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.matt.ambulance.R;
import com.matt.ambulance.util.Util;

public class Login extends AppCompatActivity {

    public static Activity MActivity;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MActivity = this;
        auth = FirebaseAuth.getInstance();
    }

    /*  @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Util.Loader(MainActivity.class, this);
        }
    }*/

    public void Register(View view) {
        Util.Loader(Register.class, this);
    }

    public void LoginAction(View view) {
        EditText etEmail, etPwrd;

        etEmail = findViewById(R.id.etEmail);
        etPwrd = findViewById(R.id.etPwrd);

        String email = etEmail.getText().toString().trim();
        String password = etPwrd.getText().toString().trim();


        if (!email.isEmpty()) {
            if (!password.isEmpty()) {

                final ProgressDialog dialog = ProgressDialog.show(Login.this, "",
                        "Logging in...", true);
                dialog.show();

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // check account type
                        Util.checkAccountType(auth.getUid(), this).addOnCompleteListener(task1 -> {
                            if (task1.isComplete()) {
                                Toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
                                Util.Loader(Navigation.class, this);
                                finish();
                            }
                        });
                    } else {
                        String msg = task.getException().getMessage();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
            } else {
                etPwrd.requestFocus();
                etPwrd.setError(empty);
            }
        } else {
            etEmail.requestFocus();
            etEmail.setError(empty);
        }

    }
}