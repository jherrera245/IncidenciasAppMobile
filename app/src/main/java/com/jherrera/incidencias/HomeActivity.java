package com.jherrera.incidencias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.jherrera.incidencias.api.API;
import com.jherrera.incidencias.controllers.IncidenciasAdapter;
import com.jherrera.incidencias.databinding.ActivityHomeBinding;
import com.jherrera.incidencias.models.UserLoged;
import com.jherrera.incidencias.ui.home.HomeFragment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static String ACCESS_TOKEN;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private NavController navController;
    private NavigationView navigationView;
    private UserLoged myUser;
    private TextView textViewUsername;
    private TextView textViewEmail;
    private TextView textViewRol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarHome.toolbar);
        setUser(); //congifiguracion del usuario
        setNavigationDrawer();
        setUserConfiguration();
    }

    public void setNavigationDrawer() {
        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
        View navHeader = navigationView.getHeaderView(0); //inflamos la vista del nav_heade
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_retroalimentacion,
                R.id.nav_users,
                R.id.nav_profile
        ).setOpenableLayout(drawer).build();

        setInitComponentsNavHeader(navHeader);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void setInitComponentsNavHeader(View navHeader) {
        textViewEmail = navHeader.findViewById(R.id.textViewEmail);
        textViewUsername = navHeader.findViewById(R.id.textViewUsername);
        textViewRol = navHeader.findViewById(R.id.textViewRol);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void setUserConfiguration() {
        textViewUsername.setText(myUser.getName());
        textViewEmail.setText(myUser.getEmail());

        if (myUser.getRol() == 1) {
            textViewRol.setText("Admistrador");
            registerToFireBaseTopic();
        }else {
            textViewRol.setText("Empleado");
            navigationView.getMenu().getItem(2).setEnabled(false);
        }

        //cerrando session de usuario
        navigationView.getMenu().findItem(R.id.nav_exit).setOnMenuItemClickListener(
                menuItem -> {
                    if (cerrarSesion()) {
                        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
                        preferences.edit().clear().commit();
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    return false;
                }
        );
    }

    private boolean cerrarSesion() {
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        try {
            StringRequest request = new StringRequest(Request.Method.POST, API.URL+"/logout", response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    Toast.makeText(HomeActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("Error JSON", e.getMessage());
                }
            }, error -> {
                Toast.makeText(HomeActivity.this, "Error petición "+error.getMessage(), Toast.LENGTH_SHORT).show();
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
            return true;
        }catch (Exception e) {
            Toast.makeText(HomeActivity.this, "Error en tiempo de ejecución "+e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void setUser() {
        SharedPreferences preferences = getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);
        myUser = new UserLoged(
                preferences.getString("name", null),
                preferences.getString("email", null),
                preferences.getInt("rol", 0)
        );
    }

    private void registerToFireBaseTopic() {
        Log.d("MainActivity", "Register");
        FirebaseMessaging.getInstance().subscribeToTopic("reporte-incidencia").addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainActivity", "Subscribed Topic");
                    }else {
                        Log.e("Error Topic","Error Task Topic");
                    }
                }
        ).addOnFailureListener(
                e -> {
                    Log.e("Topic error", e.getMessage());
                }
        );
    }
}