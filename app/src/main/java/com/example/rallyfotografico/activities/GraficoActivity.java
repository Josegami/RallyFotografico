package com.example.rallyfotografico.activities;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.rallyfotografico.R;
import com.example.rallyfotografico.model.Fotografia;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraficoActivity extends AppCompatActivity {

    private LinearLayout layoutBarras;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);

        layoutBarras = findViewById(R.id.layoutBarras);
        db = FirebaseFirestore.getInstance();

        mostrarGrafico();
    }

    private void mostrarGrafico() {
        db.collection("fotos")
                .whereEqualTo("estado", "admitida")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Map<String, Object>> listaFotos = new ArrayList<>();
                    final int totalFotos = snapshot.size();
                    final int[] fotosProcesadas = {0};

                    if (totalFotos == 0) return;

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Map<String, Object> datosFoto = new HashMap<>(doc.getData());
                        String usuarioId = (String) datosFoto.get("idParticipante");
                        int votos = ((Long) datosFoto.getOrDefault("votos", 0L)).intValue();

                        if (usuarioId != null) {
                            db.collection("participantes").document(usuarioId)
                                    .get()
                                    .addOnSuccessListener(participanteDoc -> {
                                        String nombre = participanteDoc.getString("nombre");
                                        datosFoto.put("nombre", nombre != null ? nombre : "Desconocido");
                                        datosFoto.put("votos", votos);
                                        listaFotos.add(datosFoto);
                                        fotosProcesadas[0]++;

                                        if (fotosProcesadas[0] == totalFotos) {
                                            procesarGrafico(listaFotos);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        datosFoto.put("nombre", "Desconocido");
                                        datosFoto.put("votos", votos);
                                        listaFotos.add(datosFoto);
                                        fotosProcesadas[0]++;
                                        if (fotosProcesadas[0] == totalFotos) {
                                            procesarGrafico(listaFotos);
                                        }
                                    });
                        } else {
                            datosFoto.put("nombre", "Desconocido");
                            datosFoto.put("votos", votos);
                            listaFotos.add(datosFoto);
                            fotosProcesadas[0]++;
                            if (fotosProcesadas[0] == totalFotos) {
                                procesarGrafico(listaFotos);
                            }
                        }
                    }
                });
    }


    private void agregarBarra(String nombre, int votos, int maxVotos, String base64Imagen) {
        LinearLayout barraLayout = new LinearLayout(this);
        barraLayout.setOrientation(LinearLayout.HORIZONTAL);
        barraLayout.setPadding(8, 8, 8, 8);
        barraLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_barra));
        barraLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 8, 0, 8);
        barraLayout.setLayoutParams(layoutParams);

        // Miniatura
        ImageView miniatura = new ImageView(this);
        miniatura.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        miniatura.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (base64Imagen != null && !base64Imagen.isEmpty()) {
            byte[] decodedBytes = Base64.decode(base64Imagen, Base64.DEFAULT);
            miniatura.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        } else {
            miniatura.setImageResource(R.drawable.imagen_por_defecto); // imagen por defecto
        }

        // Contenedor de nombre y barra de votos
        LinearLayout contenidoLayout = new LinearLayout(this);
        contenidoLayout.setOrientation(LinearLayout.VERTICAL);
        contenidoLayout.setPadding(16, 0, 0, 0);
        contenidoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        // Nombre + emoji si muchos votos
        TextView nombreView = new TextView(this);
        nombreView.setTextSize(16);
        String emoji = votos >= 10 ? "🔥" : votos >= 5 ? "✨" : "";
        nombreView.setText(nombre + " " + emoji);
        nombreView.setTypeface(null, Typeface.BOLD);

        // Barra visual de votos
        View barra = new View(this);
        int barraAncho = (int) (400f * votos / (float) maxVotos); // proporcional
        LinearLayout.LayoutParams barraParams = new LinearLayout.LayoutParams(
                barraAncho, 20);
        barraParams.setMargins(0, 8, 0, 0);
        barra.setLayoutParams(barraParams);
        barra.setBackgroundColor(Color.parseColor("#FF6F61")); // color coral

        // Votos al final
        TextView votosView = new TextView(this);
        votosView.setText(votos + " votos");
        votosView.setTextSize(14);
        votosView.setTextColor(Color.DKGRAY);

        contenidoLayout.addView(nombreView);
        contenidoLayout.addView(barra);
        contenidoLayout.addView(votosView);

        barraLayout.addView(miniatura);
        barraLayout.addView(contenidoLayout);

        layoutBarras.addView(barraLayout);
    }


    private void procesarGrafico(List<Map<String, Object>> listaFotos) {
        int maxVotos = 1;
        for (Map<String, Object> foto : listaFotos) {
            int votos = (Integer) foto.get("votos");
            if (votos > maxVotos) maxVotos = votos;
        }

        for (Map<String, Object> foto : listaFotos) {
            String nombre = (String) foto.get("nombre");
            int votos = (Integer) foto.get("votos");
            String imagenBase64 = (String) foto.get("imagen");
            agregarBarra(nombre, votos, maxVotos, imagenBase64);
        }
    }


}

