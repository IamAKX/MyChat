package com.application.akash.chatdemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {

    String roomName, userName, createDate;
    LinearLayout chatView;
    FirebaseDatabase database;
    DatabaseReference dbRef;
    EditText msg;
    ImageButton send;
    ScrollView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        roomName = getIntent().getStringExtra("room");
        userName = getIntent().getStringExtra("user").toUpperCase();
        createDate = getIntent().getStringExtra("date");

        getSupportActionBar().setTitle(roomName.toUpperCase());
        getSupportActionBar().setSubtitle(createDate);

        chatView = findViewById(R.id.chatView);
        msg = findViewById(R.id.msg);
        send = findViewById(R.id.sendbtn);
        sv = findViewById(R.id.topView);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("rooms").child(roomName).child("chats");

        DatabaseReference chat = dbRef.push();
        Map<String,Object> objectMap = new HashMap<String, Object>();
        objectMap.put("uname","joined");
        objectMap.put("message",userName+" has joined");
        objectMap.put("tstamp",new SimpleDateFormat("dd MMM yyy, h:mm:ss a").format(new Date()).toString());
        chat.updateChildren(objectMap);
        scrollToBottom();

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateChatList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateChatList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                updateChatList(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!msg.getText().toString().equals(""))
                {
                    DatabaseReference chat = dbRef.push();
                    Map<String,Object> objectMap = new HashMap<String, Object>();
                    objectMap.put("uname",userName);
                    objectMap.put("message",msg.getText().toString());
                    objectMap.put("tstamp",new SimpleDateFormat("dd MMM yyy, h:mm:ss a").format(new Date()).toString());
                    chat.updateChildren(objectMap);
                    msg.setText("");
                    scrollToBottom();
                }
            }
        });

    }

    private void updateChatList(DataSnapshot dataSnapshot) {
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(10,5,10,5);

        TextView tv=new TextView(this);

        if(String.valueOf(dataSnapshot.child("uname").getValue()).equalsIgnoreCase(userName))
        {
            tv.setPadding(10,10,10,10);
            tv.setTextColor(Color.DKGRAY);
            tv.setGravity(Gravity.RIGHT);
            String s = "Me<br><b>"+(String)dataSnapshot.child("message").getValue()+"</b><br><i>"+formatDate((String)dataSnapshot.child("tstamp").getValue())+"</i>";
            tv.setText(Html.fromHtml(s));
        }
        else
        if(String.valueOf(dataSnapshot.child("uname").getValue()).equalsIgnoreCase("joined")){
            tv.setPadding(10,10,10,10);
            tv.setTextColor(Color.LTGRAY);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextSize(10);
            String s = "<i>"+(String)dataSnapshot.child("message").getValue()+"</i>";
            tv.setText(Html.fromHtml(s));
        }

       else
        {
            tv.setPadding(10,10,10,10);
            tv.setTextColor(Color.BLACK);
            tv.setGravity(Gravity.LEFT);
            String s = (String)dataSnapshot.child("uname").getValue()+"<br><b>"+(String)dataSnapshot.child("message").getValue()+"</b><br><i>"+formatDate((String)dataSnapshot.child("tstamp").getValue())+"</i>";
            tv.setText(Html.fromHtml(s));
        }
        tv.setLayoutParams(lparams);

        chatView.addView(tv);
        scrollToBottom();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference chat = dbRef.push();
        Map<String,Object> objectMap = new HashMap<String, Object>();
        objectMap.put("uname","joined");
        objectMap.put("message",userName+" left");
        objectMap.put("tstamp",new SimpleDateFormat("dd MMM yyy, h:mm:ss a").format(new Date()).toString());
        chat.updateChildren(objectMap);
        Toast.makeText(getApplicationContext(),"You have left the room", Toast.LENGTH_LONG).show();
    }

    private  String formatDate(String timeStamp)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyy, h:mm:ss a");
        Date d = null;
        try {
            d = sdf.parse(timeStamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TimeAgo.using(d.getTime());
    }
    private void scrollToBottom() {
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
}
