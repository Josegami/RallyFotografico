package com.example.rallyfotografico.activities;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private List<Map<String, Object>> listaRanking = new ArrayList<>();
    private LinearLayout top3Container;
    private RecyclerView recyclerResto;
    private RestoAdapter adapter;
    private Button btnVerGrafico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        db = FirebaseFirestore.getInstance();
        top3Container = findViewById(R.id.top3Container);
        recyclerResto = findViewById(R.id.recyclerResto);
        btnVerGrafico = findViewById(R.id.btnVerGrafico);

        recyclerResto.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestoAdapter();
        recyclerResto.setAdapter(adapter);

        btnVerGrafico.setOnClickListener(v -> {
            Intent intent = new Intent(RankingActivity.this, GraficoActivity.class);
            startActivity(intent);
        });

        cargarRanking();
    }

    private void cargarRanking() {
        db.collection("fotos")
                .whereEqualTo("estado", "admitida")
                .get()
                .addOnSuccessListener(snapshot -> {
                    listaRanking.clear();
                    final int totalFotos = snapshot.size();
                    final int[] fotosProcesadas = {0};

                    if (totalFotos == 0) {
                        mostrarTop3();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> foto = new HashMap<>(doc.getData());
                        foto.put("id", doc.getId());

                        String idParticipante = (String) foto.get("idParticipante");

                        if (idParticipante != null) {
                            db.collection("participantes").document(idParticipante)
                                    .get()
                                    .addOnSuccessListener(partDoc -> {
                                        String nombre = partDoc.getString("nombre");
                                        foto.put("nombre", nombre != null ? nombre : "Desconocido");
                                        listaRanking.add(foto);
                                        fotosProcesadas[0]++;
                                        if (fotosProcesadas[0] == totalFotos) {
                                            ordenarYMostrarRanking();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        foto.put("nombre", "Desconocido");
                                        listaRanking.add(foto);
                                        fotosProcesadas[0]++;
                                        if (fotosProcesadas[0] == totalFotos) {
                                            ordenarYMostrarRanking();
                                        }
                                    });
                        } else {
                            foto.put("nombre", "Desconocido");
                            listaRanking.add(foto);
                            fotosProcesadas[0]++;
                            if (fotosProcesadas[0] == totalFotos) {
                                ordenarYMostrarRanking();
                            }
                        }
                    }
                });
    }

    private void ordenarYMostrarRanking() {
        Collections.sort(listaRanking, (a, b) -> {
            Long votosA = (Long) a.get("votos");
            Long votosB = (Long) b.get("votos");
            return Long.compare(votosB != null ? votosB : 0, votosA != null ? votosA : 0);
        });

        mostrarTop3();
        adapter.notifyDataSetChanged();
    }

    private void mostrarTop3() {
        top3Container.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        String[] medallas = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < Math.min(3, listaRanking.size()); i++) {
            Map<String, Object> foto = listaRanking.get(i);
            View vista = inflater.inflate(R.layout.item_top3_foto, top3Container, false);

            ImageView img = vista.findViewById(R.id.imagenTop);
            TextView nombre = vista.findViewById(R.id.nombreTop);
            TextView votos = vista.findViewById(R.id.votosTop);
            TextView medalla = vista.findViewById(R.id.medallaTop);

            String base64 = (String) foto.get("imagen");
            String nombrePart = (String) foto.get("nombre");
            Long votosFoto = (Long) foto.get("votos");

            medalla.setText(medallas[i]);
            nombre.setText(nombrePart != null ? nombrePart : "Participante");
            votos.setText(votosFoto != null ? votosFoto + " votos" : "0 votos");

            if (base64 != null && !base64.isEmpty()) {
                if (base64.contains(",")) base64 = base64.split(",")[1];
                try {
                    byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    img.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("IMG", "Error al cargar imagen top: " + e.getMessage());
                }
            }

            top3Container.addView(vista);
        }

        if (listaRanking.size() > 3) {
            listaRanking = new ArrayList<>(listaRanking.subList(3, listaRanking.size()));
        } else {
            listaRanking.clear();
        }
    }

    class RestoAdapter extends RecyclerView.Adapter<RestoAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resto_foto, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Map<String, Object> foto = listaRanking.get(position);
            String nombre = (String) foto.get("nombre");
            Long votos = (Long) foto.get("votos");
            String base64 = (String) foto.get("imagen");

            holder.nombre.setText(nombre != null ? nombre : "Participante");
            holder.votos.setText(votos != null ? votos + " votos" : "0 votos");

            if (base64 != null && !base64.isEmpty()) {
                if (base64.contains(",")) base64 = base64.split(",")[1];
                try {
                    byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    holder.img.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("IMG", "Error imagen lista: " + e.getMessage());
                }
            }
        }

        @Override
        public int getItemCount() {
            return listaRanking.size();
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView img;
            TextView nombre, votos;

            VH(View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.imgResto);
                nombre = itemView.findViewById(R.id.nombreResto);
                votos = itemView.findViewById(R.id.votosResto);
            }
        }
    }
}
