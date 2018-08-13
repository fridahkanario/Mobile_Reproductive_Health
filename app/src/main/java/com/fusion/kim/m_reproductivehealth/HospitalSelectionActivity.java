package com.fusion.kim.m_reproductivehealth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HospitalSelectionActivity extends AppCompatActivity {

    private String mPhoneNumber = "", mRegion = "", mHospital = "";

    private DatabaseReference mRegionsRef, mHospitalsRef, mUsersRef;

    private ProgressDialog mLoadingPD;

    private SharedPreferences mLoginSp;

    private TextView mRegionTv, mSelectedHospitalTv;
    private ProgressBar mRegionsPB, mHospipatlsPb;
    private RecyclerView mHospitalsRv;
    private Button mSubmitBtn;
    private ImageView mEditRegionIv;
    private CardView mCardview;

    private List<Hospitals> mHospitalsList = new ArrayList<>();
    private HospitalsAdapter mHospitalsAdapter;

    private List<Hospitals> mSelectedHospital = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_selection);

        mPhoneNumber = getIntent().getStringExtra("phoneNumber");
        mLoginSp = getSharedPreferences("loginPreference",MODE_PRIVATE);

        mRegion = mLoginSp.getString("region","");

        mHospitalsAdapter = new HospitalsAdapter(mHospitalsList);

        mRegionsRef = FirebaseDatabase.getInstance().getReference().child("Regions").child(mRegion);
        mHospitalsRef = FirebaseDatabase.getInstance().getReference().child("Hospitals").child(mRegion);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mPhoneNumber);

        mLoadingPD = new ProgressDialog(this);
        mLoadingPD.setMessage("Submitting. Please wait...");
        mLoadingPD.setCancelable(false);

        mRegionTv = findViewById(R.id.tv_region);
        mRegionsPB = findViewById(R.id.pb_regions);
        mRegionsPB.setVisibility(View.VISIBLE);
        mHospipatlsPb = findViewById(R.id.pb_hospitals);
        mHospipatlsPb.setVisibility(View.VISIBLE);

        mSelectedHospitalTv = findViewById(R.id.tv_selected_hospital);
        mSubmitBtn = findViewById(R.id.btn_submit_hopsitals);
        mEditRegionIv = findViewById(R.id.iv_edit_region);

        mCardview = findViewById(R.id.cv_hospitals);

        mHospitalsRv = findViewById(R.id.rv_available_hospitals);
        mHospitalsRv.setLayoutManager(new LinearLayoutManager(this));
        mHospitalsRv.setHasFixedSize(true);
        mHospitalsRv.setAdapter(mHospitalsAdapter);

        mRegionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String region = dataSnapshot.child("regionName").getValue().toString();
                mRegionTv.setText(region);
                mRegionsPB.setVisibility(View.INVISIBLE);
                mEditRegionIv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mHospitalsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Hospitals hospitals = dataSnapshot.getValue(Hospitals.class);
                mHospitalsList.add(hospitals);
                mHospitalsAdapter.notifyDataSetChanged();

                mHospipatlsPb.setVisibility(View.INVISIBLE);
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

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mHospital.equals("")){

                    mLoadingPD.show();
                    mUsersRef.child("hospital").setValue(mHospital).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mLoadingPD.hide();
                                Toast.makeText(HospitalSelectionActivity.this,
                                        "Hospital Set Successfully", Toast.LENGTH_LONG).show();

                                Intent nextIntent = new Intent(HospitalSelectionActivity.this, PortalActivity.class);
                                nextIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(nextIntent);
                                finish();

                            } else {

                                mLoadingPD.dismiss();
                                Toast.makeText(HospitalSelectionActivity.this,
                                        "Something went wrong. please try again.", Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                } else {

                    Toast.makeText(HospitalSelectionActivity.this,
                            "Please select a hospital first", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    private class HospitalsAdapter extends RecyclerView.Adapter<HospitalsAdapter.HospitalsViewHolder>{

        private List<Hospitals> hospitalsList;

        public HospitalsAdapter(List<Hospitals> hospitalsList){

            this.hospitalsList = hospitalsList;

        }

        @NonNull
        @Override
        public HospitalsAdapter.HospitalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HospitalsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final HospitalsAdapter.HospitalsViewHolder holder, int position) {

            final Hospitals hospitals = hospitalsList.get(position);

            holder.mHospitalNameTv.setText(hospitals.getHospitalName());

            if (position == hospitalsList.size()-1){

                holder.mSeparator.setVisibility(View.INVISIBLE);

            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mSelectedHospital.clear();
                    holder.mSelectedIv.setVisibility(View.INVISIBLE);
                    mSelectedHospital.add(new Hospitals(hospitals.getHospitalName(), hospitals.getKey()));

                    mCardview.setMinimumHeight(5);

                    Toast.makeText(HospitalSelectionActivity.this,
                            hospitals.getHospitalName()+" Selected" , Toast.LENGTH_LONG).show();

                    mSelectedHospitalTv.setText(hospitals.getHospitalName());
                    mHospital = hospitals.getKey();

                }
            });

        }

        @Override
        public int getItemCount() {
            return hospitalsList.size();
        }

        public class HospitalsViewHolder extends RecyclerView.ViewHolder {

            private TextView mHospitalNameTv;
            private View mSeparator;
            private ImageView mSelectedIv;
            View mView;

            public HospitalsViewHolder(View itemView) {
                super(itemView);

                mView = itemView;
                mHospitalNameTv = itemView.findViewById(R.id.tv_hospital_name);
                mSeparator = itemView.findViewById(R.id.view_sep);
                mSelectedIv = itemView.findViewById(R.id.iv_selected);

            }
        }
    }

}
