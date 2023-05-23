package com.jherrera.incidencias.models;

public class Incidencias {

    private int id;
    private String nombreTipo;
    private String descripcionIncidencia;
    private String nombreEmpleado;
    private String cargoEmpleado;
    private String departamentoEmpleado;
    private String fechaIncidencia;
    private String imagenIncidencia;
    private int statusResolucion;

    public Incidencias(
            int id,
            String nombreTipo,
            String nombreEmpleado,
            String descripcionIncidencia,
            String cargoEmpleado,
            String departamentoEmpleado,
            String fechaIncidencia,
            String imagenIncidencia,
            int statusResolucion
    ) {
        this.id = id;
        this.nombreTipo = nombreTipo;
        this.nombreEmpleado = nombreEmpleado;
        this.descripcionIncidencia = descripcionIncidencia;
        this.cargoEmpleado = cargoEmpleado;
        this.departamentoEmpleado = departamentoEmpleado;
        this.fechaIncidencia = fechaIncidencia;
        this.imagenIncidencia = imagenIncidencia;
        this.statusResolucion = statusResolucion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }
    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getDescripcionIncidencia() {
        return descripcionIncidencia;
    }

    public void setDescripcionIncidencia(String descripcionIncidencia) {
        this.descripcionIncidencia = descripcionIncidencia;
    }

    public String getCargoEmpleado() {
        return cargoEmpleado;
    }

    public void setCargoEmpleado(String cargoEmpleado) {
        this.cargoEmpleado = cargoEmpleado;
    }

    public String getDepartamentoEmpleado() {
        return departamentoEmpleado;
    }

    public void setDepartamentoEmpleado(String departamentoEmpleado) {
        this.departamentoEmpleado = departamentoEmpleado;
    }

    public String getFechaIncidencia() {
        return fechaIncidencia;
    }

    public void setFechaIncidencia(String fechaIncidencia) {
        this.fechaIncidencia = fechaIncidencia;
    }

    public String getImagenIncidencia() {
        return imagenIncidencia;
    }

    public void setImagenIncidencia(String imagenIncidencia) {
        this.imagenIncidencia = imagenIncidencia;
    }

    public int getStatusResolucion() {
        return statusResolucion;
    }

    public void setStatusResolucion(int statusResolucion) {
        this.statusResolucion = statusResolucion;
    }
}
