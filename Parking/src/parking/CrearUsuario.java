/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parking;

/**
 *
 * @author pana
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import javax.swing.*;

public class CrearUsuario extends JDialog {

    JLabel nombre, correo, contraseña, niveles;
    JTextField cajanombre, cajacorreo, cajacontraseña;
    JComboBox<String> combonivel;
    JButton crear, cancelar;
    Connection connection;

    public CrearUsuario(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Crear Usuario");
        Container c = getContentPane();
        c.setLayout(null);

        nombre = new JLabel("Nombre");
        nombre.setBounds(10, 10, 80, 25);
        cajanombre = new JTextField(15);
        cajanombre.setBounds(80, 10, 200, 25);

        correo = new JLabel("Correo");
        correo.setBounds(10, 40, 80, 25);
        cajacorreo = new JTextField(15);
        cajacorreo.setBounds(80, 40, 200, 25);

        contraseña = new JLabel("Contraseña");
        contraseña.setBounds(10, 70, 80, 25);
        cajacontraseña = new JTextField(5);
        cajacontraseña.setBounds(80, 70, 200, 25);

        niveles = new JLabel("Nivel");
        niveles.setBounds(10, 110, 80, 25);
        combonivel = new JComboBox<>();
        combonivel.addItem("1");
        combonivel.addItem("2");
        combonivel.addItem("3");
        combonivel.setBounds(80, 110, 100, 25);

        crear = new JButton("Crear");
        crear.setBounds(55, 200, 80, 25);

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(155, 200, 80, 25);

        c.add(nombre);
        c.add(cajanombre);
        c.add(correo);
        c.add(cajacorreo);
        c.add(contraseña);
        c.add(cajacontraseña);
        c.add(niveles);
        c.add(combonivel);
        c.add(crear);
        c.add(cancelar);

        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Establecer la conexión a la base de datos
        try {
            String url = "jdbc:mysql://localhost:3306/parqueadero";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        crear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = cajanombre.getText();
                String correo = cajacorreo.getText();
                String contraseña = cajacontraseña.getText();
                int nivel = Integer.parseInt(combonivel.getSelectedItem().toString());

                // Verificar si ya existen 3 usuarios de nivel 1
                PreparedStatement statement = null;
                ResultSet resultSet = null;

                try {
                    String countQuery = "SELECT COUNT(*) AS count FROM usuario WHERE Nivel_Acceso = ?";
                    statement = connection.prepareStatement(countQuery);
                    statement.setInt(1, 1);
                    resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        int countNivel1 = resultSet.getInt("count");
                        if (countNivel1 >= 3 && nivel == 1) {
                            // Se alcanzó el límite de usuarios de nivel 1
                            JOptionPane.showMessageDialog(CrearUsuario.this, "Ya no se pueden crear más usuarios de nivel 1", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    // Verificar si ya existe un usuario con el mismo nombre, correo y contraseña
                    String query = "SELECT * FROM usuario WHERE Correo = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, correo);
                    resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        // El usuario ya existe, mostrar mensaje de error
                        JOptionPane.showMessageDialog(CrearUsuario.this, "El usuario ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // El usuario no existe, insertar los datos en la base de datos
                        String insertQuery = "INSERT INTO usuario (Nombre, Correo, Contraseña, Nivel_Acceso) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                        insertStatement.setString(1, nombre);
                        insertStatement.setString(2, correo);
                        insertStatement.setString(3, contraseña);
                        insertStatement.setInt(4, nivel);
                        insertStatement.executeUpdate();
                        System.out.println("Usuario creado correctamente");
                        dispose();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    // Cerrar el ResultSet y el PreparedStatement
                    try {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                        if (statement != null) {
                            statement.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setSize(300, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

}
