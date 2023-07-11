package parking;
import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.sql.*;
    import javax.swing.*;
    import javax.swing.table.*;

    public class BuscarRegistros extends JDialog {

        JLabel usuarios, Total;
        JComboBox<String> consulta;
        JTextField ccodigo, cTotal;
        JButton buscar, cancelar;
        JTable tabla;
        DefaultTableModel modelo;
        Connection connection;
        

        public BuscarRegistros(Frame D, boolean modal) {
            super(D, modal);
            setTitle("Eliminar Usuario");
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

            modelo = new DefaultTableModel();
            modelo.addColumn("Factura");
            modelo.addColumn("Cliente");
            modelo.addColumn("Vehiculo");
            modelo.addColumn("Placa");
            modelo.addColumn("Hora_Entrada");
            modelo.addColumn("Hora_Salida");
            modelo.addColumn("ValorHora");
            modelo.addColumn("Horas");
            modelo.addColumn("Total");
            modelo.addColumn("Empleado");

            tabla = new JTable(modelo);
            JScrollPane scroll = new JScrollPane(tabla, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            c.add(scroll, BorderLayout.CENTER);

            JPanel panelInferior = new JPanel(new FlowLayout());
            Total = new JLabel("Total: ");
            cTotal = new JTextField(15);
            cancelar = new JButton("Cancelar");

            panelInferior.add(Total);
            panelInferior.add(cTotal);
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
                double totalSum = 0.0;
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
                    String valorHora = resultSet.getString("Valor_Hora");
                    String horas = resultSet.getString("Horas");
                    String total = resultSet.getString("Total");
                    String codEmpleado = resultSet.getString("Codigo_empleado");

                    modelo.addRow(new Object[]{codigoFact, nombreCliente, vehiculo, placa, horaEntrada + ":" + minutoEntrada, horaSalida + ":" + minutoSalida, valorHora, horas, total, codEmpleado});
                    totalSum += Double.parseDouble(total);
                    
                }

                tabla.repaint();
                cTotal.setText(String.valueOf(totalSum));
                // Cerrar el ResultSet y el PreparedStatement
                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }