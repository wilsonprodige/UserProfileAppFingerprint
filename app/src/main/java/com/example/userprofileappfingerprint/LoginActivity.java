package com.example.userprofileappfingerprint;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginEmail,editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    SharedPreferences sharedPreferences;


    ImageView f_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        f_image=findViewById(R.id.f_icon);
        f_image.setVisibility(View.GONE);

        getSupportActionBar().setTitle("Login");
        ImageView imageShowHidePwd = findViewById(R.id.ImageViewShow);
        imageShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //if pwd is visisble then hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change icon
                    imageShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    //if pwd is not visisble then hide it
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //change icon
                    imageShowHidePwd.setImageResource(R.drawable.ic_show_pwd);

                }

            }
        });

        editTextLoginEmail= findViewById(R.id.editText_login_email);
        editTextLoginPwd=findViewById(R.id.editText_login_password);
        progressBar=findViewById(R.id.progressbar_login);

        authProfile=FirebaseAuth.getInstance();

        //login user
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail=editTextLoginEmail.getText().toString();
                String textPwd= editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this,"Please enter your email",Toast.LENGTH_LONG);
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(LoginActivity.this,"Please enter your password",Toast.LENGTH_LONG);
                    editTextLoginPwd.setError("password is required");
                    editTextLoginPwd.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this,"Valid Email is required",Toast.LENGTH_LONG);
                    editTextLoginEmail.setError("Valid Email is required");
                    editTextLoginEmail.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);

                }
            }
        });
        sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
        boolean isLogin=sharedPreferences.getBoolean("isLogin",false);
        if(isLogin){
            f_image.setVisibility(View.VISIBLE);
        }



        BiometricManager biometricManager = BiometricManager.from(this);
        switch (BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                //startActivityForResult(enrollIntent,REQUEST_CODE);
                //startActivityForResult(enrollIntent, REQUEST_CODE);
                break;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(

                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                try {
                    String email = sharedPreferences.getString("email", "");
                    String password = sharedPreferences.getString("password", "");
                    loginUser(email, password);
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage("Error"+e.getMessage());
                    builder.show();

                }

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint login for FingerPrintTech")
                .setSubtitle("Log in using your fingerprint")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        //Button biometricLoginButton = findViewById(R.id.biometric_login);
        f_image.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //Toast.makeText(LoginActivity.this,"You are logged In",Toast.LENGTH_LONG);
                    //get instance of the current user
                    FirebaseUser firebaseUser= authProfile.getCurrentUser();

                    //check if email is verified
                    //if(firebaseUser.isEmailVerified()){
                    try {
                        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
                        editor.putString("email",textEmail);
                        editor.putString("password",textPwd);
                        editor.putBoolean("isLogin",true);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "You are logged In", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                        finish();
                    }catch (Exception e)
                    {
                        AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage("Error "+e.getMessage());
                        builder.show();

                    }
//                    }else{
//                        firebaseUser.sendEmailVerification();
//                        authProfile.signOut();
//                        showAlertDialog();
//                    }



                }else {
                    Toast.makeText(LoginActivity.this,"Invalid Email or Password",Toast.LENGTH_LONG);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //set up alert Dialog
        AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email was not verified");
        builder.setMessage("Please verify your Email now");

        //open email apps if user clicks continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                }
                catch (Exception e){
                    Toast.makeText(LoginActivity.this,"no email app"+e.getMessage().toString(),Toast.LENGTH_LONG);
                }
            }
        });

        //create the alert box
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() !=null){
            Toast.makeText(LoginActivity.this,"You are already logged in ",Toast.LENGTH_LONG);

            //start the user profile activity
            try {
                Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }catch (Exception e){
                AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Error "+e.getMessage());
                builder.show();

            }

//            startActivity(new Intent(LoginActivity.this,UserProfileActivity.class));
//            finish();
        }else{
            Toast.makeText(LoginActivity.this,"You can login now ",Toast.LENGTH_LONG);

        }
    }
}