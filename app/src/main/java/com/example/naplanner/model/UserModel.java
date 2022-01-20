package com.example.naplanner.model;

public class UserModel {
    private String mail;
    private String username;
    private String pass;

    public UserModel() {
    }

    public UserModel(String mail, String username, String pass) {
        this.mail = mail;
        this.username = username;
        this.pass = pass;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
