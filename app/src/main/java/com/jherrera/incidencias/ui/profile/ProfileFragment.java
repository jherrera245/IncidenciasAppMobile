package com.jherrera.incidencias.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jherrera.incidencias.R;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.controllers.IncidenciasAdapter;
import com.jherrera.incidencias.databinding.FragmentProfileBinding;
import com.jherrera.incidencias.databinding.FragmentUsersBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static String ACCESS_TOKEN;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewEmpleado;
    private TextView textViewCargo;
    private TextView textViewDepartamento;
    private View viewContext;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTokenUser();
        setInitComponents(view);
        getProfile();
    }
    private void setInitComponents(View view) {
        this.viewContext = view;
        textViewName = viewContext.findViewById(R.id.textViewProfileUsername);
        textViewEmail = viewContext.findViewById(R.id.textViewProfileEmail);
        textViewEmpleado = viewContext.findViewById(R.id.textViewProfileEmpleado);
        textViewCargo = viewContext.findViewById(R.id.textViewProfileCargo);
        textViewDepartamento = viewContext.findViewById(R.id.textViewProfileDepartamento);
    }

    private void getProfile() {
        RequestQueue queue = Volley.newRequestQueue(viewContext.getContext());
        try {
            StringRequest request = new StringRequest(Request.Method.GET, API.URL+"/profile", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    textViewName.setText(json.getString("name"));
                    textViewEmail.setText(json.getString("email"));
                    textViewEmpleado.setText(json.getString("nombres") + " "+ json.getString("apellidos"));
                    textViewCargo.setText(json.getString("cargo"));
                    textViewDepartamento.setText(json.getString("departamento"));
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