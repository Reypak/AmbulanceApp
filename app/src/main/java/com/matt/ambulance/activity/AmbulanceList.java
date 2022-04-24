package com.matt.ambulance.activity;

import static com.matt.ambulance.util.Strings.saccounttype;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.matt.ambulance.R;
import com.matt.ambulance.object.User;
import com.matt.ambulance.util.Strings;
import com.matt.ambulance.util.Util;

public class AmbulanceList extends AppCompatActivity {

    private FirestoreRecyclerAdapter<User, ViewHolder> adapter, adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_list);

        RecyclerView recView = findViewById(R.id.recView);
        Util.userRef.orderBy(Strings.stimestamp);

        getRequests();
        ConcatAdapter concatAdapter = new ConcatAdapter(new Stats.MyRecyclerViewAdapter(3),
                adapter,
                new Stats.MyRecyclerViewAdapter(4),
                adapter2);
        recView.setAdapter(concatAdapter);

        Util.backButton(this);

    }

    private void getRequests() {
        CollectionReference userRef = FirebaseFirestore.getInstance()
                .collection(Strings.susers);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(userRef
                                .orderBy(saccounttype)
                                .whereEqualTo(saccounttype, 1)
                                .whereLessThan(saccounttype, 2)
                                .orderBy(Strings.stimestamp, Query.Direction.DESCENDING),
                        User.class)
                .build();

        FirestoreRecyclerOptions<User> options2 = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(userRef
                                .orderBy(saccounttype)
                                .whereEqualTo(saccounttype, 3)
                                .whereLessThan(saccounttype, 4),
                        User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, ViewHolder>(options) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amb_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
                holder.setData(model);
                DocumentReference itemRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                holder.setListener(itemRef);
            }
        };

        adapter2 = new FirestoreRecyclerAdapter<User, ViewHolder>(options2) {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amb_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {
                holder.setData(model);
                DocumentReference itemRef = getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference();
                holder.setListener(itemRef);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        adapter2.stopListening();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View btnCancel, btnAccept, view1;
        TextView tvHosp, tvUname, tvAmbNo, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmbNo = itemView.findViewById(R.id.tvAmbNo);
            tvHosp = itemView.findViewById(R.id.tvHosp);
            tvUname = itemView.findViewById(R.id.tvUname);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            view1 = itemView.findViewById(R.id.view1);
            view1.setVisibility(View.GONE);
            btnCancel = itemView.findViewById(R.id.btnCancel);

            itemView.setOnClickListener(view -> {
                if (view1.getVisibility() == View.GONE) {
                    view1.setVisibility(View.VISIBLE);
                } else {
                    view1.setVisibility(View.GONE);
                }
            });
        }

        public void setData(User model) {
            if (model != null) {
                tvHosp.setText(model.getHospital());
                tvAmbNo.setText(model.getAmbNo());
                tvEmail.setText(model.getEmail());
                tvUname.setText(String.format("%s - %s", model.getUname(), model.getPhone()));

                if (model.getAccountType() == 1) {
                    btnAccept.setVisibility(View.VISIBLE);
                } else {
                    btnAccept.setVisibility(View.GONE);
                }
            }

        }

        public void setListener(DocumentReference reference) {
            btnAccept.setOnClickListener(view -> {
                reference.update(saccounttype, 3).addOnSuccessListener(unused -> {
                    Toast.makeText(view.getContext(), "Approved", Toast.LENGTH_SHORT).show();
                });
            });

            btnCancel.setOnClickListener(view -> {
                reference.update(saccounttype, 1).addOnSuccessListener(unused -> {
                    Toast.makeText(view.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }
}