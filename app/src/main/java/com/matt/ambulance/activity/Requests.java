package com.matt.ambulance.activity;

import static com.matt.ambulance.util.Util.GetSharedPrefs;
import static com.matt.ambulance.util.Util.hideProgress;
import static com.matt.ambulance.util.Util.user;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.matt.ambulance.R;
import com.matt.ambulance.object.Request;
import com.matt.ambulance.object.Stat;
import com.matt.ambulance.service.MyService;
import com.matt.ambulance.util.Strings;
import com.matt.ambulance.util.Util;

import java.util.Date;
import java.util.HashMap;

public class Requests extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Request, ViewHolder> adapter;
    private ViewGroup rootView;
    private int subWidth;
    private boolean onlyOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        //   reduce size of item to have over lap
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                if (!onlyOne) {
                    //   reduce size of item to have over lap
                    subWidth = getWidth() * 14 / 100;
                    lp.width = getWidth() - subWidth;
                }
                return true;
            }
        };
        rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        recyclerView = findViewById(R.id.recView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getRequests();
        Util.backButton(this);

        // background listening requests
        Intent intent = new Intent(this, MyService.class);

        if (!isMyServiceRunning(MyService.class)) {
            startService(intent);
        }
    }

    // check is service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void getRequests() {
        CollectionReference reviewRef = FirebaseFirestore.getInstance()
                .collection(Strings.srequests);

        FirestoreRecyclerOptions<Request> options = new FirestoreRecyclerOptions.Builder<Request>()
                .setQuery(reviewRef
                                .whereEqualTo(Strings.sstatus, 1)
                                .orderBy(Strings.stimestamp, Query.Direction.DESCENDING),
                        Request.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Request, ViewHolder>(options) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_view, parent, false);

//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                int subWidth = parent.getWidth() * 14 / 100;
//                layoutParams.width = (parent.getWidth() - subWidth); // control the recyclerView row height from here
//                view.setLayoutParams(layoutParams);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Request model) {
                String uid = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                Util.userRef.document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    String uname = null;
                    if (documentSnapshot.get("uname") != null) {
                        uname = documentSnapshot.get("uname").toString();
                    }
                    holder.setData(model.getLocation(), uname);

                    // statistics model
                    String AmbNo = GetSharedPrefs(getApplicationContext(), Strings.sAmbNo);
                    Stat stat = new Stat(AmbNo, model.getLocation(), uname, new Date());
                    // document ref
                    DocumentReference docRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();

                    holder.setListener(docRef, recyclerView, position, stat);

                });


            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (adapter.getItemCount() <= 0) {
                    hideProgress(rootView, 1);
                } else {
                    hideProgress(rootView, 0);
                }

                if (adapter.getItemCount() > 1) {
                    recyclerView.smoothScrollToPosition(0);
                }

                // if one item in list
                if (adapter.getItemCount() == 1) {
                    onlyOne = true;
//                    View v = recyclerView.getLayoutManager().findViewByPosition(1);
//                    v.post(() -> {
//                        v.getLayoutParams().width = v.getMeasuredWidth() + subWidth;
//                        v.requestLayout();
//                    });
//                    int i = v.getWidth();
//                    Toast.makeText(Requests.this, String.valueOf(i), Toast.LENGTH_SHORT).show();

                }
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnNext, btnAccept;
        TextView tvLocation, tvUname;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            btnNext = itemView.findViewById(R.id.btnNext);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvUname = itemView.findViewById(R.id.tvUname);

        }


        //        private void splitView() {
//            imageView.post(() -> {
//                imageView.getLayoutParams().width = imageView.getMeasuredWidth() / 2;
//                imageView.requestLayout();
//            });
//        }
//        public void resize() {
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.width = (view.getRootView().getWidth() - 200); // control the recyclerView row height from here
//            view.setLayoutParams(layoutParams);
//            Toast.makeText(view.getContext(), "Resize", Toast.LENGTH_SHORT).show();
//        }

        public void setListener(DocumentReference docRef, RecyclerView recyclerView, int position, Stat stat) {
            btnAccept.setOnClickListener(view1 -> {
                Util.Loader(Route.class, view.getContext());

                // set statistics
                Util.statsRef.add(stat);

                HashMap<String, Object> data = new HashMap<>();
                data.put(Strings.sstatus, 2);
                data.put("aid", user.getUid());
                docRef.update(data);

                ((Activity) view1.getContext()).finish();
                // stop service
                Intent intent = new Intent(itemView.getContext(), MyService.class);
                itemView.getContext().stopService(intent);
            });
            btnNext.setOnClickListener(view -> {
                recyclerView.smoothScrollToPosition(position + 1);
//                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, new RecyclerView.State(), position + 1);
            });
        }

        public void setData(String location, String uname) {
            tvLocation.setText(location);
            tvUname.setText(uname);
        }
    }
}