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
    private FirebaseFirestore db; // Instancia para acceder a Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Elimina cualquier consentimiento aceptado anteriormente
        SharedPreferences prefs = getSharedPreferences("configuracion", MODE_PRIVATE);
        prefs.edit().remove("consentimientoAceptado").apply();

        // Inicializa los elementos de la interfaz
        inicializarVista();

        // Inicializa la base de datos Firestore
        db = FirebaseFirestore.getInstance();

        // Configura el botón de información para mostrar los parámetros
        botonInformacion.setOnClickListener(v -> mostrarDialogoParametros());

        // Configura eventos para los botones de login y registro
        configurarEventos();
    }

    // Método para enlazar los botones con sus elementos visuales
    private void inicializarVista() {
        btnRegistro = findViewById(R.id.buttonRegister);
        btnLogin = findViewById(R.id.buttonLogin);
        botonInformacion = findViewById(R.id.buttonInfo);
    }

    // Configura las acciones de los botones de registro y login
    private void configurarEventos() {
        btnRegistro.setOnClickListener(v -> irARegistro());
        btnLogin.setOnClickListener(v -> irALogin());
    }

    // Muestra un cuadro de diálogo con los parámetros actuales del rally
    private void mostrarDialogoParametros() {
        // Crea una vista personalizada para el diálogo
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_parametros_rally, null);

        // Referencias a los elementos del diálogo
        TextView tvPlazoFotos = view.findViewById(R.id.tvPlazoFotos);
        TextView tvLimiteFotos = view.findViewById(R.id.tvLimiteFotos);
        TextView tvPlazoVotacion = view.findViewById(R.id.tvPlazoVotacion);
        TextView tvFormato = view.findViewById(R.id.tvFormato);

        // Crea el diálogo sin botones, solo informativo
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        // Accede a Firestore para obtener los parámetros del rally
        db.collection("parametros_rally").document("configuracion")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extrae datos del documento
                        String plazoRecepcion = documentSnapshot.getString("plazoRecepcion");
                        String plazoVotacion = documentSnapshot.getString("plazoVotacion");
                        Long limiteFotos = documentSnapshot.getLong("limiteFotos");
                        List<String> formatos = (List<String>) documentSnapshot.get("formatosPermitidos");

                        // Asigna los valores a los TextView con verificación
                        tvPlazoFotos.setText("📅 Plazo de recepción: " + (plazoRecepcion != null ? plazoRecepcion : "N/D"));
                        tvPlazoVotacion.setText("🗳️ Plazo de votación: " + (plazoVotacion != null ? plazoVotacion : "N/D"));
                        tvLimiteFotos.setText("📸 Límite de fotos: " + (limiteFotos != null ? limiteFotos.toString() : "N/D"));

                        // Muestra los formatos permitidos como texto plano
                        if (formatos != null && !formatos.isEmpty()) {
                            String formatosTexto = String.join(", ", formatos);
                            tvFormato.setText("✔️ Formatos permitidos: " + formatosTexto);
                        } else {
                            tvFormato.setText("✔️ Formatos permitidos: N/D");
                        }

                        // Muestra el diálogo al usuario
                        dialog.show();
                    } else {
                        // Documento no encontrado
                        Toast.makeText(this, "No se encontraron parámetros", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Error al acceder a la base de datos
                    Toast.makeText(this, "Error al cargar parámetros", Toast.LENGTH_SHORT).show();
                });
    }

    // Navega a la pantalla de registro
    private void irARegistro() {
        startActivity(new Intent(this, RegistroActivity.class));
    }

    // Navega a la pantalla de login
    private void irALogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    // Navega a la pantalla de consentimiento antes de ver la galería
    public void irAGaleria(View view) {
        startActivity(new Intent(this, ConsentimientoActivity.class));
    }
}
