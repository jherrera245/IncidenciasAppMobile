package com.jherrera.incidencias;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddRetroalimentacionActivity extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private EditText editTextRetroalimentacion;
    private RadioButton radioButtonRevision;
    private RadioButton radioButtonCorrecion;
    private RadioButton radioButtonPrevencion;
    private RadioButton radioButtonSinSolucion;
    private Button buttonGuadarRestroalimentacion;
    private int estadoIncidencia = 0;
    private int idIncidencia  = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_retroalimentacion);
        setTokenUser();
        setIdIncidencia();
        setInitComponents();
        setActionButtons();
    }

    private void setIdIncidencia() {
        idIncidencia = getIntent().getIntExtra("idIncidencia", 0);
    }

    private void setInitComponents() {
        editTextRetroalimentacion = findViewById(R.id.editTextRetroalimentacion);
        radioButtonRevision = findViewById(R.id.radioButtonRevision);
        radioButtonCorrecion = findViewById(R.id.radioButtonCorrecion);
        radioButtonPrevencion = findViewById(R.id.radioButtonPreventivo);
        radioButtonSinSolucion = findViewById(R.id.radioButtonSinSolucion);
        buttonGuadarRestroalimentacion = findViewById(R.id.buttonAddRetroalimentacion);
        radioButtonRevision.setChecked(true);
    }

    private void setActionButtons() {
        buttonGuadarRestroalimentacion.setOnClickListener(view -> {
            setEstadoIncidencia();
            if (!editTextRetroalimentacion.getText().toString().isEmpty()) {
                guadarRetroalimentacion();
            }else {
                Toast.makeText(this, "Ingresa la retroalimentación de la incidencia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guadarRetroalimentacion() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, API.URL+"/retroalimentaciones", response -> {
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
                    clearEditText();
                    return new JSONObject(params).toString().getBytes();
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

    //metodo para obtener el toquen del usuario
    private void setTokenUser() {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }

    private void clearEditText(){
        editTextRetroalimentacion.setText(null);
    }
}