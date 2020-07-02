package com.example.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends Fragment  implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerPriority;
    private String priority;
    private Switch switchDate;
    private Switch switchLocation;
    private LinearLayout linearLayoutOfDate;
    private LinearLayout linearLayoutOfLocation;
    private DatePickerDialog.OnDateSetListener setListener;
    private TextView tvDate;
    private TextView tvTimer;
    private int tHour, tMinute;

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

    private void spinner(View view) {

        String[] priority = new String[]{
                "Choose Priority",
                "High",
                "Medium",
                "Low",
        };

        spinnerPriority = (Spinner) view.findViewById(R.id.SpinnerPriority);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
//                R.array.priority_spinner, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerPriority.setAdapter(adapter);
//        spinnerPriority.setOnItemSelectedListener(this);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this.getActivity(), R.layout.spinner_item,priority);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerPriority.setAdapter(spinnerArrayAdapter);
        spinnerPriority.setOnItemSelectedListener(this);

    }

    private void chooseDate(View view) {
        tvDate = (TextView) view.findViewById(R.id.textViewChooseDate);

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
                String date = day+"/"+month+"/"+year;
                tvDate.setText(date);
            }
        };

    }

    private void chooseTime(View view) {
        tvTimer = (TextView) view.findViewById(R.id.textViewChooseTime);

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
                        },12, 0, false
                );
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
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        spinner(view);
        switches(view);
        chooseDate(view);
        chooseTime(view);


        ImageView imageExit = (ImageView) view.findViewById(R.id.imageViewExit);
        imageExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(AddTaskFragment.this).commit();
            }
        });

        return view;
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
