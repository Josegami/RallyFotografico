package com.example.rallyfotografico.model;

public class Participante {
    private String id;
    private String correo;
    private String contrasena;

    public Participante() {} // Constructor vacío para Firestore

    public Participante(String id, String correo, String contrasena) {
        this.id = id;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getId() { return id; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }

    public void setId(String id) { this.id = id; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}
