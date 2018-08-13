package com.fusion.kim.m_reproductivehealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences mLoginSp;

    private View headerView;
    private TextView navNameTv, navPhoneTv;

    private Button mConnectBtn;

    private DatabaseReference mUsersRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("About Reproductive Health");

        mLoginSp = getSharedPreferences("loginPreference",MODE_PRIVATE);

        mUsersRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mLoginSp.getString("phoneNumber",""));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        if(!mLoginSp.getBoolean("logged",false)){

            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();

        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        navNameTv =  headerView.findViewById(R.id.tv_nav_user_name);
        navNameTv.setText(mLoginSp.getString("name",""));
        navPhoneTv =  headerView.findViewById(R.id.tv_nav_phone_number);
        navPhoneTv.setText(mLoginSp.getString("phoneNumber",""));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mConnectBtn = findViewById(R.id.btn_connect);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mConnectBtn.setEnabled(false);
                progressDialog.show();

                mUsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("hospital")){

                            progressDialog.dismiss();

                            Intent selectionIntent = new Intent(MainActivity.this, PortalActivity.class);
                            selectionIntent.putExtra("phoneNumber", mLoginSp.getString("phoneNumber",""));
                            startActivity(selectionIntent);

                            mConnectBtn.setEnabled(true);

                        } else {

                            progressDialog.dismiss();

                            Intent selectionIntent = new Intent(MainActivity.this, HospitalSelectionActivity.class);
                            selectionIntent.putExtra("phoneNumber", mLoginSp.getString("phoneNumber",""));
                            startActivity(selectionIntent);

                            mConnectBtn.setEnabled(true);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            mLoginSp.edit().putBoolean("logged",false).apply();

            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();

            return true;
        }else if (id==R.id.action_help){
            Intent help=new Intent(getApplicationContext(),HelpActivity.class);
            startActivity(help);

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_antenatal_care) {

            Intent antenatalIntent=new Intent(getApplicationContext(),AntenatalCareActivity.class);
            startActivity(antenatalIntent);

        } else if (id == R.id.nav_fp_services) {

            Intent fpIntent=new Intent(getApplicationContext(),FamilyPlanningActivity.class);
            startActivity(fpIntent);

        } else if (id == R.id.nav_gyna_medical) {

            Intent gynaIntent=new Intent(getApplicationContext(),GynacologyActivity.class);
            startActivity(gynaIntent);


        } else if (id == R.id.nav_notifications) {

            Toast.makeText(this, "You have no Notifications!!", Toast.LENGTH_SHORT).show();


        }
        else if (id==R.id.nav_help){
            Intent help=new Intent(getApplicationContext(),HelpActivity.class);
            startActivity(help);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
