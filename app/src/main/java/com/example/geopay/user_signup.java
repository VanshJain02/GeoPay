package com.example.geopay;

public class user_signup {
    String fullname,password,username,phoneno;

    public user_signup() {
    }

    public user_signup(String fullname, String password, String username, String phoneno) {
        this.fullname = fullname;
        this.password = password;
        this.username = username;
        this.phoneno = phoneno;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }
}