package com.example.rallyfotografico.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VotacionActivity extends AppCompatActivity {

    private RecyclerView recyclerVotos;
    private FirebaseFirestore db;
    private List<Map<String, Object>> listaFotos = new ArrayList<>();
    private VotoAdapter adapter;
    private int votosDisponibles = 5;
    private SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votacion);

        preferencias = getSharedPreferences("PreferenciasVotos", MODE_PRIVATE);
        votosDisponibles = preferencias.getInt("votosDisponibles", 5);

        recyclerVotos = findViewById(R.id.recyclerVotos);
        recyclerVotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        db = FirebaseFirestore.getInstance();
        adapter = new VotoAdapter();
        recyclerVotos.setAdapter(adapter);

        cargarFotosAdmitidas();
    }

    private void cargarFotosAdmitidas() {
        db.collection("fotos")
                .whereEqualTo("estado", "admitida")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaFotos.clear();
                    int total = snapshot.size();
                    final int[] procesadas = {0};

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> datosFoto = new HashMap<>(doc.getData());
                        datosFoto.put("id", doc.getId());

                        if (!datosFoto.containsKey("votos")) {
                            datosFoto.put("votos", 0L); // Evitar crash si el campo no existe
                        }

                        String participanteId = (String) datosFoto.get("idParticipante");

                        if (participanteId != null) {
                            db.collection("participantes").document(participanteId)
                                    .get()
                                    .addOnSuccessListener(partDoc -> {
                                        String nombre = (String) partDoc.get("nombre");
                                        datosFoto.put("nombre", nombre != null ? nombre : "Desconocido");
                                        listaFotos.add(datosFoto);
                                        if (++procesadas[0] == total) adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FIRESTORE", "Error al obtener participante", e);
                                        if (++procesadas[0] == total) adapter.notifyDataSetChanged();
                                    });
                        } else {
                            datosFoto.put("nombre", "Desconocido");
                            listaFotos.add(datosFoto);
                            if (++procesadas[0] == total) adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar fotos", Toast.LENGTH_SHORT).show());
    }

    class VotoAdapter extends RecyclerView.Adapter<VotoAdapter.VotoViewHolder> {

        @NonNull
        @Override
        public VotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_voto_foto, parent, false);
            return new VotoViewHolder(vista);
        }

        @Override
        public void onBindViewHolder(@NonNull VotoViewHolder holder, int position) {
            Map<String, Object> foto = listaFotos.get(position);
            String base64 = (String) foto.get("imagen");
            String nombre = (String) foto.get("nombre");

            if (base64 != null && !base64.isEmpty()) {
                if (base64.contains(",")) {
                    base64 = base64.split(",")[1];
                }
                try {
                    byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    holder.imagen.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("IMAGEN", "Error al decodificar imagen: " + e.getMessage());
                }
            } else {
                holder.imagen.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            holder.nombreUsuario.setText(nombre);

            holder.btnVotar.setOnClickListener(v -> {
                if (votosDisponibles > 0) {
                    String idFoto = (String) foto.get("id");
                    Long votosActuales = (Long) foto.get("votos");

                    if (votosActuales == null) votosActuales = 0L;

                    int nuevosVotos = votosActuales.intValue() + 1;

                    DocumentReference fotoRef = db.collection("fotos").document(idFoto);
                    fotoRef.update("votos", nuevosVotos)
                            .addOnSuccessListener(unused -> {
                                foto.put("votos", (long) nuevosVotos);
                                votosDisponibles--;

                                // Guardar votos en preferencias
                                preferencias.edit().putInt("votosDisponibles", votosDisponibles).apply();

                                Toast.makeText(VotacionActivity.this, "Voto registrado. Votos restantes: " + votosDisponibles, Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(VotacionActivity.this, "Error al votar", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(VotacionActivity.this, "Ya has usado tus 5 votos", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return listaFotos.size();
        }

        class VotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imagen;
            TextView nombreUsuario;
            Button btnVotar;

            public VotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imagen = itemView.findViewById(R.id.imagenFoto);
                nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
                btnVotar = itemView.findViewById(R.id.btnVotar);
            }
        }
    }
}
