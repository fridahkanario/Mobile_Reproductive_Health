package com.fusion.kim.m_reproductivehealth;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PrevisitFormsActivity extends AppCompatActivity {

    private EditText mPatientNameInput, mAddressInput, mPhoneInput, mDocNameInput, mDocPhoneInput;
    private TextView mDateTv, mDobTv;

    private RadioGroup mGenderGroup, mRelationGroup;
    private RadioButton mGenderButton, mRelationButton;

    private Button mSubmitBtn;

    private DatabaseReference mPrevisitRef;

    private ProgressDialog mLoadingPd;

    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previst_forms);

        mPrevisitRef = FirebaseDatabase.getInstance().getReference().child("PrevisitForms");
        mPrevisitRef.keepSynced(true);

        mPatientNameInput = findViewById(R.id.input_form_name);
        mAddressInput = findViewById(R.id.input_form_address);
        mPhoneInput = findViewById(R.id.input_form_phone);
        mDocNameInput = findViewById(R.id.input_doctor_name);
        mDocPhoneInput = findViewById(R.id.input_doctor_phone);
        mDateTv = findViewById(R.id.tv_previsit_date);
        mDobTv = findViewById(R.id.tv_form_dob);

        mDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });

        mDobTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDOBPicker();
            }
        });

        mGenderGroup = findViewById(R.id.rg_gender);
        mRelationGroup = findViewById(R.id.rg_relation);

        mLoadingPd = new ProgressDialog(this);
        mLoadingPd.setCancelable(false);
        mLoadingPd.setTitle("Submitting Details");
        mLoadingPd.setMessage("Please wait...");

        mSubmitBtn = findViewById(R.id.btn_submit_form);
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String patientName = mPatientNameInput.getText().toString().trim();
                String address = mAddressInput.getText().toString().trim();
                String patientPhone = mPhoneInput.getText().toString().trim();
                String docName = mDocNameInput.getText().toString().trim();
                String docPhone = mDocPhoneInput.getText().toString().trim();
                String date = mDateTv.getText().toString().trim();
                String dob = mDobTv.getText().toString().trim();

                int genderSelectedId = mGenderGroup.getCheckedRadioButtonId();

                mGenderButton = findViewById(genderSelectedId);

                String gender = mGenderButton.getText().toString();

                int relationSelectedId = mRelationGroup.getCheckedRadioButtonId();

                mRelationButton = findViewById(relationSelectedId);

                String relation = mRelationButton.getText().toString();

                if (TextUtils.isEmpty(patientName)){

                    mPatientNameInput.setError("Please enter your name");
                    return;

                }

                if (TextUtils.isEmpty(address)){

                    mAddressInput.setError("Please enter your address");
                    return;

                }

                if (TextUtils.isEmpty(patientPhone)){

                    mPhoneInput.setError("Please enter your Phone Number");
                    return;

                }

                if (TextUtils.isEmpty(docName)){

                    mDocNameInput.setError("Please enter doctor's name");
                    return;

                }

                if (TextUtils.isEmpty(docPhone)){

                    mDocPhoneInput.setError("Please enter doctor's phone number");
                    return;

                }

                if (TextUtils.isEmpty(date)){

                    mDateTv.setError("Please pick a date");
                    return;

                }

                if (TextUtils.isEmpty(dob)){

                    mDobTv.setError("Please pick a date of birth");
                    return;

                }

                if (TextUtils.isEmpty(gender)){

                    Toast.makeText(PrevisitFormsActivity.this, "Please select a gender", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (TextUtils.isEmpty(relation)){

                    Toast.makeText(PrevisitFormsActivity.this, "Please select a relationship", Toast.LENGTH_SHORT).show();
                    return;

                }

                if (!TextUtils.isEmpty(patientName) && !TextUtils.isEmpty(address) && !TextUtils.isEmpty(patientPhone) && !TextUtils.isEmpty(docName)
                        && !TextUtils.isEmpty(docPhone) && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(dob)){

                    mLoadingPd.show();

                    Map detailsMap = new HashMap();
                    detailsMap.put("patientName",patientName);
                    detailsMap.put("patientAddress",address);
                    detailsMap.put("patientPhone",patientPhone);
                    detailsMap.put("docName",docName);
                    detailsMap.put("docPhone",docPhone);
                    detailsMap.put("date",date);
                    detailsMap.put("patientDOB",dob);
                    detailsMap.put("gender",gender);
                    detailsMap.put("relation",relation);

                    mPrevisitRef.push().setValue(detailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mLoadingPd.dismiss();
                                Toast.makeText(PrevisitFormsActivity.this, "Details Submitted successfully.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(PrevisitFormsActivity.this, PortalActivity.class);
                                startActivity(intent);
                                finish();

                            } else {

                                mLoadingPd.dismiss();
                                Toast.makeText(PrevisitFormsActivity.this, "Failed to upload details. Try again.", Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                }

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

                mDateTv.setText(year + "-" + month + "-" + day);

            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    public void showDOBPicker() {
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

                mDobTv.setText(year + "-" + month + "-" + day);

            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }


}
