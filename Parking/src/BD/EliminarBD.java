/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BD;

/**
 *
 * @author pana
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class EliminarBD {

    public static void eliminarBaseDeDatos() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=");

            Statement stmt = con.createStatement();
            String sql = "DROP DATABASE IF EXISTS parqueadero";
            stmt.executeUpdate(sql);

            stmt.close();

            System.out.println("Base de datos 'parqueadero' eliminada exitosamente.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        eliminarBaseDeDatos();
    }
}
