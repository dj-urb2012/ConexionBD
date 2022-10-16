/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author Diego
 */
public class Autor {
    private int idAutor;
    private String nombrePila;
    private String apellidoPaterno;
    private int estado;

    public Autor() {
    }

    public Autor(int idAutor, String nombrePila, String apellidoPaterno, int estado) {
        this.idAutor = idAutor;
        this.nombrePila = nombrePila;
        this.apellidoPaterno = apellidoPaterno;
        this.estado = estado;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public String getNombrePila() {
        return nombrePila;
    }

    public void setNombrePila(String nombrePila) {
        this.nombrePila = nombrePila;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
