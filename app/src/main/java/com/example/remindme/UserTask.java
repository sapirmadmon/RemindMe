package com.example.remindme;

import java.sql.Time;
import java.util.Date;

public class UserTask {
    private String mDescription;
    private Date mDate;
    private Time mTime;
    private String mLocation;
    private Priority mPriority;

    public UserTask(String mDescription, Date mDate, Time mTime, String mLocation, Priority mPriority) {
        this.mDescription = mDescription;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mLocation = mLocation;
        this.mPriority = mPriority;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public Time getmTime() {
        return mTime;
    }

    public void setmTime(Time mTime) {
        this.mTime = mTime;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Priority getmPriority() {
        return mPriority;
    }

    public void setmPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }
}
