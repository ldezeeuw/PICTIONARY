package com.etna.mypictionis;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatroomActivity extends AppCompatActivity {
    ListView listChatRooms;
    TextView message;
    EditText inputMessage;
    String userName, roomName;
    Button sendButton;

    DatabaseReference reference;
    PaintView paintView;
    String roomOwner;
    String wordToFind;

    @Override
    protected void onStart() {
        super.onStart();
        roomName = getIntent().getExtras().get("room_name").toString();
        PaintView.roomName = roomName;
        PaintView.userName = userName;
    }

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        message = (TextView) findViewById(R.id.message);
        editText = (EditText) findViewById(R.id.editText);
        paintView = (PaintView) findViewById(R.id.canvas);
        listChatRooms = (ListView) findViewById(R.id.listChatroom);
        inputMessage = (EditText) findViewById(R.id.input_message);

        roomName = getIntent().getExtras().get("room_name").toString();
        userName = getIntent().getExtras().get("user_name").toString();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (roomOwner != null && !roomOwner.equals(userName) && wordToFind != null) {
                    if (wordToFind.equals(s.toString()))
                        Toast.makeText(ChatroomActivity.this, "You won !", Toast.LENGTH_LONG).show();
                } else if (roomOwner != null && roomOwner.equals(userName)) {
                    DatabaseReference referenceRoom = reference;
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("word_to_find", s.toString());
                    referenceRoom.updateChildren(map2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child(roomName);
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Map<String, Object> map = new HashMap<String, Object>();
                  roomName = reference.push().getKey();
                  reference.updateChildren(map);

                  DatabaseReference referenceRoom = reference.child(roomName);
                  Map<String, Object> map2 = new HashMap<String, Object>();
                  map2.put("user_name", userName);
                  map2.put("message", inputMessage.getText().toString());
                  referenceRoom.updateChildren(map2);

                  inputMessage.setText("");
              }
          });


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key;

                key = dataSnapshot.getKey();

                if (key.equals("touchStart"))
                    return ;
                else if (key.equals("touchMove"))
                    return ;
                else if (key.equals("touchUp"))
                    return ;
                else if (key.equals("owner")) {
                    PaintView.roomOwner = dataSnapshot.getValue().toString();
                    roomOwner = dataSnapshot.getValue().toString();
                } else if (key.equals("word_to_find"))
                    wordToFind = dataSnapshot.getValue().toString();
                else
                    append_chat(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_chat(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                append_chat(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void append_chat(DataSnapshot dataSnapshot) {
        String msg ="";
        String user = "";

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()) {
            msg = ((DataSnapshot)iterator.next()).getValue().toString();
            user = ((DataSnapshot)iterator.next()).getValue().toString();
        }
        if (msg.length() > 0 && user.length() > 0)
            message.append(user + ": " + msg + "\n");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    Intent intent;
    public void exitView(View v)
    {
        if (roomOwner.equals(userName)) {
            reference.removeValue();
            intent = new Intent(ChatroomActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    public void clearCanvas(View v)
    {
        if (roomOwner.equals(userName)) {
            if (this.paintView != null)
                this.paintView.clearCanvas();
        }
    }

    public void selectClearBrush(View v)
    {
        if (roomOwner.equals(userName)) {
            if (this.paintView != null)
                this.paintView.selectClearBrush();
        }
    }

    public void changeColor(View v)
    {
        String value = v.getTag().toString();
        if (roomOwner.equals(userName)) {
            switch (value) {
                case "red":
                    this.paintView.setColor(Color.RED);
                    break;
                case "blue":
                    this.paintView.setColor(Color.BLUE);
                    break;
                case "green":
                    this.paintView.setColor(Color.GREEN);
                    break;
                case "yellow":
                    this.paintView.setColor(Color.YELLOW);
                    break;
                case "black":
                    this.paintView.setColor(Color.BLACK);
                    break;
            }
        }
    }
}
