package com.example.auto3.ui;

import java.math.BigInteger;

public class User {
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getColorBalance() {
        String color;
        if (this.balance > 100) {
            color = "green";
        } else if (this.balance > 0) {
            color = "yellow";
        } else {
            color = "red";
        }
        return color;
    }

    public BigInteger id;
    public String name;
    public String email;
    public String phone;
    public String password;
    public Double balance;
}
