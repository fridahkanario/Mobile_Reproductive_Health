package com.fusion.kim.m_reproductivehealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PortalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mAppointmetsRef, mHospitalsRef, mDoctorsRef, mUserRef;

    private RecyclerView mAppointmentsRv;
    private AppointmentsAdapter mAdapter;
    private List<Appointments> mAppointmentsList = new ArrayList<>();

    private TextView mNoAppsTv;
    private ProgressBar mAppsPb;

    private View headerView;
    private TextView navNameTv, navPhoneTv, mHeaderTv;

    private SharedPreferences mLoginSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLoginSp = getSharedPreferences("loginPreference",MODE_PRIVATE);

        mAdapter = new AppointmentsAdapter(mAppointmentsList);

        mAppointmetsRef = FirebaseDatabase.getInstance().getReference().child("Appointments")
                .child(mLoginSp.getString("phoneNumber", ""));

        mHospitalsRef = FirebaseDatabase.getInstance().getReference().child("Hospitals");
        mDoctorsRef = FirebaseDatabase.getInstance().getReference().child("Doctors");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mLoginSp.getString("phoneNumber", ""));

        mHeaderTv = findViewById(R.id.tv_portal_header);

        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                getSupportActionBar().setTitle(dataSnapshot.child("name").getValue().toString());

                final String hospital = dataSnapshot.child("hospital").getValue(String.class);
                String region = dataSnapshot.child("region").getValue(String.class);

                FirebaseDatabase.getInstance().getReference().child("Hospitals").child(region)
                        .child(hospital).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String hospitalName = dataSnapshot.child("hospitalName").getValue(String.class);

                        mHeaderTv.setText("Welcome to " + hospitalName);

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

        mAppointmentsRv = findViewById(R.id.rv_appointments);
        mAppointmentsRv.setLayoutManager(new LinearLayoutManager(this));
        mAppointmentsRv.setHasFixedSize(true);
        mAppointmentsRv.setAdapter(mAdapter);

        mNoAppsTv = findViewById(R.id.tv_no_appointments);
        mAppsPb = findViewById(R.id.pb_appointments);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        navNameTv =  headerView.findViewById(R.id.tv_nav_user_name);
        navNameTv.setText(mLoginSp.getString("name",""));
        navPhoneTv =  headerView.findViewById(R.id.tv_nav_phone_number);
        navPhoneTv.setText(mLoginSp.getString("phoneNumber",""));

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAppointmentsList.clear();

        mAppsPb.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference().child("Appointments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.hasChild(mLoginSp.getString("phoneNumber", ""))){

                            mNoAppsTv.setVisibility(View.VISIBLE);
                            mAppsPb.setVisibility(View.INVISIBLE);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mAppointmetsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Appointments appointments = dataSnapshot.getValue(Appointments.class);

                mAppointmentsList.add(appointments);
                mAdapter.notifyDataSetChanged();

                mAppsPb.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        getMenuInflater().inflate(R.menu.portal, menu);
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
            Intent loginIntent = new Intent(PortalActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_schedule_appointment) {

            Intent schedulingIntent = new Intent(PortalActivity.this, SchedulingActivity.class);
            schedulingIntent.putExtra("phoneNumber", mLoginSp.getString("phoneNumber", ""));
            startActivity(schedulingIntent);

        } else if (id == R.id.nav_health_info) {

            startActivity(new Intent(PortalActivity.this, HealthInformationActivity.class));

        } else if (id == R.id.nav_follow_up) {
            startActivity(new Intent(PortalActivity.this, PrescriptionActivity.class));

        } else if (id == R.id.nav_pre_visit_forms) {

            startActivity(new Intent(PortalActivity.this, PrevisitFormsActivity.class));

        } else if (id == R.id.nav_help) {

            Intent help=new Intent(getApplicationContext(),HelpActivity.class);
            startActivity(help);
        } else if (id == R.id.nav_edit_region) {

            startActivity(new Intent(PortalActivity.this, EditRegionActivity.class));

        }
     else if (id == R.id.nav_pregnancy) {

            startActivity(new Intent(PortalActivity.this, FetalUpdatesActivity.class));
    }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.AppointmentsViewHolder>{

        private List<Appointments> appointmentsList;

        public AppointmentsAdapter(List<Appointments> appointmentsList){

            this.appointmentsList = appointmentsList;

        }

        @NonNull
        @Override
        public AppointmentsAdapter.AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AppointmentsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_item, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull final AppointmentsAdapter.AppointmentsViewHolder holder, int position) {

            final Appointments appointments = appointmentsList.get(position);

            holder.mHospitalTv.setText(appointments.getHospital());

            holder.mDoctorTv.setText("Dr. "+ appointments.getDoctor());

            holder.mDateTv.setText(""+appointments.getDate());
            holder.mTimeTv.setText(""+appointments.getTime());
            holder.mPurposeTv.setText(appointments.getPurpose());


        }

        @Override
        public int getItemCount() {
            return appointmentsList.size();
        }

        public class AppointmentsViewHolder extends RecyclerView.ViewHolder {

            private TextView mDateTv, mTimeTv, mHospitalTv, mDoctorTv, mPurposeTv;
            private View mView;

            public AppointmentsViewHolder(View itemView) {
                super(itemView);

                mView = itemView;

                mDateTv = itemView.findViewById(R.id.tv_date);
                mTimeTv = itemView.findViewById(R.id.tv_time);
                mHospitalTv = itemView.findViewById(R.id.tv_app_hospital);
                mDoctorTv = itemView.findViewById(R.id.tv_doctor);
                mPurposeTv = itemView.findViewById(R.id.tv_purpose);
            }
        }
    }

}
