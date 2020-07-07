package com.example.remindme;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tab_privateTask;
    private TabItem tab_sharedTask;
    public PageAdapter pageAdapter;
   // private FloatingActionButton fab;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tab_privateTask = (TabItem) view.findViewById(R.id.tab1_privateTask);
        tab_sharedTask = (TabItem) view.findViewById(R.id.tab2_sharedTask);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        FloatingActionButton logoutImageView = (FloatingActionButton)view.findViewById(R.id.logout);
        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent mainActivityIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(mainActivityIntent);

            }
        });

       // fab = (FloatingActionButton) view.findViewById(R.id.fab);

        pageAdapter = new PageAdapter(getParentFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0) {
                    pageAdapter.notifyDataSetChanged();
                }
                else if(tab.getPosition() == 1) {
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddTaskFragment fragmentAddTask = new AddTaskFragment();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.add(R.id.container2 , fragmentAddTask).commit();
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        return view;
    }
}
