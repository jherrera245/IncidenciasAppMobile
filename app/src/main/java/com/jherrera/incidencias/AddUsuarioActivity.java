package com.jherrera.incidencias;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.controllers.EmpleadosSpinnerAdapter;
import com.jherrera.incidencias.controllers.TiposIncidenciasSpinnerAdapter;
import com.jherrera.incidencias.models.Empleados;
import com.jherrera.incidencias.models.TiposIncidencias;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddUsuarioActivity extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private ArrayList<Empleados> listaEmpleados;
    private EmpleadosSpinnerAdapter empleadosSpinnerAdapter;
    private Spinner spinnerEmpleados;
    private int idEmpleado = 0;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private RadioButton radioButtonAdmin;
    private RadioButton radioButtonEmpleado;
    private Button buttonGuadarUser;
    private int rolUser = 0; //enviar esta variable por volley

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usuario);
        //Inicializando componentes de la interfaz
        setTokenUser();
        setInitComponents();
        setDataSpinner();
        setIdEmpleado();
        setActionButtons();
    }

    private void setInitComponents() {
        spinnerEmpleados = findViewById(R.id.spinnerEmpleados);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextName);
        radioButtonAdmin = findViewById(R.id.radioButtonAdmin);
        radioButtonEmpleado = findViewById(R.id.radioButtonEmpleado);
        buttonGuadarUser = findViewById(R.id.buttonGuadarUser);
        listaEmpleados = new ArrayList<>();
        radioButtonEmpleado.setChecked(true);

    }

    private void setDataSpinner() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/empleados", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray data = json.getJSONArray("empleados");
                    setDataArrayListEmpleados(data);
                    empleadosSpinnerAdapter = new EmpleadosSpinnerAdapter(
                            AddUsuarioActivity.this,
                            listaEmpleados
                    );
                    spinnerEmpleados.setAdapter(empleadosSpinnerAdapter);
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

    private void setDataArrayListEmpleados(JSONArray data) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject empleado = data.getJSONObject(i);

                listaEmpleados.add(new Empleados(
                        Integer.parseInt(empleado.getString("id")),
                        empleado.getString("nombres")+" "+empleado.getString("apellidos")
                ));
            }
        }catch (Exception e) {
            Log.e("Error", "No se pudo recuperar los datos de los tipos de incidencias");
        }
    }

    private void setIdEmpleado() {
        spinnerEmpleados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Empleados item = (Empleados) adapterView.getItemAtPosition(i);
                idEmpleado = item.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setActionButtons() {
        //agregar aqui funciones de botones
        buttonGuadarUser.setOnClickListener(view -> {
            //agregar if con validaciones de campos
            if (editTextName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre", Toast.LENGTH_SHORT).show();
            }else if(editTextEmail.getText().toString().isEmpty()) {
                Toast.makeText(this, "Ingresa el email", Toast.LENGTH_SHORT).show();
            }else if(editTextPassword.getText().toString().isEmpty()) {
                Toast.makeText(this, "Ingresa la clave", Toast.LENGTH_SHORT).show();
            }else{
                setEstadoRol();
                guardarUsuario();
            }
        });
    }

    private void guardarUsuario() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, API.URL+"/usuarios", response -> {
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

                    clearEditText();params.put("name", String.valueOf(editTextName.getText().toString()));
                    params.put("email", editTextEmail.getText().toString());
                    params.put("password", editTextPassword.getText().toString());
                    params.put("id_empleado", String.valueOf(idEmpleado));
                    params.put("is_admin", String.valueOf(rolUser));
                    return new JSONObject(params).toString().getBytes();
                }
            };
            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(this, "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setEstadoRol() {
        if (radioButtonAdmin.isChecked()) {
            rolUser = 0;
        }

        if (radioButtonEmpleado.isChecked()) {
            rolUser = 1;
        }
    }

    private void setTokenUser() {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }

    private void clearEditText() {
        editTextName.setText(null);
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }
}