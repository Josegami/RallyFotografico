package com.example.rallyfotografico.model;

public class Participante {
    private String id;
    private String correo;
    private String contrasena;
    private String nombre;
    private String telefono;
    private String fechaNacimiento;
    private String genero;

    public Participante() {} // Constructor vacío para Firestore

    public Participante(String id, String correo, String contrasena, String nombre, String telefono, String fechaNacimiento, String genero) {
        this.id = id;
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
    }

    public String getId() { return id; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero() { return genero; }

    public void setId(String id) { this.id = id; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setGenero(String genero) { this.genero = genero; }
}
