package com.matt.ambulance.activity;

import static com.matt.ambulance.util.Strings.stimestamp;
import static com.matt.ambulance.util.Util.statsRef;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.matt.ambulance.R;
import com.matt.ambulance.object.Stat;
import com.matt.ambulance.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Stats extends AppCompatActivity {
    private RecyclerView recView;
    private FirestoreRecyclerAdapter<Stat, ViewHolder> adapter, adapter2, adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        recView = findViewById(R.id.recView);
        Util.userRef.orderBy(stimestamp);

        getRequests();

        ConcatAdapter concatAdapter = new ConcatAdapter(new MyRecyclerViewAdapter(0),
                adapter,
                new MyRecyclerViewAdapter(1),
                adapter2,
                new MyRecyclerViewAdapter(2),
                adapter3);
        recView.setAdapter(concatAdapter);

        Util.backButton(this);

    }

    private void getRequests() {
        // seven days ago
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7));
        Date oneDayAgo = new Date();

        DateFormat justDay = new SimpleDateFormat("yyyyMMdd");
        try {
            oneDayAgo = justDay.parse(justDay.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i("56y", oneDayAgo.toString());

//        Date oneDayAgo = new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));


        FirestoreRecyclerOptions<Stat> options = new FirestoreRecyclerOptions.Builder<Stat>()
                .setQuery(statsRef
                                .whereLessThan(stimestamp, new Date())
                                .whereGreaterThan(stimestamp, oneDayAgo)
                                .orderBy(stimestamp, Query.Direction.DESCENDING),
                        Stat.class)
                .build();

        FirestoreRecyclerOptions<Stat> options2 = new FirestoreRecyclerOptions.Builder<Stat>()
                .setQuery(statsRef
                                .orderBy(stimestamp, Query.Direction.DESCENDING)
                                .whereGreaterThan(stimestamp, sevenDaysAgo)
                                .whereLessThan(stimestamp, oneDayAgo),
                        Stat.class)
                .build();

        FirestoreRecyclerOptions<Stat> options3 = new FirestoreRecyclerOptions.Builder<Stat>()
                .setQuery(statsRef
                                .orderBy(stimestamp, Query.Direction.DESCENDING)
                                .whereLessThan(stimestamp, sevenDaysAgo),
                        Stat.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Stat, ViewHolder>(options) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Stat model) {
                holder.setData(model, 0);
              /*
                DocumentReference itemRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                holder.setListener(itemRef);*/
            }
        };

        adapter2 = new FirestoreRecyclerAdapter<Stat, ViewHolder>(options2) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Stat model) {
                holder.setData(model, 1);
              /*
                DocumentReference itemRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                holder.setListener(itemRef);*/
            }
        };

        adapter3 = new FirestoreRecyclerAdapter<Stat, ViewHolder>(options3) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stat_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Stat model) {
                holder.setData(model, 2);
              /*
                DocumentReference itemRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                holder.setListener(itemRef);*/
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
        adapter3.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        adapter2.stopListening();
        adapter3.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHosp, tvUname, tvAmbNo, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmbNo = itemView.findViewById(R.id.tvAmbNo);
            tvHosp = itemView.findViewById(R.id.tvHosp);
            tvUname = itemView.findViewById(R.id.tvUname);
            tvTime = itemView.findViewById(R.id.tvTime);

        }

        public void setData(Stat model, int i) {
            tvAmbNo.setText(model.getAmbNo());
            tvHosp.setText(String.format("%s  →  Nsambya Hospital", model.getLocation()));
            tvUname.setText(model.getUname());

            SimpleDateFormat formatter;
            if (i == 0) {
                formatter = new SimpleDateFormat("hh:mm a");
            } else {
                // other sections
                formatter = new SimpleDateFormat("MMM dd • HH:mm");
            }
            String time = formatter.format(model.getTimestamp());
            tvTime.setText(time);
        }

    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }

    public static class MyRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        int i;

        public MyRecyclerViewAdapter(int i) {
            this.i = i;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.title_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            String s = null;

            switch (i) {
                case 0:
                    s = "Today";
                    break;
                case 1:
                    s = "This Week";
                    break;
                case 2:
                    s = "Others";
                    break;
                case 3:
                    s = "Pending Approval";
                    break;
                case 4:
                    s = "Approved";
                    break;
            }
           /* if (i == 0) {
                s = "Today";
            } else if (i == 1) {
                s = "This Week";
            } else if (i == 2) {
                s = "Others";
            }*/
            holder.tvTitle.setText(s);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
}