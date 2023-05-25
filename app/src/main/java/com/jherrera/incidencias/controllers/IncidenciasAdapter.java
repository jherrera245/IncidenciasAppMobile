package com.jherrera.incidencias.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.incidencias.HomeActivity;
import com.jherrera.incidencias.R;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.models.Incidencias;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IncidenciasAdapter extends RecyclerView.Adapter<IncidenciasAdapter.ViewHolder> implements View.OnClickListener {

    private JSONArray jsonArrayIncidencias;
    private Context context;
    private View.OnClickListener clickListener;
    private static String ACCESS_TOKEN;

    public IncidenciasAdapter(JSONArray jsonArrayIncidencias, Context context) {
        this.jsonArrayIncidencias = jsonArrayIncidencias;
        this.context = context;
        setTokenUser();
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
                setInitElementsViewHolder(holder, incidencia);
                setActionButtons(holder, incidencia);
            }
        }catch (JSONException e) {
            Log.e("Error Json", e.getMessage());
        }
    }

    private void setActionButtons(ViewHolder holder, Incidencias incidencia) {
        holder.buttonNotificar.setOnClickListener(view -> {
            sendNotificationAdmin(incidencia.getId());
        });

        holder.buttonDetalles.setOnClickListener(view -> {

        });
    }

    private void setInitElementsViewHolder(ViewHolder holder, Incidencias incidencia) {
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
        holder.textViewEmpleado.setText("Reportado por: "+incidencia.getNombreEmpleado());
        holder.textViewDepartamento.setText("Departamento: "+incidencia.getDepartamentoEmpleado());
        holder.textViewFecha.setText(incidencia.getFechaIncidencia());

        switch (incidencia.getStatusResolucion()) {
            case 0:
                holder.textViewEstado.setText("En revisión");
                holder.textViewEstado.setTextColor(Color.RED);
                break;
            case 1:
                holder.textViewEstado.setText("Acción Correctiva");
                holder.textViewEstado.setTextColor(Color.YELLOW);
            case 2:
                holder.textViewEstado.setText("Acción Preventiva");
                holder.textViewEstado.setTextColor(Color.BLUE);
        }
    }

    private void sendNotificationAdmin(int idIncidencia){
        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, API.URL+"/notificacion-incidencia", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("message_id")){
                        Toast.makeText(context, "Notificación enviada al adminitrador", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("Error JSON", e.getMessage());
                }
            }, error -> {
                Toast.makeText(context, "Error petición "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }){
                @Nullable
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    headers.put("Connection", "keep-alive");
                    headers.put("Authorization", "Bearer "+ACCESS_TOKEN);
                    return headers;
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("id", String.valueOf(idIncidencia));
                    return new JSONObject(params).toString().getBytes();
                }

            };
            queue.add(request);
        }catch (Exception e) {
            Toast.makeText( context, "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        private TextView textViewEmpleado;
        private TextView textViewDepartamento;
        private TextView textViewFecha;
        private TextView textViewEstado;
        private ProgressBar progressBarImagen;

        private Button buttonNotificar;
        private Button buttonDetalles;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewIncidencia = itemView.findViewById(R.id.imageViewIncidencia);
            textViewTipo = itemView.findViewById(R.id.textViewTipoIncidencia);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewEmpleado = itemView.findViewById(R.id.textViewEmpleado);
            textViewDepartamento = itemView.findViewById(R.id.textViewDepartamento);
            textViewFecha = itemView.findViewById(R.id.textViewFechaIncidencia);
            textViewEstado = itemView.findViewById(R.id.textViewEstado);
            progressBarImagen = itemView.findViewById(R.id.progressBarImage);
            buttonDetalles = itemView.findViewById(R.id.buttonDetalles);
            buttonNotificar = itemView.findViewById(R.id.buttonNotificar);
        }
    }

    private void setTokenUser() {
        SharedPreferences preferences = context.getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }
}