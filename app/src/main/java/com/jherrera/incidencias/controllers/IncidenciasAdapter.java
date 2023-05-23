package com.jherrera.incidencias.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jherrera.incidencias.R;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.models.Incidencias;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IncidenciasAdapter extends RecyclerView.Adapter<IncidenciasAdapter.ViewHolder> implements View.OnClickListener {

    private JSONArray jsonArrayIncidencias;
    private Context context;

    private View.OnClickListener clickListener;

    public IncidenciasAdapter(JSONArray jsonArrayIncidencias, Context context) {
        this.jsonArrayIncidencias = jsonArrayIncidencias;
        this.context = context;
    }

    //crea un nuevo objeto de tipo tarea
    private Incidencias getIncidencias(JSONObject jsonIncidencia) {
        try {
            return new Incidencias(
                    Integer.parseInt(jsonIncidencia.getString("id")),
                    jsonIncidencia.getString("tipo"),
                    jsonIncidencia.getString("nombres") + " " + jsonIncidencia.getString("apellidos"),
                    jsonIncidencia.getString("descripcion"),
                    jsonIncidencia.getString("cargo"),
                    jsonIncidencia.getString("departamento"),
                    jsonIncidencia.getString("fecha"),
                    jsonIncidencia.getString("imagen"),
                    Integer.parseInt(jsonIncidencia.getString("resolucion"))
            );
        }catch (JSONException e) {
            Log.e("Error al crear objeto tarea:", e.getMessage());
            return null;
        }
    }

    @NonNull
    @Override
    public IncidenciasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_incidencias_layout, parent, false);
        view.setOnClickListener(this);
        return new IncidenciasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidenciasAdapter.ViewHolder holder, int position) {
        try {
            JSONObject jsonIncidencia = jsonArrayIncidencias.getJSONObject(position);
            Incidencias incidencia = getIncidencias(jsonIncidencia);

            if (incidencia != null) {
                Picasso.get().load(API.URL_IMG+"/"+incidencia.getImagenIncidencia()).into(holder.imageViewIncidencia, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBarImagen.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.imageViewIncidencia.setImageResource(R.drawable.ic_no_imagen);
                    }
                });
                holder.textViewTipo.setText(incidencia.getNombreTipo());
                holder.textViewDescripcion.setText(incidencia.getDescripcionIncidencia());
                holder.textViewFecha.setText(incidencia.getFechaIncidencia());
                Log.e("URL IMG:", API.URL_IMG+"/"+incidencia.getImagenIncidencia());
            }
        }catch (JSONException e) {
            Log.e("Error Json", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return (jsonArrayIncidencias != null) ? jsonArrayIncidencias.length() : 0;
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.onClick(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewIncidencia;
        private TextView textViewTipo;
        private TextView textViewDescripcion;
        private TextView textViewFecha;
        private ProgressBar progressBarImagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIncidencia = itemView.findViewById(R.id.imageViewIncidencia);
            textViewTipo = itemView.findViewById(R.id.textViewTipoIncidencia);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFecha = itemView.findViewById(R.id.textViewFechaIncidencia);
            progressBarImagen = itemView.findViewById(R.id.progressBarImage);
        }
    }
}