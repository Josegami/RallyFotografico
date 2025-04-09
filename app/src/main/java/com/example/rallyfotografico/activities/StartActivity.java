package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rallyfotografico.R;

public class StartActivity extends AppCompatActivity {

    private Button buttonStart; // Asegúrate de que el id en el XML sea este (o cámbialo aquí)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start); // Asegúrate que el layout se llama así

        buttonStart = findViewById(R.id.buttonStart); // ID del botón en el XML

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a HomeActivity
                Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Opcional, para que no vuelva atrás
            }
        });
    }

}