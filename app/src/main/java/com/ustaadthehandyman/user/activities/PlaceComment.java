package com.ustaadthehandyman.user.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.PostComment;
import com.ustaadthehandyman.user.api.models.body.CommentBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceComment extends AppCompatActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Button postFeedback;
    EditText comment;
    RatingBar ratingBar;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_comment);

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle("Provide feedback");

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
            finish();

        comment = findViewById(R.id.et_comment);
        ratingBar = findViewById(R.id.cooment_rating);
        postFeedback = findViewById(R.id.btn_send_comment);
        progressBar = findViewById(R.id.pb_comment);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        final String orderId = preferences.getString("order-id",null);

        final PostComment postComment = APIInitialize.postComment();

        postFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressDialog = new ProgressDialog(PlaceComment.this,ProgressDialog.STYLE_SPINNER);
                    progressDialog.setMessage("Submitting feedback");
                    progressDialog.show();
                }

                CommentBody commentBody = new CommentBody();
                commentBody.setComment(comment.getText().toString().trim());
                commentBody.setOrderId(Integer.parseInt(orderId));
                commentBody.setRating((int) ratingBar.getRating());

                postComment.postComment("barer "+preferences.getString("auth-token",null),commentBody).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("Comment",String.valueOf(response.code()));
                        if(response.code() == 200){
                            editor.putBoolean("isOrderFinished",false);
                            editor.apply();
                            Toast.makeText(getApplicationContext(),"Thank your for your feedback",Toast.LENGTH_SHORT).show();
                            finish();

                        }else {
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                progressBar.setVisibility(View.GONE);
                            }else {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(),"something wrong try again later",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            progressBar.setVisibility(View.GONE);
                        }else {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(),"something wrong try again later",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
