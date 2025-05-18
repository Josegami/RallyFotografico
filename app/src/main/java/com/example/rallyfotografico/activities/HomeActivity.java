package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.example.rallyfotografico.adapters.FotosAdapter;
import com.example.rallyfotografico.adapters.GaleriaAdapter;
import com.example.rallyfotografico.model.Fotografia;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private Button btnRegistro, btnLogin, botonInformacion;
    private LinearLayout panelInformacion;
    private TextView tvPlazoFotos, tvLimiteFotos, tvPlazoVotacion, tvFormato;
    private boolean infoVisible = false;
    private RecyclerView recyclerCarrusel;
    private GaleriaAdapter galeriaAdapter;
    private List<Fotografia> listaFotos = new ArrayList<>();
    private Map<String, String> mapaUsuarios = new HashMap<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarVista();

        botonInformacion = findViewById(R.id.buttonInfo); // corregido el ID del botón info
        panelInformacion = findViewById(R.id.panel_informacion);
        tvPlazoFotos = findViewById(R.id.tvPlazoFotos);
        tvLimiteFotos = findViewById(R.id.tvLimiteFotos);
        tvPlazoVotacion = findViewById(R.id.tvPlazoVotacion);
        db = FirebaseFirestore.getInstance();

        recyclerCarrusel = findViewById(R.id.recyclerCarrusel);
        recyclerCarrusel.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        galeriaAdapter = new GaleriaAdapter(listaFotos, mapaUsuarios);
        recyclerCarrusel.setAdapter(galeriaAdapter);

        botonInformacion.setOnClickListener(v -> {
            if (!infoVisible) {
                cargarParametrosDesdeFirestore();
                panelInformacion.setVisibility(View.VISIBLE);
            } else {
                panelInformacion.setVisibility(View.GONE);
            }
            infoVisible = !infoVisible;
        });

        configurarEventos();
        //cargarParticipantesYFotos();
    }

    private void inicializarVista() {
        btnRegistro = findViewById(R.id.buttonRegister);
        btnLogin = findViewById(R.id.buttonLogin);
    }

    private void configurarEventos() {
        btnRegistro.setOnClickListener(v -> irARegistro());
        btnLogin.setOnClickListener(v -> irALogin());
    }

    private void cargarParametrosDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("parametros_rally").document("configuracion")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String plazoRecepcion = documentSnapshot.getString("plazoRecepcion");
                        String plazoVotacion = documentSnapshot.getString("plazoVotacion");
                        Long limiteFotos = documentSnapshot.getLong("limiteFotos");
                        String formatoFoto = documentSnapshot.getString("formatosPermitidos");


                        tvPlazoFotos.setText("Plazo de recepción de fotografías: " + plazoRecepcion);
                        tvLimiteFotos.setText("Límite de fotos por participante: " + (limiteFotos != null ? limiteFotos.toString() : "N/D"));
                        tvPlazoVotacion.setText("Plazo permitido de votación: " + plazoVotacion);
                        tvFormato.setText("Formatos permitidos: " + formatoFoto);
                    } else {
                        Toast.makeText(this, "No se encontraron parámetros", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar parámetros", Toast.LENGTH_SHORT).show();
                });
    }

    /*private void cargarParticipantesYFotos() {
        db.collection("participantes")
                .get()
                .addOnSuccessListener(participantesSnapshot -> {
                    for (DocumentSnapshot doc : participantesSnapshot) {
                        String id = doc.getId();
                        String nombre = doc.getString("nombre");
                        if (nombre != null) {
                            mapaUsuarios.put(id, nombre);
                        }
                    }
                    cargarFotosAdmitidas();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error cargando participantes", Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "Error cargando participantes", e);
                });
    }

    private void cargarFotosAdmitidas() {
        db.collection("fotos")
                .whereEqualTo("estado", "admitida")
                .get()
                .addOnSuccessListener(fotosSnapshot -> {
                    listaFotos.clear();
                    for (DocumentSnapshot doc : fotosSnapshot) {
                        Fotografia foto = doc.toObject(Fotografia.class);
                        if (foto != null) {
                            foto.setId(doc.getId());
                            listaFotos.add(foto);
                        }
                    }
                    galeriaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error cargando fotos admitidas", Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "Error cargando fotos", e);
                });
    }
*/


    private void irARegistro() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
