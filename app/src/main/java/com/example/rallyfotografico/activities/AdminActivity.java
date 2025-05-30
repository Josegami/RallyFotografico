package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rallyfotografico.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    public void parametros(View view){
        Intent intent = new Intent(this, ParametrosActivity.class);
        startActivity(intent);
    }

    public void usuarios(View view){
        Intent intent = new Intent(this, GestionUsuariosActivity.class);
        startActivity(intent);
    }

    public void validarFotos(View view){
        startActivity(new Intent(this, ValidarFotosActivity.class));
    }

    public void estadisticas(View view){
        startActivity(new Intent(this, EstadisticasActivity.class));
    }

}