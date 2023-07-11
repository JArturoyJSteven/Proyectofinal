/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parking;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ModificarUsuario extends JDialog {

    JLabel codigo, nombre, correo, contraseña, niveles;
    JTextField cajacodigo, cajanombre, cajacorreo, cajacontraseña;
    JComboBox<String> combonivel;
    JButton buscar, modificar, cancelar;
    Connection connection;

    public ModificarUsuario(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Modificar Usuario");
        Container c = getContentPane();
        c.setLayout(null);

        codigo = new JLabel("Cod");
        codigo.setBounds(10, 20, 80, 25);
        cajacodigo = new JTextField(15);
        cajacodigo.setBounds(80, 20, 80, 25);

        buscar = new JButton("Buscar");
        buscar.setBounds(180, 20, 80, 25);

        nombre = new JLabel("Nombre");
        nombre.setBounds(10, 50, 80, 25);
        cajanombre = new JTextField(15);
        cajanombre.setBounds(80, 50, 200, 25);

        correo = new JLabel("Correo");
        correo.setBounds(10, 80, 80, 25);
        cajacorreo = new JTextField(15);
        cajacorreo.setBounds(80, 80, 200, 25);

        contraseña = new JLabel("Contraseña");
        contraseña.setBounds(10, 110, 80, 25);
        cajacontraseña = new JTextField(5);
        cajacontraseña.setBounds(80, 110, 200, 25);

        niveles = new JLabel("Nivel");
        niveles.setBounds(10, 140, 80, 25);
        combonivel = new JComboBox<>();
        combonivel.addItem("1");
        combonivel.addItem("2");
        combonivel.addItem("3");
        combonivel.setBounds(80, 140, 100, 25);

        modificar = new JButton("Modificar");
        modificar.setBounds(55, 200, 80, 25);
        cancelar = new JButton("Cancelar");
        cancelar.setBounds(155, 200, 80, 25);
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        try {
            String url = "jdbc:mysql://localhost:3306/parqueadero";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        buscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int codigo = Integer.parseInt(cajacodigo.getText());
                buscarUsuario(codigo);
            }
        });

        modificar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int codigo = Integer.parseInt(cajacodigo.getText());
                String nombre = cajanombre.getText();
                String correo = cajacorreo.getText();
                String contraseña = cajacontraseña.getText();
                int nivel = Integer.parseInt(combonivel.getSelectedItem().toString());

                if (cajacodigo.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(ModificarUsuario.this, "Debe completar el campo 'Cod'", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty()) {
                    JOptionPane.showMessageDialog(ModificarUsuario.this, "Debe completar todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (correoExiste(correo)) {
                    JOptionPane.showMessageDialog(ModificarUsuario.this, "El correo ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    modificarUsuario(codigo, nombre, correo, contraseña, nivel);
                }
            }
        });

        c.add(codigo);
        c.add(cajacodigo);
        c.add(buscar);
        c.add(nombre);
        c.add(cajanombre);
        c.add(correo);
        c.add(cajacorreo);
        c.add(contraseña);
        c.add(cajacontraseña);
        c.add(niveles);
        c.add(combonivel);
        c.add(modificar);
        c.add(cancelar);

        setSize(300, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    private void buscarUsuario(int codigo) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String query = "SELECT * FROM usuario WHERE Codigo = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, codigo);
            
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String nombre = resultSet.getString("Nombre");
                String correo = resultSet.getString("Correo");
                String contraseña = resultSet.getString("Contraseña");
                int nivel = resultSet.getInt("Nivel_Acceso");
                
                cajanombre.setText(nombre);
                cajacorreo.setText(correo);
                cajacontraseña.setText(contraseña);
                combonivel.setSelectedItem(String.valueOf(nivel));
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
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
    
    private boolean correoExiste(String correo) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean existe = false;
        
        try {
            String query = "SELECT * FROM usuario WHERE Correo = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, correo);
            
            resultSet = statement.executeQuery();
            existe = resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
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
        
        return existe;
    }
    
    private void modificarUsuario(int codigo, String nombre, String correo, String contraseña, int nivel) {
        PreparedStatement statement = null;
        
        try {
            String query = "UPDATE usuario SET Nombre = ?, Correo = ?, Contraseña = ?, Nivel_Acceso = ? WHERE Codigo = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, nombre);
            statement.setString(2, correo);
            statement.setString(3, contraseña);
            statement.setInt(4, nivel);
            statement.setInt(5, codigo);
    
            
            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Usuario modificado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el usuario a modificar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}