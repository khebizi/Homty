package com.sizii.homty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText email, pwd;
    Button registerBtn;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    TextView alreadyMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email_register);
        pwd = findViewById(R.id.pwd_register);
        registerBtn = findViewById(R.id.register_btn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Création du compte.");
        firebaseAuth = FirebaseAuth.getInstance();
        alreadyMember = findViewById(R.id.already_member);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String password = pwd.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    email.setError("E-mail invalide.");
                    email.setFocusable(true);
                } else if (password.length() < 6) {
                    pwd.setError("Le mot de passe doit être plus de 6 charactères.");
                    pwd.setFocusable(true);
                } else {
                    registerUser(mail, password);
                }
            }
        });

        alreadyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(String mail, String password) {
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("nom", "");
                            hashMap.put("numéro de téléphone", "");
                            hashMap.put("image", "");
                            hashMap.put("cover", "");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users");
                            reference.child(uid).setValue(hashMap);
                            Toast.makeText(RegisterActivity.this, "Connecté !\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Erreur.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
