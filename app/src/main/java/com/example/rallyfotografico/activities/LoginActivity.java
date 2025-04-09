package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText campoCorreo, campoContrasena;
    private Button botonLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoCorreo = findViewById(R.id.editTextCorreo);
        campoContrasena = findViewById(R.id.editTextContrasena);
        botonLogin = findViewById(R.id.botonLogin);
        auth = FirebaseAuth.getInstance();

        FirebaseUser usuario = auth.getCurrentUser();
        /*if (usuario != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference referenciaUsuario = db.collection("usuarios").document(usuario.getUid());

            referenciaUsuario.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("rol");

                            if ("admin".equals(rol)) {
                                // Ir a pantalla de administrador
                                Intent intent = new Intent(this, AdminActivity.class);
                                startActivity(intent);
                                finish();
                            } else if ("participante".equals(rol)) {
                                // Ir a pantalla de participante
                                Intent intent = new Intent(this, ParticipanteActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al verificar el rol: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }*/


        botonLogin.setOnClickListener(view -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = campoCorreo.getText().toString().trim();
        String contrasena = campoContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
}