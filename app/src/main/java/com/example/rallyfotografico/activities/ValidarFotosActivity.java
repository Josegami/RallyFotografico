package com.example.rallyfotografico.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rallyfotografico.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidarFotosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<Map<String, Object>> listaFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_fotos);

        recyclerView = findViewById(R.id.recyclerFotosPendientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        cargarFotosPendientes();
    }

    private void cargarFotosPendientes() {
        db.collection("fotos")
                .whereEqualTo("estado", "pendiente")
                .get()
                .addOnSuccessListener(query -> {
                    listaFotos.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Map<String, Object> datos = doc.getData();
                        datos.put("id", doc.getId());
                        listaFotos.add(datos);
                    }
                    recyclerView.setAdapter(new FotoAdapter(listaFotos));
                });
    }

    private class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {
        private final List<Map<String, Object>> fotos;

        FotoAdapter(List<Map<String, Object>> fotos) {
            this.fotos = fotos;
        }

        @NonNull
        @Override
        public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_foto_validar, parent, false);
            return new FotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
            Map<String, Object> foto = fotos.get(position);
            String base64 = (String) foto.get("imagenBase64");

            Glide.with(holder.imagen.getContext())
                    .asBitmap()
                    .load("data:image/jpeg;base64," + base64)
                    .into(holder.imagen);

            String idFoto = (String) foto.get("id");

            holder.botonAceptar.setOnClickListener(v -> actualizarEstado(idFoto, "admitida", position));
            holder.botonRechazar.setOnClickListener(v -> actualizarEstado(idFoto, "rechazada", position));
        }

        @Override
        public int getItemCount() {
            return fotos.size();
        }

        class FotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imagen;
            Button botonAceptar, botonRechazar;

            FotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imagen = itemView.findViewById(R.id.imagenPendiente);
                botonAceptar = itemView.findViewById(R.id.botonAceptar);
                botonRechazar = itemView.findViewById(R.id.botonRechazar);
            }
        }

        private void actualizarEstado(String id, String nuevoEstado, int posicion) {
            db.collection("fotos").document(id)
                    .update("estado", nuevoEstado)
                    .addOnSuccessListener(unused -> {
                        fotos.remove(posicion);
                        notifyItemRemoved(posicion);
                    });
        }
    }
}
