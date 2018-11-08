package com.meaforsoftware.warshetak;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Sign_Up_Activity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient ;

    FirebaseAuth mAuth ;
    FirebaseAuth.AuthStateListener mAuthListner ;



    public static String User,Mobile,EMail;

    String checkedUserEMail ;


    ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef =  db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);



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

//                    getUserData();

//addUser(null);
                }
            }


            //==========================================================================================
//            public void getUserData(){
////                UserName = "";
////                mobile = "";
////                UserType = "";
//
//
//                progressBar.setVisibility(View.VISIBLE);
//                usersRef.whereEqualTo("userID",UserID)
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                    Users user = documentSnapshot.toObject(Users.class);
//
//                                    UserName = user.getUserName();
//                                    mobile = user.getMobile();
//                                    UserType = user.getUserType();
//
//
//
//                                    if (UserName.equals(null)|| UserName ==""){
//                                        logInName.setText(User);
//                                    }else {
//                                        logInName.setText(UserName);
//                                    }
//
//                                    if (mobile.equals(null)||mobile==""){
//                                        logInMobile.setText(Mobile);
//                                    }else {
//                                        logInMobile.setText(mobile);
//                                    }
//
//                                    if (UserType.equals("Owner")){
//                                        Owner.setChecked(true);
//                                    }else {
//                                        Renter.setChecked(true);
//                                    }
//
//
//                                    // Toast.makeText(LogIn_Activity.this,user.getEmail(),Toast.LENGTH_LONG).show();
//                                    progressBar.setVisibility(View.INVISIBLE);
//                                    //logInGoogle.setVisibility(View.GONE);
//                                    logInGoogle.setBackgroundResource(R.drawable.btn_rounded_login_hidden);
//                                    logInGoogle.setClickable(false);
//
//
//
//
//
//
//
//
//                                }
//
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                                progressBar.setVisibility(View.VISIBLE);
//                                Toast.makeText(getApplicationContext(),"User Not Found",Toast.LENGTH_LONG).show();
//
//                            }
//                        });
//
//
//            }


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


        Users users = new Users(EMail, User, Mobile);

//                            DocumentReference user = usersRef.document(EMail);

        usersRef.add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Sign_Up_Activity.this, "Done", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(getApplicationContext(), Profile_Owner.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Sign_Up_Activity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });





//
////todo ==================== save document id =====================
//
//        // =========================================check user ==================================
//        usersRef.whereEqualTo("eMail",EMail)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            Users user = documentSnapshot.toObject(Users.class);
//
//                            checkedUserEMail = user.geteMail();
//                        }
//                        if (!EMail.equals(checkedUserEMail)) {
//
//                            String userEMail = EMail;  // get From Google Account
//                            String UserName = User;
//                            String mobile = Mobile;
//
//
//                            Users users = new Users(EMail, User, Mobile);
//
////                            DocumentReference user = usersRef.document(EMail);
//
//                            usersRef.add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    Toast.makeText(Sign_Up_Activity.this, "Done", Toast.LENGTH_SHORT).show();
////                                    startActivity(new Intent(getApplicationContext(), Profile_Owner.class));
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(Sign_Up_Activity.this, "Error", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//
//                        }
//                        else {
//                            Toast.makeText(Sign_Up_Activity.this, "Done", Toast.LENGTH_SHORT).show();
//
//
//                        }
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });



        //=========================================================================================


    }


}