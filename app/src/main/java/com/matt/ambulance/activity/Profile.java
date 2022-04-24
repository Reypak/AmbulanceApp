package com.matt.ambulance.activity;

import static com.matt.ambulance.util.Strings.susers;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.matt.ambulance.R;
import com.matt.ambulance.object.User;
import com.matt.ambulance.util.Util;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    private FirebaseUser user;
    private FirebaseFirestore db;
    private EditText etUname, etPhone, etAmbNo, etHosp, etEmail;
    private DocumentReference userRef;
    private View ambView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();


        Util.backButton(this);

        getUserData();
    }

    private void initialize() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection(susers).document(user.getUid());
        etUname = findViewById(R.id.etUname);
        etPhone = findViewById(R.id.etPhone);
        etHosp = findViewById(R.id.etHospital);
        etEmail = findViewById(R.id.etEmail);
        etAmbNo = findViewById(R.id.etAmbNo);
        ambView = findViewById(R.id.AmbView);
        hideViews(0);
    }

    private void hideViews(int i) {
        if (i == 0) {
            ambView.setVisibility(View.GONE);
        } else {
            ambView.setVisibility(View.VISIBLE);
        }
    }

    private void getUserData() {
        if (user != null) {
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    if (user != null) {
                        etUname.setText(user.getUname());
                        etEmail.setText(user.getEmail());
                        etAmbNo.setText(user.getAmbNo());
                        etPhone.setText(user.getPhone());
                        etHosp.setText(user.getHospital());

                        if (user.getAmbNo() != null) {
                            hideViews(1);
                        }
                    }
                }
            });
        }
    }

    public void SignOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Util.Loader(Login.class, this);
        finish();
        Navigation.MActivity.finish();
    }

    public void UpdateProfile(View view) {
        String uname = etUname.getText().toString().trim();

        HashMap<String, Object> userdata = new HashMap<>();
        userdata.put("uname", uname);

        userRef.update(userdata).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(uname)
                        .build();
                user.updateProfile(profileUpdates).addOnSuccessListener(runnable -> {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                });
            } else {

            }
        });

    }
}