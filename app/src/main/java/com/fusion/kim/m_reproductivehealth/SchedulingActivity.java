package com.fusion.kim.m_reproductivehealth;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulingActivity extends AppCompatActivity {

    private TextView mDateInput, mTimeInput, mHospitalsTv;
    private Spinner mDoctorsSpinner;
    private Button mBookBtn;
    private EditText mPurposeInput;

    private String mHsopital = "", mDoctor = "", mRegion = "";

    Calendar date;

    private DatabaseReference mAppointmentsRef;

    String hospitalName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling);

        mAppointmentsRef = FirebaseDatabase.getInstance().getReference().child("Appointments");

        mDateInput = findViewById(R.id.tv_date);
        mTimeInput = findViewById(R.id.tv_app_time);

        mHospitalsTv = findViewById(R.id.tv_app_hospital);
        mDoctorsSpinner = findViewById(R.id.spinner_doctor);
        mBookBtn = findViewById(R.id.btn_book);
        mPurposeInput = findViewById(R.id.et_purpose);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(getIntent().getStringExtra("phoneNumber")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mRegion = dataSnapshot.child("region").getValue().toString();
                mHsopital = dataSnapshot.child("hospital").getValue().toString();

                if (!mHsopital.equals("")){

                    FirebaseDatabase.getInstance().getReference().child("Hospitals").child(mRegion)
                            .child(mHsopital).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            hospitalName = dataSnapshot.child("hospitalName").getValue(String.class);
                            mHospitalsTv.setText(hospitalName);

                            FirebaseDatabase.getInstance().getReference().child("Doctors").child(mHsopital).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    final List<String> doctors = new ArrayList<String>();

                                    for (DataSnapshot hospitalSnapshot: dataSnapshot.getChildren()) {

                                        String firstName = hospitalSnapshot.child("firstName").getValue().toString();
                                        String lastName = hospitalSnapshot.child("lastName").getValue().toString();
                                        doctors.add(firstName+ " " + lastName);

                                        ArrayAdapter<String> doctorsAdapter = new ArrayAdapter<String>(SchedulingActivity.this, android.R.layout.simple_spinner_item, doctors);
                                        doctorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        mDoctorsSpinner.setAdapter(doctorsAdapter);
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDoctorsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);

                mDoctor = selectedItemText;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePicker();

            }
        });

        mTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePicker();

            }
        });

        mBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String purpose = mPurposeInput.getText().toString().trim();
                String date = mDateInput.getText().toString().trim();
                String time = mTimeInput.getText().toString().trim();

                if (TextUtils.isEmpty(purpose)){

                    mPurposeInput.setError("Enter the purpose for the visit");
                    return;

                }

                if (TextUtils.isEmpty(date)){

                    mDateInput.setError("Enter the purpose for the visit");

                    return;

                }

                if (TextUtils.isEmpty(time)){

                    mTimeInput.setError("Enter the purpose for the visit");

                    return;

                }

                if (mHsopital.equals("")){

                    Toast.makeText(SchedulingActivity.this, "Select a Hospital", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (mDoctor.equals("")){

                    Toast.makeText(SchedulingActivity.this, "Select a Doctor", Toast.LENGTH_SHORT).show();
                    return;

                }

                Map appMap = new HashMap();
                appMap.put("purpose",purpose);
                appMap.put("date",date);
                appMap.put("time",time);
                appMap.put("hospital",hospitalName);
                appMap.put("doctor",mDoctor);


                mAppointmentsRef.child(getIntent().getStringExtra("phoneNumber")).push().setValue(appMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            Intent portalIntent = new Intent(SchedulingActivity.this, PortalActivity.class);
                            portalIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(portalIntent);
                            finish();

                            Toast.makeText(SchedulingActivity.this, "Appointment Made", Toast.LENGTH_LONG).show();

                        }

                    }
                });

            }
        });

    }

    public void showDatePicker() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);

                String day = "";
                String month = "";

                if (dayOfMonth < 10){

                    day = "0"+dayOfMonth;

                } else {

                    day = ""+dayOfMonth;

                }

                if (monthOfYear < 10){

                    month = "0"+monthOfYear;

                } else {

                    month = ""+monthOfYear;

                }

                Date currentDate = null;

                Date c = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedCurrentDate = df.format(c);

                String formattedPickedDate = year+"-"+month+"-"+day;


                try {
                    Date date2 = df.parse(formattedCurrentDate);
                    Date date1 = df.parse(formattedPickedDate);

                    if (date2.compareTo(date1) > 0) {

                        Toast.makeText(SchedulingActivity.this, "You cannot book this date", Toast.LENGTH_LONG).show();
                    } else {

                        mDateInput.setText(formattedPickedDate);

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public void showTimePicker(){

        final Calendar currentDate = Calendar.getInstance();

        new TimePickerDialog(SchedulingActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minuteOfHour);

                String minute = "";
                String hour = "";

                if (hourOfDay < 10){

                    hour = "0"+hourOfDay;

                } else {

                    hour = ""+hourOfDay;

                }

                if (minuteOfHour < 10){

                    minute = "0"+minuteOfHour;

                } else {

                    minute = ""+minuteOfHour;

                }

                mTimeInput.setText(hour + ":"+  minute);


            }
        }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();

    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
