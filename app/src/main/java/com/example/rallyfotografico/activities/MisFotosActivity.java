package com.example.rallyfotografico.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MisFotosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FotoAdapter adapter;
    private final List<Map<String, Object>> listaFotos = new ArrayList<>();
    private final List<String> listaDocumentos = new ArrayList<>(); // Guardar IDs de documentos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_fotos);

        recyclerView = findViewById(R.id.recyclerMisFotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new FotoAdapter(listaFotos, listaDocumentos, db);
        recyclerView.setAdapter(adapter);

        String idParticipante = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                .getString("idParticipante", null);

        if (idParticipante != null) {
            cargarMisFotos(idParticipante);
        } else {
            Log.e("MISFOTOS", "ID del participante no encontrado");
        }
    }

    private void cargarMisFotos(String idParticipante) {
        db.collection("fotos")
                .whereEqualTo("idParticipante", idParticipante)
                .get()
                .addOnSuccessListener(query -> {
                    listaFotos.clear();
                    listaDocumentos.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        listaFotos.add(doc.getData());
                        listaDocumentos.add(doc.getId());
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error al obtener mis fotos: " + e.getMessage()));
    }

    private static class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {
        private final List<Map<String, Object>> fotos;
        private final List<String> documentos;
        private final FirebaseFirestore db;

        FotoAdapter(List<Map<String, Object>> fotos, List<String> documentos, FirebaseFirestore db) {
            this.fotos = fotos;
            this.documentos = documentos;
            this.db = db;
        }

        @NonNull
        @Override
        public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_foto_participante, parent, false);
            return new FotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
            Map<String, Object> foto = fotos.get(position);
            String base64 = (String) foto.get("imagen");
            String estado = (String) foto.get("estado");

            if (base64 != null && !base64.isEmpty()) {
                if (base64.contains(",")) {
                    base64 = base64.split(",")[1];
                }

                try {
                    byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    holder.imagen.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("DECODIFICAR", "Error al decodificar imagen: " + e.getMessage());
                }
            }

            holder.estado.setText(estado != null ? estado : "pendiente");

            holder.btnEliminar.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Eliminar foto")
                        .setMessage("¿Estás seguro de que quieres eliminar esta foto?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            int actualPos = holder.getAdapterPosition();
                            String docId = documentos.get(actualPos);

                            db.collection("fotos").document(docId)
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        fotos.remove(actualPos);
                                        documentos.remove(actualPos);
                                        notifyItemRemoved(actualPos);
                                        Toast.makeText(v.getContext(), "Foto eliminada", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(v.getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                                        Log.e("FIRESTORE", "Error eliminando foto: " + e.getMessage());
                                    });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });

        }

        @Override
        public int getItemCount() {
            return fotos.size();
        }

        static class FotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imagen, btnEliminar;
            TextView estado;

            FotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imagen = itemView.findViewById(R.id.imagenParticipante);
                estado = itemView.findViewById(R.id.estadoFoto);
                btnEliminar = itemView.findViewById(R.id.btnEliminar);
            }
        }
    }
}
