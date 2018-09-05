package com.etna.mypictionis;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WelcomeActivity extends AppCompatActivity {

    Button logoutBtn;
    Button createBtn;

    DatabaseReference reference;
    EditText inputChatrooms, alertDialogInput;
    ListView listChatrooms;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    String name;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //\\ Authentication //\\
        mAuth         = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {

                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                }
            }
        };


        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        createBtn = (Button) findViewById(R.id.createButton);
        inputChatrooms = (EditText) findViewById(R.id.inputChatRoom);
        listChatrooms = (ListView) findViewById(R.id.listChatroom);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list);
        getUserName();
        listChatrooms.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().getRoot();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> setter = new HashSet<String>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    setter.add(((DataSnapshot)iterator.next()).getKey());
                }
                list.clear();
                list.addAll(setter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Network issue", Toast.LENGTH_LONG).show();
            }
        });

        listChatrooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WelcomeActivity.this, ChatroomActivity.class);
                intent.putExtra("room_name", ((TextView)view).getText().toString());
                intent.putExtra("user_name", name);

                startActivity(intent);

            }
        });

        // Déconnexion
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
        });
        // Bouton de creation de room
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(inputChatrooms.getText().toString().trim(), "Chatroom");
                reference.updateChildren(hashMap);

                list.add(inputChatrooms.getText().toString().trim());
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getUserName() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Enter your name, please");
        alertDialogInput = new EditText(this);
        alertDialog.setView(alertDialogInput);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (alertDialogInput.getText().toString().isEmpty()) {
                    dialogInterface.cancel();
                    Toast.makeText(WelcomeActivity.this, "Name is empty", Toast.LENGTH_LONG).show();
                    getUserName();
                }
                name = alertDialogInput.getText().toString();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                Toast.makeText(WelcomeActivity.this, "Name required to continue !", Toast.LENGTH_LONG).show();
                getUserName();
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }
}
