package com.application.akash.chatdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.akash.chatdemo.Adapter.RoomAdapter;
import com.application.akash.chatdemo.Model.RoomModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference dbRef;
    ListView roomList;
    ArrayList<RoomModel> roomModelArrayList = new ArrayList<>();
    RoomAdapter adapter;
    TextView prompt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prompt = findViewById(R.id.empty_prompt);
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("rooms");



        roomList = findViewById(R.id.listview);
        adapter = new RoomAdapter(getBaseContext(),roomModelArrayList);
        roomList.setAdapter(adapter);
        roomList.requestLayout();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewRoom();
            }
        });

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomModelArrayList.clear();
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    prompt.setVisibility(View.VISIBLE);
                    roomList.setVisibility(View.GONE);
                }
                else
                {
                    prompt.setVisibility(View.GONE);
                    roomList.setVisibility(View.VISIBLE);
                }
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    RoomModel model = new RoomModel();
                    DataSnapshot snap = ((DataSnapshot)iterator.next());
                    model.setName(snap.getKey());

                    model.setCreateDate((String)snap.child("date").getValue());

                    Log.e("akx",snap.toString()+ " ");

                    roomModelArrayList.add(model);
                    adapter.notifyDataSetChanged();
                    roomList.invalidate();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                joinChatRoom(position);

            }
        });
    }

    private void joinChatRoom(final int position) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.join_room, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Join Room");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText user = dialogView.findViewById(R.id.user_name);

                if(user.getText().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(),"Input field is empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(getBaseContext(),ChatRoom.class)
                        .putExtra("room",roomModelArrayList.get(position).getName())
                        .putExtra("date",roomModelArrayList.get(position).getCreateDate())
                        .putExtra("user",user.getText().toString())
                );

                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private void createNewRoom() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_new_room, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Create Room");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText room = dialogView.findViewById(R.id.room_name);
                EditText user = dialogView.findViewById(R.id.user_name);

                Map<String,Object> map = new HashMap<>();
                map.put(room.getText().toString(),"");

                dbRef.updateChildren(map);

                DatabaseReference roomRef = dbRef.child(room.getText().toString());
                map = new HashMap<>();
                SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
                map.put("date",format.format(new Date()));
                map.put("chats","");
                roomRef.updateChildren(map);

                startActivity(new Intent(getBaseContext(),ChatRoom.class)
                        .putExtra("room",room.getText().toString())
                        .putExtra("date",format.format(new Date()))
                        .putExtra("user",user.getText().toString())
                );
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getBaseContext(),"Designed and developed by : Akash Giri", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
