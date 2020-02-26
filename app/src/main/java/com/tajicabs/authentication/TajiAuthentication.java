package com.tajicabs.authentication;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tajicabs.passengers.PassengerHome;
import com.tajicabs.passengers.SignInActivity;

import static androidx.core.content.ContextCompat.startActivity;

public class TajiAuthentication {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    public void tajiClient(Context context) {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    public void checkAuthentication(Object object, Class bClass) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

//        if (currentUser == null) {
//            /*TO SIGN IN*/
//
//            Intent intent = new Intent(object, SignInActivity.class)
//            startActivity(intent);
////            finish();
//            return 500;
//        } else {
//            /*CONTINUE*/
//            return 200;
//        }
    }

    private void checkFirebaseSession(FirebaseUser user, Context context) {
//        if (user == null) {
//            //Return to SignInActivity
//            Intent intent = new Intent(context, SignInActivity.class);
//            startActivity(intent);
////            finish();
//        }
    }

}
