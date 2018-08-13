package com.fusion.kim.m_reproductivehealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private NestedScrollView nestedScrollView;

    Context context;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private AppCompatButton appCompatButtonLogin;

    private AppCompatTextView textViewLinkRegister;

    private DatabaseReference mUsersRef;
    private ProgressDialog mProgressDialog;

    private SharedPreferences mLoginSp;

    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        mLoginSp = getSharedPreferences("loginPreference",MODE_PRIVATE);


        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        textViewLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register=new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(register);
            }
        });
        appCompatButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String phone = textInputEditTextEmail.getText().toString();
                String password = textInputEditTextPassword.getText().toString();

                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)){

                    //login call
                    if(checknet()) {
                        showProgressDialog();
                        startLogin(phone, password);
                    }else{
                        //mSignInProgress.setVisibility(View.INVISIBLE);


                        Toast.makeText(getApplicationContext(),"Kindly check your internet connection!",Toast.LENGTH_LONG).show();
                    }

                }else {

                    Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                    //  hideProgressDialog();
                }

            }
        });
    }

    private void startLogin(final String phone, final String password) {

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(phone)){

                    DataSnapshot infoSnapshot = dataSnapshot.child(phone);

                    String   dbPhone= infoSnapshot.child("phone").getValue().toString();
                    String dbPassword   = infoSnapshot.child("password").getValue().toString();
                    String dbName = infoSnapshot.child("name").getValue().toString();
                    String dbEmail= infoSnapshot.child("email").getValue().toString();
                    String dbRegion= infoSnapshot.child("region").getValue().toString();

                    if (phone.equals(dbPhone) && password.equals(dbPassword)){

                        mLoginSp.edit().putString("phoneNumber",dbPhone).apply();
                        mLoginSp.edit().putString("name",dbName).apply();
                        mLoginSp.edit().putString("emailAddress",dbEmail).apply();
                        mLoginSp.edit().putString("region",dbRegion).apply();
                        mLoginSp.edit().putBoolean("logged",true).apply();

                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(loginIntent);
                        finish();

                    } else {

                        hideProgressDialog();
                        Toast.makeText(LoginActivity.this, "Wrong Credentials. Please try Again.", Toast.LENGTH_SHORT).show();

                    }


                }else {

                    Toast.makeText(LoginActivity.this, "The user does not exist", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
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

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutPhone);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.textInputEditTextPhone);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.textInputEditTextPassword);

        appCompatButtonLogin = (AppCompatButton) findViewById(R.id.appCompatButtonLogin);

        textViewLinkRegister = (AppCompatTextView) findViewById(R.id.textViewLinkRegister);}

    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }
    private void hideProgressDialog() {
        mProgressDialog.hide();
    }

    //Show the Progress Dialog
    private void showProgressDialog() {

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Signing In...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();

    }
}
