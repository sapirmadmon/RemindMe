package com.example.remindme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Task[] mTask;
    private Context context;

    public MyAdapter(Context context, Task[] task) {
        this.context = context;
        this.mTask = task;
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
       holder.text1.setText(mTask[position].getmDescription());
       holder.text2.setText(mTask[position].getmDate().toString());
       holder.text3.setText(mTask[position].getmLocation());



    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mTask.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text1, text2, text3;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            text1 = itemView.findViewById(R.id.textView_description);
            text2 = itemView.findViewById(R.id.textView_date);
            text3 = itemView.findViewById(R.id.textView_location);
        }
    }
}