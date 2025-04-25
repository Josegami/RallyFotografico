package com.example.rallyfotografico.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rallyfotografico.R;
import com.example.rallyfotografico.activities.EditarPerfilActivity;
import com.example.rallyfotografico.model.Participante;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ParticipanteAdapter extends RecyclerView.Adapter<ParticipanteAdapter.ViewHolder> {

    private List<Participante> lista;
    private Context context;

    public ParticipanteAdapter(Context context, List<Participante> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_participante, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Participante p = lista.get(position);
        holder.textCorreo.setText(p.getCorreo());

        holder.btnEliminar.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("participantes").document(p.getId())
                    .delete()
                    .addOnSuccessListener(unused -> {
                        lista.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    });
        });
        holder.botonModificar.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditarPerfilActivity.class);
            intent.putExtra("idParticipante", p.getId()); // este ID debe estar en el modelo
            intent.putExtra("modoAdmin", true);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textCorreo;
        Button btnEliminar, botonModificar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCorreo = itemView.findViewById(R.id.tvCorreoParticipante);
            btnEliminar = itemView.findViewById(R.id.btnEliminarParticipante);
            botonModificar = itemView.findViewById(R.id.botonModificar);
        }
    }
}
