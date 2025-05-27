package com.example.rallyfotografico.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;

public class ConsentimientoActivity extends AppCompatActivity {

    private CheckBox checkBases, checkDerechos;
    private Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Si ya aceptó antes, ir directamente a la galería
        SharedPreferences prefs = getSharedPreferences("configuracion", Context.MODE_PRIVATE);
        if (prefs.getBoolean("consentimientoAceptado", false)) {
            startActivity(new Intent(this, GeneralActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_consentimiento);

        checkBases = findViewById(R.id.checkBases);
        checkDerechos = findViewById(R.id.checkDerechos);
        btnContinuar = findViewById(R.id.btnContinuar);

        btnContinuar.setOnClickListener(v -> {
            if (checkBases.isChecked() && checkDerechos.isChecked()) {
                // Guardar que ya aceptó
                prefs.edit().putBoolean("consentimientoAceptado", true).apply();

                // Ir a la galería
                startActivity(new Intent(this, GeneralActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Debes aceptar ambas condiciones", Toast.LENGTH_SHORT).show();
            }
        });
    }
}