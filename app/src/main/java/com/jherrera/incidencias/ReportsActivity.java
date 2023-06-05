package com.jherrera.incidencias;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.controllers.EmpleadosSpinnerAdapter;
import com.jherrera.incidencias.models.Empleados;
import com.jherrera.incidencias.reports.ReportPDF;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportsActivity extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private ArrayList<Empleados> listaEmpleados;
    private EmpleadosSpinnerAdapter empleadosSpinnerAdapter;
    private Spinner spinnerEmpleados;
    private int idEmpleado = 0;
    private EditText editTextFrom;
    private EditText editTextTo;
    private Button buttonPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        setTokenUser();
        setInitComponents();
        setDataSpinner();
        setIdEmpleado();
        setActionButtons();
    }

    private void setInitComponents() {
        spinnerEmpleados = findViewById(R.id.spinnerEmpleadosReporte);
        editTextFrom = findViewById(R.id.editTextDateFrom);
        editTextTo = findViewById(R.id.editTextDateTo);
        buttonPDF = findViewById(R.id.buttonPDFReport);
        listaEmpleados = new ArrayList<>();
    }

    public void setActionButtons() {
        buttonPDF.setOnClickListener(view -> {
            getIncidencias();
        });

        editTextFrom.setOnFocusChangeListener((view, isFocus) -> {
            if(isFocus){
                showCalendarDate(editTextFrom);
            }
        });

        editTextTo.setOnFocusChangeListener((view, isFocus) -> {
            if(isFocus){
                showCalendarDate(editTextTo);
            }
        });
    }

    private void showCalendarDate(EditText editText) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Motramos un date piker para mostrar un calendario
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ReportsActivity.this,
                (v, yearDatePiker, monthOfYear, dayOfMonth) -> {
                    editText.setText(yearDatePiker + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void getIncidencias() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String from = editTextFrom.getText().toString();
        String to = editTextTo.getText().toString();

        String query = "empleado="+idEmpleado+"&from="+from+"&to="+to;
        try {
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/reports-all?"+query, response -> {
                try {
                    JSONArray json = new JSONArray(response);
                    ReportPDF pdf = new ReportPDF(json, this);
                    try {
                        pdf.createPDFAll();
                    }catch (FileNotFoundException e) {
                        Toast.makeText(this, "No se pudo crear el pdf", Toast.LENGTH_SHORT).show();
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

    private void setDataSpinner() {
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/empleados", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray data = json.getJSONArray("empleados");
                    setDataArrayListEmpleados(data);
                    empleadosSpinnerAdapter = new EmpleadosSpinnerAdapter(
                            ReportsActivity.this,
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

    private void setTokenUser() {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }

}