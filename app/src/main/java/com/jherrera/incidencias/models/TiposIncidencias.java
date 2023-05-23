package com.jherrera.incidencias.models;

public class TiposIncidencias {
    private int idTipoInciencia;
    private String nombreTipoIncidencia;

    public TiposIncidencias(int idTipoInciencia, String nombreTipoIncidencia) {
        this.idTipoInciencia = idTipoInciencia;
        this.nombreTipoIncidencia = nombreTipoIncidencia;
    }

    public int getIdTipoInciencia() {
        return idTipoInciencia;
    }

    public void setIdTipoInciencia(int idTipoInciencia) {
        this.idTipoInciencia = idTipoInciencia;
    }

    public String getNombreTipoIncidencia() {
        return nombreTipoIncidencia;
    }

    public void setNombreTipoIncidencia(String nombreTipoIncidencia) {
        this.nombreTipoIncidencia = nombreTipoIncidencia;
    }
}
