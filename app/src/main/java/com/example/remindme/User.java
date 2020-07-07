package com.example.remindme;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String email;
    private String uId;
    private String userName;
    private String avatar;
    private List<UserTask> tasks;

    public User() {
    }

    public User(String email, String userName, List<UserTask> tasks) {
        this.email = email;
        this.userName = userName;
        this.tasks = tasks;
    }

    public User(String email, String uId, String userName, String avatar, List<UserTask> tasks) {
        this.email = email;
        this.uId = uId;
        this.userName = userName;
        this.avatar = avatar;
        this.tasks = tasks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<UserTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<UserTask> tasks) {
        this.tasks = tasks;
    }
}
