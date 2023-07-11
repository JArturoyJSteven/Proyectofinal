package parking;
import java.time.LocalTime;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.*;
import java.time.Duration;
import java.util.List;
import javax.swing.*;

public class ModificarSeleccion extends JDialog {

    JLabel nFactura, vehiculo, nombreC, placa, horaE, minE, horaS, minS, valorhora, horas, totalP;
    JTextField cnFactura, cvehiculo, cnombreC, cplaca, choraE, cminE, choraS, cminS, cvalorhora, choras, ctotalP;
    JButton modificar, cancelar;

    public ModificarSeleccion(Frame D, boolean modal, List<Object[]> datosSeleccionados) {
        super(D, modal);
        setTitle("Modificar selección");
        Container c = getContentPane();
        c.setLayout(null);

        nFactura = new JLabel("No. Factura");
        nFactura.setBounds(10, 15, 100, 25);
        cnFactura = new JTextField(15);
        cnFactura.setBounds(120, 15, 50, 25);

        modificar = new JButton("Modificar");
        modificar.setBounds(185, 15, 80, 25);

        vehiculo = new JLabel("Vehiculos");
        vehiculo.setBounds(10, 55, 100, 25);
        cvehiculo = new JTextField(15);
        cvehiculo.setBounds(120, 55, 160, 25);

        nombreC = new JLabel("Nombre Cliente");
        nombreC.setBounds(10, 85, 100, 25);
        cnombreC = new JTextField(15);
        cnombreC.setBounds(120, 85, 160, 25);

        placa = new JLabel("Placa");
        placa.setBounds(10, 115, 100, 25);
        cplaca = new JTextField(15);
        cplaca.setBounds(120, 115, 160, 25);

        horaE = new JLabel("Hora Entrada");
        horaE.setBounds(10, 145, 100, 25);
        choraE = new JTextField(15);
        choraE.setBounds(120, 145, 160, 25);

        minE = new JLabel("Min Entrada");
        minE.setBounds(10, 175, 100, 25);
        cminE = new JTextField(15);
        cminE.setBounds(120, 175, 160, 25);

        horaS = new JLabel("Hora Salida");
        horaS.setBounds(10, 205, 100, 25);
        choraS = new JTextField(15);
        choraS.setBounds(120, 205, 160, 25);

        minS = new JLabel("Min Salida");
        minS.setBounds(10, 235, 100, 25);
        cminS = new JTextField(15);
        cminS.setBounds(120, 235, 160, 25);

        valorhora = new JLabel("Valor Hora");
        valorhora.setBounds(10, 265, 100, 25);
        cvalorhora = new JTextField(15);
        cvalorhora.setBounds(120, 265, 160, 25);

        horas = new JLabel("Horas");
        horas.setBounds(10, 295, 100, 25);
        choras = new JTextField(15);
        choras.setBounds(120, 295, 160, 25);

        totalP = new JLabel("Total a Pagar");
        totalP.setBounds(10, 325, 100, 25);
        ctotalP = new JTextField(15);
        ctotalP.setBounds(120, 325, 160, 25);

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(105, 375, 80, 25);
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        modificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificar();
            }
        });

        c.add(nFactura);
        c.add(cnFactura);
        c.add(modificar);
        c.add(vehiculo);
        c.add(cvehiculo);
        c.add(nombreC);
        c.add(cnombreC);
        c.add(placa);
        c.add(cplaca);
        c.add(horaE);
        c.add(choraE);
        c.add(minE);
        c.add(cminE);
        c.add(horaS);
        c.add(choraS);
        c.add(minS);
        c.add(cminS);
        c.add(valorhora);
        c.add(cvalorhora);
        c.add(cancelar);

        // Rellenar los campos con los datos de la primera fila seleccionada
        if (!datosSeleccionados.isEmpty()) {
            Object[] primerDatoSeleccionado = datosSeleccionados.get(0);
            cnFactura.setText(primerDatoSeleccionado[0].toString());
            cnombreC.setText(primerDatoSeleccionado[1].toString());
            cvehiculo.setText(primerDatoSeleccionado[2].toString());
            cplaca.setText(primerDatoSeleccionado[3].toString());

            // Obtener la hora y los minutos de entrada
            String horaEntrada = primerDatoSeleccionado[4].toString();
            String[] partesHoraEntrada = horaEntrada.split(":");
            if (partesHoraEntrada.length == 2) {
                choraE.setText(partesHoraEntrada[0]);
                cminE.setText(partesHoraEntrada[1]);
            }

            // Obtener la hora y los minutos de salida
            String horaSalida = primerDatoSeleccionado[5].toString();
            String[] partesHoraSalida = horaSalida.split(":");
            if (partesHoraSalida.length == 2) {
                choraS.setText(partesHoraSalida[0]);
                cminS.setText(partesHoraSalida[1]);
            }

            cvalorhora.setText(primerDatoSeleccionado[6].toString());
            choras.setText(primerDatoSeleccionado[7].toString());
            ctotalP.setText(primerDatoSeleccionado[8].toString());
        }

        setSize(305, 455);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void modificar() {
        String codigoFactura = cnFactura.getText();
        String vehiculo = cvehiculo.getText();
        String nombreCliente = cnombreC.getText();
        String placa = cplaca.getText();
        String horaEntrada = choraE.getText();
        String minutoEntrada = cminE.getText();
        String horaSalida = choraS.getText();
        String minutoSalida = cminS.getText();
        String valorHora = cvalorhora.getText();
        String horas_entrada = choraE.getText() + ":" + cminE.getText();
        String horas_salida = choraS.getText() + ":" + cminS.getText();

        // Verificar que las horas y minutos estén dentro del rango permitido
        int horasEntradaInt = Integer.parseInt(horaEntrada);
        int minutosEntradaInt = Integer.parseInt(minutoEntrada);
        int horasSalidaInt = Integer.parseInt(horaSalida);
        int minutosSalidaInt = Integer.parseInt(minutoSalida);

        if (horasEntradaInt < 0 || horasEntradaInt > 23) {
            JOptionPane.showMessageDialog(ModificarSeleccion.this, "La hora de entrada debe estar entre 0 y 23", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (minutosEntradaInt < 0 || minutosEntradaInt > 59) {
            JOptionPane.showMessageDialog(ModificarSeleccion.this, "Los minutos de entrada deben estar entre 0 y 59", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (horasSalidaInt < 0 || horasSalidaInt > 23) {
            JOptionPane.showMessageDialog(ModificarSeleccion.this, "La hora de salida debe estar entre 0 y 23", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (minutosSalidaInt < 0 || minutosSalidaInt > 59) {
            JOptionPane.showMessageDialog(ModificarSeleccion.this, "Los minutos de salida deben estar entre 0 y 59", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calcular las horas y minutos de entrada
        LocalTime horaEntradaTime = LocalTime.of(horasEntradaInt, minutosEntradaInt);

        // Calcular las horas y minutos de salida
        LocalTime horaSalidaTime = LocalTime.of(horasSalidaInt, minutosSalidaInt);

        // Verificar si la hora de salida es mayor que la hora de entrada
        if (horaSalidaTime.isBefore(horaEntradaTime)) {
            JOptionPane.showMessageDialog(ModificarSeleccion.this, "La hora de salida debe ser mayor que la hora de entrada", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calcular la diferencia de tiempo en minutos
        Duration duracion = Duration.between(horaEntradaTime, horaSalidaTime);
        long minutos = duracion.toMinutes();
        int horas = (int) (minutos / 60);
        int minutosRestantes = (int) (minutos % 60);

        // Calcular el total de horas
        double totalHoras = horas + minutosRestantes / 100.0;

        // Calcular el total a pagar
        double valorPorHora = Double.parseDouble(valorHora);
        double totalPagar = totalHoras * valorPorHora;

        // Actualizar la información en la tabla
        choras.setText(String.valueOf(totalHoras));
        ctotalP.setText(String.valueOf(totalPagar));

        // Realizar la actualización en la base de datos
        try {
            String url = "jdbc:mysql://localhost:3306/parqueadero";
            String username = "root";
            String password = "";
            Connection connection = DriverManager.getConnection(url, username, password);

            // Actualizar la tabla "facturas"
            String sqlFacturas = "UPDATE facturas SET Nombre_cliente = ?, Vehiculo = ?, Placa = ?, Hora_Entrada = ?, Minuto_Entrada = ?, Hora_Salida = ?, Minuto_Salida = ?, Valor_Hora = ?, Horas = ?, Total = ? WHERE Codigo_fact = ?";
            PreparedStatement statementFacturas = connection.prepareStatement(sqlFacturas);
            statementFacturas.setString(1, nombreCliente);
            statementFacturas.setString(2, vehiculo);
            statementFacturas.setString(3, placa);
            statementFacturas.setString(4, horaEntrada);
            statementFacturas.setString(5, minutoEntrada);
            statementFacturas.setString(6, horaSalida);
            statementFacturas.setString(7, minutoSalida);
            statementFacturas.setString(8, valorHora);
            statementFacturas.setDouble(9, totalHoras);
            statementFacturas.setDouble(10, totalPagar);
            statementFacturas.setString(11, codigoFactura);
            statementFacturas.executeUpdate();
            statementFacturas.close();

            // Actualizar la tabla "usuario"
            String sqlUsuario = "UPDATE usuario SET Nombre = ? WHERE Codigo = (SELECT Codigo_empleado FROM facturas WHERE Codigo_fact = ?)";
            PreparedStatement statementUsuario = connection.prepareStatement(sqlUsuario);
            statementUsuario.setString(1, nombreCliente);
            statementUsuario.setString(2, codigoFactura);
            statementUsuario.executeUpdate();
            statementUsuario.close();

            // Actualizar la tabla "entrada"
            String sqlEntrada = "UPDATE entrada SET Placa = ?, Hora_Entrada = ? WHERE Codigo_Entrada = ?";
            PreparedStatement statementEntrada = connection.prepareStatement(sqlEntrada);
            statementEntrada.setString(1, placa);
            statementEntrada.setString(2, horas_entrada);
            statementEntrada.setString(3, codigoFactura);
            statementEntrada.executeUpdate();
            statementEntrada.close();

            // Actualizar la tabla "salida"
            String sqlSalida = "UPDATE salida SET Horas_Salida = ?, Total_Horas = ? WHERE Codigo_Entrada = ?";
            PreparedStatement statementSalida = connection.prepareStatement(sqlSalida);
            statementSalida.setString(1, horas_salida);
            statementSalida.setDouble(2, totalHoras);
            statementSalida.setString(3, codigoFactura);
            statementSalida.executeUpdate();
            statementSalida.close();

            connection.close();

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(ModificarSeleccion.this, "Los datos se han actualizado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Cerrar el diálogo
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Mostrar mensaje de error
            JOptionPane.showMessageDialog(ModificarSeleccion.this, "Error al actualizar los datos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}