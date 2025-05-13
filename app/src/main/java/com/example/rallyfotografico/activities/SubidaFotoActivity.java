package com.example.rallyfotografico.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rallyfotografico.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SubidaFotoActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101;

    private ImageView imageViewFoto;
    private Button botonSeleccionar, botonSubir;
    private Uri imagenUri;
    private String idParticipante;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subida_foto);

        imageViewFoto = findViewById(R.id.imageViewFoto);
        botonSeleccionar = findViewById(R.id.botonSeleccionarFoto);
        botonSubir = findViewById(R.id.botonSubirFoto);

        db = FirebaseFirestore.getInstance();

        idParticipante = getSharedPreferences("UsuarioPrefs", MODE_PRIVATE)
                .getString("idParticipante", null);

        botonSeleccionar.setOnClickListener(v -> seleccionarImagen());
        botonSubir.setOnClickListener(v -> subirImagen());
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imagenUri = data.getData();

            // Solicitar permiso persistente
            getContentResolver().takePersistableUriPermission(imagenUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            imageViewFoto.setImageURI(imagenUri);
        }
    }

    private void subirImagen() {
        if (imagenUri == null || idParticipante == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream inputStream = getContentResolver().openInputStream(imagenUri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            Map<String, Object> fotoMap = new HashMap<>();
            fotoMap.put("idParticipante", idParticipante);
            fotoMap.put("imagen", imageBase64);
            fotoMap.put("estado", "pendiente");

            db.collection("fotos")
                    .add(fotoMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Foto subida correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al subir la foto", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
