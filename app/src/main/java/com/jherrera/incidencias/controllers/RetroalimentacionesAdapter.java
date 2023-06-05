package com.jherrera.incidencias.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.jherrera.incidencias.ModRetroalimentacionActivity;
import com.jherrera.incidencias.R;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.models.Retroalimentaciones;
import com.jherrera.incidencias.models.UserLoged;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RetroalimentacionesAdapter extends RecyclerView.Adapter<RetroalimentacionesAdapter.ViewHolder> implements View.OnClickListener{

    private JSONArray jsonArrayRetroalimentaciones;
    private Context context;
    private View.OnClickListener clickListener;
    private static String ACCESS_TOKEN;
    private UserLoged loged;

    public RetroalimentacionesAdapter(JSONArray jsonArrayRetroalimentaciones, Context context) {
        this.jsonArrayRetroalimentaciones = jsonArrayRetroalimentaciones;
        this.context = context;
        setTokenUser();
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.onClick(view);
        }
    }

    //crea un nuevo objeto de tipo retroalimentaciones
    private Retroalimentaciones getRetroalimentacion(JSONObject jsonRetroalimentacion) {
        try {
            return new Retroalimentaciones(
                    Integer.parseInt(jsonRetroalimentacion.getString("id")),
                    Integer.parseInt(jsonRetroalimentacion.getString("idIncidencia")),
                    jsonRetroalimentacion.getString("tipo"),
                    jsonRetroalimentacion.getString("retroalimentacion"),
                    jsonRetroalimentacion.getString("nombres") + " " + jsonRetroalimentacion.getString("apellidos"),
                    jsonRetroalimentacion.getString("cargo"),
                    jsonRetroalimentacion.getString("departamento"),
                    jsonRetroalimentacion.getString("fecha"),
                    Integer.parseInt(jsonRetroalimentacion.getString("estadoIncidencia"))
            );
        }catch (JSONException e) {
            Log.e("Error al crear objeto:", e.getMessage());
            return null;
        }
    }

    @NonNull
    @Override
    public RetroalimentacionesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_retroalimentacion_layout, parent, false);
        view.setOnClickListener(this);
        return new RetroalimentacionesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject jsonRetroalimentacion= jsonArrayRetroalimentaciones.getJSONObject(position);
            Retroalimentaciones retroalimentacion = getRetroalimentacion(jsonRetroalimentacion);

            if (retroalimentacion != null) {
                setInitElementsViewHolder(holder, retroalimentacion);
                setActionButtons(holder, retroalimentacion);
            }
        }catch (JSONException e) {
            Log.e("Error Json", e.getMessage());
        }
    }

    private void setActionButtons(ViewHolder holder, Retroalimentaciones retroalimentacion) {
        holder.buttonSendNotificacion.setOnClickListener(view -> {
            sendNotificationUser(retroalimentacion.getId());
        });

        holder.buttonDetalles.setOnClickListener(view -> {
            Intent intent = new Intent(context, ModRetroalimentacionActivity.class);
            intent.putExtra("id", retroalimentacion.getId());
            intent.putExtra("idIncidencia", retroalimentacion.getIdIncidencia());
            intent.putExtra("estadoIncidencia", retroalimentacion.getEstadoIncidencia());
            context.startActivity(intent);
        });
    }

    private void setInitElementsViewHolder(ViewHolder holder, Retroalimentaciones retroalimentacion) {
        holder.textViewTipo.setText(retroalimentacion.getNombreTipo());
        holder.textViewDescripcion.setText("Descripción: "+retroalimentacion.getDescripcionResolucion());
        holder.textViewRevisadoPor.setText("Revisado por: "+retroalimentacion.getNombreEmpleado());
        holder.textViewCargo.setText("Cargo: "+retroalimentacion.getCargoEmpleado());
        holder.textViewFecha.setText("Fecha de revisión: "+retroalimentacion.getFechaResolucion());

        if (loged.getRol() == 0) {
            holder.buttonSendNotificacion.setVisibility(View.GONE);
            holder.buttonDetalles.setVisibility(View.GONE);
        }
    }

    private void sendNotificationUser(int idRetroalimentacion){
        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, API.URL+"/notificacion-resolucion", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("multicast_id")){
                        Toast.makeText(context, "Notificación enviada al empleado", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "No se puedo enviar la notifcación (Token de usuario no disponible)", Toast.LENGTH_SHORT).show();
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
                    params.put("id", String.valueOf(idRetroalimentacion));
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
        return (jsonArrayRetroalimentaciones != null) ? jsonArrayRetroalimentaciones.length() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private TextView textViewTipo;
        private TextView textViewDescripcion;
        private TextView textViewRevisadoPor;
        private TextView textViewCargo;
        private TextView textViewFecha;
        private Button buttonSendNotificacion;
        private Button buttonDetalles;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTipo = itemView.findViewById(R.id.textViewTipoIncidencia);
            textViewDescripcion = itemView.findViewById(R.id.textViewRetroalimentacion);
            textViewRevisadoPor = itemView.findViewById(R.id.textViewRevisadoPor);
            textViewCargo = itemView.findViewById(R.id.textViewCargo);
            textViewFecha = itemView.findViewById(R.id.textViewFechaRevision);
            buttonSendNotificacion = itemView.findViewById(R.id.buttonNotificarEmpleado);
            buttonDetalles = itemView.findViewById(R.id.buttonDetalles);
        }
    }

    private void setTokenUser() {
        SharedPreferences preferences = context.getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);

        loged = new UserLoged(
                preferences.getString("name", null),
                preferences.getString("email", null),
                preferences.getInt("rol", 0)
        );
    }
}
