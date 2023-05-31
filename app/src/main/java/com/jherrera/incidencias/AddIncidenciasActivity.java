package com.jherrera.incidencias;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.controllers.TiposIncidenciasSpinnerAdapter;
import com.jherrera.incidencias.databinding.ActivityAddIncidenciasBinding;
import com.jherrera.incidencias.models.TiposIncidencias;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddIncidenciasActivity extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private ArrayList<TiposIncidencias> listaTiposIncidencias;
    private TiposIncidenciasSpinnerAdapter tiposIncidenciasSpinnerAdapter;
    private ActivityAddIncidenciasBinding binding;
    private FloatingActionButton buttonTakePicture;
    private ImageView imageViewTake;
    private Spinner spinnerTiposIncidencias;
    private int idTipoIncidencia;
    private EditText editTextDescripcionIncidencia;
    private Button buttonGuadarIncidencia;
    private Uri uriImagen;
    private String imagenBase64;
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddIncidenciasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        //Inicializando componentes de la interfaz
        setTokenUser();
        setInitComponents();
        setDataSpinner();
        setIdTipoInciencias();
        setActionButtons();
    }

    //Este metodo pemite determinar el id de tipo de incidencia seleccionado
    private void setIdTipoInciencias() {
        spinnerTiposIncidencias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TiposIncidencias item = (TiposIncidencias) adapterView.getItemAtPosition(i);
                idTipoIncidencia = item.getIdTipoInciencia();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setInitComponents() {
        buttonTakePicture = binding.buttonTakePicture;
        imageViewTake = binding.imageViewTake;
        spinnerTiposIncidencias = findViewById(R.id.spinnerTipoIncidencias);
        editTextDescripcionIncidencia = findViewById(R.id.editTextDescripcionInciencia);
        buttonGuadarIncidencia = findViewById(R.id.buttonGuardarIncidencias);
        listaTiposIncidencias = new ArrayList<>();
    }

    private void setActionButtons() {
        buttonTakePicture.setOnClickListener(view -> {
            processPicture();
        });

        buttonGuadarIncidencia.setOnClickListener(view -> {
            if (!editTextDescripcionIncidencia.getText().toString().isEmpty() && uriImagen != null) {
                guadarIncidencia();
            }else {
                Toast.makeText(this, "Ingresa la descripción de la incidencia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guadarIncidencia() {
        //Toast.makeText(this, String.valueOf(idTipoIncidencia) +"/n"+ bitmapImagen, Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, API.URL+"/incidencias", response -> {
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
                    params.put("tipo", String.valueOf(idTipoIncidencia));
                    params.put("descripcion", editTextDescripcionIncidencia.getText().toString());
                    params.put("imagen", imagenBase64);
                    params.put("encode", String.valueOf(true));
                    clearEditText();
                    return new JSONObject(params).toString().getBytes();
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
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/tipos-incidencias", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray data = json.getJSONArray("tipos");
                    setDataArrayListTiposIncidencias(data);
                    tiposIncidenciasSpinnerAdapter = new TiposIncidenciasSpinnerAdapter(
                            AddIncidenciasActivity.this,
                            listaTiposIncidencias
                    );
                    spinnerTiposIncidencias.setAdapter(tiposIncidenciasSpinnerAdapter);
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
    private void setDataArrayListTiposIncidencias(JSONArray data) {
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject tipo = data.getJSONObject(i);

                listaTiposIncidencias.add(new TiposIncidencias(
                        Integer.parseInt(tipo.getString("id")),
                        tipo.getString("nombre")
                ));
            }
        }catch (Exception e) {
            Log.e("Error", "No se pudo recuperar los datos de los tipos de incidencias");
        }
    }

    private void processPicture() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                takePicture();
            }else {
                ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            }
        }else {
            takePicture();
        }
    }

    private File createFile() {
        String nomeclature = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.getDefault()
        ).format(new Date());

        String filePrefix = "INCIDENCIA_PICTURE"+nomeclature+"_";
        File fileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File myImage = null;

        try {
            myImage = File.createTempFile(filePrefix, ".jpg", fileDir);
        }catch (IOException e) {
            e.printStackTrace();
        }

        return myImage;
    }
    /**
     * Using camera for take a picture
     * */
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File filePicture = null;
            filePicture = createFile();

            if (filePicture != null) {
                Uri urlFile = FileProvider.getUriForFile(
                        this,
                        "com.jherrera.incidencias",
                        filePicture
                );
                //guardando la uri global de la imagen para poder compartir por whatsapp o email
                this.uriImagen = urlFile;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, urlFile);
                startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
            }
        }
    }

    public String getBase64FromUri(Uri uri) {
        String base64 = "";
        try {
            Bitmap bitmap= MediaStore.Images.Media.getBitmap(
                    getContentResolver(),
                    uri
            );
            // initialize byte stream
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            // compress Bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream);
            // Initialize byte array
            byte[] bytes=stream.toByteArray();
            // get base64 encoded string
            base64= Base64.encodeToString(bytes,Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "data:image/png;base64,"+base64;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {

        if(requestCode == REQUEST_CODE_CAMERA) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }else {
                Toast.makeText(this, "Se requieren permisos para la camara", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                imageViewTake.setImageURI(uriImagen);
                this.imagenBase64 = getBase64FromUri(uriImagen);
                Log.e("Encode imagen", imagenBase64);
            }
        }
    }

    //metodo para obtener el toquen del usuario
    private void setTokenUser() {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }

    private void clearEditText() {
        editTextDescripcionIncidencia.setText(null);
    }
}