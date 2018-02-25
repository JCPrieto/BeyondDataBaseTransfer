package es.jklabs.gui.dialogos;

import es.jklabs.gui.MainUI;
import es.jklabs.utilidades.Constantes;
import es.jklabs.utilidades.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class AcercaDe extends JDialog {

    private static ResourceBundle mensajes = ResourceBundle.getBundle("i18n/mensajes", Locale.getDefault());
    private static final long serialVersionUID = -5470046546293155454L;
    private static final Logger LOG = Logger.getLogger();
    private JLabel etq3;

    public AcercaDe(MainUI mainUI) {
        super(mainUI, mensajes.getString("acerca.de"), true);
        cargarPantalla();
    }

    private void cargarPantalla() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        final GridBagConstraints cns = new GridBagConstraints();
        final JLabel etq1 = new JLabel(
                "<html><h1>" + Constantes.NOMBRE_APP + " " + Constantes.VERSION + "</h1></html>", new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource
                ("img/icons/database.png"))), JLabel.CENTER);
        cns.fill = GridBagConstraints.HORIZONTAL;
        cns.insets = new Insets(10, 0, 10, 0);
        cns.gridx = 0;
        cns.gridy = 0;
        cns.gridwidth = 3;
        panel.add(etq1, cns);
        final JLabel etq2 = new JLabel(mensajes.getString("creado.por"), JLabel.CENTER);
        cns.gridy = 1;
        panel.add(etq2, cns);
        etq3 = new JLabel("JuanC.Prieto.Silos@gmail.com", JLabel.CENTER);
        etq3.setAlignmentX(CENTER_ALIGNMENT);
        etq3.setForeground(Color.blue);
        etq3.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop()
                            .browse(new URI(
                                    "mailto:JuanC.Prieto.Silos@gmail.com?subject=BeyondDataBaseTransefer"));
                    etq3.setForeground(Color.red);
                } catch (IOException | URISyntaxException e1) {
                    LOG.error("app.envio.correo", e1);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        cns.gridy = 2;
        panel.add(etq3, cns);
        JButton botonOk = new JButton(mensajes.getString("aceptar"));
        botonOk.addActionListener(al -> pressAceptar());
        cns.gridx = 0;
        cns.gridy = 4;
        cns.gridwidth = 3;
        panel.add(botonOk, cns);
        super.add(panel);
        super.pack();
    }

    private void pressAceptar() {
        this.dispose();
    }
}
