package com.example.remindme;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<UserTask> mUserTask;
    private Context context;
    private Dialog dialog;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private static final String USERS = "users";

    public MyAdapter(ArrayList<UserTask> mUserTask, Context context) {
        this.mUserTask = mUserTask;
        this.context = context;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_task, parent, false);

        return new MyViewHolder(view);
    }


    public void showDialog_shareTask (final int position) {
        //TODO - If the email is not found -> error message

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_share_task);

        Button btn_positive = (Button) dialog.findViewById(R.id.dialog_positive_btn);
        Button btn_negative = (Button) dialog.findViewById(R.id.dialog_negative_btn);
        final EditText et_emailUser = (EditText) dialog.findViewById(R.id.et_email);
        final TextView error = dialog.findViewById(R.id.textView_error);

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_emailUser.getText().toString().isEmpty()) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Insert email");

                } else {
                    dialog.cancel();
                    String emailUser = et_emailUser.getText().toString();

                    database = FirebaseDatabase.getInstance();
                    mDatabase = database.getReference(USERS);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS);

                    reference.orderByChild("email").equalTo(emailUser).addListenerForSingleValueEvent
                            (new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        String uid = datas.child("uId").getValue().toString();
                                        String key = mDatabase.child(uid).child("tasks").push().getKey();

                                        UserTask newTask = mUserTask.get(position);
                                        newTask.setmIsShared(true);

                                        Map<String, Object> map = new HashMap<>();
                                        map.put(key, newTask);
                                        mDatabase.child(uid).child("tasks").updateChildren(map);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d("ERROR", "onCancelled");
                                }
                            });
                }
            }
        });

        // Set negative/no button click listener
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Log.d("CANCEL", "cancel dialog box");
            }
        });

        dialog.show();
    }




    //Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

       final String desc = mUserTask.get(position).getmDescription();
       final String date = mUserTask.get(position).getmDate();
       final String location = mUserTask.get(position).getmLocation();
       final String time = mUserTask.get(position).getmTime();
       final String priority = mUserTask.get(position).getmPriority();
       final Boolean ifShared = mUserTask.get(position).getmIsShared();
       final Boolean isDone = mUserTask.get(position).getmIsDone();

       holder.text1.setText(desc);
       holder.text2.setText(date);
       holder.text3.setText(location);
       holder.text4.setText(time);
       holder.checkboxIfDone.setChecked(isDone);


       if(mUserTask.get(position).getmPriority().equals("High")) {
           holder.priorityImage.setVisibility(View.VISIBLE);
        }
       else holder.priorityImage.setVisibility(View.GONE);

        if(mUserTask.get(position).getmPriority().equals("Medium")) {
            holder.mediumPriorityImage.setVisibility(View.VISIBLE);
        }
        else holder.mediumPriorityImage.setVisibility(View.GONE);

        if(mUserTask.get(position).getmPriority().equals("Low")) {
            holder.lowPriorityImage.setVisibility(View.VISIBLE);
        }
        else holder.lowPriorityImage.setVisibility(View.GONE);

        holder.checkboxIfDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                String userId = null;
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (userId != null) {
                    final String finalUserId = userId;
                    database = FirebaseDatabase.getInstance();
                    mDatabase = database.getReference(USERS).child(finalUserId).child("tasks");
                    Query applesQuery = mDatabase.orderByChild("mDescription").equalTo(desc);

                    applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                ds.getRef().child("mIsDone").setValue(isChecked);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("MyAdapter", "onCancelled", databaseError.toException());
                        }
                    });
                }
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                PopupMenu popup = new PopupMenu(context, holder.cardView);
                popup.inflate(R.menu.popup_menu_task);
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuShare:
                                Log.d("SHARE", "click on share");
                                showDialog_shareTask(position);
                                break;

                            case R.id.menuEdit:
                                Log.d("UPDATE", "click on update");

                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                Fragment fragmentAddTask = new AddTaskFragment();

                                Bundle args = new Bundle();
                                args.putString("mDescription", desc);
                                args.putString("mDate", date);
                                args.putString("mLocation", location);
                                args.putString("mPriority", priority);
                                args.putString("mTime", time);
                                args.putBoolean("mIsShared", ifShared);
                                fragmentAddTask.setArguments(args);

                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragmentAddTask).addToBackStack(null).commit();

                                break;
                        }
                        return false;
                    }
                });

                return false;
            }


        });


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mUserTask.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
            //implements View.OnCreateContextMenuListener {

        TextView text1, text2, text3, text4;
        ImageView priorityImage, lowPriorityImage, mediumPriorityImage;
        CheckBox checkboxIfDone;
        CardView cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            text1 = itemView.findViewById(R.id.textView_description);
            text2 = itemView.findViewById(R.id.textView_date);
            text3 = itemView.findViewById(R.id.textView_location);
            text4 = itemView.findViewById(R.id.textView_time);

            priorityImage = itemView.findViewById(R.id.imageView_priority);
            mediumPriorityImage = itemView.findViewById(R.id.imageView_medium_priority);
            lowPriorityImage = itemView.findViewById(R.id.imageView_low_priority);

            cardView = itemView.findViewById(R.id.cardViewTask);
            checkboxIfDone = itemView.findViewById(R.id.checkboxIfDone);
          //  cardView.setOnCreateContextMenuListener(this);

        }

//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.add(this.getAdapterPosition(), 121, 0, "Delete");
//            menu.add(this.getAdapterPosition(), 122, 1, "Update");
//            menu.add(this.getAdapterPosition(), 123, 2, "Share");
//
//        }
    }
}