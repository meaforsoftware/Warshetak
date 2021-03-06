package com.meaforsoftware.warshetak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Sign_Up_Activity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient ;

    FirebaseAuth mAuth ;
    FirebaseAuth.AuthStateListener mAuthListner ;

    public static String User,Mobile,EMail;

    String checkedUserEMail,checkedUserName , checkedUserMobile ;

    SignInButton signInButton ;
    ProgressBar progressBar;

    EditText eTFullName , eTMobile , eTAge,eTEMail , eTPassword , eTConfirmPassword ;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef =  db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        eTFullName = (EditText) findViewById(R.id.et_su_fullName);
        eTMobile = (EditText) findViewById(R.id.et_su_mobile);
        eTAge= (EditText) findViewById(R.id.et_su_age);
        eTEMail = (EditText)findViewById(R.id.et_su_eMail);
        eTPassword = (EditText) findViewById(R.id.et_su_password);
        eTConfirmPassword = (EditText) findViewById(R.id.et_su_confirmPassword);

        signInButton = (SignInButton) findViewById(R.id.button1);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               GoogleSignUp(null);
            }
        });


        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuth=FirebaseAuth.getInstance();

        mAuthListner=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){

                    User = user.getDisplayName();
                    Mobile = user.getPhoneNumber();
                    EMail = user.getEmail();
                }
            }
            //==========================================================================================
//todo creat update method

        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListner);
        finish();
    }

    public void GoogleSignUp(View view) {

        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode==RC_SIGN_IN){
            GoogleSignInResult result =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firbaseAuthWithGoogle(account);
                getUserData();
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void firbaseAuthWithGoogle(GoogleSignInAccount acct){

        progressBar.setVisibility(View.VISIBLE);


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(Sign_Up_Activity.this,"Failed",Toast.LENGTH_LONG).show();

                        }
                        //  progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    public void addUser(View view) {

        String EnteredUserName = eTFullName.getText().toString();
        String EnteredMobile = eTMobile.getText().toString();


        Users users = new Users(EMail, EnteredUserName, EnteredMobile);

        DocumentReference user = usersRef.document(EMail);

        user.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                Toast.makeText(Sign_Up_Activity.this, "Done", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(getApplicationContext(), Profile_Owner.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sign_Up_Activity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUser(View view) {


        String EnteredUserName = eTFullName.getText().toString();
        String EnteredMobile = eTMobile.getText().toString();

        DocumentReference user = usersRef.document(EMail);

        user.update(
                "mobile",EnteredMobile,
                "userName",EnteredUserName
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                Toast.makeText(Sign_Up_Activity.this, "Done", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(getApplicationContext(), Profile_Owner.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sign_Up_Activity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }



        // =========================================check user ==================================

    public void getUserData(){
        usersRef.whereEqualTo("eMail", EMail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Users user = documentSnapshot.toObject(Users.class);

                            checkedUserEMail = user.geteMail();
                            checkedUserName = user.getUserName();
                            checkedUserMobile = user.getMobile();
}
                        if (TextUtils.isEmpty(checkedUserEMail)) {

                            eTFullName.setVisibility(View.VISIBLE);
                            eTMobile.setVisibility(View.VISIBLE);
                            eTAge.setVisibility(View.VISIBLE);
                            eTEMail.setVisibility(View.VISIBLE);
//                            eTEMail.setFocusable(false);

                            eTFullName.setText(User);
                            eTMobile.setText(Mobile);
                            eTEMail.setText(EMail);



                        } else {
                            Toast.makeText(Sign_Up_Activity.this, "User Already Exist", Toast.LENGTH_SHORT).show();

                            eTFullName.setVisibility(View.VISIBLE);
                            eTMobile.setVisibility(View.VISIBLE);
                            eTAge.setVisibility(View.VISIBLE);
                            eTEMail.setVisibility(View.VISIBLE);
//                            eTEMail.setInputType(0);

                            eTFullName.setText(checkedUserName);
                            eTMobile.setText(checkedUserMobile);
                            eTEMail.setText(checkedUserEMail);

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public void confirmUser(View view) {


        if (TextUtils.isEmpty(checkedUserEMail)) {


            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setTimestampsInSnapshotsEnabled(true)
                    .build();
            firestore.setFirestoreSettings(settings);
            addUser(null);

        } else {

            updateUser(null);

        }
    }
}
