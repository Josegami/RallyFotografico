package com.example.rallyfotografico.adapters;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.example.rallyfotografico.model.Fotografia;

import java.util.List;
import java.util.Map;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.GaleriaViewHolder> {

    private final List<Fotografia> lista;
    private final Map<String, String> mapaUsuarios; // idParticipante -> nombre

    public GaleriaAdapter(List<Fotografia> lista, Map<String, String> mapaUsuarios) {
        this.lista = lista;
        this.mapaUsuarios = mapaUsuarios;
    }

    @NonNull
    @Override
    public GaleriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foto_galeria, parent, false);
        return new GaleriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GaleriaViewHolder holder, int position) {
        Fotografia foto = lista.get(position);
        byte[] imageBytes = Base64.decode(foto.getImagenBase64(), Base64.DEFAULT);
        holder.imagen.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

        String nombre = mapaUsuarios.getOrDefault(foto.getIdParticipante(), "participantes");
        holder.nombreUsuario.setText(nombre);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class GaleriaViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombreUsuario;

        public GaleriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagenGaleria);
            nombreUsuario = itemView.findViewById(R.id.usuarioNombre);
        }
    }
}

