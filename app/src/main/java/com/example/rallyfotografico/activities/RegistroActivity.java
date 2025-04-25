package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText campoCorreo, campoContrasena;
    private Button botonRegistrar;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        campoCorreo = findViewById(R.id.editTextCorreo);
        campoContrasena = findViewById(R.id.editTextContrasena);
        botonRegistrar = findViewById(R.id.botonLogin);
        firestore = FirebaseFirestore.getInstance();

        botonRegistrar.setOnClickListener(view -> registrarParticipante());
    }

    private void registrarParticipante() {
        String correo = campoCorreo.getText().toString().trim();
        String contrasena = campoContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> participante = new HashMap<>();
        participante.put("correo", correo);
        participante.put("contrasena", contrasena); // Nota: guardar contraseñas sin cifrar no es seguro

        firestore.collection("participantes")
                .add(participante)
                .addOnSuccessListener(documentReference -> {
                    String idParticipante = documentReference.getId(); // <- este es el idFirestore

                    // Guardar en SharedPreferences para reutilizarlo después
                    getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("idParticipante", idParticipante)
                            .apply();

                    Toast.makeText(this, "Participante registrado correctamente", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, FormularioDatosPersonalesActivity.class);
                    intent.putExtra("idParticipante", idParticipante);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });


    }
}
