package com.jherrera.incidencias.models;

public class Retroalimentaciones {
    private int id;
    private int idIncidencia;
    private String nombreTipo;
    private String descripcionResolucion;
    private String nombreEmpleado;
    private String cargoEmpleado;
    private String departamentoEmpleado;
    private String fechaResolucion;
    private int estadoIncidencia;

    public Retroalimentaciones(
            int id,
            int idIncidencia,
            String nombreTipo,
            String descripcionResolucion,
            String nombreEmpleado,
            String cargoEmpleado,
            String departamentoEmpleado,
            String fechaResolucion,
            int estadoIncidencia
    ) {
        this.id = id;
        this.idIncidencia = idIncidencia;
        this.nombreTipo = nombreTipo;
        this.descripcionResolucion = descripcionResolucion;
        this.nombreEmpleado = nombreEmpleado;
        this.cargoEmpleado = cargoEmpleado;
        this.departamentoEmpleado = departamentoEmpleado;
        this.fechaResolucion = fechaResolucion;
        this.estadoIncidencia = estadoIncidencia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdIncidencia() {
        return idIncidencia;
    }

    public void setIdIncidencia(int idIncidencia) {
        this.idIncidencia = idIncidencia;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getDescripcionResolucion() {
        return descripcionResolucion;
    }

    public void setDescripcionResolucion(String descripcionResolucion) {
        this.descripcionResolucion = descripcionResolucion;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
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

    public String getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(String fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public int getEstadoIncidencia() {
        return estadoIncidencia;
    }

    public void setEstadoIncidencia(int estadoIncidencia) {
        this.estadoIncidencia = estadoIncidencia;
    }
}
