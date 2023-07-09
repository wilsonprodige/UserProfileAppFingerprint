package com.example.userprofileappfingerprint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.userprofileappfingerprint.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName,editTextRegisterEmail,editTextRegisterDOB,editTextRegisterMobile,
            editTextRegisterPwd,editTextRegisterConfirmPwd;
    FirebaseFirestore fStore;
    String userID;
    //private FirebaseAuth mAuth;

    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private static final String TAG="RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getSupportActionBar().setTitle("Register");
        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_fullname);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDOB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        //radioButton for gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        Button buttonRegister = findViewById(R.id.register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define the radio button selected
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDOB = editTextRegisterDOB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                //Toast.makeText(RegisterActivity.this,"before listener"+textFullName,Toast.LENGTH_LONG);
                String textGender;

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(RegisterActivity.this, "please enter your fullname", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(RegisterActivity.this, "please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("valid email is required");
                    editTextRegisterEmail.requestFocus();

                } else if (TextUtils.isEmpty(textDOB)) {
                    Toast.makeText(RegisterActivity.this, "please enter your date of birth", Toast.LENGTH_LONG).show();
                    editTextRegisterDOB.setError("Date of birth is required");
                    editTextRegisterDOB.requestFocus();
                } else if (selectedGenderId == -1) {
                    //none was selected
                    Toast.makeText(RegisterActivity.this, "please select your gender", Toast.LENGTH_LONG).show();
//                    radioButtonRegisterGenderSelected.setError("Gender is required");
//                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(RegisterActivity.this, "please enter your mobile number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("mobile number is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 9) {
                    Toast.makeText(RegisterActivity.this, "please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile number should be 9 digits");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(RegisterActivity.this, "please enter your password", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("password is required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "password should be atleast 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password too weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "password confirmation is required", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("password confirmation  is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "please both passwords must match", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    //clear the entered passwords
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {

                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    //Toast.makeText(RegisterActivity.this,""+textFullName+" "+textEmail+textDOB+textGender+textMobile+textPwd,Toast.LENGTH_LONG);
                    registerUser(textFullName, textEmail, textDOB, textGender, textMobile, textPwd);
                    /*
                    AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage("name:"+textFullName+",email:"+textEmail+",DOB:"+textDOB+",gender:"+textGender+",mobile:"+textMobile+",pwd:"+textPwd);
                    builder.show();*/



                }


            }
        });
    }


    //register using the credentials given
    private void registerUser(String textFullName, String textEmail, String textDOB, String textGender, String textMobile, String textPwd) {
        //Get firebase instance and save in to auth variable
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //create  a new user using email and password
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    fStore=FirebaseFirestore.getInstance();
                    FirebaseAuth userA=FirebaseAuth.getInstance();
                    userID=userA.getCurrentUser().getUid();
                    DocumentReference documentReference=fStore.collection("users").document(userID);
                    Map<String,Object> data=new HashMap<>();
                    data.put("fullname",textFullName);
                    data.put("email",textEmail);
                    data.put("gender",textGender);
                    data.put("mobile",textMobile);
                    data.put("pwd",textPwd);
                    data.put("DOB",textDOB);
                    userA.getCurrentUser().sendEmailVerification();
                    try {


                        documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
//                                builder.setCancelable(true);
//                                builder.setMessage("registration successful");
//                                builder.show();
                                Intent intent = new Intent(RegisterActivity.this,UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }catch (Exception e){
                        Log.d(TAG,"error "+e.getMessage());
                    }

                    //db=FirebaseDatabase.getInstance();
                    //user=db.getReference("User");

                    User u=new User();
                    u.setEmail(textEmail);
                    u.setName(textFullName);
                    u.setGender(textGender);
                    u.setMobile(textMobile);
                    u.setPassword(textPwd);
                    u.setDOB(textDOB);





                     //FirebaseUser n = auth.getCurrentUser();
                    //send email verification
                   // auth.getCurrentUser().sendEmailVerification();
                   // UserA.sendEmailVerification();




                   // AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);


                    //open user profile after successful registration
//                    //close register







                }else {
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        editTextRegisterPwd.setError("Your password is too weak!!,characters must be equal to or more than six");
                        editTextRegisterPwd.requestFocus();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        editTextRegisterEmail.setError("your email is invalid or has already been used. kindly re-enter");
                        editTextRegisterEmail.requestFocus();
                    }catch (FirebaseAuthUserCollisionException e){
                        editTextRegisterPwd.setError("user is already registered using this email");
                        editTextRegisterPwd.requestFocus();
                    }catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });



    }
}