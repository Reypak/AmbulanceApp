package com.matt.ambulance.activity;

import static com.matt.ambulance.util.Util.GetSharedPrefs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.matt.ambulance.R;
import com.matt.ambulance.fragment.DriverDashboard;
import com.matt.ambulance.fragment.UserMain;
import com.matt.ambulance.util.Strings;
import com.matt.ambulance.util.Util;

public class Navigation extends AppCompatActivity {
    public static Activity MActivity;
    private FirebaseUser user;
    private TextView tvGrt, tvUname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        MActivity = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        tvUname = findViewById(R.id.tvUname);
        tvGrt = findViewById(R.id.tvGreeting);

        getUserData();

        String v = GetSharedPrefs(MActivity, Strings.saccounttype);
        if (v != null) {
            if (v.equals("0")) {
                loadFragment(new UserMain());
            } else {
                loadFragment(new DriverDashboard());
            }
        }

    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void getUserData() {
        if (user != null) {
            if (user.getDisplayName() != null) {
                tvUname.setText(String.format("%s%s", user.getDisplayName(), Strings.hello_emoji));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getGreeting();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Util.Loader(Login.class, this);
            finish();
        } else {
            user.reload();
        }
    }

    private void getGreeting() {
        tvGrt.setText(String.format("Good %s,", Util.getGreetings()));
    }

    public void OpenProfile(View view) {
        Util.Loader(Profile.class, this);
    }

    public void OpenRequests(View view) {
        if (!DriverDashboard.ongoing) {
            Util.Loader(Requests.class, this);
        } else {
            Toast.makeText(MActivity, "You have an Ongoing Trip", Toast.LENGTH_SHORT).show();
        }
    }

    public void OpenRoute(View view) {
        Util.Loader(Route.class, this);
    }

    public void ShowMenu(View view) {

    }

    public void OpenRegistration(View view) {
        Util.Loader(AmbulanceList.class, this);
    }

    public void OpenStats(View view) {
        Util.Loader(Stats.class, this);
    }
}