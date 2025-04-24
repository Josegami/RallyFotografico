package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private Button btnRegistro, btnLogin, botonInformacion;
    private LinearLayout panelInformacion;
    private TextView tvPlazoFotos, tvLimiteFotos, tvPlazoVotacion;
    private boolean infoVisible = false;

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
                        Long limiteFotos = documentSnapshot.getLong("limiteFotos"); // ⚠️ importante

                        tvPlazoFotos.setText("Plazo de recepción de fotografías: " + plazoRecepcion);
                        tvLimiteFotos.setText("Límite de fotos por participante: " + (limiteFotos != null ? limiteFotos.toString() : "N/D"));
                        tvPlazoVotacion.setText("Plazo permitido de votación: " + plazoVotacion);
                    } else {
                        Toast.makeText(this, "No se encontraron parámetros", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar parámetros", Toast.LENGTH_SHORT).show();
                });
    }


    private void irARegistro() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
