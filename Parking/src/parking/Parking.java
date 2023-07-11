/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import BD.*;

public class Parking extends JFrame {

    JMenu BD, login, usuario, vehiculo;
    JMenuItem crearBD, eliminarBD, iniciarsesion, cerrarsesion, crearusuario, modificarusuario, visu_elimusuario,
            ingresarvehiculo, salidavehiculo, buscarregisVehiculo, modificarregisVehiculo, buscarfacVehiculo,
            valorhoraVehiculo;
    int nivelAcceso;

    public Parking() {
        setTitle("Parqueadero");
        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        JMenuBar barra = new JMenuBar();
        setJMenuBar(barra);

        BD = new JMenu("Base de Datos");
        crearBD = new JMenuItem("Crear Base de Datos");
        eliminarBD = new JMenuItem("Eliminar Base de Datos");

        login = new JMenu("Login");
        iniciarsesion = new JMenuItem("Iniciar Sesión");
        cerrarsesion = new JMenuItem("Cerrar Sesión");

        usuario = new JMenu("Usuario");
        crearusuario = new JMenuItem("Crear ");
        modificarusuario = new JMenuItem("Modificar");
        visu_elimusuario = new JMenuItem("Visualizar / Eliminar");

        vehiculo = new JMenu("Vehiculo");
        ingresarvehiculo = new JMenuItem("Ingresar");
        salidavehiculo = new JMenuItem("Salida");
        buscarregisVehiculo = new JMenuItem("Buscar Registro");
        modificarregisVehiculo = new JMenuItem("Modificar Registro");
        buscarfacVehiculo = new JMenuItem("Buscar Factura");
        valorhoraVehiculo = new JMenuItem("Valor Hora");

        barra.add(BD);
        BD.add(crearBD);
        BD.add(eliminarBD);
        barra.add(login);
        login.add(iniciarsesion);
        login.add(cerrarsesion);
        barra.add(usuario);
        usuario.add(crearusuario);
        usuario.add(modificarusuario);
        usuario.add(visu_elimusuario);
        barra.add(vehiculo);
        vehiculo.add(ingresarvehiculo);
        vehiculo.add(salidavehiculo);
        vehiculo.add(buscarregisVehiculo);
        vehiculo.add(modificarregisVehiculo);
        vehiculo.add(buscarfacVehiculo);
        vehiculo.add(valorhoraVehiculo);

        // Item Crear Usuario 
        crearusuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CrearUsuario(null, true);
            }
        });

        modificarusuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(nivelAcceso==1){ModificarUsuario obj = new ModificarUsuario(null, true);}else{System.out.println("No posee permisos para esta funcion");}
            }
        });

        ingresarvehiculo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nivelAcceso == 1 || nivelAcceso == 2) {
                   String mensaje = "INSTRUCCION AL INGRESARVEHICULO\n" +
                    "SI QUIERE VER LOS PUESTOS OCUPADOS POR\n" +
                    "CADA TIPO DE VEHICULO, SELECCIONE QUE TIPO\n" +
                    "DE VEHICULO Y EN MOSTRAR\n" +
                    "PARA VER TODOS LOS PUESTOS, EN MOSTRAR TODO";
                    JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
                    IngresarVehiculo obj = new IngresarVehiculo(Parking.this, true);
                    obj.mostrarVehiculos();
                } else {
                    System.out.println("No tiene permisos para esta funcion");
                }
            }
        });

        salidavehiculo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nivelAcceso == 1 || nivelAcceso == 2) {
                    String mensaje = "PRIMERA INSTRUCCION EN LA CLASE VALIDARSALIDA, \n" +
                                            "\n" +
                    "V= VEHICULO INGRESADO\n" +
                    "P= PUESTO DE VEHICULO INGRESADO\n" +
                    "PLACA = PLACA DE VEHICULO INGRESADO\n" +
                    "HS = HORA DE SALIDA DE VEHICULO\n" +
                    "MS = MINUTO DE SALIDA DE VEHICULO";
                    JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
                    SalidaVehiculo obj = new SalidaVehiculo(null, true);
                    
                    
                } else {
                    System.out.println("No tiene permisos para esta funcion");
                }
            }
        });

        crearBD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nivelAcceso == 0) {
                    CrearBD.main(null);
                } else {
                    JOptionPane.showMessageDialog(Parking.this, "No tiene permiso para esta función", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        eliminarBD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nivelAcceso == 1) {
                    int confirmacion = JOptionPane.showConfirmDialog(Parking.this, "Antes de eliminar la base de datos, debe cerrar sesión. ¿Desea continuar?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        nivelAcceso = 0;
                        EliminarBD.eliminarBaseDeDatos();
                    }
                } else {
                    JOptionPane.showMessageDialog(Parking.this, "No tiene permiso para esta función", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        iniciarsesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Login login = new Login(null, true);
                if (login.sesion) {
                    nivelAcceso = login.nivelAcceso;
                    System.out.println("Nivel de acceso: " + nivelAcceso);
                }
            }
        });

        valorhoraVehiculo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nivelAcceso == 1) {
                    ValorHora valorHora = new ValorHora(null, true);

                } else {
                    JOptionPane.showMessageDialog(Parking.this, "No se ha iniciado sesión", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cerrarsesion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CerrarSesion cerrarSesion = new CerrarSesion(null, true);
                if (cerrarSesion.isConfirmado()) {
                    nivelAcceso = 0;
                    System.out.println("Sesión cerrada");
                    System.out.println("Nivel de acceso: " + nivelAcceso);
                }
            }
        });

        visu_elimusuario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nivelAcceso == 1) {
                    VisualizarEliminar VisualizarEliminar = new VisualizarEliminar(null, true);
                } else {
                    System.out.println("No tiene Permiso parta este funcion");
                }
            }
        });
        buscarfacVehiculo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nivelAcceso == 3 || nivelAcceso == 1) {
                    BuscarFactura BuscarFactura = new BuscarFactura(null, true);
                } else {
                    System.out.println("No tiene Permiso parta este funcion");
                }
            }
        });
        
        buscarregisVehiculo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                    BuscarRegistros obj = new BuscarRegistros(null, true);
               
            }
        });
        modificarregisVehiculo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
               if(nivelAcceso ==1){  ModificarRegistros obj = new ModificarRegistros(null, true);}
               else{System.out.println("No tiene permisos para esta funcion ");}
               
            }
        });
        
        
        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new Parking();
    }
}