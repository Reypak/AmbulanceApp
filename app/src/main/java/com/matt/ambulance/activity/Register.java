package com.matt.ambulance.activity;

import static com.matt.ambulance.util.Strings.saccounttype;
import static com.matt.ambulance.util.Strings.stimestamp;
import static com.matt.ambulance.util.Strings.susers;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.matt.ambulance.R;
import com.matt.ambulance.util.Strings;
import com.matt.ambulance.util.Util;

import java.util.Date;
import java.util.HashMap;

public class Register extends AppCompatActivity {
    String p = "Patient", a = Strings.sambulance;
    private EditText etEmail, etPwrd, etPhone, etUname, etAmbNo, etHospital;
    private View regView2;
    private FirebaseFirestore db;
    private TextView btnAmb, tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regView2 = findViewById(R.id.regView2);
        regView2.setVisibility(View.GONE);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(String.format("%s %s", p, tvTitle.getText()));

        btnAmb = findViewById(R.id.btnAmb);
        btnAmb.setOnClickListener(view -> {
            String s;
            if (regView2.getVisibility() == View.GONE) {
                regView2.setVisibility(View.VISIBLE);
                btnAmb.setText(btnAmb.getText().toString().replace(a, "User"));
                s = tvTitle.getText().toString().replace(p, a);
            } else {
                regView2.setVisibility(View.GONE);
                btnAmb.setText(getString(R.string.register_as_ambulance));
                s = tvTitle.getText().toString().replace(a, p);
            }
            tvTitle.setText(s);
        });
    }

    public void SignUp(View view) {
        etEmail = findViewById(R.id.etEmail);
        etPwrd = findViewById(R.id.etPwrd);
        etUname = findViewById(R.id.etUname);
        etPhone = findViewById(R.id.etPhone);
        etAmbNo = findViewById(R.id.etAmbNo);
        etHospital = findViewById(R.id.etHospital);
        String email, password, uname, phone;
        String ambNo, hospital;

        email = etEmail.getText().toString().trim();
        password = etPwrd.getText().toString().trim();
        phone = etPhone.getText().toString().trim();
        uname = etUname.getText().toString().trim();
        ambNo = etAmbNo.getText().toString().trim();
        hospital = etHospital.getText().toString().trim();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (!email.isEmpty()) {
            if (!password.isEmpty()) {
                final ProgressDialog dialog = ProgressDialog.show(this, "",
                        "Logging in...", true);
                dialog.show();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        db = FirebaseFirestore.getInstance();

                        HashMap<String, Object> userdata = new HashMap<>();
                        userdata.put("uname", uname);
                        userdata.put("email", user.getEmail());
                        userdata.put("phone", phone);
                        int aType = 0;

                        // registration for Ambulance
                        if (regView2.getVisibility() == View.VISIBLE) {
                            userdata.put("hospital", hospital);
                            userdata.put("ambNo", ambNo);
                            userdata.put(stimestamp, new Date());
                            aType = 1;
                        }

                        userdata.put(saccounttype, aType);

                        db.collection(susers).document(user.getUid())
                                .set(userdata).addOnSuccessListener(unused -> {

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(uname)
                                    .build();
                            user.updateProfile(profileUpdates);

                            // record accountType
                            Util.checkAccountType(mAuth.getUid(), this).addOnCompleteListener(task1 -> {
                                if (task1.isComplete()) {
                                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
                                    Util.Loader(Navigation.class, this);
                                    finish();
                                    Login.MActivity.finish();
                                }
                            });
                        });
                    } else {
                        String msg = task.getException().getMessage();
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Register.this, msg, Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
            } else {
                etPwrd.requestFocus();
            }
        } else {
            etEmail.requestFocus();
        }

    }
}