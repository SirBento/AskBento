package com.example.askbento;

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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    //declaring variables to access components on the Login form
    private EditText log_Email, logPass;
    // creating a variable for accessing the database authentication
    private FirebaseAuth mAuth;
    private Button btn_login , btn_Sign_Up;
    private TextView forgotPassword;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotPassword = findViewById(R.id.forgotPass);
        btn_login = findViewById(R.id.btnLogin);
        btn_Sign_Up = findViewById(R.id.btnSignUp);
        loadingBar = new ProgressDialog(this);
        log_Email = findViewById(R.id.logEmail);
        logPass = findViewById(R.id.logPassword);

        // initializing the database authentication
        mAuth = FirebaseAuth.getInstance();




        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                log_Email = findViewById(R.id.logEmail);
                logPass = findViewById(R.id.logPassword);

                if(!validatePassword() |  !validateEmail() ){
                    // If the functions continue to return false repeat validation until true
                    return;

                } else{

                    loadingBar.setTitle("Sign In");
                    loadingBar.setMessage("Please wait...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();
                    userLogin();

                }

            }
        });



        btn_Sign_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( Login.this,SignUp.class) );
            }
        });



        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( Login.this,ForgotPassword.class) );
            }
        });

    }


    private boolean validateEmail(){
        String e_mail = log_Email.getText().toString().trim();

        //checking if the email text box is filled or not
        if(e_mail.isEmpty()){
            YoYo.with(Techniques.Shake).duration(1000).repeat(1).playOn(log_Email);
            log_Email.setError("Email is required");
            log_Email.requestFocus();
            return false;

            //checking if the email entered is valid
        }else if(!Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()){
            YoYo.with(Techniques.Shake).duration(1000).repeat(1).playOn(log_Email);
            log_Email.setError("Please enter a email ");
            log_Email.requestFocus();
            return false;

        } else {
            log_Email.setError(null);
            return true;

        }

    }

    private boolean validatePassword(){

        String pass = logPass.getText().toString().trim();

        // checking if the password text box is filled or not

        if(pass.isEmpty()){
            YoYo.with(Techniques.Shake).duration(1000).repeat(1).playOn(logPass);
            logPass.setError("Password is required");
            logPass.requestFocus();
            return false;

            // checking the password length due to firebase restrictions
        } else if (pass.length()< 6) {
            YoYo.with(Techniques.Shake).duration(1000).repeat(1).playOn(logPass);
            logPass.setError("Your password is too short");
            logPass.requestFocus();
            return false;

        } else {
            log_Email.setError(null);
            return true;

        }

    }



    private void userLogin(){

        String LogIne_mail = log_Email.getText().toString().trim();
        String LogInpass = logPass.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(LogIne_mail,LogInpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    //checking if the account is verified , if not the user has to first verify the account before logging in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){

                        loadingBar.dismiss();
                        startActivity(new Intent( Login.this,MainActivity.class) );

                    }else{

                        user.sendEmailVerification();
                        loadingBar.dismiss();
                        Toast.makeText(Login.this, "Account not verified. Please check your email to verify your account the Login",
                                Toast.LENGTH_LONG).show();
                    }

                }else {
                    loadingBar.dismiss();
                    Toast.makeText(Login.this, "LogIn Failed!!! Please check your credentials and your internet connection", Toast.LENGTH_LONG).show();

                }

            }
        });


    }
}