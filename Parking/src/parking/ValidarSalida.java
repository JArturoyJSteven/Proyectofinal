/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parking;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import java.sql.*;

/**
 *
 * @author pana
 */
public class ValidarSalida extends JDialog {

    JLabel vehiculo, nombrecliente, lbplaca, horaentrada, minentrada, horasalida, minsalida, valorhora, horas, totalpagar;
    JTextField cajavehiculo, cajanombrecliente, cajaplaca, cajahoraentrada, cajaminentrada, cajahorasalida, cajaminsalida, cajavalorhora, cajahoras, cajatotalpagar;
    JButton guardar, cancelar;

    private String placa;
    private int horaSalida;
    private int minutoSalida;
    private double valorHora;
    private double duracionHoras;

    public ValidarSalida(Frame D, boolean modal, String placa, int horaSalida, int minutoSalida) {
        super(D, modal);
        setTitle("Validar Salida");
        Container c = getContentPane();
        c.setLayout(null);

        this.placa = placa;
        this.horaSalida = horaSalida;
        this.minutoSalida = minutoSalida;

        vehiculo = new JLabel("Vehiculos");
        vehiculo.setBounds(10, 15, 100, 25);
        cajavehiculo = new JTextField(15);
        cajavehiculo.setBounds(120, 15, 160, 25);
        cajavehiculo.setText(obtenerTipoVehiculo(placa));

        nombrecliente = new JLabel("Nombre Cliente");
        nombrecliente.setBounds(10, 45, 100, 25);
        cajanombrecliente = new JTextField(15);
        cajanombrecliente.setBounds(120, 45, 160, 25);
        cajanombrecliente.setText(obtenerNombreCliente(placa));

        lbplaca = new JLabel("Placa");
        lbplaca.setBounds(10, 75, 100, 25);
        cajaplaca = new JTextField(15);
        cajaplaca.setBounds(120, 75, 160, 25);
        cajaplaca.setText(placa);

        horaentrada = new JLabel("Hora Entrada");
        horaentrada.setBounds(10, 105, 100, 25);
        cajahoraentrada = new JTextField(15);
        cajahoraentrada.setBounds(120, 105, 160, 25);
        cajahoraentrada.setText(obtenerHoraEntrada(placa));

        minentrada = new JLabel("Min Entrada");
        minentrada.setBounds(10, 135, 100, 25);
        cajaminentrada = new JTextField(15);
        cajaminentrada.setBounds(120, 135, 160, 25);
        cajaminentrada.setText(obtenerMinEntrada(placa));

        horasalida = new JLabel("Hora Salida");
        horasalida.setBounds(10, 165, 100, 25);
        cajahorasalida = new JTextField(15);
        cajahorasalida.setBounds(120, 165, 160, 25);
        cajahorasalida.setText(String.valueOf(horaSalida));

        minsalida = new JLabel("Min Salida");
        minsalida.setBounds(10, 195, 100, 25);
        cajaminsalida = new JTextField(15);
        cajaminsalida.setBounds(120, 195, 160, 25);
        cajaminsalida.setText(String.valueOf(minutoSalida));

        valorhora = new JLabel("Valor Hora");
        valorhora.setBounds(10, 225, 100, 25);
        cajavalorhora = new JTextField(15);
        cajavalorhora.setBounds(120, 225, 160, 25);
        String tipoVehiculo = obtenerTipoVehiculo(placa);
        if (!tipoVehiculo.isEmpty()) {
            valorHora = obtenerValorHora(tipoVehiculo);
            cajavalorhora.setText(String.valueOf(valorHora));
        }

        horas = new JLabel("Horas");
        horas.setBounds(10, 255, 100, 25);
        cajahoras = new JTextField(15);
        cajahoras.setBounds(120, 255, 160, 25);
        cajahoras.setText(String.valueOf(duracionHoras));

        totalpagar = new JLabel("Total a Pagar");
        totalpagar.setBounds(10, 285, 100, 25);
        cajatotalpagar = new JTextField(15);
        cajatotalpagar.setBounds(120, 285, 160, 25);

        calcularDuracionHoras();
        double totalPagar = valorHora * duracionHoras;
        cajatotalpagar.setText(String.valueOf(totalPagar));

        guardar = new JButton("Guardar");
        guardar.setBounds(55, 335, 80, 25);
        guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Guardar los datos y cerrar la ventana
                guardarDatos();
                generarFactura();
                
                dispose();
            }
        });

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(150, 335, 80, 25);
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        c.add(vehiculo);
        c.add(cajavehiculo);
        c.add(nombrecliente);
        c.add(cajanombrecliente);
        c.add(lbplaca);
        c.add(cajaplaca);
        c.add(horaentrada);
        c.add(cajahoraentrada);
        c.add(minentrada);
        c.add(cajaminentrada);
        c.add(horasalida);
        c.add(cajahorasalida);
        c.add(minsalida);
        c.add(cajaminsalida);
        c.add(valorhora);
        c.add(cajavalorhora);
        c.add(horas);
        c.add(cajahoras);
        c.add(totalpagar);
        c.add(cajatotalpagar);
        c.add(guardar);
        c.add(cancelar);

        calcularDuracionHoras();
        cajahoras.setText(String.valueOf(duracionHoras));

        setSize(305, 415);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

   private void guardarDatos() {
    Connection connection = null;
    try {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

        // Obtener los datos necesarios para el insert en la tabla 'salida'
        String placa = cajaplaca.getText().trim();
        int horaSalida = Integer.parseInt(cajahorasalida.getText().trim());
        int minutoSalida = Integer.parseInt(cajaminsalida.getText().trim());
        double valorHora = Double.parseDouble(cajavalorhora.getText().trim());
        String tipoVehiculo = cajavehiculo.getText().trim();

        // Insertar el registro en la tabla 'salida'
        // Obtener el código de entrada correspondiente a la placa
        int codigoEntrada = obtenerCodigoEntrada(placa);
        actualizarEstacionamiento(codigoEntrada);

        // Calcular la duración en horas
        calcularDuracionHoras();
        double totalHoras = duracionHoras;

        // Obtener el código de precio (asumiendo que siempre es 1)
        int codigoPrecio = 1;

        String sqlSalida = "INSERT INTO salida (Codigo_entrada, Horas_Salida, Total_Horas, Codigo_Precio) VALUES (?, ?, ?, ?)";
        PreparedStatement statementSalida = connection.prepareStatement(sqlSalida, Statement.RETURN_GENERATED_KEYS);
        statementSalida.setInt(1, codigoEntrada);
        statementSalida.setString(2, horaSalida + ":" + minutoSalida);
        statementSalida.setDouble(3, duracionHoras);
        statementSalida.setInt(4, codigoPrecio);

       statementSalida.executeUpdate();

 
                // Obtener los datos del usuario correspondiente al código de entrada
                String sqlUsuario = "SELECT u.Codigo, u.Nombre FROM entrada e JOIN usuario u ON e.Codigo = u.Codigo WHERE e.Codigo_entrada = ?";
                PreparedStatement statementUsuario = connection.prepareStatement(sqlUsuario);
                statementUsuario.setInt(1, codigoEntrada);
                ResultSet resultSetUsuario = statementUsuario.executeQuery();

                if (resultSetUsuario.next()) {
                    int codigoUsuario = resultSetUsuario.getInt("Codigo");
                    String nombreCliente = resultSetUsuario.getString("Nombre");

                    // Insertar en la tabla 'facturas'
                    String sqlFacturas = "INSERT INTO facturas (Nombre_Cliente, Vehiculo, Placa, Hora_Entrada, Minuto_Entrada, Hora_Salida, Minuto_Salida, Valor_Hora, Horas, Total, Codigo_empleado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statementFacturas = connection.prepareStatement(sqlFacturas);
                    statementFacturas.setString(1, nombreCliente);
                    statementFacturas.setString(2, tipoVehiculo);
                    statementFacturas.setString(3, placa);
                    statementFacturas.setString(4, obtenerHoraEntrada(placa));
                    statementFacturas.setString(5, obtenerMinEntrada(placa));
                    statementFacturas.setString(6, String.valueOf(horaSalida));
                    statementFacturas.setString(7, String.valueOf(minutoSalida));
                    statementFacturas.setString(8, String.valueOf(valorHora));
                    statementFacturas.setString(9, String.valueOf(duracionHoras));
                    statementFacturas.setString(10, cajatotalpagar.getText().trim());
                    statementFacturas.setInt(11, codigoUsuario);
                    statementFacturas.executeUpdate();
                }
            
        

        // Cerrar la conexión
        connection.close();

        // Mostrar mensaje de éxito
        JOptionPane.showMessageDialog(null, "Datos guardados exitosamente.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private String obtenerTipoVehiculo(String placa) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            String sql = "SELECT Tipo_vehiculo FROM entrada WHERE Placa = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, placa);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("Tipo_vehiculo");
            }
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
        return "";
    }

    private String obtenerNombreCliente(String placa) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            String sql = "SELECT u.Nombre FROM entrada e JOIN usuario u ON e.Codigo = u.Codigo WHERE e.Placa = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, placa);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("Nombre");
            }
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
        return "";
    }

    private String obtenerHoraEntrada(String placa) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            String sql = "SELECT HOUR(Hora_entrada) AS Hora_entrada FROM entrada WHERE Placa = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, placa);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("Hora_entrada");
            }
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
        return "";
    }

    private String obtenerMinEntrada(String placa) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            String sql = "SELECT TIME_FORMAT(Hora_entrada, '%i') AS Minuto_entrada FROM entrada WHERE Placa = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, placa);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("Minuto_entrada");
            }
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
        return "";
    }

    private void calcularDuracionHoras() {
        String horaEntradaStr = cajahoraentrada.getText().trim();
        String minEntradaStr = cajaminentrada.getText().trim();

        int horaEntrada = Integer.parseInt(horaEntradaStr);
        int minEntrada = Integer.parseInt(minEntradaStr);

        int minutosEntrada = horaEntrada * 60 + minEntrada;
        int minutosSalida = horaSalida * 60 + minutoSalida;
        int minutosDuracion = minutosSalida - minutosEntrada;

        int horas = minutosDuracion / 60;
        int minutos = minutosDuracion % 60;

        double duracionHoras = horas + minutos / 100.0;
        duracionHoras = Math.round(duracionHoras * 100) / 100.0;

        // Si los minutos superan 59, ajusta las horas y minutos en consecuencia
        if (minutos > 59) {
            horas += minutos / 60;
            minutos %= 60;
        }

        // Actualiza el valor en la variable de instancia
        this.duracionHoras = duracionHoras;
    }

    private int obtenerValorHora(String tipoVehiculo) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            String sql = "SELECT Precio_Cicla, Precio_moto, Precio_Carro FROM precio";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                switch (tipoVehiculo) {
                    case "Carro":
                        return resultSet.getInt("Precio_Carro");
                    case "Moto":
                        return resultSet.getInt("Precio_moto");
                    case "Bicicleta":
                        return resultSet.getInt("Precio_Cicla");
                }
            }
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
        return 0; // Valor predeterminado si no se encuentra el tipo de vehículo en la tabla
    }

    private int obtenerCodigoEntrada(String placa) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            String sql = "SELECT e.Codigo_Entrada FROM entrada e JOIN usuario u ON e.Codigo = u.Codigo WHERE e.Placa = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, placa);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("Codigo_Entrada");
            }
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
        return 0; // Valor predeterminado si no se encuentra el código de entrada
    }

    private int obtenerCodigoPrecio() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

            String sql = "SELECT Codigo_Precio FROM precio WHERE Codigo_Precio = 1";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("Codigo_Precio");
            }
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
        return 0; // Valor predeterminado si no se encuentra el código de precio
    }
private void generarFactura() {
    // Obtener los datos necesarios para la factura
    String placa = cajaplaca.getText().trim();
    String tipoVehiculo = cajavehiculo.getText().trim();
    String nombreCliente = cajanombrecliente.getText().trim();
    String horaEntrada = cajahoraentrada.getText().trim();
    String minEntrada = cajaminentrada.getText().trim();
    String horaSalida = cajahorasalida.getText().trim();
    String minSalida = cajaminsalida.getText().trim();
    String valorHora = cajavalorhora.getText().trim();
    String duracionHoras = cajahoras.getText().trim();
    String totalPagar = cajatotalpagar.getText().trim();

    // Generar el contenido de la factura
    StringBuilder facturaBuilder = new StringBuilder();
    facturaBuilder.append("========================================\r\n");
    facturaBuilder.append("            FACTURA DE PARQUEADERO       \r\n");
    facturaBuilder.append("========================================\r\n\r\n");
    facturaBuilder.append("Placa: ").append(placa).append("\r\n");
    facturaBuilder.append("Tipo de vehículo: ").append(tipoVehiculo).append("\r\n");
    facturaBuilder.append("Nombre del cliente: ").append(nombreCliente).append("\r\n");
    facturaBuilder.append("Hora de entrada: ").append(horaEntrada).append(":").append(minEntrada).append("\r\n");
    facturaBuilder.append("Hora de salida: ").append(horaSalida).append(":").append(minSalida).append("\r\n\r\n");
    facturaBuilder.append("========================================\r\n");
    facturaBuilder.append("            DETALLES DE COBRO             \r\n");
    facturaBuilder.append("========================================\r\n\r\n");
    facturaBuilder.append("Valor por hora: $").append(valorHora).append("\r\n");
    facturaBuilder.append("Duración en horas: ").append(duracionHoras).append("\r\n");
    facturaBuilder.append("Total a pagar: $").append(totalPagar).append("\r\n\r\n");
    facturaBuilder.append("========================================\r\n");

    // Guardar la factura en un archivo
    try {
        String nombreArchivo = "FACTURA - " + placa + ".txt";
        FileWriter writer = new FileWriter(nombreArchivo);
        writer.write(facturaBuilder.toString());
        writer.close();
        JOptionPane.showMessageDialog(null, "Factura generada con éxito.\r\nSe ha guardado en el archivo " + nombreArchivo);
    } catch (IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al generar la factura.");
    }
}
private void actualizarEstacionamiento(int codigoEntrada) {
    Connection connection = null;
    try {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

        // Actualizar el campo Estacionamiento a 0 en la tabla entrada
        String sqlUpdate = "UPDATE entrada SET Estacionamiento = NULL WHERE Codigo_Entrada = ?";
        PreparedStatement statementUpdate = connection.prepareStatement(sqlUpdate);
        statementUpdate.setInt(1, codigoEntrada);
        int rowsAffected = statementUpdate.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Estacionamiento actualizado correctamente");
        } else {
            System.out.println("No se encontró el registro con el código de entrada especificado");
        }
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
}

}
