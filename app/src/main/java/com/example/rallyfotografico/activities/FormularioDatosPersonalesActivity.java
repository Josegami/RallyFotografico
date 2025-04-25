package com.example.rallyfotografico.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormularioDatosPersonalesActivity extends AppCompatActivity {

    private EditText editTextNombre, editTextTelefono, editTextNacimiento;
    private Spinner spinnerGenero;
    private Button botonGuardar;
    private FirebaseFirestore firestore;
    private String idParticipante;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_datos_personales);

        spinnerGenero = findViewById(R.id.spinnerGenero);

        // Lista de opciones para el Spinner
        String[] generos = {"Hombre", "Mujer", "Otro"};

        // Adaptador para mostrar las opciones
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                generos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapter);


        // Referencias UI
        editTextNombre = findViewById(R.id.editNombre);
        editTextTelefono = findViewById(R.id.editTelefono);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        editTextNacimiento = findViewById(R.id.editFechaNacimiento);
        botonGuardar = findViewById(R.id.botonGuardar);

        // Firestore
        firestore = FirebaseFirestore.getInstance();

        // Obtener ID del participante
        idParticipante = getIntent().getStringExtra("idParticipante");
        if (idParticipante == null || idParticipante.isEmpty()) {
            idParticipante = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                    .getString("idParticipante", null);
        }

        if (idParticipante == null) {
            Toast.makeText(this, "Error: no se encontró el ID del participante", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        botonGuardar.setOnClickListener(v -> guardarDatosPersonales());
    }

    private void guardarDatosPersonales() {
        String nombre = editTextNombre.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();
        String genero = spinnerGenero.getSelectedItem().toString();
        String nacimiento = editTextNacimiento.getText().toString().trim();

        if (nombre.isEmpty() || telefono.isEmpty() || genero.isEmpty() || nacimiento.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("nombre", nombre);
        datos.put("telefono", telefono);
        datos.put("genero", genero);
        datos.put("fechaNacimiento", nacimiento);

        firestore.collection("participantes")
                .document(idParticipante)
                .update(datos)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Datos personales guardados", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
