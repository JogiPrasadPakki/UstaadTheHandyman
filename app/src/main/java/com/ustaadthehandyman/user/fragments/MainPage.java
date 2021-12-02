package com.ustaadthehandyman.user.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.activities.BookingForm;
import com.ustaadthehandyman.user.util.EventHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MainPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainPage.
     */
    // TODO: Rename and change types and number of parameters
    public static MainPage newInstance(String param1, String param2) {
        MainPage fragment = new MainPage();
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

    private CardView btn_service_plumbing, btn_service_painter,btn_service_solar_pannel,btn_service_cctv,btn_service_electric,btn_service_carpenter,btn_service_mobile,btn_service_computer,btn_service_ac,btn_service_logistics,btn_service_electronics,btn_service_worker;
    Intent intent;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.activity_main,container,false);
        btn_service_cctv = view.findViewById(R.id.btn_service_cctv);
        btn_service_plumbing = view.findViewById(R.id.btn_service_plumbing);
        btn_service_painter = view.findViewById(R.id.btn_service_painter);
        btn_service_solar_pannel = view.findViewById(R.id.btn_service_solar);
        btn_service_computer = view.findViewById(R.id.btn_service_computer);
        btn_service_electric = view.findViewById(R.id.btn_service_electric);
        btn_service_carpenter = view.findViewById(R.id.btn_service_carpenter);
        btn_service_mobile = view.findViewById(R.id.btn_service_mobile);
        btn_service_ac = view.findViewById(R.id.btn_service_ac);
        btn_service_logistics = view.findViewById(R.id.btn_service_logistics);
        btn_service_worker = view.findViewById(R.id.btn_service_worker);
        btn_service_electronics = view.findViewById(R.id.btn_service_home_applience);


        btn_service_cctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3011");
                startActivity(intent);
            }
        });
        btn_service_electric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3021");
                startActivity(intent);
            }
        });
        btn_service_painter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3031");
                startActivity(intent);
            }
        });
        btn_service_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3041");
                startActivity(intent);
            }
        });
        btn_service_plumbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3051");
                startActivity(intent);
            }
        });
        btn_service_electronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3061");
                startActivity(intent);
            }
        });
        btn_service_carpenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3071");
                startActivity(intent);
            }
        });
        btn_service_computer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3081");
                startActivity(intent);
            }
        });
        btn_service_solar_pannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3091");
                startActivity(intent);
            }
        });
        btn_service_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3101");
                startActivity(intent);
            }
        });
        btn_service_logistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3111");
                startActivity(intent);
            }
        });
        btn_service_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getContext(), BookingForm.class);
                intent.putExtra("serviceDepartmentId","3121");
                startActivity(intent);
            }
        });







       return view;
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
