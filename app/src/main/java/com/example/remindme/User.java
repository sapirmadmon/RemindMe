package com.example.remindme;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public class User {

    private String email;
    private String password;
    private String userName;
    //private Uri avatar;
    private Task[] tasks;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, String userName, Task[] tasks) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        //this.avatar = avatar;
        this.tasks = tasks;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

//    public Uri getAvatar() {
//        return avatar;
//    }
//
//    public void setAvatar(Uri avatar) {
//        this.avatar = avatar;
//    }

    public Task[] getTasks() {
        return tasks;
    }

    public void setTasks(Task[] tasks) {
        this.tasks = tasks;
    }
}
