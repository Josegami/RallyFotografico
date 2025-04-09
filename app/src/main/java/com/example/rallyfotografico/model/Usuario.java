package com.example.rallyfotografico.model;

public class Usuario {
    private String uid;
    private String correo;
    private String rol;

    public Usuario() {
        // Constructor vacío obligatorio para Firebase
    }

    public Usuario(String uid, String correo, String rol) {
        this.uid = uid;
        this.correo = correo;
        this.rol = rol;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
