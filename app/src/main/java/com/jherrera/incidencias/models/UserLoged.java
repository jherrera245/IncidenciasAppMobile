package com.jherrera.incidencias.models;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLoged {
    private String name;
    private String  email;
    private int rol;

    public UserLoged(String name, String email, int rol) {
        this.name = name;
        this.email = email;
        this.rol = rol;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getRol() {
        return rol;
    }
}
