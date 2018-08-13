package com.fusion.kim.m_reproductivehealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private ProgressDialog mProgressDialog;

    private boolean mVerificationInProgress = false;
    private String mVerificationId;

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private TextInputLayout textInputLayoutCode;

    private TextInputEditText textInputEditTextName;
    private TextInputEditText textInputEditTextPhone;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;
    private TextInputEditText textInputEditTextCode;

    private AppCompatButton appCompatButtonRegister;
    private AppCompatTextView appCompatTextViewLoginLink;

    private DatabaseReference mUsersRef, mRegionsRef;

    private Spinner spinner;

    private String mRegion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();


        //get the regions in the database
        mRegionsRef = FirebaseDatabase.getInstance().getReference().child("Regions");

        //show dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing In...");
        mProgressDialog.setCancelable(false);

        //put the regions in a spinner
        spinner =findViewById(R.id.sp_location);
        final List<Regions> regionsList = new ArrayList<>();

        //make the regions to respond when clicked
        mRegionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot regionSnapshot : dataSnapshot.getChildren()){

                    String region = regionSnapshot.child("regionName").getValue(String.class);
                    String key = regionSnapshot.getKey();

                    regionsList.add(new Regions(key, region));

                    spinner.setAdapter(new RegionSpinnerAdapter(RegisterActivity.this, R.layout.row, regionsList));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRegion = regionsList.get(position).getKey();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        appCompatButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = textInputEditTextName.getText().toString().trim();
                String password = textInputEditTextPassword.getText().toString().trim();
                String confirmPassword = textInputEditTextConfirmPassword.getText().toString().trim();
                String email = textInputEditTextCode.getText().toString().trim();
                String phone = textInputEditTextPhone.getText().toString().trim();

                if (!password.equals(confirmPassword)){

                    Toast.makeText(RegisterActivity.this, "Passwords do not match. Please check and try again.", Toast.LENGTH_LONG).show();


                    return;

                } else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(phone)&& !mRegion.equals("")){

                    if(checknet() ) {

                        mProgressDialog.show();

                        beginRegistration(name, password, email, phone);
                    }else{

                        mProgressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Kindly check your internet connection!",Toast.LENGTH_LONG).show();
                    }


                }else {

                    Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                }

            }
        });
        appCompatTextViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
            }
        });

    }
    private void beginRegistration(final String name, final String password, final String email, final String phone) {

        final Map detailsMap = new HashMap<>();

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(phone)) {

                    detailsMap.put("name", name);
                    detailsMap.put("password", password);
                    detailsMap.put("email", email);
                    detailsMap.put("phone", phone);
                    detailsMap.put("region", mRegion);

                    mUsersRef.child(phone).setValue(detailsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();

                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            } else {

                                mProgressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registration Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {

                    mProgressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "PhoneNumber already used!!", Toast.LENGTH_SHORT).show();

                    return;

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public boolean  checknet(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net=connectivityManager.getActiveNetworkInfo();
        return net !=null&&net.isConnected();
    }
    private void initViews() {

        textInputLayoutName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        textInputLayoutPhone = (TextInputLayout) findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutCode = (TextInputLayout) findViewById(R.id.textInputLayoutCode);

        textInputEditTextName = (TextInputEditText) findViewById(R.id.textInputEditTextName);
        textInputEditTextPhone = (TextInputEditText) findViewById(R.id.textInputEditTextPhone);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = (TextInputEditText) findViewById(R.id.textInputEditTextConfirmPassword);
        textInputEditTextCode = (TextInputEditText) findViewById(R.id.textInputEditTextCode);

        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);

        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);
    }


    //populate spinner with regions in the database
    public class RegionSpinnerAdapter extends ArrayAdapter<Regions> {
        List<Regions> mRegionsList;
        Context mContext;

        public RegionSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Regions> objects) {
            super(context, resource, objects);

            mRegionsList = objects;
            mContext = context;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();;
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.tv_row_text);
            label.setText(mRegionsList.get(position).getRegionName());

            return row;
        }

    }


}
