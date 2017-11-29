package es.jklabs.gui.configuracion;

import es.jklabs.json.configuracion.server.Carpeta;

import javax.swing.*;
import java.awt.*;

public class DialogoCarpeta extends JDialog {

    private final Carpeta carpeta;
    private JTextField txNombre;

    public DialogoCarpeta(ConfiguracionUI configuracionUI, Carpeta carpeta) {
        super(configuracionUI, "Carpeta", true);
        this.carpeta = carpeta;
        cargarPantalla();
    }

    private void cargarPantalla() {
        this.setLayout(new BorderLayout());
        this.add(new JLabel("Nombre"), BorderLayout.WEST);
        txNombre = new JTextField();
        txNombre.setColumns(10);
        this.add(txNombre, BorderLayout.CENTER);
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(al -> guardarNombre());
        this.add(btnAceptar, BorderLayout.SOUTH);
        this.pack();
    }

    private void guardarNombre() {
        if (!txNombre.getText().trim().isEmpty()) {
            carpeta.setNombre(txNombre.getText());
            super.dispose();
        } else {
            //ToDo
        }
    }
}
