package com.example.rallyfotografico.adapters;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.example.rallyfotografico.model.Fotografia;

import java.util.List;

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.FotoViewHolder> {

    public interface EstadoCallback {
        void onEstadoCambiado(String id, String nuevoEstado);
    }

    private List<Fotografia> lista;
    private EstadoCallback callback;

    public FotosAdapter(List<Fotografia> lista, EstadoCallback callback) {
        this.lista = lista;
        this.callback = callback;
    }

    @NonNull
    @Override
    public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foto_validar, parent, false);
        return new FotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
        Fotografia foto = lista.get(position);
        byte[] imageBytes = Base64.decode(foto.getImagenBase64(), Base64.DEFAULT);
        holder.imagen.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));

        holder.btnAceptar.setOnClickListener(v -> callback.onEstadoCambiado(foto.getId(), "admitida"));
        holder.btnRechazar.setOnClickListener(v -> callback.onEstadoCambiado(foto.getId(), "rechazada"));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class FotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        Button btnAceptar, btnRechazar;

        public FotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagenPendiente);
            btnAceptar = itemView.findViewById(R.id.botonAceptar);
            btnRechazar = itemView.findViewById(R.id.botonRechazar);
        }
    }
}
