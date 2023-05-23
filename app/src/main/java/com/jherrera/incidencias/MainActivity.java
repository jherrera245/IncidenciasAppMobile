package com.jherrera.incidencias;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.incidencias.api.API;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //object views
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInitComponents();
        setActionButtons();
    }

    /**
     * This method initialize the view object
     */
    private void setInitComponents() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
    }

    /**
     * This method add event button or others objects
     */
    private void setActionButtons() {
        buttonLogin.setOnClickListener(view -> {
            sessionStart();
        });
    }

    /**
     * this method send to the api the petitions to be start session
     */
    private void sessionStart() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, API.URL+"/login", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.has("errors")) {
                        JSONObject errors = json.getJSONObject("errors");
                        if (errors.has("email")) {
                            Toast.makeText(this, errors.getString("email"), Toast.LENGTH_LONG).show();
                        }
                        if (errors.has("password")) {
                            Toast.makeText(this, errors.getString("password"), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        if (json.getBoolean("status")) {
                            Intent intent  = new Intent(this, HomeActivity.class);
                            String token = json.getString("access_token");
                            //guardamos el token de la session
                            savePrefereces(token);
                            startActivity(intent);
                            finish();
                        }
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
                    headers.put("Authorization", "Bearer 1|k1x4yetWcmQHxPQZd397qY4ef9sV8hn9zNXCIF7H");
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("email", editTextEmail.getText().toString());
                    params.put("password", editTextPassword.getText().toString());
                    return new JSONObject(params).toString().getBytes();
                }
            };

            queue.add(request);
        }catch (Exception e) {
            Toast.makeText(this, "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void savePrefereces(String token) {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("access_token", token);
        editor.putBoolean("session", true);
        editor.commit();
    }
}