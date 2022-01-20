package com.example.socialmediaintegration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewKt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GoogleAccount extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener {

    private CircleImageView image;
    private TextView name, email, link;
    private Button signOutButton;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_account);

        image = findViewById(R.id.profile_photo);
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.email);
        link = findViewById(R.id.link);
        signOutButton = findViewById(R.id.google_signout_button);

        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options).build();

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess())
                            gotoMainActivity();
                        else
                            Toast.makeText(getApplicationContext(), "Session not close", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void gotoMainActivity() {
        startActivity(new Intent(GoogleAccount.this, MainActivity.class));
    }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> signInResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(signInResult.isDone()){
            GoogleSignInResult result = signInResult.get();
            handleSignInResult(result);
        }else{
            signInResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            name.setText("Name : "+account.getDisplayName());
            email.setText("Email id : "+account.getEmail());
            link.setText(account.getId());

            try{
                Glide.with(this).load(account.getPhotoUrl()).into(image);
            }catch (NullPointerException e){
                Toast.makeText(getApplicationContext(), "Image not Found", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            gotoMainActivity();
        }
    }
}