package com.example.remindme;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<UserTask> mUserTask;
    private Context context;


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

    //Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
       holder.text1.setText(mUserTask.get(position).getmDescription());
       holder.text2.setText(mUserTask.get(position).getmDate());
       holder.text3.setText(mUserTask.get(position).getmLocation());
       holder.text4.setText(mUserTask.get(position).getmTime());


       if(mUserTask.get(position).getmPriority().equals("High")) {
           holder.priorityImage.setVisibility(View.VISIBLE);
        }
       else holder.priorityImage.setVisibility(View.GONE);

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popup = new PopupMenu(context, holder.cardView);
                popup.inflate(R.menu.popup_menu_task);
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuShare:
                                Log.d("SHARE", "click on share");
                                break;
                            case R.id.menuEdit:
                                Log.d("UPDATE", "click on update");

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
        ImageView priorityImage;
        CardView cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            text1 = itemView.findViewById(R.id.textView_description);
            text2 = itemView.findViewById(R.id.textView_date);
            text3 = itemView.findViewById(R.id.textView_location);
            text4 = itemView.findViewById(R.id.textView_time);

            priorityImage = itemView.findViewById(R.id.imageView_priority);
            cardView = itemView.findViewById(R.id.cardViewTask);
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