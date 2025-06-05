package com.example.rallyfotografico.activities;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneralActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<Map<String, Object>> listaFotos = new ArrayList<>();
    private FotoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        recyclerView = findViewById(R.id.recyclerGaleria);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        db = FirebaseFirestore.getInstance();
        adapter = new FotoAdapter(listaFotos);
        recyclerView.setAdapter(adapter);

        cargarFotosAdmitidas();

        LinearLayout generalLayout = findViewById(R.id.generalLayout);


    }

    public void irAVotos(View view) {
        Intent intent = new Intent(this, VotacionActivity.class);
        startActivity(intent);
    }

    public void irARanking(View view) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    private void cargarFotosAdmitidas() {
        db.collection("fotos")
                .whereEqualTo("estado", "admitida")
                .get()
                .addOnSuccessListener(query -> {
                    listaFotos.clear();
                    int totalFotos = query.size();

                    if (totalFotos == 0) {
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    final int[] fotosProcesadas = {0};

                    for (QueryDocumentSnapshot doc : query) {
                        Map<String, Object> datosFoto = doc.getData();
                        String usuarioId = (String) datosFoto.get("idParticipante");

                        if (usuarioId != null) {
                            db.collection("participantes").document(usuarioId)
                                    .get()
                                    .addOnSuccessListener(participanteDoc -> {
                                        String nombre = (String) participanteDoc.get("nombre");
                                        datosFoto.put("nombre", nombre != null ? nombre : "Desconocido");
                                        datosFoto.put("id", doc.getId());

                                        listaFotos.add(datosFoto);
                                        fotosProcesadas[0]++;

                                        if (fotosProcesadas[0] == totalFotos) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FIREBASE", "Error al obtener nombre del participante: " + e.getMessage());
                                        fotosProcesadas[0]++;
                                        if (fotosProcesadas[0] == totalFotos) {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        } else {
                            fotosProcesadas[0]++;
                            if (fotosProcesadas[0] == totalFotos) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("FIREBASE", "Error al obtener fotos admitidas: " + e.getMessage()));
    }


    private class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {
        private final List<Map<String, Object>> fotos;

        FotoAdapter(List<Map<String, Object>> fotos) {
            this.fotos = fotos;
        }

        @NonNull
        @Override
        public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_foto, parent, false);
            return new FotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
            Map<String, Object> foto = fotos.get(position);
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
                    Log.e("DECODIFICAR", "Error al decodificar la imagen: " + e.getMessage());
                }
            }

            holder.nombre.setText(nombre != null ? nombre : "Desconocido");
        }

        @Override
        public int getItemCount() {
            return fotos.size();
        }

        class FotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imagen;
            TextView nombre;

            FotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imagen = itemView.findViewById(R.id.imagenAdmitida);
                nombre = itemView.findViewById(R.id.nombreParticipante);
            }
        }
    }
}
