package com.example.remotevisualassistant;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CallLogsActivity extends AppCompatActivity {

    List<CallLog> logList;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        logList = new ArrayList<CallLog>();
        listView = (ListView)findViewById(R.id.lv_call_logs);
        final MyCustomListAdapter adapter = new MyCustomListAdapter(CallLogsActivity.this,R.layout.list_item_logs,logList);

        //populate loglist
        String my_id = FirebaseAuth.getInstance().getUid();
        DatabaseReference logdbr = FirebaseDatabase.getInstance().getReference("call_logs");
        logdbr.child(my_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AllLogs my_logs = dataSnapshot.getValue(AllLogs.class);
                List<CallLog> call_logs = my_logs.getLogList();
                if(call_logs.size()<1){
                    build_an_alert("Logs empty","You currently have no logs","okay");
                }
                for(int i=1;i<call_logs.size();i++){
                    adapter.add(call_logs.get(i));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Toast.makeText(CallLogsActivity.this,logList.size(),Toast.LENGTH_LONG).show();
//        if(logList.size()>0){
            listView.setAdapter(adapter);
//        }
//        else{
//        }
    }

    private void build_an_alert(String t, String m, String b){
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(CallLogsActivity.this);
        builder.setTitle(t);
        builder.setMessage(m);
        builder.setCancelable(false);
        builder.setPositiveButton(
                b,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        AlertDialog alert1 = builder.create();
        alert1.show();
    }
}
