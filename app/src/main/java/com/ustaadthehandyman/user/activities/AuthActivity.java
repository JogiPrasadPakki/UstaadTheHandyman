package com.ustaadthehandyman.user.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.http.Url;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.jogiprasadpakki.android.PinEnteryView;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.AuthUser;
import com.ustaadthehandyman.user.api.GetUser;
import com.ustaadthehandyman.user.api.UpdateFcmToken;
import com.ustaadthehandyman.user.api.models.body.Auth;
import com.ustaadthehandyman.user.api.models.body.UpdateFCM;
import com.ustaadthehandyman.user.api.models.body.UserId;
import com.ustaadthehandyman.user.util.EventHandler;
import com.ustaadthehandyman.user.util.GlobalFields;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Subscriber;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    EditText mobileInput;
    Button sendOtp,btnSkip;
    LinearLayout mobileInputSection,otpInputSection;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    PinEnteryView OtpText;

    String verificationId;
    FirebaseAuth firebaseAuth;
    AuthUser authUser;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    GetUser userInfo;
    String fcmToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        mobileInput = findViewById(R.id.txt_auth_mobile_input);
        sendOtp = findViewById(R.id.btn_auth_send_otp);

        mobileInputSection = findViewById(R.id.li_auth_number_enter);
        otpInputSection = findViewById(R.id.ll_auth_otp);
        btnSkip = findViewById(R.id.btn_auth_skip);
        progressBar = findViewById(R.id.pb_auth);
        OtpText = findViewById(R.id.otp_entry);

        userInfo = APIInitialize.GetUserInfo();
        OtpText.addTextChangedListener(OtpWatcher);


        ZohoSalesIQ.Chat.setFloatingChatButtonVisibility(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        firebaseAuth = FirebaseAuth.getInstance();

        authUser = APIInitialize.authService();

        //Btn send otp after enter number
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mobileInput.getText().toString().length() < 10)
                    mobileInput.setError("Please enter valid number");
                else{
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        progressBar.setVisibility(View.VISIBLE);
                    }else {
                        progressDialog = new ProgressDialog(AuthActivity.this,ProgressDialog.STYLE_SPINNER);
                        progressDialog.setMessage("Sending OTP");
                        progressDialog.show();
                    }
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+mobileInput.getText().toString(), 60, TimeUnit.SECONDS, AuthActivity.this, authCallback);
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("isAuthSkipped",true);
                editor.apply();
                finish();
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks authCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                progressBar.setVisibility(View.VISIBLE);
            }else {
                progressDialog = new ProgressDialog(AuthActivity.this,ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Wait Login");
                progressDialog.show();
            }
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(getApplicationContext(),"Login Failed Try Again",Toast.LENGTH_LONG).show();
            Log.d("Login",e.toString());
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                progressBar.setVisibility(View.GONE);
            }else {
                progressDialog.dismiss();
            }
            verificationId = s;
            mobileInputSection.setVisibility(View.GONE);
            otpInputSection.setVisibility(View.VISIBLE);
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean newuser = Objects.requireNonNull(task.getResult()).getAdditionalUserInfo().isNewUser();
                            if(newuser){
                                login(firebaseAuth.getUid(),mobileInput.getText().toString().trim(),"true");
                            }else {
                                login(firebaseAuth.getUid(),mobileInput.getText().toString().trim(),"false");
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),"Login Failed Try Again",Toast.LENGTH_LONG).show();
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressBar.setVisibility(View.GONE);
                            }else {
                                progressDialog.dismiss();
                            }
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void login(final String uid, final String mobile, final String isNew){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                fcmToken = instanceIdResult.getToken();
                Auth auth = new Auth();
                auth.setUid(uid);
                auth.setMobile(mobile);
                auth.setIsnew(isNew);
                auth.setJoinedOn(String.valueOf(System.currentTimeMillis()));
                auth.setFcmtoken(fcmToken);
                authUser.createUser(auth)
                       .flatMap(response -> {
                           if(response.code() == 200) {
                               String token = response.body().getToken();
                               editor.putString("auth-token", token);
                               editor.putString("user-mobile", mobile);
                               editor.putBoolean("isAuthSkipped", true);
                               editor.apply();
                               UserId getUser = new UserId();
                               getUser.setUid(firebaseAuth.getUid());

                               if(!Boolean.parseBoolean(isNew)){
                                   return userInfo.CurrentUser("barer " + sharedPreferences.getString("auth-token", null), getUser);
                               }else{
                                   return Observable.empty();
                               }
                           }else return Observable.empty();
                       })
                        .flatMap(response -> {
                            if(response.code() == 200){
                                String Name =   response.body().get(0).getName();
                                String ProfilePick = response.body().get(0).getProfilePick();
                                if(Name != null)
                                    editor.putString("user-name",Name);
                                if(ProfilePick != null){
                                    Imageloader imageloader = new Imageloader();
                                    imageloader.execute(ProfilePick);
                                }
                        }
                            UpdateFCM updateFCM = new UpdateFCM();
                            updateFCM.setToken(fcmToken);
                            updateFCM.setUid(FirebaseAuth.getInstance().getUid());
                            UpdateFcmToken updateFcmToken = APIInitialize.updateFcmToken();

                           return  updateFcmToken.update("barer "+sharedPreferences.getString("auth-token",null),updateFCM);

                       })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Observer<Response<Void>>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Response<Void> voidResponse) {
                                if(voidResponse.code() == 200){
                                    Toast.makeText(getApplicationContext(),"Login success",Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().postSticky(new EventHandler(GlobalFields.LoginSuccess));
                                    finish();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("FlatMap error",e.toString());
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }

        });

    }

    //Text view for otp enter and submit to server automatically
    private TextWatcher OtpWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String otp = editable.toString();
            if(otp.length() == 6){
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressDialog = new ProgressDialog(AuthActivity.this,ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Login");
                    progressDialog.show();
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }

        }
    };
    private class Imageloader extends AsyncTask<String,String,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encodedImg = Base64.encodeToString(byteArray, Base64.DEFAULT);
            editor.putString("user-image",encodedImg);
            editor.apply();

        }
    }
}

