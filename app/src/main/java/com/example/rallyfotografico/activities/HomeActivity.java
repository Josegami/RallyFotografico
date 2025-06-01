package com.example.rallyfotografico.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Button btnRegistro, btnLogin, botonInformacion;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("configuracion", MODE_PRIVATE);
        prefs.edit().remove("consentimientoAceptado").apply();

        inicializarVista();

        db = FirebaseFirestore.getInstance();

        botonInformacion.setOnClickListener(v -> mostrarDialogoParametros());

        configurarEventos();
    }

    private void inicializarVista() {
        btnRegistro = findViewById(R.id.buttonRegister);
        btnLogin = findViewById(R.id.buttonLogin);
        botonInformacion = findViewById(R.id.buttonInfo);
    }

    private void configurarEventos() {
        btnRegistro.setOnClickListener(v -> irARegistro());
        btnLogin.setOnClickListener(v -> irALogin());
    }

    private void mostrarDialogoParametros() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_parametros_rally, null);

        TextView tvPlazoFotos = view.findViewById(R.id.tvPlazoFotos);
        TextView tvLimiteFotos = view.findViewById(R.id.tvLimiteFotos);
        TextView tvPlazoVotacion = view.findViewById(R.id.tvPlazoVotacion);
        TextView tvFormato = view.findViewById(R.id.tvFormato);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        db.collection("parametros_rally").document("configuracion")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String plazoRecepcion = documentSnapshot.getString("plazoRecepcion");
                        String plazoVotacion = documentSnapshot.getString("plazoVotacion");
                        Long limiteFotos = documentSnapshot.getLong("limiteFotos");
                        List<String> formatos = (List<String>) documentSnapshot.get("formatosPermitidos");

                        tvPlazoFotos.setText("📅 Plazo de recepción: " + (plazoRecepcion != null ? plazoRecepcion : "N/D"));
                        tvPlazoVotacion.setText("🗳️ Plazo de votación: " + (plazoVotacion != null ? plazoVotacion : "N/D"));
                        tvLimiteFotos.setText("📸 Límite de fotos: " + (limiteFotos != null ? limiteFotos.toString() : "N/D"));

                        if (formatos != null && !formatos.isEmpty()) {
                            String formatosTexto = String.join(", ", formatos);
                            tvFormato.setText("✔️ Formatos permitidos: " + formatosTexto);
                        } else {
                            tvFormato.setText("✔️ Formatos permitidos: N/D");
                        }

                        dialog.show();
                    } else {
                        Toast.makeText(this, "No se encontraron parámetros", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar parámetros", Toast.LENGTH_SHORT).show();
                });
    }

    private void irARegistro() {
        startActivity(new Intent(this, RegistroActivity.class));
    }

    private void irALogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void irAGaleria(View view) {
        startActivity(new Intent(this, ConsentimientoActivity.class));
    }
}
