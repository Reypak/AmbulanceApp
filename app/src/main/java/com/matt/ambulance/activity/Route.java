package com.matt.ambulance.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.matt.ambulance.R;
import com.matt.ambulance.object.Request;
import com.matt.ambulance.object.User;
import com.matt.ambulance.util.Strings;
import com.matt.ambulance.util.Util;

public class Route extends AppCompatActivity {

    private Button btnAccept, btnNext;
    private TextView tvUname, tvLocation, tvTitle;
    private String sTrip = "Trip";
    private String sStart = "Start " + sTrip;
    private String sOnTrip = "On " + sTrip;
    private String sEnd = "End " + sTrip;
    private String sEnded = sTrip + " Ended";
    private View sep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        btnAccept = findViewById(R.id.btnAccept);
        btnNext = findViewById(R.id.btnNext);
        tvLocation = findViewById(R.id.tvLocation);
        tvUname = findViewById(R.id.tvUname);
        sep = findViewById(R.id.sep);
        tvTitle = findViewById(R.id.tvTitle);

        btnNext.setText(R.string.call);
        btnAccept.setText("Arrived");

        getData();

        Util.backButton(this);
    }

    private void getData() {
        Util.ongoingRef().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    Request request = snapshot.toObject(Request.class);
                    tvLocation.setText(request.getLocation());

                    Util.userRef.document(snapshot.getId()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            User user = task1.getResult().toObject(User.class);
                            if (user != null) {
                                tvUname.setText(user.getUname());

                                btnNext.setOnClickListener(view -> {
                                    Util.callPhone(this, user.getPhone());
                                });
                            }
                        }
                    });

                    btnAccept.setOnClickListener(view -> {
                        if (request.getStatus() == 2) {
                            snapshot.getReference().update(Strings.sstatus, 3).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    btnAccept.setText(sStart);
                                    request.setStatus(3); // manually set the request status since no active listening
                                } else {
                                    Toast.makeText(this, "User Cancelled", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        } else if (request.getStatus() == 3) {
                            snapshot.getReference().update(Strings.sstatus, 4);
                            request.setStatus(4);
                            btnAccept.setText(sEnd);
                            hideViews();
                        } else if (request.getStatus() == 4) {
                            snapshot.getReference().delete();
                            tvTitle.setText(sEnded);
                            btnAccept.setText(sEnded);
                            btnAccept.setEnabled(false);
                            hideViews();
                        }

                    });

                    if (request.getStatus() == 3) {
                        btnAccept.setText(sStart);
                    } else if (request.getStatus() == 4) {
                        btnAccept.setText(sEnd);
                        hideViews();
                    }
                }

            }
        });
    }

    private void hideViews() {
        sep.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
    }

}