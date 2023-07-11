/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parking;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.Border;

public class IngresarVehiculo extends JDialog {

    JLabel vehiculo, codigovehiculo;
    JComboBox<String> combovehiculos;
    JPanel panel;
    JScrollPane scroll;
    JTextField cajacodigoVehiculos;
    JButton mostrar, mostrartodos, validar, cancelar;
    JTextArea area;

    public IngresarVehiculo(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Ingreso Vehiculo");
        Container c = getContentPane();
        c.setLayout(null);

        vehiculo = new JLabel("Vehiculo");
        vehiculo.setBounds(10, 15, 80, 25);

        combovehiculos = new JComboBox<>();
        combovehiculos.setBounds(90, 15, 110, 25);
        combovehiculos.addItem("  ");
        combovehiculos.addItem("Moto");
        combovehiculos.addItem("Carro");
        combovehiculos.addItem("Bicicleta");

        mostrar = new JButton("Mostrar");
        mostrar.setBounds(215, 15, 80, 25);
        mostrar.addActionListener(e -> mostrarVehiculos());

        mostrartodos = new JButton("Mostrar Todo");
        mostrartodos.setBounds(305, 15, 140, 25);

        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 5, 5, 5));
        panel.setBounds(75, 200, 335, 75);

        area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            sb.append(i).append("          ");
        }
        sb.append("\n");
        for (int i = 11; i <= 20; i++) {
            sb.append(i).append("        ");
        }
        sb.append("\n");
        for (int i = 21; i <= 25; i++) {
            sb.append(i).append("        ");
        }
        area.setText(sb.toString());

        scroll = new JScrollPane(area);
        scroll.setBounds(75, 75, 335, 100);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // Eliminar el borde del JScrollPane

codigovehiculo = new JLabel("Click en el botón validar para ingresar");
codigovehiculo.setBounds(10, 215, 300, 25);

validar = new JButton("Validar");
validar.setBounds(315, 215, 85, 25);
        validar.addActionListener(e -> {
            DetalleIngreso obj = new DetalleIngreso(null, true);
        });
        mostrartodos.addActionListener(e -> {
            mostrarTodo();

        });

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(200, 285, 80, 25);
        cancelar.addActionListener(e -> dispose());

        c.add(vehiculo);
        c.add(combovehiculos);
        c.add(mostrar);
        c.add(mostrartodos);
        c.add(scroll);
        c.add(codigovehiculo);
        c.add(validar);
        c.add(cancelar);
        c.add(panel);

        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private boolean esVehiculoRegistrado(int puesto) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            // Consultar la tabla de detalle de ingreso para buscar el vehículo registrado en el puesto dado
            String sql = "SELECT * FROM entrada WHERE Estacionamiento = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, puesto);
            ResultSet resultSet = statement.executeQuery();

            boolean registrado = resultSet.next();

            statement.close();

            return registrado;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public void mostrarTodo() {
        // Limpiar el contenido del TextArea
        area.setText("");

        StringBuilder sb = new StringBuilder();

        // Recorrer los puestos de estacionamiento y obtener la información de cada vehículo
        for (int i = 1; i <= 25; i++) {
            String inicialTipoVehiculo = obtenerInicialTipoVehiculo(i);
            sb.append(i).append(inicialTipoVehiculo);
            sb.append("       ");
            if (i % 10 == 0) {
                sb.append("\n");
            }
        }

        area.setText(sb.toString());
    }

    public void mostrarVehiculos() {
        String opcion = combovehiculos.getSelectedItem().toString();
        String tipoVehiculo = "";

        if (opcion.equals("Moto")) {
            tipoVehiculo = "M";
        } else if (opcion.equals("Carro")) {
            tipoVehiculo = "C";
        } else if (opcion.equals("Bicicleta")) {
            tipoVehiculo = "B";
        }

        // Limpiar el contenido del TextArea
        area.setText("");

        StringBuilder sb = new StringBuilder();

        // Recorrer los puestos de estacionamiento y construir el contenido del TextArea
        for (int i = 1; i <= 25; i++) {
            String inicialTipoVehiculo = obtenerInicialTipoVehiculo(i);
            if (inicialTipoVehiculo.equals(tipoVehiculo)) {
                sb.append(i).append(inicialTipoVehiculo);
            } else {
                sb.append(i).append("");
            }
            sb.append("       ");
            if (i % 10 == 0) {
                sb.append("\n");
            }
        }

        area.setText(sb.toString());
    }

    private String obtenerInicialTipoVehiculo(int puesto) {
        String inicial = "";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            // Consultar la tabla de detalle de ingreso para buscar el vehículo registrado en el puesto dado
            String sql = "SELECT Tipo_vehiculo FROM entrada WHERE Estacionamiento = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, puesto);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String tipoVehiculo = resultSet.getString("Tipo_vehiculo");
                inicial = String.valueOf(tipoVehiculo.charAt(0)).toUpperCase(); // Obtener la inicial del tipo de vehículo
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return inicial;
    }
    

}
