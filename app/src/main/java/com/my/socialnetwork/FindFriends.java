package com.my.socialnetwork;

public class FindFriends {
    public String profileimage, fullname, status, userID;

    public FindFriends() {}

    public FindFriends(String profileimage, String fullname, String status, String userID) {
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.status = status;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
