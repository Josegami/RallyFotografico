package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText campoCorreo, campoContrasena;
    private Button botonIniciarSesion;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        campoCorreo = findViewById(R.id.editTextCorreo);
        campoContrasena = findViewById(R.id.editTextContrasena);
        botonIniciarSesion = findViewById(R.id.botonLogin);

        botonIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = campoCorreo.getText().toString().trim();
        String contrasena = campoContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correo.equalsIgnoreCase("usuarioAdmin@gmail.com")) {
            // Login del administrador a través de Authentication
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
            // Login de participante validando en Firestore
            firestore.collection("participantes")
                    .whereEqualTo("correo", correo)
                    .whereEqualTo("contrasena", contrasena) // ⚠️ No recomendable en producción
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
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
}
