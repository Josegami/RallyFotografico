package com.example.rallyfotografico.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ParametrosActivity extends AppCompatActivity {

    private EditText campoPlazoRecepcion, campoLimiteFotos, campoPlazoVotacion;
    private Button botonGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametros);

        campoPlazoRecepcion = findViewById(R.id.editTextPlazoRecepcion);
        campoLimiteFotos = findViewById(R.id.editTextLimiteFotos);
        campoPlazoVotacion = findViewById(R.id.editTextPlazoVotacion);
        botonGuardar = findViewById(R.id.botonGuardarParametros);

        db = FirebaseFirestore.getInstance();

        botonGuardar.setOnClickListener(v -> guardarParametros());
    }

    private void guardarParametros() {
        String plazoRecepcion = campoPlazoRecepcion.getText().toString();
        String limiteFotos = campoLimiteFotos.getText().toString();
        String plazoVotacion = campoPlazoVotacion.getText().toString();

        if (plazoRecepcion.isEmpty() || limiteFotos.isEmpty() || plazoVotacion.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("plazoRecepcion", plazoRecepcion);
        parametros.put("limiteFotos", Integer.parseInt(limiteFotos));
        parametros.put("plazoVotacion", plazoVotacion);

        DocumentReference docRef = db.collection("parametros_rally").document("configuracion");
        docRef.set(parametros)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Parámetros guardados", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}
