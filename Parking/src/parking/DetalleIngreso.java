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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

import javax.swing.*;

public class DetalleIngreso extends JDialog {

    JLabel vehiculo, placa, horaentrada, minentrada, nombrecliente;
    JTextField cajavehiculo, cajaplaca, cajahoraentrada, cajaminentrada, cajanombrecliente;
    JButton guardar, cancelar;

    public DetalleIngreso(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Detalle Ingreso");
        Container c = getContentPane();
        c.setLayout(null);

        vehiculo = new JLabel("Vehiculo");
        vehiculo.setBounds(10, 15, 80, 25);
        cajavehiculo = new JTextField(15);
        cajavehiculo.setBounds(120, 15, 160, 25);

        placa = new JLabel("Placa");
        placa.setBounds(10, 45, 80, 25);
        cajaplaca = new JTextField(15);
        cajaplaca.setBounds(120, 45, 160, 25);

        horaentrada = new JLabel("Hora Entrada");
        horaentrada.setBounds(10, 75, 80, 25);
        cajahoraentrada = new JTextField(15);
        cajahoraentrada.setBounds(120, 75, 160, 25);
        cajahoraentrada.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarHora(cajahoraentrada);
            }
        });

        minentrada = new JLabel("Min Entrada");
        minentrada.setBounds(10, 105, 80, 25);
        cajaminentrada = new JTextField(15);
        cajaminentrada.setBounds(120, 105, 160, 25);
        cajaminentrada = new JTextField(15);
        cajaminentrada.setBounds(120, 105, 160, 25);
        cajaminentrada.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarMinutos(cajaminentrada);
            }
        });

        nombrecliente = new JLabel("Nombre Cliente");
        nombrecliente.setBounds(10, 135, 100, 25);
        cajanombrecliente = new JTextField(15);
        cajanombrecliente.setBounds(120, 135, 160, 25);

        guardar = new JButton("Guardar");
        guardar.setBounds(60, 165, 80, 25);
        guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarDetalleIngreso();
            }
        });

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(150, 165, 80, 25);
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        c.add(vehiculo);
        c.add(cajavehiculo);
        c.add(placa);
        c.add(cajaplaca);
        c.add(horaentrada);
        c.add(cajahoraentrada);
        c.add(minentrada);
        c.add(cajaminentrada);
        c.add(nombrecliente);
        c.add(cajanombrecliente);
        c.add(guardar);
        c.add(cancelar);

        setSize(300, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void guardarDetalleIngreso() {
        String vehiculo = cajavehiculo.getText();
        String placa = cajaplaca.getText();
        String horaEntrada = cajahoraentrada.getText();
        String minEntrada = cajaminentrada.getText();
        String tipoVehiculo = "";

        // Obtener el tipo de vehículo según el valor seleccionado en el campo "Vehiculo"
        String opcion = cajavehiculo.getText().trim().toLowerCase();
        if (opcion.equals("carro")) {
            tipoVehiculo = "Carro";
            if (!placa.matches("[A-Z]{3}\\d{3}")) {
                System.out.println("Formato de placa incorrecto. Debe ser en el formato ABC123.");
                return;
            }
        } else if (opcion.equals("moto")) {
            tipoVehiculo = "Moto";
            if (!placa.matches("[A-Z]{3}\\d{2}[A-Z]{1}")) {
                System.out.println("Formato de placa incorrecto. Debe ser en el formato ABC12D.");
                return;
            }
        } else if (opcion.equals("bicicleta")) {
            tipoVehiculo = "Bicicleta";
            if (!placa.matches("\\d{4}")) {
                System.out.println("Formato de placa incorrecto. Debe ser en el formato 1234.");
                return;
            }
        } else {
            System.out.println("Tipo de vehículo no válido.");
            return;
        }

        String nombreCliente = cajanombrecliente.getText();

        // Establecer conexión con la base de datos
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            // Verificar si el nombre de cliente existe en la tabla usuario
            String sql = "SELECT * FROM usuario WHERE Nombre = ? ";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nombreCliente);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // El nombre de cliente existe en la tabla usuario y tiene nivel de acceso 1
                // Obtener el próximo estacionamiento disponible según el tipo de vehículo
                int estacionamiento = Lugar(connection, tipoVehiculo);
                if (estacionamiento == -1) {
                    System.out.println("No hay estacionamientos disponibles para el tipo de vehículo especificado.");
                    return;
                }
                String horaCompleta = String.format("%s:%s", horaEntrada, minEntrada);
                // Insertar los detalles de ingreso en la tabla entrada
                sql = "INSERT INTO entrada (Estacionamiento, Placa, Hora_Entrada,  Tipo_vehiculo, Codigo) VALUES (?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, estacionamiento);
                statement.setString(2, placa);
                statement.setString(3, horaCompleta);
                statement.setString(4, tipoVehiculo);
                statement.setInt(5, resultSet.getInt("Codigo"));

                int filasAfectadas = statement.executeUpdate();
                if (filasAfectadas > 0) {
                    System.out.println("Detalle de ingreso guardado exitosamente.");
                    crearArchivoFormato(vehiculo, estacionamiento, horaEntrada, minEntrada, placa);
                } else {
                    System.out.println("Error al guardar el detalle de ingreso.");
                }
            } else {
                // El nombre de cliente no existe en la base de datos o no tiene nivel de acceso 1
                System.out.println("El nombre de cliente no existe en la base de datos o no tiene nivel de acceso 1.");
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

        dispose();
    }

    private int Lugar(Connection connection, String tipoVehiculo) throws SQLException {
        String sql = "";
        int rangoInicio = 0;
        int rangoFin = 0;

        // Establecer los rangos de estacionamiento según el tipo de vehículo
        if (tipoVehiculo.equals("Carro")) {
            rangoInicio = 1;
            rangoFin = 10;
        } else if (tipoVehiculo.equals("Moto")) {
            rangoInicio = 11;
            rangoFin = 20;
        } else if (tipoVehiculo.equals("Bicicleta")) {
            rangoInicio = 21;
            rangoFin = 25;
        }

        // Verificar si hay estacionamientos disponibles en el rango especificado
        sql = "SELECT COUNT(*) FROM entrada WHERE Estacionamiento BETWEEN ? AND ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, String.valueOf(rangoInicio));
        statement.setString(2, String.valueOf(rangoFin));
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            if (count < (rangoFin - rangoInicio + 1)) {
                // Hay estacionamientos disponibles, buscar el primer estacionamiento libre
                sql = "SELECT Estacionamiento FROM entrada WHERE Estacionamiento BETWEEN ? AND ? ORDER BY Estacionamiento ASC";
                statement = connection.prepareStatement(sql);
                statement.setString(1, String.valueOf(rangoInicio));
                statement.setString(2, String.valueOf(rangoFin));
                resultSet = statement.executeQuery();

                int estacionamientoAnterior = rangoInicio - 1;
                while (resultSet.next()) {
                    int estacionamientoActual = resultSet.getInt("Estacionamiento");
                    if (estacionamientoActual - estacionamientoAnterior > 1) {
                        // Se encontró un estacionamiento libre
                        return estacionamientoAnterior + 1;
                    }
                    estacionamientoAnterior = estacionamientoActual;
                }

                // No se encontró un estacionamiento libre en el rango, asignar el siguiente
                return estacionamientoAnterior + 1;
            } else {
                // No hay estacionamientos disponibles en el rango especificado
                return -1;
            }
        } else {
            // Error al obtener la cantidad de estacionamientos
            return -1;
        }
    }

    public void crearArchivoFormato(String vehiculo, int estacionamiento, String horaEntrada, String minEntrada, String placa) {
        String formato = "%s-%s-%s-H%s-M%s";
        String contenido = String.format(formato, vehiculo.substring(0, 1).toUpperCase(), String.valueOf(estacionamiento), placa.toUpperCase(), horaEntrada, minEntrada);

        String nombreArchivo = placa + "-" + estacionamiento + ".txt"; // Utilizar la placa del vehículo como nombre del archivo

        try {
            FileWriter archivo = new FileWriter(nombreArchivo);
            archivo.write(contenido);
            archivo.close();
            System.out.println("Archivo creado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al crear el archivo.");
            e.printStackTrace();
        }
    }

    private void validarHora(JTextField textField) {
        String horaText = textField.getText();
        if (!horaText.isEmpty()) {
            int hora = Integer.parseInt(horaText);
            if (hora < 0 || hora > 23) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un valor válido para la hora (0-23).", "Error", JOptionPane.ERROR_MESSAGE);
                textField.setText("");
            }
        }
    }

    private void validarMinutos(JTextField textField) {
        String minutosText = textField.getText();
        if (!minutosText.isEmpty()) {
            int minutos = Integer.parseInt(minutosText);
            if (minutos < 0 || minutos > 59) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese un valor válido para los minutos (0-59).", "Error", JOptionPane.ERROR_MESSAGE);
                textField.setText("");
            }
        }
    }
    
    

}
