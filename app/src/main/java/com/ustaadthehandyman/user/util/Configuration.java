package com.ustaadthehandyman.user.util;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.zoho.deskportalsdk.DeskConfig;
import com.zoho.deskportalsdk.ZohoDeskPortalSDK;
import com.zoho.salesiqembed.ZohoSalesIQ;

/**
 * Created by Jogi Prasad Pakki on 15-May-18.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018 All reserved by Jogi Prasad Pakki.
 */
public class Configuration extends Application{

    public static Configuration mInstance;

    public static ZohoDeskPortalSDK deskInstance;
    @Override
    public void onCreate() {
        super.onCreate();


        //ZohoSalesIQ.init(this,"rC8PCkyTAtzeTlGJd3vss%2FHUexh48A99cRg%2BlhkgWOqWYtstoBN3IFJPM1s7qql0hFqYzdwiPNRYHsQ4pTJe8zzTYhxgrUL4QwclqOK%2Bm4s%3D_in","DO6C0XZRILBjPu%2Bduk9eGr1LjNr%2FH4yrIXZOUkREhrTg%2Bq2RTve5V4Kk2T0TLZqkwT1hv08N4xTVvjairLwzhJcz%2Fjze3gJ10nNoQuhYRS24eLmOPK9J5NR%2BXXF1q93j%2Fc1IXkmFbEYsxhHVtYjNWwguqGgl6R2xZ8CK8orbDcs%3D");
        ZohoDeskPortalSDK.Logger.enableLogs();
        DeskConfig config = new DeskConfig.Builder().build();
        deskInstance = ZohoDeskPortalSDK.getInstance(this);
        deskInstance.initDesk(60000922304L, "95e051450f258412784c059ea622548adc91a236738b3d70", ZohoDeskPortalSDK.DataCenter.IN, config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized Configuration getInstance() {
        return mInstance;
    }
}
