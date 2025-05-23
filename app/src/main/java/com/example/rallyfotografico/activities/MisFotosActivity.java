package com.example.rallyfotografico.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MisFotosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FotoAdapter adapter;
    private final List<Map<String, Object>> listaFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_fotos);

        recyclerView = findViewById(R.id.recyclerMisFotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new FotoAdapter(listaFotos);
        recyclerView.setAdapter(adapter);

        // Obtener el ID del participante desde SharedPreferences
        String idParticipante = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                .getString("idParticipante", null);

        if (idParticipante != null) {
            cargarMisFotos(idParticipante);
        } else {
            Log.e("MISFOTOS", "ID del participante no encontrado");
        }
    }

    private void cargarMisFotos(String idParticipante) {
        db.collection("fotos")
                .whereEqualTo("idParticipante", idParticipante)
                .get()
                .addOnSuccessListener(query -> {
                    listaFotos.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        listaFotos.add(doc.getData());
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error al obtener mis fotos: " + e.getMessage()));
    }

    private static class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.FotoViewHolder> {
        private final List<Map<String, Object>> fotos;

        FotoAdapter(List<Map<String, Object>> fotos) {
            this.fotos = fotos;
        }

        @NonNull
        @Override
        public FotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_foto_participante, parent, false);
            return new FotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FotoViewHolder holder, int position) {
            Map<String, Object> foto = fotos.get(position);
            String base64 = (String) foto.get("imagen");

            if (base64 != null && !base64.isEmpty()) {
                if (base64.contains(",")) {
                    base64 = base64.split(",")[1];
                }

                try {
                    byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    holder.imagen.setImageBitmap(bitmap);
                } catch (Exception e) {
                    Log.e("DECODIFICAR", "Error al decodificar imagen: " + e.getMessage());
                }
            }
        }

        @Override
        public int getItemCount() {
            return fotos.size();
        }

        static class FotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imagen;

            FotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imagen = itemView.findViewById(R.id.imagenParticipante); // Usa el ID correcto aquí
            }
        }
    }
}
