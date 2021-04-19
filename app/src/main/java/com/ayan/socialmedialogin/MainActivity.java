package com.ayan.socialmedialogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButtonFacebook;
    private TextView nameTextView;
    private TextView emailTextView;
    private CallbackManager callbackManager;
    private ProfilePictureView profilePictureView;

    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN=100;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = findViewById(R.id.nameTextView);
        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
        loginButtonFacebook = findViewById(R.id.login_button);
        emailTextView = findViewById(R.id.emailTextView);

        profilePictureView.setVisibility(View.GONE);


        callbackManager = CallbackManager.Factory.create();

        loginButtonFacebook.setPermissions(Arrays.asList("email","public_profile"));


        loginButtonFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        //    Google Login Integration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, GoogleLogin.class);

                startActivity(intent);

//                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {


        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if(currentAccessToken==null){
                nameTextView.setText("");
                emailTextView.setText("");
                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                profilePictureView.setVisibility(View.GONE);



                Toast.makeText(MainActivity.this,"Logged out", Toast.LENGTH_SHORT).show();
            }

            else
            {
                loaduserProfile(currentAccessToken);
            }

        }
    };

    private  void loaduserProfile(AccessToken newAccessToken)

    {


        GraphRequest graphRequest = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback()

        {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {

                if(object!=null)
                {
                    try
                    {

                           String email = object.getString("email");
                           String id = object.getString("id");
                           String name = object.getString("name");

                        nameTextView.setText(name);
                        emailTextView.setText(email);

                        findViewById(R.id.sign_in_button).setVisibility(View.GONE);

                        profilePictureView.setVisibility(View.VISIBLE);

                        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);

                        profilePictureView.setProfileId(id);




                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        });



        Bundle parameters = new Bundle();
        parameters.putString("fields","email,name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }


}

