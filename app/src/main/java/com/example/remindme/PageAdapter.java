package com.example.remindme;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {


    private int numOfTabs;

    public PageAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numOfTabs = numOfTabs;
    }


//    public PageAdapter(FragmentManager fm, int numOfTabs) {
//        super(fm);
//        this.numOfTabs = numOfTabs;
//    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RemindersFragment privateTasks = new RemindersFragment();
                Bundle args = new Bundle();
                args.putBoolean(RemindersFragment.IS_SHARED_KEY, false);
                privateTasks.setArguments(args);
                return privateTasks;
            case 1:
                RemindersFragment sharedTasks = new RemindersFragment();
                Bundle args1 = new Bundle();
                args1.putBoolean(RemindersFragment.IS_SHARED_KEY, true);
                sharedTasks.setArguments(args1);
                return sharedTasks;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
