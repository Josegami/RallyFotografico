package com.example.rallyfotografico;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);

        // Aquí puedes inicializar otras cosas globales si las necesitas en el futuro
    }
}