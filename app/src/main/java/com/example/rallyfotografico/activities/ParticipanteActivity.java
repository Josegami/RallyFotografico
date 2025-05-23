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

public class ParticipanteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_participante);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void editar(View view) {
        String idFirestore = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                .getString("idParticipante", null);

        if (idFirestore != null) {
            Intent intent = new Intent(this, EditarPerfilActivity.class);
            intent.putExtra("idParticipante", idFirestore);
            startActivity(intent);
        }
    }

    public void subirFoto(View view) {
        Intent intent = new Intent(this, SubidaFotoActivity.class);
        startActivity(intent);
    }

    public void verMisFotos(View view){
        String idFirestore = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                .getString("idParticipante", null);

        if (idFirestore != null) {
            Intent intent = new Intent(this, MisFotosActivity.class);
            intent.putExtra("idParticipante", idFirestore);
            startActivity(intent);
        }
    }


    public void cerrarSesion(View view) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que deseas cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                            .edit()
                            .remove("idParticipante")
                            .apply();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}
