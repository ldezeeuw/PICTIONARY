package com.etna.mypictionis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button   createUser, moveToLogin;
    EditText userEmailEdit, userPasswordEdit;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createUser       = (Button)   findViewById(R.id.createUserBtn);
        moveToLogin      = (Button)   findViewById(R.id.GoLoginBtn);
        userEmailEdit    = (EditText) findViewById(R.id.emailEditText);
        userPasswordEdit = (EditText) findViewById(R.id.passwordEditText);

        //\\ Authentication //\\
        mAuth         = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                } else {

                }
            }
        };

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmailString, userPasswordString;

                userEmailString    = userEmailEdit.getText().toString().trim();
                userPasswordString = userPasswordEdit.getText().toString().trim();

                if (!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPasswordString)) {

                    mAuth
                        .createUserWithEmailAndPassword(userEmailString, userPasswordString)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "User Account Created !", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                                } else {
                                    task.getException();
                                    Toast.makeText(MainActivity.this, "Failed to create User Account !", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                }


            }
        });

        moveToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
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
