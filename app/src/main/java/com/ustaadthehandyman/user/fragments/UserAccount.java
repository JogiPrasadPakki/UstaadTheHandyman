package com.ustaadthehandyman.user.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jogiprasadpakki.android.CircularImageView;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.activities.About;
import com.ustaadthehandyman.user.activities.Address;
import com.ustaadthehandyman.user.activities.AuthActivity;
import com.ustaadthehandyman.user.activities.EditProfile;
import com.ustaadthehandyman.user.activities.Feedback;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.GetUser;
import com.ustaadthehandyman.user.api.models.body.UserId;
import com.ustaadthehandyman.user.api.models.response.UserInfo;
import com.ustaadthehandyman.user.util.EventHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserAccount.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserAccount#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserAccount extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserAccount() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment userAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static UserAccount newInstance(String param1, String param2) {
        UserAccount fragment = new UserAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private LinearLayout myAddress,feedback,userDetails,about,rateus;
    private Intent intent;
    private CircularImageView userIcon;
    private ImageView editIcon;
    TextView AccountName,AccountMobile;
    FirebaseAuth firebaseAuth;
    Button login;
    GetUser userInfo;
    SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_account,container,false);
        myAddress = view.findViewById(R.id.ll_my_address);
        userIcon  = view.findViewById(R.id.img_user_profile);
        editIcon = view.findViewById(R.id.img_usr_edit_profile);
        feedback = view.findViewById(R.id.ll_account_feedback);
        about = view.findViewById(R.id.ll_account_about);
        rateus = view.findViewById(R.id.ll_account_rate);
        userDetails = view.findViewById(R.id.ll_account_user_info);
        login = view.findViewById(R.id.btn_account_login);
        AccountName = view.findViewById(R.id.tv_account_name);
        AccountMobile = view.findViewById(R.id.tv_account_mobile);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        firebaseAuth = FirebaseAuth.getInstance();
        userInfo = APIInitialize.GetUserInfo();

        if(firebaseAuth.getCurrentUser() == null){
            userDetails.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
            editIcon.setVisibility(View.GONE);
        }else {
            userDetails.setVisibility(View.VISIBLE);
            editIcon.setVisibility(View.VISIBLE);
            login.setVisibility(View.GONE);

            String image =  preferences.getString("user-image",null);
            String name =  preferences.getString("user-name",null);
            if(image != null){
                byte[] bytes = Base64.decode(image,Base64.DEFAULT);
                Bitmap bitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                userIcon.setImageBitmap(bitmap);
            }
            if(name != null){
                AccountName.setText(name);
            }else {
                AccountName.setVisibility(View.GONE);
            }
            AccountMobile.setText(preferences.getString("user-mobile",null));
        }

        myAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), Address.class);
                intent.putExtra("Title","My address");
                startActivity(intent);
            }
        });
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), Feedback.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), AuthActivity.class);
                startActivity(intent);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), About.class);
                startActivity(intent);
            }
        });
        rateus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
            }
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventHandler eventHandler){
        switch (eventHandler.getEventId()){
            case  121:
                if(firebaseAuth.getCurrentUser() == null){
                    userDetails.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    editIcon.setVisibility(View.GONE);
                }else {
                    userDetails.setVisibility(View.VISIBLE);
                    editIcon.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                }
                break;
            case 123:
                String image =  preferences.getString("user-image",null);
                String name =  preferences.getString("user-name",null);
                if(image != null){
                    byte[] bytes = Base64.decode(image,Base64.DEFAULT);
                    Bitmap bitmap =  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    userIcon.setImageBitmap(bitmap);
                }
                if(name != null){
                    AccountName.setText(name);
                }else {
                    AccountName.setVisibility(View.GONE);
                }
                AccountMobile.setText(preferences.getString("user-mobile",null));
                break;
            case 122:
                if(firebaseAuth.getCurrentUser() == null){
                    userDetails.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    editIcon.setVisibility(View.GONE);
                }else {
                    userDetails.setVisibility(View.VISIBLE);
                    editIcon.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                }
                break;
        }

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
