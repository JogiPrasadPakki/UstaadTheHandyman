package com.ustaadthehandyman.user.util;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ustaadthehandyman.user.R;
import com.ustaadthehandyman.user.activities.PlaceComment;
import com.ustaadthehandyman.user.api.APIInitialize;
import com.ustaadthehandyman.user.api.UpdateFcmToken;
import com.ustaadthehandyman.user.api.models.body.UpdateFCM;

import java.util.Date;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jogi Prasad Pakki on 03-Jul-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
public   class NotificationService extends FirebaseMessagingService   {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM Token",token);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            UpdateFCM updateFCM = new UpdateFCM();
            updateFCM.setToken(token);
            updateFCM.setUid(FirebaseAuth.getInstance().getUid());
            UpdateFcmToken updateFcmToken = APIInitialize.updateFcmToken();
            updateFcmToken.update(preferences.getString("auth-token",null),updateFCM)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new Observer<Response<Void>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Response<Void> voidResponse) {
                            if(voidResponse.code() == 200){
                                Log.d("FCM ","Token Updated");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null){
            Log.d("FCM Notification",remoteMessage.getNotification().getTitle());
            Log.d("FCM Notification",remoteMessage.getNotification().getBody());
        }

        Map<String,String> data = remoteMessage.getData();
        if(Boolean.parseBoolean(data.get("isOrderFinished"))){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("for-order-comment",true);
            editor.putString("order-id",data.get("orderId"));
            editor.apply();

            Intent intent = new Intent(getApplicationContext(), PlaceComment.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),(int) System.currentTimeMillis(),intent,0);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("1", "Order finished", importance);
                channel.setDescription("Notify on order successfully finished");
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }
            long time = new Date().getTime();
            String tmpStr = String.valueOf(time);
            String last4Str = tmpStr.substring(tmpStr.length() - 5);
            int notificationId = Integer.valueOf(last4Str);

            Notification notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Your Orders finished")
                    .setContentText("Your order for "+data.get("serviceName")+" finished please provide feedback for this service for improving our services")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifiction)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .build();
            assert notificationManager != null;
            notificationManager.notify(notificationId,notification);

        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}

