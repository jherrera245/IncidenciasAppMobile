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
import com.jherrera.incidencias.models.Empleados;
import com.jherrera.incidencias.models.TiposIncidencias;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModUsuarioActivity extends AppCompatActivity {
    private int idUser = 0; //id del usuario a modificar o eleminar
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
    private Button buttonActualizarUser;
    private Button buttonBorrarUser;
    private int rolUser = 0; //enviar esta variable por volley
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_usuario);
        //Inicializando componentes de la interfaz
        setTokenUser();
        setDataIntent();
        setInitComponents();
        setDataSpinner();
        setIdEmpleado();
        getUser();
        setActionButtons();
    }


    private void setInitComponents() {
        spinnerEmpleados = findViewById(R.id.spinnerEmpleados);
        editTextName = findViewById(R.id.editTextModName);
        editTextEmail = findViewById(R.id.editTextModEmail);
        editTextPassword = findViewById(R.id.editTextModPassword);
        radioButtonAdmin = findViewById(R.id.radioButtonAdmin);
        radioButtonEmpleado = findViewById(R.id.radioButtonEmpleado);
        buttonActualizarUser = findViewById(R.id.buttonModUser);
        buttonBorrarUser = findViewById(R.id.buttonBorrarUser);
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
                            ModUsuarioActivity.this,
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

    private void getUser() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/get-user?id="+idUser, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    int rolActual = Integer.parseInt(json.getString("is_admin"));
                    int idEmpleado = Integer.parseInt(json.getString("id_empleado"));
                    setSelectIdEmpleado(idEmpleado);

                    switch (rolActual) {
                        case 0:
                            radioButtonEmpleado.setChecked(true);
                            break;
                        case 1:
                            radioButtonAdmin.setChecked(true);
                            break;
                    }

                    editTextName.setText(json.getString("name"));
                    editTextEmail.setText(json.getString("email"));
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

    private void setSelectIdEmpleado(int idEmpleado) {
        for (int i = 0; i < spinnerEmpleados.getCount(); i++) {
            Empleados item = (Empleados) spinnerEmpleados.getItemAtPosition(i);
            if (item.getId() == idEmpleado) {
                spinnerEmpleados.setSelection(i);
            }
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
        buttonBorrarUser.setOnClickListener(view -> {
            setRolUsuario();
            //agregar if con validaciones de campos
        });

        //accion para eliminar registro
        buttonBorrarUser.setOnClickListener(view -> {

        });
    }

    private void setRolUsuario() {
        if (radioButtonEmpleado.isChecked()) {
            rolUser = 0;
        }

        if (radioButtonAdmin.isChecked()) {
            rolUser = 1;
        }
    }

    private void setDataIntent() {
        idUser = getIntent().getIntExtra("id", 0);
    }

    private void setTokenUser() {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }
}