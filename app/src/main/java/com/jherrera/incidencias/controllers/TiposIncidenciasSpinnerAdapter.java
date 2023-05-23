package com.jherrera.incidencias.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jherrera.incidencias.R;
import com.jherrera.incidencias.models.TiposIncidencias;

import java.util.ArrayList;

public class TiposIncidenciasSpinnerAdapter extends ArrayAdapter<TiposIncidencias> {

    public TiposIncidenciasSpinnerAdapter(@NonNull Context context, ArrayList<TiposIncidencias> listaTipos) {
        super(context, 0, listaTipos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable
    View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
    View convertView, @NonNull ViewGroup parent)
    {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent)
    {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tipos_incidencias_spinner_layout, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textViewLayoutSpinnerNombreTipo);
        TiposIncidencias item = getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (item != null) {
            textViewName.setText(item.getNombreTipoIncidencia());
        }
        return convertView;
    }
}
