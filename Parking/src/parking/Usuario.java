/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parking;

/**
 *
 * @author bynyu
 */
public class Usuario {

    public Usuario(int ultimoCodigo, int nivel, String nombre, String correo, String contraseña) {
    }
    int Nivel, Codigo;
    String Nombre, Correo,Contraseña;

    public int getNivel() {
        return Nivel;
    }

    public int getCodigo() {
        return Codigo;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getContraseña() {
        return Contraseña;
    }

    public void setNivel(int Nivel) {
        this.Nivel = Nivel;
    }

    public void setCodigo(int Codigo) {
        this.Codigo = Codigo;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public void setCorreo(String Correo) {
        this.Correo = Correo;
    }

    public void setContraseña(String Contraseña) {
        this.Contraseña = Contraseña;
    }
    
    
    
}
