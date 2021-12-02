package com.ustaadthehandyman.user.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.adapters.OrdersListAdapter;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.GetOrders;
import com.ustaadthehandyman.user.api.models.body.UserId;
import com.ustaadthehandyman.user.api.models.response.Orders;
import com.ustaadthehandyman.user.api.models.response.OrdersList;
import com.ustaadthehandyman.user.util.EventHandler;
import com.ustaadthehandyman.user.util.GlobalFields;
import com.ustaadthehandyman.user.util.NetworkWatcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ustaadthehandyman.user.util.NetworkWatcher.IS_NETWORK_AVAILABLE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrdersPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrdersPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersPage extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OrdersPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ordersPage.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdersPage newInstance(String param1, String param2) {
        OrdersPage fragment = new OrdersPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    GetOrders getOrders;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private RecyclerView recyclerView;
    private SharedPreferences preference;
    private List<Orders> orders;
    private OrdersListAdapter adapter;
    private LinearLayout statusWindow;
    private ImageView warnImage;
    private SwipeRefreshLayout refreshLayout;
    private String token;
    private UserId userId;
    private TextView warnMessage;
    private Button btnLogin;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_page, container, false);
        statusWindow = view.findViewById(R.id.ll_user_orders_warn);
        refreshLayout = view.findViewById(R.id.swl_order_histry);
        warnImage = view.findViewById(R.id.img_user_orders_warn_image);
        warnMessage = view.findViewById(R.id.tv_user_orders_warn);
        recyclerView = view.findViewById(R.id.rv_user_orders);
        btnLogin = view.findViewById(R.id.btn_user_orders_login);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        preference = PreferenceManager.getDefaultSharedPreferences(getContext());
        token = preference.getString("auth-token",null);
        getOrders = APIInitialize.GetOrdersList();
        userId = new UserId();
        userId.setUid(FirebaseAuth.getInstance().getUid());

        if(!GlobalFields.isInternetAvailable(getContext())){
            statusWindow.setVisibility(View.VISIBLE);
            warnImage.setImageResource(R.drawable.ic_no_connecton);
            warnMessage.setText("No Internet connection");
        }else if(FirebaseAuth.getInstance().getCurrentUser() == null){
            warnImage.setVisibility(View.GONE);
            warnMessage.setText("Your not yet login");
            btnLogin.setVisibility(View.VISIBLE);
        } else  {
           getOrders();
        }
        IntentFilter intentFilter = new IntentFilter(NetworkWatcher.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                if(isNetworkAvailable && FirebaseAuth.getInstance().getCurrentUser() != null){
                    statusWindow.setVisibility(View.GONE);
                    getOrders();
                }
            }
        }, intentFilter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrders.getOrder("barer "+token,userId).enqueue(new Callback<List<Orders>>() {
                    @Override
                    public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                        if(response.code() == 200) {
                            List<Orders> list = response.body();
                            if(list.size() == 0){
                                statusWindow.setVisibility(View.VISIBLE);
                                warnImage.setImageResource(R.drawable.ic_no_orders);
                                warnMessage.setText("No Orders found");
                            }else {
                                adapter = new OrdersListAdapter(list);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Orders>> call, Throwable t) {

                    }
                });
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

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventHandler e){
        if(e.getEventId() == GlobalFields.OrderAdded){
            getOrders();
        }
    }
    private void getOrders(){
        getOrders.getOrder("barer "+token,userId).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                Log.d("API",String.valueOf(response.code()));
                if(response.code() == 200) {
                    orders = response.body();
                    if(orders.size() == 0){
                        statusWindow.setVisibility(View.VISIBLE);
                        warnImage.setImageResource(R.drawable.ic_no_orders);
                    }else {
                        adapter = new OrdersListAdapter(orders);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {

            }
        });
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
