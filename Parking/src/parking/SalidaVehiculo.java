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
import javax.swing.*;

public class SalidaVehiculo extends JDialog {

    JLabel codigovehiculo;
    JTextField cajacodigoVehiculos;
    JButton validar, cancelar;

    public SalidaVehiculo(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Salida Vehiculo");
        Container c = getContentPane();
        c.setLayout(null);

        codigovehiculo = new JLabel("V-P-PLACA-HS-MS");
        codigovehiculo.setBounds(20, 10, 160, 25);

        cajacodigoVehiculos = new JTextField();
        cajacodigoVehiculos.setBounds(140, 10, 140, 25);

        validar = new JButton("Validar");
        validar.setBounds(70, 55, 80, 25);
        validar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                validarSalida();
            }
        });

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(160, 55, 80, 25);
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        c.add(codigovehiculo);
        c.add(cajacodigoVehiculos);
        c.add(validar);
        c.add(cancelar);

        setSize(320, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

private void validarSalida() {
    String codigoVehiculo = cajacodigoVehiculos.getText().trim();
    String formatoEsperado = "[A-Z]-\\d+-[A-Z]{3}\\d{3}-\\d{2}-\\d{2}";
    String formatoMoto = "[A-Z]-\\d+-[A-Z]{3}\\d{2}[A-Z]{1}-\\d{2}-\\d{2}";
    String formatoBicicleta = "[A-Z]-\\d+-\\d{4}-\\d{2}-\\d{2}";
    
    if (codigoVehiculo.matches(formatoEsperado) || codigoVehiculo.matches(formatoMoto) || codigoVehiculo.matches(formatoBicicleta)) {
        // El formato del código es válido
        String[] partes = codigoVehiculo.split("-");
        String tipoVehiculo = String.valueOf(partes[0].charAt(0)).toUpperCase();
        String puestoEstacionamiento = partes[1];
        String placa = partes[2];
        int horaSalida = Integer.parseInt(partes[3]);
        int minutoSalida = Integer.parseInt(partes[4]);

        // Verificar formato de hora
        if (esFormato24HorasValido(horaSalida) && esFormato60MinutosValido(minutoSalida)) {
            // Los datos son válidos, verificar la hora de salida
            String horaEntradaStr = obtenerHoraEntrada(placa);
            String minutoEntradaStr = obtenerMinEntrada(placa);

            if (!horaEntradaStr.isEmpty() && !minutoEntradaStr.isEmpty()) {
                int horaEntrada = Integer.parseInt(horaEntradaStr);
                int minutoEntrada = Integer.parseInt(minutoEntradaStr);

                if (horaSalida > horaEntrada || (horaSalida == horaEntrada && minutoSalida > minutoEntrada)) {
                    // La hora de salida es mayor a la hora de entrada
                    // Abrir la ventana ValidarSalida y pasar los datos
                    dispose();
                    ValidarSalida validarSalida = new ValidarSalida(null, true, placa, horaSalida, minutoSalida);
                } else {
                    // La hora de salida no es mayor a la hora de entrada
                    JOptionPane.showMessageDialog(this, "La hora de salida debe ser mayor a la hora de entrada.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // No se encontraron los datos de hora de entrada para la placa
                JOptionPane.showMessageDialog(this, "No se encontraron los datos de hora de entrada para la placa especificada.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // El formato de hora o minutos no es válido
            JOptionPane.showMessageDialog(this, "Formato de hora o minutos inválido. Deben estar en formato de 24 horas y 60 minutos respectivamente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        // El formato del código no es válido
        JOptionPane.showMessageDialog(this, "Formato de código incorrecto. Debe ser en el formato V-P-PLACA-HS-MS.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private boolean esFormato24HorasValido(int hora) {
        return hora >= 0 && hora <= 23;
    }

    private boolean esFormato60MinutosValido(int minutos) {
        return minutos >= 0 && minutos <= 59;
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

    
    
}



