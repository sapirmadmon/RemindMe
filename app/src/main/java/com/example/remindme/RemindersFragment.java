package com.example.remindme;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RemindersFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    //***for test***
//    private Task[] tasks = {new Task("task1", new Date("25/11/2020"), new Time(143000),"aaaaa", Priority.HIGH),
//                            new Task("task2", new Date("25/11/2020"), new Time(143000),"bbbbb", Priority.HIGH),
//                            new Task("task3", new Date("25/11/2020"), new Time(143000),"ccccc",  Priority.HIGH)};

    private ArrayList<UserTask> userTasks = new ArrayList<>();

    public RemindersFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);


      //  tasks.add(new Task("task1", new Date("25/11/2020"), new Time(143000),"aaaaa", Priority.HIGH));
      //  tasks.add(new Task("task2", new Date("25/11/2020"), new Time(143000),"aaaaa", Priority.HIGH));
      //  tasks.add(new Task("task3", new Date("25/11/2020"), new Time(143000),"aaaaa", Priority.HIGH));

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);



        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(userTasks, getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
