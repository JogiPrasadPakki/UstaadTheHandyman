package com.ustaadthehandyman.user.api;


import com.ustaadthehandyman.user.util.GlobalFields;

/**
 * Created by Jogi Prasad Pakki on 06-May-18.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2019 All reserved by Jogi Prasad Pakki.
 */
public class APIInitialize {

    private APIInitialize() {
    }
    public static AuthUser authService() {
        return APIClient.getClient(GlobalFields.BackendUrl).create(AuthUser.class);
    }
    public static PlaceOrder PostOrder(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(PlaceOrder.class);
    }
    public  static SendFeedback PostFeedback(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(SendFeedback.class);
    }
    public static UpdateUser  uploadeProfile(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(UpdateUser.class);
    }
    public static GetUser GetUserInfo(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(GetUser.class);
    }
    public static  GetOrders GetOrdersList(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(GetOrders.class);
    }
    public static OrderDetails GetOrderInfo(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(OrderDetails.class);
    }
    public static LastActive setLastSeen(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(LastActive.class);
    }
    public static PostComment postComment(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(PostComment.class);
    }
    public static UpdateFcmToken updateFcmToken(){
        return APIClient.getClient(GlobalFields.BackendUrl).create(UpdateFcmToken.class);
    }
}
