package com.example.remindme;

import android.util.Log;

import java.sql.Time;
import java.util.Date;

public class UserTask {
    private String mDescription;
    //private Date mDate;
    private String mDate;
    //private Time mTime;
    private String mTime;
    private String mLocation;
   // private Priority mPriority;
   private String mPriority;
   private Boolean mIsShared;
   private Boolean mIsDone;

   public UserTask () {
   }


    public UserTask(String mDescription, String mDate, String mTime, String mLocation, String mPriority, Boolean mIsShared, Boolean mIsDone) {
        this.mDescription = mDescription;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mLocation = mLocation;
        this.mPriority = mPriority;
        this.mIsShared = mIsShared;
        this.mIsDone = mIsDone;
    }

    public void setmIsDone(Boolean mIsDone) {
        this.mIsDone = mIsDone;
    }

    public Boolean getmIsDone() {
        return mIsDone;
    }

    public void setmIsShared(Boolean mIsShared) {
        this.mIsShared = mIsShared;
    }

    public Boolean getmIsShared() {
        return mIsShared;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmPriority() {
        return mPriority;
    }

    public void setmPriority(String mPriority) {
        this.mPriority = mPriority;
    }
}
