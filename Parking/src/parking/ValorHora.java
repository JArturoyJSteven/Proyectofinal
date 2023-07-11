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
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ValorHora extends JDialog {

    JLabel horacarro, horamoto, horabicicleta;
    JTextField cajahoracarro, cajahoramoto, cajahorabicicleta;
    JButton guardar, cancelar;

    public ValorHora(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Valor Hora");
        Container c = getContentPane();
        c.setLayout(null);

        horacarro = new JLabel("Hora Carro");
        horacarro.setBounds(15, 15, 80, 25);
        cajahoracarro = new JTextField(15);
        cajahoracarro.setBounds(110, 15, 180, 25);

        horamoto = new JLabel("Hora Moto");
        horamoto.setBounds(15, 45, 80, 25);
        cajahoramoto = new JTextField(15);
        cajahoramoto.setBounds(110, 45, 180, 25);

        horabicicleta = new JLabel("Hora Bicicleta");
        horabicicleta.setBounds(15, 75, 80, 25);
        cajahorabicicleta = new JTextField(15);
        cajahorabicicleta.setBounds(110, 75, 180, 25);

        guardar = new JButton("Guardar");
        guardar.setBounds(65, 125, 80, 25);
        guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarPrecioEnBaseDeDatos();
                dispose();
            }
        });

        cancelar = new JButton("Cancelar");
        cancelar.setBounds(160, 125, 80, 25);
        cancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        c.add(horacarro);
        c.add(cajahoracarro);
        c.add(horamoto);
        c.add(cajahoramoto);
        c.add(horabicicleta);
        c.add(cajahorabicicleta);
        c.add(guardar);
        c.add(cancelar);

        setSize(320, 200);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void actualizarPrecioEnBaseDeDatos() {
        String url = "jdbc:mysql://localhost/parqueadero";
        String usuario = "root";
        String contraseña = "";

        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña)) {
            String selectSql = "SELECT * FROM precio WHERE Codigo_precio = 1";
            String updateSql = "UPDATE precio SET Precio_Carro = ?, Precio_moto = ?, Precio_Cicla = ? WHERE Codigo_precio = 1";
            boolean existeFila = false;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery()) {
                existeFila = rs.next();
            }

            if (existeFila) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, cajahoracarro.getText());
                    updateStmt.setString(2, cajahoramoto.getText());
                    updateStmt.setString(3, cajahorabicicleta.getText());
                    updateStmt.executeUpdate();
                    System.out.println("Actualización realizada correctamente.");
                }
            } else {
                String insertSql = "INSERT INTO precio (Codigo_precio, Precio_Cicla, Precio_moto, Precio_Carro) VALUES (1, ?, ?, ?)";

                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, cajahoracarro.getText());
                    insertStmt.setString(2, cajahoramoto.getText());
                    insertStmt.setString(3, cajahorabicicleta.getText());
                    insertStmt.executeUpdate();
                    System.out.println("Inserción realizada correctamente.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el precio: " + e.getMessage());
        }
    }
}