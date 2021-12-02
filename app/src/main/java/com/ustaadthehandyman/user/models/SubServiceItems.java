package com.ustaadthehandyman.user.models;

/**
 * Created by Jogi Prasad Pakki on 30-Apr-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2019 All reserved by Jogi Prasad Pakki.
 */
public class SubServiceItems {

    public String serviceName;
    public int serviceIcon;

    public SubServiceItems(String serviceName, int serviceIcon) {
        this.serviceName = serviceName;
        this.serviceIcon = serviceIcon;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getServiceIcon() {
        return serviceIcon;
    }
}
