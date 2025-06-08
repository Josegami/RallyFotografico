package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // Campos de entrada del formulario
    private EditText campoCorreo, campoContrasena;
    private Button botonIniciarSesion;

    // Firebase Authentication y Firestore
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Enlaza con el diseño XML

        // Inicializa Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Revisa si ya hay una sesión activa de participante
        SharedPreferences prefs = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
        boolean sesionParticipanteActiva = prefs.getBoolean("sesionParticipanteActiva", false);

        // Si el usuario autenticado es el administrador, redirige al panel de admin
        if (auth.getCurrentUser() != null) {
            String correo = auth.getCurrentUser().getEmail();
            if (correo != null && correo.equalsIgnoreCase("usuarioAdmin@gmail.com")) {
                startActivity(new Intent(this, AdminActivity.class));
                finish();
                return;
            }
        }

        // Si hay sesión activa como participante, redirige a su pantalla principal
        if (sesionParticipanteActiva) {
            startActivity(new Intent(this, ParticipanteActivity.class));
            finish();
            return;
        }

        // Inicializa vistas del formulario de login
        campoCorreo = findViewById(R.id.editTextCorreo);
        campoContrasena = findViewById(R.id.editTextContrasena);
        botonIniciarSesion = findViewById(R.id.botonLogin);

        // Configura botón para iniciar sesión
        botonIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    // Método principal para el proceso de inicio de sesión
    private void iniciarSesion() {
        String correo = campoCorreo.getText().toString().trim();
        String contrasena = campoContrasena.getText().toString().trim();

        // Validación básica de campos vacíos
        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si es el administrador, autenticar con Firebase Auth
        if (correo.equalsIgnoreCase("usuarioAdmin@gmail.com")) {
            auth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(this, AdminActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Error al iniciar sesión como administrador", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Autenticación manual para participantes (no segura para producción)
            firestore.collection("participantes")
                    .whereEqualTo("correo", correo)
                    .whereEqualTo("contrasena", contrasena) // ⚠️ Alerta: No guardar contraseñas en texto plano
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Usuario encontrado, guardar sesión
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            String idParticipante = doc.getId();

                            SharedPreferences prefs = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("idParticipante", idParticipante)
                                    .putBoolean("sesionParticipanteActiva", true)
                                    .apply();

                            // Redirige a la actividad de participante
                            startActivity(new Intent(this, ParticipanteActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Método para cambiar a la pantalla de registro
    public void irARegistro(View view) {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
}
