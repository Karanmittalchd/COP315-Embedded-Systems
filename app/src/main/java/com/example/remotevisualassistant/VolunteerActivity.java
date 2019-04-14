package com.example.remotevisualassistant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VolunteerActivity extends AppCompatActivity {

    private Button b_signout, b_accept, b_reject, b_nss;
    private TextView in_name, in_number, status_nss, hours_nss, call_logs, settings;
    private FirebaseAuth mAuth;
    private EditText eno;
    private CheckBox active;
    private boolean in_coming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        setup_UI_components();


        mAuth = FirebaseAuth.getInstance();
        final String my_id = mAuth.getUid();
        final DatabaseReference cidbr = FirebaseDatabase.getInstance().getReference("in_comms");
        cidbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(my_id)){
                    CommunicationIn my_ci = dataSnapshot.child(my_id).getValue(CommunicationIn.class);
                    in_name.setText(my_ci.getName_from());
                    in_number.setText(my_ci.getNumber_from());
//                            b_accept.setBackgroundColor(getResources().getColor(R.color.my_bright_green));
                    b_accept.setVisibility(View.VISIBLE);
                    b_reject.setVisibility(View.VISIBLE);
                    in_coming=true;
                }
                else{
                    Toast.makeText(VolunteerActivity.this,"No incoming",Toast.LENGTH_SHORT).show();
                    b_accept.setVisibility(View.INVISIBLE);
                    b_reject.setVisibility(View.INVISIBLE);
                    in_name.setText("...");
                    in_number.setText("....");
                    in_coming=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //clicking answer
        b_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(in_coming){
                    //set call status active
                    final String my_id = mAuth.getUid();
                    DatabaseReference cidbr = FirebaseDatabase.getInstance().getReference("in_comms");
                    cidbr.child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            CommunicationIn cin = dataSnapshot.getValue(CommunicationIn.class);
                            final String cin_id = cin.getId_from();
                            final DatabaseReference codbr = FirebaseDatabase.getInstance().getReference("out_comms");
                            codbr.child(cin_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dSnapshot) {
                                    CommunicationOut cou = dSnapshot.getValue(CommunicationOut.class);
                                    cou.setWaiting(1);
                                    codbr.child(cin_id).setValue(cou);

                                    Intent my_intent = new Intent(VolunteerActivity.this,VolunteerVideoActivity.class);
                                    startActivity(my_intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Toast.makeText(VolunteerActivity.this,"No Incoming Communications",Toast.LENGTH_SHORT).show();
                }
            }
        });

        b_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(in_coming){
                    final String my_id = FirebaseAuth.getInstance().getUid();
                    DatabaseReference cidbr = FirebaseDatabase.getInstance().getReference("in_comms");
                    cidbr.child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            CommunicationIn cin = dataSnapshot.getValue(CommunicationIn.class);
                            final String cin_id = cin.getId_from();
                            final DatabaseReference codbr = FirebaseDatabase.getInstance().getReference("out_comms");
                            codbr.child(cin_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dSnapshot) {
                                    CommunicationOut cou = dSnapshot.getValue(CommunicationOut.class);
                                    cou.setWaiting(2);
                                    codbr.child(cin_id).setValue(cou);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                    cidbr.child(my_id).removeValue();
                    Toast.makeText(VolunteerActivity.this,"Call Rejected",Toast.LENGTH_SHORT).show();
                    in_coming=false;
                    b_accept.setVisibility(View.INVISIBLE);
                    b_reject.setVisibility(View.INVISIBLE);
                    in_name.setText("...");
                    in_number.setText("....");
                    //b_refresh_in.performClick();
                }
            }
        });

        b_nss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e_num = eno.getText().toString().trim();
                if(entry_no_format(e_num)){
                    String my_id = FirebaseAuth.getInstance().getUid();
                    DatabaseReference nssdbr = FirebaseDatabase.getInstance().getReference("nssvolunteers");
                    final NssDetails nd = new NssDetails(my_id,e_num,0.0, true);
                    nssdbr.child(my_id).setValue(nd).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                b_nss.setVisibility(View.INVISIBLE);
                                eno.setVisibility(View.INVISIBLE);
                                status_nss.setText("Registered to "+nd.getEntry_no());
                                hours_nss.setText(nd.getHours()+" hrs");
                            }
                            else{
                                Toast.makeText(VolunteerActivity.this,"NSS registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        });
                }
                else{
                    Toast.makeText(VolunteerActivity.this,"Invalid Entry Number", Toast.LENGTH_SHORT).show();
                }
                update_nss_status();
            }
        });

        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    final String my_id = FirebaseAuth.getInstance().getUid();
                    final DatabaseReference activenss = FirebaseDatabase.getInstance().getReference("active_volunteers");
                    activenss.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0){
                                List<String> online_ids = new ArrayList<String>();
                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                    online_ids.add(postSnapshot.getValue(String.class));
                                }
                                if(!online_ids.contains(my_id)){
                                    online_ids.add(my_id);
                                    activenss.setValue(online_ids);
                                }
                            }
                            else{
                                List<String> online_ids = new ArrayList<String>();
                                online_ids.add(my_id);
                                activenss.setValue(online_ids);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    final String my_id = FirebaseAuth.getInstance().getUid();
                    final DatabaseReference activenss = FirebaseDatabase.getInstance().getReference("active_volunteers");
                    activenss.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0){
                                List<String> online_ids = new ArrayList<String>();
                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                    online_ids.add(postSnapshot.getValue(String.class));
                                }
                                online_ids.remove(my_id);
                                activenss.setValue(online_ids);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        call_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_intent = new Intent(VolunteerActivity.this, CallLogsActivity.class);
                startActivity(my_intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent my_intent = new Intent(VolunteerActivity.this, ProfileSettingsActivity.class);
                startActivity(my_intent);
            }
        });

        //clicking signout
        b_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active.setChecked(false);
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent my_intent = new Intent(VolunteerActivity.this, MainActivity.class);
                startActivity(my_intent);
                finish();
            }
        });
    }

    private void setup_UI_components(){
        b_signout = (Button)findViewById(R.id.button_v_signout);
        b_accept = (Button)findViewById(R.id.button_accept);
        in_name = (TextView)findViewById(R.id.text_name);
        in_number = (TextView)findViewById(R.id.text_number);
        in_coming = false;
        b_nss = (Button)findViewById(R.id.button_register_nss);
        eno = (EditText)findViewById(R.id.entry_no);
        status_nss = (TextView)findViewById(R.id.nss_status);
        hours_nss = (TextView)findViewById(R.id.nss_hours);
        call_logs = (TextView)findViewById(R.id.logs_volunteer);
        settings = (TextView)findViewById(R.id.ps_volunteer);
        b_reject = (Button)findViewById(R.id.button_Reject);
        active = (CheckBox)findViewById(R.id.nss_active);

        update_nss_status();
    }

    private void update_nss_status(){
        final String my_id = FirebaseAuth.getInstance().getUid();
        DatabaseReference nssdbr = FirebaseDatabase.getInstance().getReference("nssvolunteers");
        nssdbr.child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NssDetails nd = dataSnapshot.getValue(NssDetails.class);
                if(nd.isRegistered()){
                    b_nss.setVisibility(View.INVISIBLE);
                    eno.setVisibility(View.INVISIBLE);
                    status_nss.setVisibility(View.VISIBLE);
                    hours_nss.setVisibility(View.VISIBLE);
                    status_nss.setText("Registered to "+nd.getEntry_no());
                    hours_nss.setText(nd.getHours()+" hrs");
                    active.setVisibility(View.VISIBLE);
                    DatabaseReference activenss = FirebaseDatabase.getInstance().getReference("active_volunteers");
                    activenss.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0){
                                List<String> online_ids = new ArrayList<String>();
                                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                    online_ids.add(postSnapshot.getValue(String.class));
                                }
                                if(online_ids.contains(my_id)){
                                    active.setChecked(true);
                                }
                                else{
                                    active.setChecked(false);
                                }
                            }
                            else{
                                active.setChecked(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                else{
                    status_nss.setVisibility(View.INVISIBLE);
                    hours_nss.setVisibility(View.INVISIBLE);
                    active.setVisibility(View.INVISIBLE);
                    b_nss.setVisibility(View.VISIBLE);
                    eno.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean entry_no_format(String s){
        //which formats to be included. only for btech?
        if(s.length()==11){
            return true;
        }
        else{
            return false;
        }
    }
}
