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
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;

/**
 *
 * @author pana
 */
public class CerrarSesion extends JDialog {
    
    JLabel deseo;
    JButton si, no;
    boolean confirmado = false;

    public CerrarSesion(Frame D, boolean modal) {
        super(D, modal);
        setTitle("Cerrar sesión");
        Container c = getContentPane();
        c.setLayout(null);
        
        deseo = new JLabel("¿Desea cerrar la sesión?");
        deseo.setBounds(10, 10, 200, 25);
        
        si = new JButton("Sí");
        si.setBounds(50, 50, 80, 25);
        
        no = new JButton("No");
        no.setBounds(140, 50, 80, 25);
        
        c.add(deseo);
        c.add(si);
        c.add(no);
        
        si.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmado = true;
                dispose();
            }
        });
        
        no.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmado = false;
                dispose();
            }
        });
        
        setSize(250, 130);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }
    
    public boolean isConfirmado() {
        return confirmado;
    }
}
