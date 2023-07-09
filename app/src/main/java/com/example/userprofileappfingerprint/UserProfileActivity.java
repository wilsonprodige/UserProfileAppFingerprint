package com.example.userprofileappfingerprint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textviewWelcome,textViewFullname,textViewEmail,textViewDOB,textViewGender,textViewMobile;
    private ProgressBar progressBar;
    private String fullname,DOB,email,gender,mobile;
    private ImageView imageView;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("User Profile");

        progressBar=findViewById(R.id.progress);

        textviewWelcome=findViewById(R.id.textview_show_welcome);
        textViewFullname=findViewById(R.id.textview_show_full_name);
        textViewEmail=findViewById(R.id.textview_show_email);
        textViewDOB=findViewById(R.id.textview_show_dob);
        textViewGender=findViewById(R.id.textview_show_gender);
        textViewMobile=findViewById(R.id.textview_show_mobile);

        fAuth = FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance();
        userID=fAuth.getCurrentUser().getUid();
        if(fAuth.getCurrentUser()!=null){
            progressBar.setVisibility(View.VISIBLE);
        //create document reference
            try {
                DocumentReference documentReference = fstore.collection("users").document(userID);
                documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        textViewMobile.setText(value.getString("mobile"));
                        textViewGender.setText(value.getString("gender"));
                        textViewDOB.setText(value.getString("DOB"));
                        textViewEmail.setText(value.getString("email"));
                        textViewFullname.setText(value.getString("fullname"));
                        textviewWelcome.setText("Welcome, " + value.getString("fullname"));



                    }
                });
            }catch (Exception e){
                AlertDialog.Builder builder=new AlertDialog.Builder(UserProfileActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Error:"+e.getMessage());
                builder.show();
            }
            progressBar.setVisibility(View.GONE);

            //Toast.makeText(UserProfileActivity.this,"something went wrong user's detial not available",Toast.LENGTH_LONG);

        }else{
            AlertDialog.Builder builder=new AlertDialog.Builder(UserProfileActivity.this);
            builder.setCancelable(true);
            builder.setMessage("No session opened");
            builder.show();
        }


        Button logoutbtn=findViewById(R.id.btn_logout);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home=new Intent(UserProfileActivity.this,MainActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                fAuth.signOut();
                startActivity(home);
                finish();


            }
        });
    }



}