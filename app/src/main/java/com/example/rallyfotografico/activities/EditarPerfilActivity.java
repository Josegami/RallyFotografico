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

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText editTelefono, editFechaNacimiento, editGenero, editCiudad;
    private Button botonGuardar;
    private FirebaseFirestore firestore;
    private String idParticipante; // este ID se debe pasar desde el login o mantenerse en sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        editTelefono = findViewById(R.id.editTextTelefono);
        editFechaNacimiento = findViewById(R.id.editTextFechaNacimiento);
        editGenero = findViewById(R.id.editTextGenero);
        editCiudad = findViewById(R.id.editTextCiudad);
        botonGuardar = findViewById(R.id.botonGuardarCambios);

        firestore = FirebaseFirestore.getInstance();

        idParticipante = getIntent().getStringExtra("idParticipante"); // debes pasarlo al abrir esta actividad

        cargarDatos();

        botonGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void cargarDatos() {
        firestore.collection("participantes").document(idParticipante)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        editTelefono.setText(doc.getString("telefono"));
                        editFechaNacimiento.setText(doc.getString("fechaNacimiento"));
                        editGenero.setText(doc.getString("genero"));
                        editCiudad.setText(doc.getString("ciudad"));
                    }
                });
    }

    private void guardarCambios() {
        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("telefono", editTelefono.getText().toString().trim());
        datosActualizados.put("fechaNacimiento", editFechaNacimiento.getText().toString().trim());
        datosActualizados.put("genero", editGenero.getText().toString().trim());
        datosActualizados.put("ciudad", editCiudad.getText().toString().trim());

        DocumentReference docRef = firestore.collection("participantes").document(idParticipante);
        docRef.update(datosActualizados)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
    }
}
