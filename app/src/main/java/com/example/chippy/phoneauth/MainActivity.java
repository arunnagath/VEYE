package com.example.chippy.phoneauth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
        import com.google.firebase.auth.FirebaseUser;


        import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PhoneLogin";
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myref;
    String phone;
    TextView t1,t2;

    EditText e1,e2;
    Button b1,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        e1 = (EditText) findViewById(R.id.Phonenoedittext);
        b1 = (Button) findViewById(R.id.PhoneVerify);
        t1 = (TextView)findViewById(R.id.textView2Phone);

        e2 = (EditText) findViewById(R.id.OTPeditText);
        b2 = (Button)findViewById(R.id.OTPVERIFY);
        t2 = (TextView)findViewById(R.id.textViewVerified);
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            startActivity(new Intent(MainActivity.this,main.class));
            finish();
        } else {
            // No user is signed in

            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    Log.d(TAG, "onVerificationCompleted:" + credential);
                    mVerificationInProgress = false;
                    Toast.makeText(MainActivity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
                    signInWithPhoneAuthCredential(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.w(TAG, "onVerificationFailed", e);
                    Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        Toast.makeText(MainActivity.this, "InValid Phone Number", Toast.LENGTH_SHORT).show();
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                    }

                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    // Log.d(TAG, "onCodeSent:" + verificationId);
                    Toast.makeText(MainActivity.this, "Verification code has been send on your number", Toast.LENGTH_SHORT).show();
                    // Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;

                    e1.setVisibility(View.GONE);
                    b1.setVisibility(View.GONE);
                    t1.setVisibility(View.GONE);

                    t2.setVisibility(View.VISIBLE);
                    e2.setVisibility(View.VISIBLE);
                    b2.setVisibility(View.VISIBLE);
                    // ...
                }
            };
        }
        // [START on_start_check_user]
       ///// @Override
      //  public void onStart() {
           // super.onStart();
            // Check if user is signed in (non-null) and update UI accordingly.
           // user =
            // [START_EXCLUDE]
           // if (mVerificationInProgress && validatePhoneNumber()) {
               // startPhoneNumberVerification(mPhoneNumberField.getText().toString());
          //  }
            // [END_EXCLUDE]
       // }
        // [END on_start_check_user]

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                       phone= e1.getText().toString(),
                        60,
                        java.util.concurrent.TimeUnit.SECONDS,
                        MainActivity.this,
                        mCallbacks);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, e2.getText().toString());
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential);
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Log.d(TAG, "signInWithCredential:success");


                            startActivity(new Intent(MainActivity.this,main.class));
                            finish();
                            myref = FirebaseDatabase.getInstance().getReference();
                            myref.child("users").push().setValue(phone);
                            Toast.makeText(MainActivity.this,phone,Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this,"Verification Done",Toast.LENGTH_SHORT).show();
                            // ...
                        } else {
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(MainActivity.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
