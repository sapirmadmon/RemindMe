package com.example.remindme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RemindersFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    public static final String IS_SHARED_KEY = "IsSharedKey";
    private String TAG = "RegistrationActivity";
    private static final String USERS = "users";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    private ArrayList<UserTask> userTasks;
            //= new ArrayList<>();

    public RemindersFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        final Boolean isShared = getArguments().getBoolean(IS_SHARED_KEY);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);


        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS).child(userKey).child("tasks");
        userTasks = new ArrayList<UserTask>();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userTasks.clear();
                ArrayList<UserTask> tasks = new ArrayList<>();
                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                    UserTask ut = dataSnapshot1.getValue(UserTask.class);
                    if (ut.getmIsDone() == true) {
                        stopAlarm(getContext(), ut.getmNotificationId());
                    }
                    tasks.add(ut);

                }

                for (UserTask task:
                     tasks) {
                    if (task.getmIsShared() == isShared) {
                        userTasks.add(task);
                    }
                }

                // specify an adapter
                mAdapter = new MyAdapter(userTasks, getActivity());
                recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROR", "error in RemindersFragment");
            }
        });

        // New Feature - Swipe to delete item, also delete it from firebase
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                UserTask taskToRemove = userTasks.get(position);
                removeItem(taskToRemove.getmDescription());
                userTasks.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        helper.attachToRecyclerView(recyclerView);

        return view;
    }

    public static void stopAlarm(Context context, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    private void removeItem(String desc) {
        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS).child(userKey).child("tasks");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = mDatabase.orderByChild("mDescription").equalTo(desc);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
