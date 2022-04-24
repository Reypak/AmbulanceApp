package com.matt.ambulance.fragment;

import static com.matt.ambulance.util.Util.GetSharedPrefs;
import static com.matt.ambulance.util.Util.ongoingRef;
import static com.matt.ambulance.util.Util.user;
import static com.matt.ambulance.util.Util.userRef;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.matt.ambulance.R;
import com.matt.ambulance.activity.Route;
import com.matt.ambulance.object.User;
import com.matt.ambulance.util.Strings;
import com.matt.ambulance.util.Util;

public class DriverDashboard extends Fragment {
    public static boolean ongoing;
    private View btnOngoing, btnAmb, viewPend, btnRequests, btnStats;
    private ViewGroup rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_driver_dashboard, container, false);
        btnOngoing = rootView.findViewById(R.id.btnOngoing);
        btnAmb = rootView.findViewById(R.id.btnAmb);
        btnStats = rootView.findViewById(R.id.btnStats);
        btnRequests = rootView.findViewById(R.id.btnRequests);
        viewPend = rootView.findViewById(R.id.viewPending);
        btnOngoing.setVisibility(View.GONE);
        btnAmb.setVisibility(View.GONE);
        viewPend.setVisibility(View.GONE);
        btnStats.setVisibility(View.GONE);
        btnRequests.setVisibility(View.GONE);

        if (user != null) {
            ongoingRef().addSnapshotListener((value, error) -> {
                if (error == null) {
                    if (!value.isEmpty()) {
                        btnOngoing.setVisibility(View.VISIBLE);
                        ongoing = true;
                    } else {
                        btnOngoing.setVisibility(View.GONE);
                        ongoing = false;
                    }
                }
            });
            checkStatus();
        }

        checkAccountType();

        return rootView;
    }

    private void checkAccountType() {
        String v = GetSharedPrefs(requireContext(), Strings.saccounttype);

        if (v.equals("2")) {
            TextView tvTitle = rootView.findViewById(R.id.tvTitle);
            tvTitle.setText("Administrator Dashboard");
            btnAmb.setVisibility(View.VISIBLE);
            btnStats.setVisibility(View.VISIBLE);
        }

        if (v.equals("1") || v.equals("3")) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                userRef.document(user.getUid()).addSnapshotListener((value, error) -> {
                    if (error == null) {
                        if (value.exists()) {
                            User user1 = value.toObject(User.class);
                            if (user1.getAccountType() == 1) {
                                viewPend.setVisibility(View.VISIBLE);
                                btnRequests.setVisibility(View.GONE);
                            } else if (user1.getAccountType() == 3) {
                                btnRequests.setVisibility(View.VISIBLE);
                                viewPend.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        }
    }

    private void checkStatus() {
        ongoingRef().get().addOnSuccessListener(runnable -> {
            if (!runnable.isEmpty()) {
                Util.Loader(Route.class, requireContext());
            }
        });
    }
}
