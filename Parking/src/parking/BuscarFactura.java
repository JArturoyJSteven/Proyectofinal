/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parking;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.*;
import javax.swing.*;
/**
 *
 * @author pana
 */
public class BuscarFactura extends JDialog{
    
    JLabel nFactura, vehiculo, nombreC, placa, horaE, minE, horaS, minS, valorhora, horas, totalP;
    JTextField cnFactura, cvehiculo, cnombreC, cplaca, choraE, cminE, choraS, cminS, cvalorhora, choras, ctotalP;
    JButton buscar, cancelar;
    
    public BuscarFactura(Frame D, boolean modal){
        super(D, modal);
        setTitle("Buscar Factura");
        Container c = getContentPane();
        c.setLayout(null);
        
        nFactura = new JLabel("No. Factura");
        nFactura.setBounds(10, 15, 100, 25);
        cnFactura = new JTextField(15);
        cnFactura.setBounds(120, 15, 50, 25);
        
        buscar = new JButton("Buscar");
        buscar.setBounds(185, 15, 80, 25);
        
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
        
        buscar.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        buscarFactura();
    }
});

        
        c.add(nFactura);
        c.add(cnFactura);
        c.add(buscar);
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
        c.add(horas);
        c.add(choras);
        c.add(totalP);
        c.add(ctotalP);
         c.add(cancelar);
        
        setSize(305, 455);
        setLocationRelativeTo(null);
        setResizable(false);       
        setVisible(true);

    }
    private void buscarFactura() {
    int numeroFactura = Integer.parseInt(cnFactura.getText().trim());

    Connection connection = null;
    try {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/parqueadero", "root", "");

        String sql = "SELECT * FROM facturas WHERE Codigo_fact = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, numeroFactura);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            cvehiculo.setText(resultSet.getString("Vehiculo"));
            cnombreC.setText(resultSet.getString("Nombre_Cliente"));
            cplaca.setText(resultSet.getString("Placa"));
            choraE.setText(resultSet.getString("Hora_Entrada"));
            cminE.setText(resultSet.getString("Minuto_Entrada"));
            choraS.setText(resultSet.getString("Hora_Salida"));
            cminS.setText(resultSet.getString("Minuto_Salida"));
            cvalorhora.setText(resultSet.getString("Valor_Hora"));
            choras.setText(resultSet.getString("Horas"));
            ctotalP.setText(resultSet.getString("Total"));
        } else {
            System.out.println("No se encontró la factura con el número especificado." + "Error");
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

