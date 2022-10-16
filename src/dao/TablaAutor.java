/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelos.Autor;

/**
 *
 * @author Diego
 */
public class TablaAutor {
    private ArrayList<Autor> lista;
    private final Conexion conexion = new Conexion();
    private Connection conn; //Gestiona la conexion
    private PreparedStatement mostrarRegistros;
    private PreparedStatement insertarRegistro;
    private PreparedStatement modificarRegistro;
    private PreparedStatement eliminarRegistro;
    
    public TablaAutor() {
        try {
            conn = conexion.obtenerConexion();
            mostrarRegistros = conn.prepareStatement("Select * from Autor");
            insertarRegistro = conn.prepareStatement("Insert Into Autor(nombrePila,"
                    + " apellidoPaterno) Values(?, ?)");
            modificarRegistro = conn.prepareStatement("Update Autor set nombrePila = ?,"
                    + " apellidoPaterno = ? where idAutor = ?");
            eliminarRegistro = conn.prepareStatement("Delete From Autor where idAutor = ?");
            lista = new ArrayList<>();
            lista = listarRegistro();
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
    
    public ArrayList<Autor> listarRegistro() {
        ArrayList<Autor> result = null;
        ResultSet rs = null;
        try {
            rs = mostrarRegistros.executeQuery();
            result = new ArrayList<>();
            while(rs.next()) {
                result.add(new Autor(
                   rs.getInt("idAutor"),
                   rs.getString("nombrePila"),
                   rs.getString("ApellidoPaterno"),
                   1 //Estado original que viene desde la BD
                ));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch(SQLException ex) {
                conexion.close(conn);
            }
        }
        return result;
    }
    
    public int agregarALista(String nombrePila, String apellidoPaterno) {
        try {
            lista.add(new Autor(0,
                    nombrePila,
                    apellidoPaterno,
                    4 // Estado nuevo registro sin guardar en la BD
            ));
            return 1;
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }
    
    public int editarEnLista(int idAutor, String nombrePila, String apellidoPaterno) {
        try {
            Autor autor = new Autor(
                    idAutor,
                    nombrePila,
                    apellidoPaterno,
                    2 // Estado modificado en la lista, no guardado en la BD
            );
            for(Autor a : lista) {
                if(a.getIdAutor() == autor.getIdAutor()) {
                    a.setNombrePila(autor.getNombrePila());
                    a.setApellidoPaterno(autor.getApellidoPaterno());
                    if(a.getEstado() != 0) a.setEstado(autor.getEstado());
                    return 1;
                }
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }
    
    public int eliminarEnLista(int idAutor) {
        try {
            for(Autor a : lista) {
                if(a.getIdAutor() == idAutor) {
                    a.setEstado(3); //Estado eliminado en la lista
                    //Aun no eliminado en la base de datos
                    return 1;
                }
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return 0;
    }
    
    public int agregarRegistroBD(Autor autor) {
        int result = 0;
        try {
            insertarRegistro.setString(1, autor.getNombrePila());
            insertarRegistro.setString(2, autor.getApellidoPaterno());
            result = insertarRegistro.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace();
            conexion.close(conn);
        }
        return result;
    }
    
    public int modificarRegistroBD(Autor autor) {
        int result = 0;
        try {
            modificarRegistro.setString(1, autor.getNombrePila());
            modificarRegistro.setString(1, autor.getApellidoPaterno());
            modificarRegistro.setInt(3, autor.getIdAutor());
            result = modificarRegistro.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace();
            conexion.close(conn);
        }
        return result;
    }
    
    public int eliminarRegistroBD(Autor autor) {
        int result = 0;
        try {
            eliminarRegistro.setInt(1, autor.getIdAutor());
            result = eliminarRegistro.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace();
            conexion.close(conn);
        }
        return result;
    }
    
    public String actualizarBD() {
        String msn = "";
        String msnError = "Errores en: ";
        int nuevos = 0, modificados = 0, eliminados = 0;
        int errorNuevos = 0, errorModificados = 0, errorEliminados = 0;
        
        for(Autor autor: lista) {
            switch(autor.getEstado()) {
                case 1:
                    //No hacer nada si es original
                    break;
                case 2:
                    //Estado modificado
                    if(this.modificarRegistroBD(autor) != 0) {
                        modificados++;
                    } else {
                        errorModificados++;
                        msnError += "\n - Error al modificar: " + autor.getNombrePila()
                                + "" + autor.getApellidoPaterno();
                    }
                    break;
                case 3:
                    //Estado eliminado
                    if(this.eliminarRegistroBD(autor) != 0) {
                        eliminados++;
                    } else {
                        errorEliminados++;
                        msnError += "\n - Error al eliminar: " + autor.getNombrePila()
                                + " " + autor.getApellidoPaterno();
                    }
                    break;
                case 4:
                    //Estado nuevo registro 
                    if(this.agregarRegistroBD(autor) != 0) {
                        nuevos++;
                    } else {
                        errorNuevos++;
                        msnError += "\n - Error al agregar nuevo registro: "
                                + autor.getNombrePila() + " " + autor.getApellidoPaterno();
                    }
                    break;
                default:
                    msnError += "\n Revise el registro: " + autor.getNombrePila()
                            + " " + autor.getApellidoPaterno();
                    break;
            }
            msn = "Registros guardados: " + nuevos + "\nRegistros editados: " + modificados + 
                    "\nRegistros eliminados: " + eliminados;
            if(!msnError.equals("Errpres en: ")) {
                msn += "\n" + msnError;
            }
            lista = this.listarRegistro();
        }
        return msn;
    }

    public ArrayList<Autor> getLista() {
        return lista;
    }
    
    
    
}
