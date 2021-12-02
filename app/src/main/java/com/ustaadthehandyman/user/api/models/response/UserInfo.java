package com.ustaadthehandyman.user.api.models.response;
/**
 * Created by Jogi Prasad Pakki on 25-Jun-19.
 * No one allowed to use or modify this script.
 * If you any questions mail me to jogiprasadpakki@gmail.com
 * Copy right 2018-2019 All reserved by Jogi Prasad Pakki.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfo {

@SerializedName("Name")
@Expose
private String name;
@SerializedName("ProfilePick")
@Expose
private String profilePick;
@SerializedName("MobileNo")
@Expose
private String mobileNo;

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getProfilePick() {
return profilePick;
}

public void setProfilePick(String profilePick) {
this.profilePick = profilePick;
}

public String getMobileNo() {
return mobileNo;
}

public void setMobileNo(String mobileNo) {
this.mobileNo = mobileNo;
}

}