package com.fusion.kim.m_reproductivehealth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class EditRegionActivity extends AppCompatActivity {

    private DatabaseReference mUserRef, mRegionsRef, mHospitalsRef;

    private TextView mCurrentRegionTv;
    private Spinner mRegionSpinner, mHospitalSpinner;
    private Button mUpdateBtn;

    private String mRegion = "", mHospital = "";

    private SharedPreferences mLoginSp;

    ProgressDialog mLoadingPD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_region);

        mLoginSp = getSharedPreferences("loginPreference",MODE_PRIVATE);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mRegionsRef = FirebaseDatabase.getInstance().getReference().child("Regions");
        mHospitalsRef = FirebaseDatabase.getInstance().getReference().child("Hospitals");

        mCurrentRegionTv = findViewById(R.id.tv_current_region);
        mRegionSpinner = findViewById(R.id.spinner_regions);
        mHospitalSpinner = findViewById(R.id.spinner_new_hospitals);
        mUpdateBtn = findViewById(R.id.btn_submit_new_region);

        mLoadingPD = new ProgressDialog(this);
        mLoadingPD.setMessage("Updating information...");
        mLoadingPD.setIndeterminate(true);
        mLoadingPD.setCancelable(false);

        mUserRef.child(mLoginSp.getString("phoneNumber","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String region = dataSnapshot.child("region").getValue(String.class);


                mRegionsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child(region).child("regionName").getValue(String.class);
                        mCurrentRegionTv.setText(name);

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

        final List<Regions> regionsList = new ArrayList<>();

        mRegionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot regionSnapshot : dataSnapshot.getChildren()){

                    String region = regionSnapshot.child("regionName").getValue(String.class);
                    String key = regionSnapshot.getKey();

                    regionsList.add(new Regions(key, region));

                    mRegionSpinner.setAdapter(new RegionSpinnerAdapter(EditRegionActivity.this, R.layout.row, regionsList));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final List<Hospitals> hospitalsList = new ArrayList<>();

        mRegionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                hospitalsList.clear();

                mRegion = regionsList.get(position).getKey();

                mHospitalsRef.child(mRegion).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot hospitalSnapshot : dataSnapshot.getChildren()){

                            String hospitalName = hospitalSnapshot.child("hospitalName").getValue(String.class);
                            String key = hospitalSnapshot.getKey();

                            hospitalsList.add(new Hospitals(hospitalName, key));

                            mHospitalSpinner.setAdapter(new HospitalSpinnerAdapter(EditRegionActivity.this, R.layout.row, hospitalsList));


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mHospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mHospital = hospitalsList.get(position).getKey();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRegion.equals("")){

                    Toast.makeText(EditRegionActivity.this, "Please provide the new Region",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (mHospital.equals("")){

                    Toast.makeText(EditRegionActivity.this, "Please provide the new Hospital",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!mRegion.equals("") && !mHospital.equals("")){

                    mLoadingPD.show();

                    Map updateMap = new HashMap();
                    updateMap.put("region", mRegion);
                    updateMap.put("hospital", mHospital);

                    mUserRef.child(mLoginSp.getString("phoneNumber", ""))
                            .updateChildren(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mLoadingPD.hide();

                                Toast.makeText(EditRegionActivity.this, "Region and Hospital Updated Successfully", Toast.LENGTH_LONG).show();

                                Intent portalIntent = new Intent(EditRegionActivity.this, PortalActivity.class);
                                portalIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(portalIntent);
                                finish();

                            } else {

                                mLoadingPD.dismiss();

                                Toast.makeText(EditRegionActivity.this, "Failed to update Region and Hospital",
                                        Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                }

            }
        });

    }

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

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.tv_row_text);
            label.setText(mRegionsList.get(position).getRegionName());

            return row;
        }

    }

    public class HospitalSpinnerAdapter extends ArrayAdapter<Hospitals> {


        List<Hospitals> mHospitalList;
        Context mContext;

        public HospitalSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Hospitals> objects) {
            super(context, resource, objects);

            mHospitalList = objects;
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
            label.setText(mHospitalList.get(position).getHospitalName());

            return row;
        }

    }
}
