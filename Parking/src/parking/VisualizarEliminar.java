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
import javax.swing.table.*;

public class VisualizarEliminar extends JDialog {

    JLabel usuarios, codigo;
    JComboBox<String> niveles;
    JTextField ccodigo;
    JButton buscar, eliminarseleccionado, cancelar;
    JTable tabla;
    DefaultTableModel modelo;
    Connection connection;

    public VisualizarEliminar(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Eliminar Usuario");
        Container c = getContentPane();
        c.setLayout(new FlowLayout());

        usuarios = new JLabel("Usuarios: ");
        usuarios.setBounds(10, 10, 80, 25);

        niveles = new JComboBox<>();
        niveles.addItem("Todos");
        niveles.addItem("Codigo");
        niveles.addItem("Nivel 1");
        niveles.addItem("Nivel 2");
        niveles.addItem("Nivel 3");
        niveles.setBounds(100, 10, 80, 25);

        codigo = new JLabel("Codigo: ");
        codigo.setBounds(190, 10, 80, 25);

        ccodigo = new JTextField(15);
        ccodigo.setBounds(280, 10, 140, 25);

        buscar = new JButton("Buscar");
        buscar.setBounds(360, 10, 80, 25);

        eliminarseleccionado = new JButton("Eliminar Seleccionado");
        eliminarseleccionado.setBounds(50, 230, 140, 25);

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(200, 230, 100, 25);
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        c.add(usuarios);
        c.add(niveles);
        c.add(codigo);
        c.add(ccodigo);
        c.add(buscar);

        modelo = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) {
                    return Boolean.class;
                }
                return super.getColumnClass(column);
            }
        };
        modelo.addColumn("Codigo");
        modelo.addColumn("Nivel");
        modelo.addColumn("Nombre");
        modelo.addColumn("Correo");
        modelo.addColumn("Contraseña");
        modelo.addColumn("Eliminar");

        tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(5).setCellEditor(tabla.getDefaultEditor(Boolean.class));
        tabla.getColumnModel().getColumn(5).setCellRenderer(tabla.getDefaultRenderer(Boolean.class));
        JScrollPane scroll = new JScrollPane(tabla, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        c.add(scroll);

        c.add(eliminarseleccionado);
        c.add(cancelar);

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
                String seleccion = niveles.getSelectedItem().toString();
                if (seleccion.equals("Todos")) {
                    mostrarUsuarios();
                } else if (seleccion.equals("Codigo")) {
                    int codigo = Integer.parseInt(ccodigo.getText());
                    buscarPorCodigo(codigo);
                } else {
                    int nivel = Integer.parseInt(seleccion.substring(seleccion.length() - 1));
                    buscarPorNivel(nivel);
                }
            }
        });

        eliminarseleccionado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarUsuariosSeleccionados();
                mostrarUsuarios();
            }
        });

        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void mostrarUsuarios() {
        limpiarTabla();

        try {
            String query = "SELECT * FROM usuario";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int codigo = resultSet.getInt("Codigo");
                int nivel = resultSet.getInt("Nivel_Acceso");
                String nombre = resultSet.getString("Nombre");
                String correo = resultSet.getString("Correo");
                String contraseña = resultSet.getString("Contraseña");

                Object[] fila = { codigo, nivel, nombre, correo, contraseña, false };
                modelo.addRow(fila);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void buscarPorCodigo(int codigo) {
        limpiarTabla();

        try {
            String query = "SELECT * FROM usuario WHERE Codigo = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigo);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int nivel = resultSet.getInt("Nivel_Acceso");
                String nombre = resultSet.getString("Nombre");
                String correo = resultSet.getString("Correo");
                String contraseña = resultSet.getString("Contraseña");

                Object[] fila = { codigo, nivel, nombre, correo, contraseña, false };
                modelo.addRow(fila);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void buscarPorNivel(int nivel) {
        limpiarTabla();

        try {
            String query = "SELECT * FROM usuario WHERE Nivel_Acceso = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, nivel);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int codigo = resultSet.getInt("Codigo");
                String nombre = resultSet.getString("Nombre");
                String correo = resultSet.getString("Correo");
                String contraseña = resultSet.getString("Contraseña");

                Object[] fila = { codigo, nivel, nombre, correo, contraseña, false };
                modelo.addRow(fila);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarTabla() {
        int rowCount = modelo.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            modelo.removeRow(i);
        }
    }

    private void eliminarUsuariosSeleccionados() {
        for (int i = modelo.getRowCount() - 1; i >= 0; i--) {
            boolean seleccionado = (boolean) modelo.getValueAt(i, 5);
            if (seleccionado) {
                int codigo = (int) modelo.getValueAt(i, 0);
                eliminarUsuario(codigo);
            }
        }
    }

    private void eliminarUsuario(int codigo) {
        try {
            String query = "DELETE FROM usuario WHERE Codigo = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codigo);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}