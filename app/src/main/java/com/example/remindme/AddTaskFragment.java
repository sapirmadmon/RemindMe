package com.example.remindme;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.StorageReference;

import java.math.BigInteger;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;

    private static final String CHANNEL_ID = "RemindMe";
    private static final String apiKey = "AIzaSyCbfyKcBZfwZ6EMalOchUYvQx6S7vWBXUc";
    private String TAG = "RegistrationActivity";
    private static final String USERS = "users";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

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
    private AutocompleteSupportFragment autocompleteFragment;

    private UserTask newTask;

    private String cDesc, cLocation, cDate, cTime, cPriority;
    private Boolean isDateChecked = false, isLocationChecked = false;
    private Boolean cIfShared;
    private double longtitude, latitude;


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


    private void switches(View view) {
        switchDate = (Switch) view.findViewById(R.id.switchDate);
        switchLocation = (Switch) view.findViewById(R.id.switchLocation);
        linearLayoutOfDate = (LinearLayout) view.findViewById(R.id.linearLayoutDate);
        linearLayoutOfLocation = (LinearLayout) view.findViewById(R.id.linearLayoutLocation);

        switchDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDateChecked = isChecked;
                if (isChecked) {
                    linearLayoutOfDate.setVisibility(View.VISIBLE);
                    Log.d("SWITCH", "switch date on");
                } else {
                    linearLayoutOfDate.setVisibility(View.GONE);
                    Log.d("SWITCH", "switch date off");
                }
            }
        });

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLocationChecked = isChecked;
                if (isChecked) {
                    linearLayoutOfLocation.setVisibility(View.VISIBLE);
                    Log.d("SWITCH", "switch location on");
                } else {
                    linearLayoutOfLocation.setVisibility(View.GONE);
                    Log.d("SWITCH", "switch location off");
                }
            }
        });
    }

    private void spinner() {

        spinnerArrayAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_item, Priorities);
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
                        , setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
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
                                    tvTimer.setText(f24Hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, hour, min, false);

                //set transparent background
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //display previous selected time
                timePickerDialog.updateTime(tHour, tMinute);
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

        if (getContext() != null)
            Places.initialize(getContext(), apiKey);

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS);

        tvDate = (TextView) view.findViewById(R.id.textViewChooseDate);
        tvTimer = (TextView) view.findViewById(R.id.textViewChooseTime);
        et_description = (EditText) view.findViewById(R.id.description_task_textView);
        spinnerPriority = (Spinner) view.findViewById(R.id.SpinnerPriority);

        final Button btnAddTask = (Button) view.findViewById(R.id.addTask_button);
        TextView tvTitle = (TextView) view.findViewById(R.id.title_task);
        Switch swDate = (Switch) view.findViewById(R.id.switchDate);
        Switch swLocation = (Switch) view.findViewById(R.id.switchLocation);

        spinner();
        switches(view);
        chooseDate();
        chooseTime();

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            // Specify the types of place data to return.
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NotNull Place place) {
                    location = place.getName();
                    if (place.getLatLng() != null) {
                        longtitude = place.getLatLng().longitude;
                        latitude = place.getLatLng().latitude;
                    }

                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                }


                @Override
                public void onError(@NotNull Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }

//        Bundle arg = getArguments();
//        if (arg != null) {
//
//            cDesc = getArguments().getString("mDescription");
//            cDate = getArguments().getString("mDate");
//            cTime = getArguments().getString("mTime");
//            cLocation = getArguments().getString("mLocation");
//            cPriority = getArguments().getString("mPriority");
//            cIfShared = getArguments().getBoolean("mIsShared");
//
//            //set this data to views
//            tvDate.setText(cDate);
//            tvTimer.setText(cTime);
//            et_description.setText(cDesc);
//            autocompleteFragment.setText(cLocation);
//
//            int spinnerPosition = spinnerArrayAdapter.getPosition(cPriority);
//            spinnerPriority.setSelection(spinnerPosition);
//
//            btnAddTask.setText("Update");
//            tvTitle.setText("Edit Task");
//            swDate.setVisibility(View.GONE);
//            swLocation.setVisibility(View.GONE);
//
//        }


        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (btnAddTask.getText().equals("Update")) {
//                    updateTaskDatabase();
//                    getParentFragmentManager().beginTransaction().remove(AddTaskFragment.this).commit();
//
//                } else {
                    description = (et_description).getText().toString();
                    //location = (et_location).getText().toString();
                    date = tvDate.getText().toString();
                    time = tvTimer.getText().toString();

                    if (description.isEmpty()) {
                        Toast.makeText(getContext(), "The description must be added to the task", Toast.LENGTH_SHORT).show();
                    } else {
                        newTask = new UserTask(description, date, time, location, priority, false, false); //create new task

                        setNewNotification();

                        String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String key = mDatabase.child(userKey).child("tasks").push().getKey();

                        Map<String, Object> map = new HashMap<>();
                        map.put(key, newTask);
                        mDatabase.child(userKey).child("tasks").updateChildren(map);

                        getParentFragmentManager().beginTransaction().remove(AddTaskFragment.this).commit();

                    }
 //               }
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




    private final int REQUEST_PERMISSION_LOCATION=1;
    private void setNewNotification() {
        if (newTask != null) {
            int prio = NotificationCompat.PRIORITY_DEFAULT;

            switch (newTask.getmPriority()) {
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



            if (getActivity() != null) {
                int notificationId = Sequence.nextValue();
                newTask.setmNotificationId(notificationId);
                Intent intent = new Intent(this.getContext(), MainActivity.class);
                PendingIntent activity = PendingIntent.getActivity(this.getContext(), notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

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

                if (isDateChecked){
                    SimpleDateFormat f24Hours = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date date = new Date();
                    try {
                        String dateStr = newTask.getmDate()+ " " + newTask.getmTime();
                        date = f24Hours.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    long delta = 0;
                    if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                        delta += System.currentTimeMillis() + 1500;
                    }
                    else {
                        delta = System.currentTimeMillis() + (calendar.getTimeInMillis() - System.currentTimeMillis());
                    }
                    AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null)
                        alarmManager.setRepeating(AlarmManager.RTC, delta, 86400*1000, pendingIntent);
                }

                if (isLocationChecked){
                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_LOCATION);
                    }
                    if (locationManager != null) {
                        locationManager.addProximityAlert(latitude, longtitude, 1000, -1, pendingIntent);
                    }
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        priority = parent.getItemAtPosition(position).toString();

        Log.d("PRIORITY_SPINNER" , priority);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}
