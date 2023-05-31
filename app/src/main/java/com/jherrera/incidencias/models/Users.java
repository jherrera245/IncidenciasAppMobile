package com.jherrera.incidencias.models;

public class Users {
    private int id;
    private String username;
    private String email;
    private String nombreEmpleado;
    private int rol;
    public Users(int id, String username, String email, String nombreEmpleado, int rol) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nombreEmpleado = nombreEmpleado;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }
}
