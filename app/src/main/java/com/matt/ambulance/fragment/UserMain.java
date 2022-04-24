package com.matt.ambulance.fragment;

import static com.matt.ambulance.util.Strings.slocation;
import static com.matt.ambulance.util.Strings.srequests;
import static com.matt.ambulance.util.Strings.sstatus;
import static com.matt.ambulance.util.Strings.stimestamp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.matt.ambulance.R;
import com.matt.ambulance.object.Request;
import com.matt.ambulance.object.User;
import com.matt.ambulance.util.Util;

import java.util.Date;
import java.util.HashMap;

public class UserMain extends Fragment {


    private FirebaseFirestore db;
    private Button btnReq, btnCancel, btnCancel2, btnAccept;
    private DocumentReference reqRef;
    private DocumentReference cancelRef;
    private TextView status_msg, etAmbNo, etHosp, etUname;
    private View sep, view1, view2;
    private ProgressBar progBar;
    private FirebaseUser user;
    private ViewGroup rootView;
    private Context context;
    private EditText etLocation;

    private void initialize() {
        db = FirebaseFirestore.getInstance();

        btnReq = rootView.findViewById(R.id.btnRequest);
        etAmbNo = rootView.findViewById(R.id.etAmbNo);
        etHosp = rootView.findViewById(R.id.etHospital);
        etLocation = rootView.findViewById(R.id.etLocation);
        etUname = rootView.findViewById(R.id.etUname);
        sep = rootView.findViewById(R.id.sep);
        view1 = rootView.findViewById(R.id.view1);
        view2 = rootView.findViewById(R.id.view2);

        btnCancel = rootView.findViewById(R.id.btnCancel);
        btnAccept = rootView.findViewById(R.id.btnAccept);
        btnCancel2 = rootView.findViewById(R.id.btnCancel2);
        progBar = rootView.findViewById(R.id.progBar);
        status_msg = rootView.findViewById(R.id.status_msg);
        user = FirebaseAuth.getInstance().getCurrentUser();
        context = getContext();

        hideViews();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.match_view, container, false);


        initialize();

        if (user != null) {
            reqRef = db.collection(srequests).document(user.getUid());

            btnReq.setOnClickListener(view -> {
                requestAmbulance();
            });

            // cancel click event
            View.OnClickListener cancelClick = view -> {
                if (cancelRef != null) {
                    cancelRef.delete().addOnSuccessListener(runnable -> {
                        Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
                    });
                }
            };

            btnCancel.setOnClickListener(cancelClick);
            btnCancel2.setOnClickListener(cancelClick);

            checkStatus();


        }
        return rootView;
    }


    private void checkStatus() {
        reqRef.addSnapshotListener((value, error) -> {
            if (error == null) {
                // on going ambulance request
                if (value.exists()) {
                    Request request = value.toObject(Request.class);

                    cancelRef = value.getReference();

                    // recurring within the loop
                    view1.setVisibility(View.GONE);

                    if (request.getStatus() >= 2) {
                        toggleSearchView(0);

                        // show details view
                        view2.setVisibility(View.VISIBLE);
                        if (request.getAid() != null) { // get ambulance user details
                            Util.userRef.document(request.getAid()).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    User user = task.getResult().toObject(User.class);
                                    if (user != null) {
                                        etAmbNo.setText(user.getAmbNo());
                                        etHosp.setText(user.getHospital());
                                        etUname.setText(user.getUname());
                                        btnAccept.setOnClickListener(view -> {
                                            Util.callPhone(view.getContext(), user.getPhone());
                                        });
                                    }

                                }
                            });
                        }

                        if (request.getStatus() == 2) {
                            // remove cancel option
                            btnCancel.setVisibility(View.VISIBLE);
                            sep.setVisibility(View.VISIBLE);

                            status_msg.setText(R.string.on_way);
                        } else if (request.getStatus() == 3) {
                            status_msg.setText(R.string.msg_arrived);
                            btnCancel.setVisibility(View.GONE);
                            sep.setVisibility(View.GONE);
                        } else if (request.getStatus() == 4) {
                            status_msg.setText(R.string.msg_started);
                        }
                    } else { // if equals to 1 - SEARCHING
                        status_msg.setText(R.string.msg_search);
                        view2.setVisibility(View.GONE);

                        toggleSearchView(1);
                    }

                } else {
                    status_msg.setText(R.string.app_name);
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);

                    toggleSearchView(0);
                }
            }
        });
    }


    private void requestAmbulance() {

        String location = etLocation.getText().toString().trim();

        if (!location.isEmpty()) {
            HashMap<String, Object> reqData = new HashMap<>();
            reqData.put(stimestamp, new Date());
            reqData.put(slocation, location);
            reqData.put(sstatus, 1);

            reqRef.set(reqData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Searching", Toast.LENGTH_SHORT).show();
                } else {
                }
            });
        } else {
            etLocation.requestFocus();
        }
    }

    private void hideViews() {
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        toggleSearchView(0);
    }

    private void toggleSearchView(int i) {
        if (i == 0) {
            progBar.setVisibility(View.GONE);
            btnCancel2.setVisibility(View.GONE);
        } else {
            progBar.setVisibility(View.VISIBLE);
            btnCancel2.setVisibility(View.VISIBLE);
        }
    }
}
