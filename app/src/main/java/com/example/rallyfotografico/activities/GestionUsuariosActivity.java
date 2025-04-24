package com.example.rallyfotografico.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.example.rallyfotografico.adapters.ParticipanteAdapter;
import com.example.rallyfotografico.model.Participante;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosActivity extends AppCompatActivity {

    private RecyclerView recyclerUsuarios;
    private ParticipanteAdapter adapter;
    private List<Participante> listaParticipantes = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParticipanteAdapter(this, listaParticipantes);
        recyclerUsuarios.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        cargarParticipantes();
    }

    private void cargarParticipantes() {
        db.collection("participantes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaParticipantes.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Participante p = doc.toObject(Participante.class);
                        p.setId(doc.getId());
                        listaParticipantes.add(p);
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
