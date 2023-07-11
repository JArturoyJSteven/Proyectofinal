/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parking;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author user
 */
public class Login extends JDialog{
    
    JLabel correo, contraseña;
    JTextField ccorreo, ccontraseña;
    JButton aceptar;
    boolean sesion = false;
     public int nivelAcceso;
    
    public Login(Frame D, boolean modal){
        super(D, modal);
        setTitle("Login");
        Container c = getContentPane();
        c.setLayout(null);
        
        correo = new JLabel("Correo");
        correo.setBounds(10, 15, 80, 25);
        ccorreo = new JTextField(15); 
        ccorreo.setBounds(100, 15, 200, 25);
        
        contraseña = new JLabel("Contraseña");
        contraseña.setBounds(10, 45, 80, 25);
        ccontraseña = new JTextField(15); 
        ccontraseña.setBounds(100, 45, 200, 25);
        
        aceptar = new JButton("Aceptar");
        aceptar.setBounds(115, 90, 80, 25);
        
        c.add(correo);
        c.add(ccorreo);
        c.add(contraseña);
        c.add(ccontraseña);
        c.add(aceptar);
        
        aceptar.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String correo = ccorreo.getText();
        String contraseña = ccontraseña.getText();

        // Verificar el inicio de sesión en la base de datos
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String url = "jdbc:mysql://localhost:3306/parqueadero";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);

            String query = "SELECT Nivel_Acceso FROM usuario WHERE Correo = ? AND Contraseña = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, correo);
            statement.setString(2, contraseña);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                 nivelAcceso = resultSet.getInt("Nivel_Acceso");

                sesion = true;

                dispose(); // Cerrar la ventana de inicio de sesión
            } else {
                JOptionPane.showMessageDialog(Login.this, "Credenciales inválidas", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            // Cerrar el ResultSet, PreparedStatement y Connection
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
});

        
        setSize(330, 170);
        setLocationRelativeTo(null);
        setResizable(false);       
        setVisible(true);
    }
}   
