package com.matt.ambulance.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.matt.ambulance.R;
import com.matt.ambulance.object.User;

import java.util.Calendar;

public class Util {

    public static CollectionReference userRef = FirebaseFirestore.getInstance()
            .collection(Strings.susers);

    public static CollectionReference statsRef = FirebaseFirestore.getInstance()
            .collection("stats");

    public static CollectionReference reqRef = FirebaseFirestore.getInstance()
            .collection(Strings.srequests);

    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static Query ongoingRef() {
        return reqRef.whereEqualTo("aid", user.getUid());
    }

    public static void Loader(Class<?> aClass, Context context) {
        Intent intent = new Intent(context, aClass);
        context.startActivity(intent);
    }

    public static String getGreetings() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay < 12) {
            return "Morning";
        } else if (timeOfDay < 16) {
            return "Afternoon";
        } else if (timeOfDay < 21) {
            return "Evening";
        } else {
            return "Night";
        }
    }

    public static void backButton(Activity activity) {
        View view = activity.findViewById(R.id.btnBack);
        view.setOnClickListener(view1 -> {
            activity.finish();
        });
    }

    public static void hideProgress(ViewGroup rootView, int status) {
        View progBar = rootView.findViewById(R.id.progBar);
        progBar.setVisibility(View.GONE);

        if (status != 0) {
            View tvError = rootView.findViewById(R.id.msg_view);
            tvError.setVisibility(View.VISIBLE);
        }
    }

    public static Task<DocumentSnapshot> checkAccountType(String uid, Context context) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        return userRef.document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);

                String s = null;
                if (user.getAccountType() == 0) {
                    s = "0";
                } else if (user.getAccountType() == 1) {
                    s = "1";
                } else if (user.getAccountType() == 2) {
                    s = "2";
                } else if (user.getAccountType() == 3) {
                    s = "3";
                }

                SetSharedPrefs(context, Strings.saccounttype, s);

                // save Amb Number
                if (user.getAmbNo() != null) {
                    SetSharedPrefs(context, Strings.sAmbNo, user.getAmbNo());
                }

                /*
                0- User
                1- Driver
                2- Admin
                3- Approved Driver
                */

            }
        });
    }

    public static String GetSharedPrefs(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public static SharedPreferences mySharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void SetSharedPrefs(Context context, String key, String o) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key, o).apply();
       /* if (o instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) o).apply();
        } else if (o instanceof String) {
            sharedPreferences.edit().putString(key, o.toString()).apply();
        } else if (o instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) o).apply();
        }*/
    }

    public static void callPhone(Context context, String tel) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + tel));
        context.startActivity(intent);
    }

    public static Snackbar snackbar(View rootView, String msg) {
        Snackbar snackbar = null;
        if (rootView != null) {
            snackbar = Snackbar.make(rootView.getRootView().findViewById(android.R.id.content),
                    msg, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        return snackbar;
    }

}
