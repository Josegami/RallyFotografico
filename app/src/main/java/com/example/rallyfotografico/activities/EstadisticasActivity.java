package com.example.rallyfotografico.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rallyfotografico.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class EstadisticasActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView txtParticipantes, txtFotosSubidas, txtTotalVotos, txtPromedio, txtNombreTop, txtVotosTop;
    private ImageView imagenTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        db = FirebaseFirestore.getInstance();

        txtParticipantes = findViewById(R.id.txtParticipantes);
        txtFotosSubidas = findViewById(R.id.txtFotosSubidas);
        txtTotalVotos = findViewById(R.id.txtTotalVotos);
        txtPromedio = findViewById(R.id.txtPromedio);
        txtNombreTop = findViewById(R.id.txtNombreTop);
        txtVotosTop = findViewById(R.id.txtVotosTop);
        imagenTop = findViewById(R.id.imagenTop);

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        CollectionReference participantesRef = db.collection("participantes");
        CollectionReference fotosRef = db.collection("fotos");

        participantesRef.get().addOnSuccessListener(snapshot -> {
            int totalParticipantes = snapshot.size();
            txtParticipantes.setText("Participantes: " + totalParticipantes);

            fotosRef.whereEqualTo("estado", "admitida").get().addOnSuccessListener(fotoSnapshot -> {
                int totalFotos = fotoSnapshot.size();
                int totalVotos = 0;
                Map<String, Object> fotoTop = null;

                for (QueryDocumentSnapshot doc : fotoSnapshot) {
                    Long votos = doc.getLong("votos");
                    totalVotos += votos != null ? votos : 0;

                    if (fotoTop == null || (votos != null && votos > (Long) fotoTop.get("votos"))) {
                        fotoTop = new HashMap<>(doc.getData());
                        fotoTop.put("id", doc.getId());
                    }
                }

                txtFotosSubidas.setText("Fotos subidas: " + totalFotos);
                txtTotalVotos.setText("Total de votos: " + totalVotos);
                float promedio = totalParticipantes == 0 ? 0 : (float) totalFotos / totalParticipantes;
                txtPromedio.setText("Promedio de fotos por participante: " + String.format("%.2f", promedio));

                if (fotoTop != null) {
                    cargarParticipanteYFotoTop(fotoTop);
                }
            }).addOnFailureListener(e ->
                    Log.e("FIREBASE", "Error al cargar fotos: " + e.getMessage())
            );

        }).addOnFailureListener(e ->
                Log.e("FIREBASE", "Error al cargar participantes: " + e.getMessage())
        );
    }

    private void cargarParticipanteYFotoTop(Map<String, Object> fotoTop) {
        String participanteId = (String) fotoTop.get("idParticipante");
        Long votos = (Long) fotoTop.get("votos");
        String imagenBase64 = (String) fotoTop.get("imagen");

        if (participanteId != null) {
            db.collection("participantes").document(participanteId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String nombre = doc.contains("nombre") ? (String) doc.get("nombre") : "Desconocido";
                        txtNombreTop.setText("Participante: " + nombre);
                        txtVotosTop.setText("Votos: " + (votos != null ? votos : 0));

                        if (imagenBase64 != null && !imagenBase64.isEmpty()) {
                            byte[] decodedBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                            imagenTop.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("FIREBASE", "Error al obtener participante top: " + e.getMessage()));
        }
    }
}