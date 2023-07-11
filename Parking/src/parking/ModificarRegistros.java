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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ModificarRegistros extends JDialog {

    JLabel usuarios, Total;
    JComboBox<String> consulta;
    JTextField ccodigo, cTotal;
    JButton buscar, modificarseleccionados, cancelar;
    JTable tabla;
    DefaultTableModel modelo;
    Connection connection;

    public ModificarRegistros(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Modificar Registros");
        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout());
        usuarios = new JLabel("Usuarios: ");
        consulta = new JComboBox<>();
        consulta.addItem("Placa");
        consulta.addItem("Factura");
        consulta.addItem("Todos");
        consulta.addItem("Empleado");
        ccodigo = new JTextField(15);
        buscar = new JButton("Buscar");

        panelSuperior.add(usuarios);
        panelSuperior.add(consulta);
        panelSuperior.add(ccodigo);
        panelSuperior.add(buscar);

        c.add(panelSuperior, BorderLayout.NORTH);

        modelo = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 10) {
                    return Boolean.class; // La columna "Modificar" es de tipo Boolean (checkbox)
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }
        };

        modelo.addColumn("Factura");
        modelo.addColumn("Cliente");
        modelo.addColumn("Vehiculo");
        modelo.addColumn("Placa");
        modelo.addColumn("HoraEntrada");
        modelo.addColumn("HoraSalida");
        modelo.addColumn("ValorHora");
        modelo.addColumn("Horas");
        modelo.addColumn("Total");
        modelo.addColumn("Empleado");
        modelo.addColumn("Modificar");

        tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(10).setCellRenderer(tabla.getDefaultRenderer(Boolean.class)); // Renderizar la columna "Modificar" como checkbox
        JScrollPane scroll = new JScrollPane(tabla, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        c.add(scroll, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout());
        modificarseleccionados = new JButton("Modificar ");

        cancelar = new JButton("Cancelar");

        panelInferior.add(modificarseleccionados);
        panelInferior.add(cancelar);

        c.add(panelInferior, BorderLayout.SOUTH);

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
                buscarRegistros();
            }
        });

        modificarseleccionados.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<Integer> filasSeleccionadas = obtenerFilasSeleccionadas();
                if (!filasSeleccionadas.isEmpty()) {
                    List<Object[]> datosSeleccionados = obtenerDatosSeleccionados(filasSeleccionadas);
                    ModificarSeleccion dialogoModificar = new ModificarSeleccion(D, true, datosSeleccionados);
                }
            }
        });

        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setSize(800, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void buscarRegistros() {
        String seleccion = (String) consulta.getSelectedItem();
        String filtro = ccodigo.getText().trim();
        String sql = "";

        if (seleccion.equals("Placa")) {
            sql = "SELECT * FROM facturas WHERE Placa = ?";
        } else if (seleccion.equals("Factura")) {
            sql = "SELECT * FROM facturas WHERE Codigo_fact = ?";
        } else if (seleccion.equals("Empleado")) {
            sql = "SELECT * FROM facturas WHERE CodEmpleado IN (SELECT Codigo FROM usuario WHERE Nombre = ?)";
        } else if (seleccion.equals("Todos")) {
            sql = "SELECT * FROM facturas";
        }

        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            if (!seleccion.equals("Todos")) {
                statement.setString(1, filtro);
            }

            ResultSet resultSet = statement.executeQuery();

            // Limpiar el modelo
            modelo.setRowCount(0);

            // Llenar el modelo con los datos del resultado de la consulta
            while (resultSet.next()) {
                String codigoFact = resultSet.getString("Codigo_fact");
                String nombreCliente = resultSet.getString("Nombre_cliente");
                String vehiculo = resultSet.getString("Vehiculo");
                String placa = resultSet.getString("Placa");
                String horaEntrada = resultSet.getString("Hora_Entrada");
                String minutoEntrada = resultSet.getString("Minuto_Entrada");
                String horaSalida = resultSet.getString("Hora_Salida");
                String minutoSalida = resultSet.getString("Minuto_Salida");
                String valorHora = resultSet.getString("Valor_hora");
                String horas = resultSet.getString("Horas");
                String total = resultSet.getString("Total");
                String codEmpleado = resultSet.getString("Codigo_empleado");

                modelo.addRow(new Object[]{codigoFact, nombreCliente, vehiculo, placa, horaEntrada + ":" + minutoEntrada, horaSalida + ":" + minutoSalida, valorHora, horas, total, codEmpleado, false});
            }

            tabla.repaint();

            // Cerrar el ResultSet y el PreparedStatement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> obtenerFilasSeleccionadas() {
        List<Integer> filasSeleccionadas = new ArrayList<>();
        int rowCount = tabla.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Boolean seleccionado = (Boolean) tabla.getValueAt(i, 10);
            if (seleccionado != null && seleccionado) {
                filasSeleccionadas.add(i);
            }
        }
        return filasSeleccionadas;
    }

    private List<Object[]> obtenerDatosSeleccionados(List<Integer> filasSeleccionadas) {
        List<Object[]> datosSeleccionados = new ArrayList<>();
        for (int fila : filasSeleccionadas) {
            Object[] datos = new Object[9];
            for (int i = 0; i < 9; i++) {
                datos[i] = tabla.getValueAt(fila, i);
            }
            datosSeleccionados.add(datos);
        }
        return datosSeleccionados;
    }
}