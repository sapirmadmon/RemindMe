package com.example.remindme;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.math.BigInteger;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends Fragment  implements AdapterView.OnItemSelectedListener {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    private static final String CHANNEL_ID = "RemindMe";
    private String TAG = "RegistrationActivity";
    private static final String USERS = "users";

    private Switch switchDate;
    private Switch switchLocation;
    private LinearLayout linearLayoutOfDate;
    private LinearLayout linearLayoutOfLocation;
    private DatePickerDialog.OnDateSetListener setListener;

    private int tHour, tMinute;
    private String location;
    private String description;
    private String date;
    private String time;
    private String priority;

    private Spinner spinnerPriority;
    private EditText et_description;
    private EditText et_location;
    private TextView tvDate;
    private TextView tvTimer;

    private UserTask newTask;

    private String cDesc, cLocation, cDate, cTime, cPriority;
    private Boolean cIfShared;


    private String[] Priorities = new String[]{
            "Choose Priority",
            "High",
            "Medium",
            "Low",
    };

    private ArrayAdapter<String> spinnerArrayAdapter;
    public AddTaskFragment() {
        // Required empty public constructor
    }



    private  void switches(View view) {
        switchDate = (Switch) view.findViewById(R.id.switchDate);
        switchLocation = (Switch) view.findViewById(R.id.switchLocation);
        linearLayoutOfDate = (LinearLayout) view.findViewById(R.id.linearLayoutDate);
        linearLayoutOfLocation = (LinearLayout) view.findViewById(R.id.linearLayoutLocation);

        switchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    linearLayoutOfDate.setVisibility(View.VISIBLE);
                    Log.d("SWITCH", "switch date on");
                }
                else {
                    linearLayoutOfDate.setVisibility(View.GONE);
                    Log.d("SWITCH", "switch date off");
                }
            }
        });

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    linearLayoutOfLocation.setVisibility(View.VISIBLE);
                    Log.d("SWITCH", "switch location on");
                }
                else {
                    linearLayoutOfLocation.setVisibility(View.GONE);
                    Log.d("SWITCH", "switch location off");
                }
            }
        });
    }

    private void spinner() {

        spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item,Priorities);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerPriority.setAdapter(spinnerArrayAdapter);
        spinnerPriority.setOnItemSelectedListener(this);

    }

    private void chooseDate() {

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;
                String date = day+"/"+ month +"/"+year;
                tvDate.setText(date);
            }
        };

    }

    private void chooseTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        final int hour = calendar.get(Calendar.HOUR);
        final int min = calendar.get(Calendar.MINUTE);

        tvTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //init time picker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                //init hour and minute
                                tHour = hourOfDay;
                                tMinute = minute;
                                //store hour and minute in string
                                String time = tHour + ":" + tMinute;
                                //init 24 hours time format
                                SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");

                                try {
                                    Date date = f24Hours.parse(time);
                                    //init 12 hours time format
                                    SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                                    //set selected time on textview
                                    tvTimer.setText(f12Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },hour, min, false);

                //set transparent background
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //display previous selected time
                timePickerDialog.updateTime(tHour,tMinute);
                //show dialog
                timePickerDialog.show();
            }
        });
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS);

        tvDate = (TextView) view.findViewById(R.id.textViewChooseDate);
        tvTimer = (TextView) view.findViewById(R.id.textViewChooseTime);
        et_description = (EditText) view.findViewById(R.id.description_task_textView);
        et_location = (AutoCompleteTextView)view.findViewById(R.id.autocompleteLocation);
        spinnerPriority = (Spinner) view.findViewById(R.id.SpinnerPriority);

        final Button btnAddTask = (Button) view.findViewById(R.id.addTask_button);
        TextView tvTitle = (TextView) view.findViewById(R.id.title_task);

        spinner();
        switches(view);
        chooseDate();
        chooseTime();

        Bundle arg = getArguments();
        if(arg != null) {

            cDesc = getArguments().getString("mDescription");
            cDate = getArguments().getString("mDate");
            cTime = getArguments().getString("mTime");
            cLocation = getArguments().getString("mLocation");
            cPriority = getArguments().getString("mPriority");
            cIfShared = getArguments().getBoolean("mIsShared");

            //set this data to views
            tvDate.setText(cDate);
            tvTimer.setText(cTime);
            et_description.setText(cDesc);
            et_location.setText(cLocation);
            int spinnerPosition = spinnerArrayAdapter.getPosition(cPriority);
            spinnerPriority.setSelection(spinnerPosition);

            btnAddTask.setText("Update");
            tvTitle.setText("Edit Task");

        }


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tasks = new ArrayList<UserTask>();

                if(btnAddTask.getText().equals("Update")) {
                    updateTaskDatabase();
                }

                else {
                    description = (et_description).getText().toString();
                    location = (et_location).getText().toString();
                    date = tvDate.getText().toString();
                    time = tvTimer.getText().toString();

                    newTask = new UserTask(description, date, time, location, priority, false); //create new task
                    setNewNotification();

                    String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String key = mDatabase.child(userKey).child("tasks").push().getKey();

                    Map<String, Object> map = new HashMap<>();
                    map.put(key, newTask);
                    mDatabase.child(userKey).child("tasks").updateChildren(map);

                }
                    getParentFragmentManager().beginTransaction().remove(AddTaskFragment.this).commit();
            }
        });

        ImageView imageExit = (ImageView) view.findViewById(R.id.imageViewExit);
        imageExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction().remove(AddTaskFragment.this).commit();
            }
        });

        return view;
    }


    private void updateTaskDatabase() {
        description = (et_description).getText().toString();
        location = (et_location).getText().toString();
        date = tvDate.getText().toString();
        time = tvTimer.getText().toString();

        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS).child(userKey).child("tasks");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = mDatabase.orderByChild("mDescription").equalTo(cDesc);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    ds.getRef().child("mDescription").setValue(description);
                    ds.getRef().child("mDate").setValue(date);
                    ds.getRef().child("mTime").setValue(time);
                    ds.getRef().child("mLocation").setValue(location);
                    ds.getRef().child("mPriority").setValue(priority);
                    ds.getRef().child("mIsShared").setValue(cIfShared);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }


    private void setNewNotification() {
        if (newTask != null) {
            int prio = NotificationCompat.PRIORITY_DEFAULT;

            switch (newTask.getmPriority()){
                case "High":
                    prio = NotificationCompat.PRIORITY_HIGH;
                    break;
                case "Medium":
                    prio = NotificationCompat.PRIORITY_DEFAULT;
                    break;
                case "Low":
                    prio = NotificationCompat.PRIORITY_LOW;
                    break;
            }

            SimpleDateFormat f24Hours = new SimpleDateFormat("dd/MM/yyyy HH:mm aa");
            String dateStr = newTask.getmDate()+ " " + newTask.getmTime();
            Date date = null;
            try {
                date = f24Hours.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (getActivity() != null &&  date != null) {
                int notificationId = Sequence.nextValue();
                Intent intent = new Intent(this.getContext(), MainActivity.class);
                PendingIntent activity = PendingIntent.getActivity(this.getContext(), notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                Calendar calendar = Calendar.getInstance();
                long delta = 0;
                if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                    delta += 15;
                }
                else {
                    delta = calendar.getTimeInMillis() - System.currentTimeMillis();
                }

                Notification notification = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_date_range_black_24dp)
                        .setContentIntent(activity)
                        .setContentTitle(newTask.getmDescription())
                        .setAutoCancel(true)
                        .setContentText(newTask.getmDescription())
                        .setPriority(prio)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .build();

                Intent notificationIntent = new Intent(this.getContext(), MyNotificationPublisher.class);
                notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
                notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null)
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, delta, pendingIntent);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        priority = parent.getItemAtPosition(position).toString();

//        if(priority.equals(Priority.HIGH.name()))
//            priorityEnum = Priority.HIGH;
//        else if(priority.equals(Priority.MEDIUM.name()))
//            priorityEnum = Priority.MEDIUM;
//        else if(priority.equals(Priority.LOW.name()))
//            priorityEnum = Priority.LOW;

        Log.d("PRIORITY_SPINNER" , priority);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
