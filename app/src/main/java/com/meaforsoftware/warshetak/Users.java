package com.meaforsoftware.warshetak;

public class Users {

    private String eMail;
    private String userName;
    private String mobile;
    private String userType;

    public Users() {}

    public Users(String EMail, String UserName, String Mobile) {

        this.eMail = EMail;
        this.userName = UserName;
        this.mobile = Mobile;



    }

    public String geteMail() {
        return eMail;
    }

    public String getUserName() {
        return userName;
    }

    public String getMobile() {
        return mobile;
    }



}
