package com.jherrera.incidencias;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.incidencias.api.API;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ModRetroalimentacionActivity extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private int idRetroalimentacion  = 0;
    private EditText editTextRetroalimentacion;
    private RadioButton radioButtonRevision;
    private RadioButton radioButtonCorrecion;
    private RadioButton radioButtonPrevencion;
    private RadioButton radioButtonSinSolucion;
    private Button buttonActualizarRestroalimentacion;
    private Button buttonBorrarRetroalimentacion;
    private int estadoIncidencia = 0;
    private int idIncidencia  = 0;
    private int estadoIncidenciaActual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_retroalimentacion);
        setTokenUser();
        setDataIntent();
        setInitComponenets();
        setActionButtons();
        getRetroalimentacion();
    }

    private void setInitComponenets() {
        editTextRetroalimentacion = findViewById(R.id.editTextModRetroalimentacion);
        radioButtonRevision = findViewById(R.id.radioButtonRevision);
        radioButtonCorrecion = findViewById(R.id.radioButtonCorrecion);
        radioButtonPrevencion = findViewById(R.id.radioButtonPreventivo);
        radioButtonSinSolucion = findViewById(R.id.radioButtonSinSolucion);
        buttonActualizarRestroalimentacion = findViewById(R.id.buttonModRetroalimentacion);
        buttonBorrarRetroalimentacion = findViewById(R.id.buttonBorrarRetroalimentacion);

        switch (estadoIncidenciaActual) {
            case 0:
                radioButtonRevision.setChecked(true);
                break;

            case 1:
                radioButtonCorrecion.setChecked(true);
                break;
            case 2:
                radioButtonPrevencion.setChecked(true);
                break;
            case 3:
                radioButtonSinSolucion.setChecked(true);
                break;
        }
    }

    private void setActionButtons() {
        buttonActualizarRestroalimentacion.setOnClickListener(view -> {
            setEstadoIncidencia();
            if (!editTextRetroalimentacion.getText().toString().isEmpty()) {
                actualizarRetroalimentacion();
            }else {
                Toast.makeText(this, "Ingresa la retroalimentación de la incidencia", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBorrarRetroalimentacion.setOnClickListener(view -> {
            borrarRetroalimentacion();
        });
    }

    private void getRetroalimentacion() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/get-retroalimentacion?id="+idRetroalimentacion, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    editTextRetroalimentacion.setText(json.getString("descripcion"));
                }catch (Exception e){
                    Log.e("Error JSON", e.getMessage());
                }
            }, error -> {
                Toast.makeText(this, "Error petición "+error.getMessage(), Toast.LENGTH_LONG).show();
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
            };
            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(this, "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarRetroalimentacion() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.PUT, API.URL+"/retroalimentaciones/"+idRetroalimentacion, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("message")){
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("Error JSON", e.getMessage());
                }
            }, error -> {
                Toast.makeText(this, "Error petición "+error.getMessage(), Toast.LENGTH_LONG).show();
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
                    params.put("id_incidencia", String.valueOf(idIncidencia));
                    params.put("descripcion", editTextRetroalimentacion.getText().toString());
                    params.put("status", String.valueOf(estadoIncidencia));
                    return new JSONObject(params).toString().getBytes();
                }
            };
            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(this, "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void borrarRetroalimentacion() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.DELETE, API.URL+"/retroalimentaciones/"+idRetroalimentacion, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("message")){
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("Error JSON", e.getMessage());
                }
            }, error -> {
                Toast.makeText(this, "Error petición "+error.getMessage(), Toast.LENGTH_LONG).show();
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
            };
            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(this, "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setEstadoIncidencia() {
        if (radioButtonRevision.isChecked()) {
            estadoIncidencia = 0;
        }

        if (radioButtonCorrecion.isChecked()) {
            estadoIncidencia = 1;
        }

        if (radioButtonPrevencion.isChecked()) {
            estadoIncidencia = 2;
        }

        if (radioButtonSinSolucion.isChecked()) {
            estadoIncidencia = 3;
        }
    }

    private void setDataIntent() {
        idRetroalimentacion = getIntent().getIntExtra("id", 0);
        idIncidencia = getIntent().getIntExtra("idIncidencia", 0);
        estadoIncidenciaActual = getIntent().getIntExtra("estadoIncidencia", 0);
    }

    private void setTokenUser() {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }
}