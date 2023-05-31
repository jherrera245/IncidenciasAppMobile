package com.jherrera.incidencias.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jherrera.incidencias.ModUsuarioActivity;
import com.jherrera.incidencias.R;
import com.jherrera.incidencias.models.UserLoged;
import com.jherrera.incidencias.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements View.OnClickListener {

    private JSONArray jsonArrayUsers;
    private Context context;
    private View.OnClickListener clickListener;
    private static String ACCESS_TOKEN;
    private UserLoged loged;

    public UsersAdapter(JSONArray jsonArrayUsers, Context context) {
        this.jsonArrayUsers = jsonArrayUsers;
        this.context = context;
        setTokenUser();
    }

    private Users getUser(JSONObject jsonUser) {
        try {
            return new Users(
                    Integer.parseInt(jsonUser.getString("id")),
                    jsonUser.getString("name"),
                    jsonUser.getString("email"),
                    jsonUser.getString("nombres") + " " + jsonUser.getString("apellidos"),
                    Integer.parseInt(jsonUser.getString("is_admin"))
            );
        }catch (JSONException e) {
            Log.e("Error al crear objeto:", e.getMessage());
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if (clickListener != null) {
            clickListener.onClick(view);
        }
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_users_layout, parent, false);
        view.setOnClickListener(this);
        return new UsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject jsonUser = jsonArrayUsers.getJSONObject(position);
            Users user = getUser(jsonUser);

            if (user != null) {
                setInitElementsViewHolder(holder, user);
                setActionButtons(holder, user);
            }
        }catch (JSONException e) {
            Log.e("Error Json", e.getMessage());
        }
    }

    private void setInitElementsViewHolder(ViewHolder holder, Users user) {
        holder.textViewUsername.setText("Username: "+user.getUsername());
        holder.textViewEmail.setText("Email: "+user.getEmail());
        holder.textViewEmpleado.setText("Nombre completo: "+user.getNombreEmpleado());

        if (user.getRol() == 0) {
            holder.textViewRol.setText("Rol: Empleado");
        }

        if (user.getRol() == 1) {
            holder.textViewRol.setText("Rol: Administrador");
        }

        if (loged.getEmail().equals(user.getEmail())) {
            holder.buttonDetalle.setVisibility(View.GONE);
        }
    }

    private void setActionButtons(ViewHolder holder, Users user) {
        holder.buttonDetalle.setOnClickListener(view -> {
            Intent intent = new Intent(context, ModUsuarioActivity.class);
            intent.putExtra("id", user.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (jsonArrayUsers != null) ? jsonArrayUsers.length() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewUsername;
        private TextView textViewEmail;
        private TextView textViewEmpleado;
        private TextView textViewRol;
        private Button buttonDetalle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewEmpleado = itemView.findViewById(R.id.textViewEmpleado);
            textViewRol = itemView.findViewById(R.id.textViewRol);
            buttonDetalle = itemView.findViewById(R.id.buttonDetalles);
        }
    }

    private void setTokenUser() {
        SharedPreferences preferences = context.getSharedPreferences("preferenceSession", Context.MODE_PRIVATE);
        ACCESS_TOKEN = preferences.getString("access_token", null);

        loged = new UserLoged(
                preferences.getString("name", null),
                preferences.getString("email", null),
                preferences.getInt("rol", 0)
        );
    }
}
