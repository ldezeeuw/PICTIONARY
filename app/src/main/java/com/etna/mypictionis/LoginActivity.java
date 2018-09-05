package com.etna.mypictionis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button   loginBtn;
    EditText userEmail, userPassword;

    String userEmailString, userPasswordString;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn     = (Button) findViewById(R.id.loginBtn);
        userEmail    = (EditText) findViewById(R.id.loginEmailEditText);
        userPassword = (EditText) findViewById(R.id.loginPasswordEditText);


        //\\ Authentication //\\
        mAuth         = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                } else {

                }
            }
        };

        //
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmailString = userEmail.getText().toString().trim();
                userPasswordString = userPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(userEmailString) && !TextUtils.isEmpty(userPasswordString)) {
                    mAuth.signInWithEmailAndPassword(userEmailString, userPasswordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "User Login Failed !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
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

    class TestMyTest {

    }
}
