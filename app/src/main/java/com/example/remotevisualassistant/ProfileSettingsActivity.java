package com.example.remotevisualassistant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileSettingsActivity extends AppCompatActivity {

    private TextView email,name,number,role;
    private EditText oldp, newp1, newp2;
    private Button b_update, b_signout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        set_UI_components();

        b_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String oldpassword = oldp.getText().toString().trim();
                AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(),oldpassword);
                final String newpassword1 = newp1.getText().toString().trim();
                String newpassword2 = newp2.getText().toString().trim();
                if(newpassword1.equals(newpassword2)){
                    if(password_format(newpassword1)){
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        user.updatePassword(newpassword1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    build_an_alert("Success","Password updated");
                                                }
                                                else{
                                                    build_an_alert("Error",task.getException().getMessage());
                                                }
                                            }
                                        });
                                    }
                                    else{
                                        build_an_alert("Success","Authentication failed");
                                    }
                                }
                            });

                        }
                        else{
                            build_an_alert("Error","Invalid password format");
                        }
                    }
                    else{
                        build_an_alert("Error","new passwords are not matching");
                    }
            }
        });

        b_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                //mAuth.signOut();
                //if user, sognout. if volunteer, set nss active status to false
                //also remove fcm token
                final String my_id = mAuth.getUid();
                DatabaseReference usrdbr = FirebaseDatabase.getInstance().getReference("userdetails");
                usrdbr.child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        UserDetails ud = dataSnapshot.getValue(UserDetails.class);
                        if(ud.getType().equals("User")){
                            DatabaseReference tokdbr = FirebaseDatabase.getInstance().getReference("usr_fcm_tokens");
                            tokdbr.child(my_id).removeValue();
                        }
                        else{
                            DatabaseReference tokdbr = FirebaseDatabase.getInstance().getReference("usr_fcm_tokens");
                            tokdbr.child(my_id).removeValue();
                            final DatabaseReference activenss = FirebaseDatabase.getInstance().getReference("active_volunteers");
                            activenss.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dSnapshot) {
                                    if(dSnapshot.getChildrenCount()>0){
                                        List<String> online_ids = new ArrayList<String>();
                                        for(DataSnapshot postSnapshot : dSnapshot.getChildren()){
                                            online_ids.add(postSnapshot.getValue(String.class));
                                        }
                                        if(online_ids.contains(my_id)){
                                            online_ids.remove(my_id);
                                            activenss.setValue(online_ids);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mAuth.signOut();
                Intent my_intent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
                my_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(my_intent);
                finish();
            }
        });
    }

    private void set_UI_components(){
        email = (TextView)findViewById(R.id.t1_email);
        role = (TextView)findViewById(R.id.t2_role);
        number = (TextView)findViewById(R.id.t3_number);
        name = (TextView)findViewById(R.id.t4_name);
        oldp = (EditText)findViewById(R.id.old_p);
        newp1 = (EditText)findViewById(R.id.new_p1);
        newp2 = (EditText)findViewById(R.id.new_p2);
        b_update = (Button)findViewById(R.id.button_upd_p);
        b_signout = (Button)findViewById(R.id.buttonsignout);

        final String my_id = FirebaseAuth.getInstance().getUid();
        DatabaseReference mydbr = FirebaseDatabase.getInstance().getReference("userdetails");
        mydbr.child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails my_ud = dataSnapshot.getValue(UserDetails.class);
                email.setText(my_ud.getEmail());
                role.setText(my_ud.getType());
                number.setText(my_ud.getNumber());
                name.setText(my_ud.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean password_format(String s){
        //format of a valid password
        return true;
    }

    private void build_an_alert(String t, String m){
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettingsActivity.this);
        builder.setTitle(t);
        builder.setMessage(m);
        builder.setCancelable(true);
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
}
