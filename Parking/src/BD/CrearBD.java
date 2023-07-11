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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class CrearBD {

    public static void main(String[] args) {
        // Establecer conexión con la base de datos
        Connection con = null;
        boolean BASECREADA = false;
        boolean TABLACREADA = false;

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root & password=");

            BASECREADA = CrearBD(con);

            // Continuar solo si la base de datos se creó exitosamente
            if (BASECREADA) {
                // Usar la base de datos
                Statement stmt = con.createStatement();
                String sql = "USE parqueadero";
                stmt.executeUpdate(sql);

                // Crear la tabla usuario si no existe
                sql = "CREATE TABLE IF NOT EXISTS usuario ("
                        + "Nombre VARCHAR(50) NOT NULL, "
                        + "Contraseña VARCHAR(50) NOT NULL, "
                        + "Correo VARCHAR(50) NOT NULL, "
                        + "Nivel_Acceso INT(11) NOT NULL, "
                        + "Codigo INT(11) NOT NULL AUTO_INCREMENT, "
                        + "PRIMARY KEY (Codigo)"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";
                int result = stmt.executeUpdate(sql);
                if (result >= 0) {
                    System.out.println("Tabla 'usuario' creada exitosamente.");
                } else {
                    System.out.println("La tabla 'usuario' ya existe.");
                }

                // Insertar un nuevo usuario "Admin"
                sql = "INSERT INTO usuario (Nombre, Contraseña, Correo, Nivel_Acceso) VALUES ('Admin', 'Univalle', 'admin@parqueadero.com', 1)";
                result = stmt.executeUpdate(sql);
                if (result >= 0) {
                    System.out.println("Nuevo usuario 'Admin' insertado exitosamente.");
                } else {
                    System.out.println("Error al insertar el usuario 'Admin'.");
                }

                // Crear la tabla entrada si no existe
                sql = "CREATE TABLE IF NOT EXISTS entrada ("
                        + "Codigo_Entrada INT(11) NOT NULL AUTO_INCREMENT, "
                        + "Estacionamiento INT(11) NULL DEFAULT NULL, "
                        + "Placa VARCHAR(6) NOT NULL COLLATE 'utf8mb4_general_ci', "
                        + "Hora_Entrada TIME NOT NULL, "
                        + "Tipo_vehiculo VARCHAR(50) NOT NULL COLLATE 'utf8mb4_general_ci', "
                        + "Codigo INT(11) NOT NULL, "
                        + "PRIMARY KEY (Codigo_Entrada), "
                        + "FOREIGN KEY (Codigo) REFERENCES usuario (Codigo) ON UPDATE CASCADE ON DELETE CASCADE"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";
                result = stmt.executeUpdate(sql);
                if (result >= 0) {
                    TABLACREADA = true;
                    System.out.println("Tabla 'entrada' creada exitosamente.");
                } else {
                    System.out.println("La tabla 'entrada' ya existe.");
                }

                // Crear la tabla precio
                sql = "CREATE TABLE IF NOT EXISTS precio ("
                        + "Codigo_precio INT(11) NOT NULL AUTO_INCREMENT, "
                        + "Precio_Cicla BIGINT(20) NOT NULL, "
                        + "Precio_moto BIGINT(20) NOT NULL, "
                        + "Precio_Carro BIGINT(20) NOT NULL, "
                        + "PRIMARY KEY (Codigo_precio)"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";
                stmt.executeUpdate(sql);

                // Crear la tabla salida
                sql = "CREATE TABLE IF NOT EXISTS salida ("
                        + "Horas_Salida TIME NOT NULL, "
                        + "Total_Horas DOUBLE NOT NULL DEFAULT '0', "
                        + "Codigo_salida INT(11) NOT NULL AUTO_INCREMENT, "
                        + "Codigo_Precio INT(11) NOT NULL, "
                        + "Codigo_Entrada INT(11) NOT NULL, "
                        + "PRIMARY KEY (Codigo_salida), "
                        + "FOREIGN KEY (Codigo_Entrada) REFERENCES entrada (Codigo_Entrada) ON UPDATE CASCADE ON DELETE CASCADE, "
                        + "FOREIGN KEY (Codigo_Precio) REFERENCES precio (Codigo_precio) ON UPDATE CASCADE ON DELETE CASCADE"
                        + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";
                stmt.executeUpdate(sql);

                // Crear la tabla facturas
                sql = "CREATE TABLE IF NOT EXISTS facturas ("
                        + "Codigo_fact INT(11) NOT NULL AUTO_INCREMENT, "
                        + "Vehiculo VARCHAR(50) NOT NULL DEFAULT '0', "
                        + "Nombre_Cliente VARCHAR(50) NOT NULL DEFAULT '0', "
                        + "Placa VARCHAR(6) NOT NULL DEFAULT '0', "
                        + "Hora_Entrada INT(11) NOT NULL DEFAULT '0', "
                        + "Minuto_Entrada INT(11) NOT NULL DEFAULT '0', "
                        + "Hora_Salida INT(11) NOT NULL DEFAULT '0', "
                        + "Minuto_Salida INT(11) NOT NULL DEFAULT '0', "
                        + "Valor_hora INT(11) NOT NULL DEFAULT '0', "
                        + "Horas DOUBLE NOT NULL DEFAULT '0', "
                        + "Total DOUBLE NOT NULL DEFAULT '0', "
                        + "Codigo_empleado INT(11) NOT NULL DEFAULT '0', "
                        + "PRIMARY KEY (Codigo_fact),"
                        + "FOREIGN KEY (Codigo_empleado) REFERENCES usuario (Codigo) ON UPDATE CASCADE ON DELETE CASCADE)"
                        + " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci";

                stmt.executeUpdate(sql);

                stmt.close();

                if (TABLACREADA) {
                    System.out.println("Base de datos y tablas fueron creadas exitosamente.");
                    
                     String mensaje = "Base creada con el nombre 'parqueadero'.\n"
                            + "También se creó el usuario Admin con correo.\n 'admin@parqueadero.com' y contraseña 'univalle'.";
                    JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
                
                } else {
                    System.out.println("Base de datos creada exitosamente o Algunas tablas ya existen.");
                }
            } else {
                System.out.println("La base de datos ya existe.");
            }
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

    private static boolean CrearBD(Connection con) throws SQLException {
        boolean baseCreada = false;

        // Verificar si la base de datos ya existe
        Statement stmt = con.createStatement();
        String sql = "SHOW DATABASES LIKE 'parqueadero'";
        ResultSet Rs = stmt.executeQuery(sql);

        if (!Rs.next()) {
            // La base de datos no existe, crearla
            sql = "CREATE DATABASE parqueadero";
            int result = stmt.executeUpdate(sql);

            if (result >= 0) {
                baseCreada = true;
                System.out.println("LA BASE DE DATOS SE LLAMA parqueadero.");
                System.out.println("Usuario Admin creado Exitosamenete");
            } else {
                System.out.println("Error al crear la base de datos 'parqueadero'.");
            }
        }

        Rs.close();
        stmt.close();

        return baseCreada;
    }
}