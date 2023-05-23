package com.jherrera.incidencias.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jherrera.incidencias.AddIncidenciasActivity;
import com.jherrera.incidencias.R;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.controllers.IncidenciasAdapter;
import com.jherrera.incidencias.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static String ACCESS_TOKEN;
    private RecyclerView recyclerViewIncidencias;
    private JSONArray jsonArrayIncidencias;
    private EditText editTextSearch;
    private FloatingActionButton buttonAddInciencias;
    private View viewContext;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTokenUser();
        setInitComponents(view);
        getIncidencias();
        setEventSearch();
        setActionsButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        getIncidencias();
    }

    private void setActionsButtons() {
        buttonAddInciencias.setOnClickListener(view -> {
            Intent intent = new Intent(viewContext.getContext(), AddIncidenciasActivity.class);
            intent.putExtra("access_token", ACCESS_TOKEN);
            startActivity(intent);
        });
    }

    private void setEventSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getIncidencias();
            }
        });
    }

    private void setInitComponents(View view) {
        this.viewContext = view;
        editTextSearch = viewContext.findViewById(R.id.editTextSearch);
        buttonAddInciencias = viewContext.findViewById(R.id.buttonAddInciencias);
        recyclerViewIncidencias = viewContext.findViewById(R.id.recyclerIncidencias);
        recyclerViewIncidencias.setLayoutManager(new LinearLayoutManager(viewContext.getContext()));
    }

    private void getIncidencias() {
        RequestQueue queue = Volley.newRequestQueue(viewContext.getContext());
        try {
            String searchText = "searchText="+editTextSearch.getText().toString();
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/incidencias?"+searchText, response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject data = json.getJSONObject("incidencias");
                    jsonArrayIncidencias = data.getJSONArray("data");
                    //configurando recycler view
                    IncidenciasAdapter adapter = new IncidenciasAdapter(jsonArrayIncidencias, viewContext.getContext());
                    recyclerViewIncidencias.setAdapter(adapter);
                }catch (Exception e){
                    Log.e("Error JSON", e.getMessage());
                }
            }, error -> {
                Toast.makeText(viewContext.getContext(), "Error petición "+error.getMessage(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(viewContext.getContext(), "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setTokenUser() {
        SharedPreferences preferences = getActivity().getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}