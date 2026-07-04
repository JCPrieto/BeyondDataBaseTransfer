package es.jklabs.gui.configuracion.panel;

import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import es.jklabs.utilidades.UtilidadesEncryptacion;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ServersConfigPanelTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String configDirProperty;

    @Before
    public void setUp() throws Exception {
        configDirProperty = System.getProperty("beyond.database.transfer.config.dir");
        System.setProperty("beyond.database.transfer.config.dir", temporaryFolder.newFolder("config").getAbsolutePath());
    }

    @After
    public void tearDown() {
        if (configDirProperty == null) {
            System.clearProperty("beyond.database.transfer.config.dir");
        } else {
            System.setProperty("beyond.database.transfer.config.dir", configDirProperty);
        }
    }

    @Test
    public void noEliminaCarpetaDeSistemaDelArbolSiNoSeEliminaDeLaConfiguracion() throws Exception {
        Configuracion configuracion = new Configuracion();
        Carpeta carpetaSistema = new Carpeta("Sistema", true, 20);
        configuracion.getServerConfig().getRaiz().getCarpetas().add(carpetaSistema);
        ServersConfigPanel panel = new ServersConfigPanel(null, configuracion);
        DefaultMutableTreeNode raiz = panel.getRaizArbol();
        DefaultMutableTreeNode nodoSistema = (DefaultMutableTreeNode) raiz.getChildAt(0);

        panel.getArbol().setSelectionPath(new TreePath(nodoSistema.getPath()));
        invocarEliminarNodoArbol(panel);

        assertEquals(1, configuracion.getServerConfig().getRaiz().getCarpetas().size());
        assertSame(carpetaSistema, configuracion.getServerConfig().getRaiz().getCarpetas().getFirst());
        assertEquals(1, raiz.getChildCount());
        assertSame(carpetaSistema, ((DefaultMutableTreeNode) raiz.getChildAt(0)).getUserObject());
    }

    @Test
    public void eliminaServidorDelArbolYDeLaConfiguracion() throws Exception {
        Configuracion configuracion = new Configuracion();
        Servidor servidor = new Servidor(10);
        ServidorBBDD servidorBBDD = new ServidorBBDD();
        servidor.setNombre("Servidor");
        servidorBBDD.setUsuario("usuario");
        servidorBBDD.setPassword(UtilidadesEncryptacion.encrypt("password"));
        servidor.setServidorBBDD(servidorBBDD);
        configuracion.getServerConfig().getRaiz().getServidores().add(servidor);
        ServersConfigPanel panel = new ServersConfigPanel(null, configuracion);
        DefaultMutableTreeNode raiz = panel.getRaizArbol();
        DefaultMutableTreeNode nodoServidor = (DefaultMutableTreeNode) raiz.getChildAt(0);

        panel.getArbol().setSelectionPath(new TreePath(nodoServidor.getPath()));
        invocarEliminarNodoArbol(panel);

        assertEquals(0, configuracion.getServerConfig().getRaiz().getServidores().size());
        assertEquals(0, raiz.getChildCount());
    }

    @Test
    public void guardaServidorMarcadoComoSoloOrigenDesdeElFormulario() throws Exception {
        Configuracion configuracion = new Configuracion();
        Servidor servidor = new Servidor(10);
        ServidorBBDD servidorBBDD = new ServidorBBDD();
        servidor.setNombre("Servidor");
        servidor.setIp("localhost");
        servidor.setPuerto(3306);
        servidorBBDD.setUsuario("usuario");
        servidorBBDD.setPassword(UtilidadesEncryptacion.encrypt("password"));
        servidor.setServidorBBDD(servidorBBDD);
        configuracion.getServerConfig().getRaiz().getServidores().add(servidor);
        ServersConfigPanel panel = new ServersConfigPanel(null, configuracion);
        DefaultMutableTreeNode nodoServidor = (DefaultMutableTreeNode) panel.getRaizArbol().getChildAt(0);

        panel.getArbol().setSelectionPath(new TreePath(nodoServidor.getPath()));
        getCheckBox(panel, "cbSoloOrigen").setSelected(true);
        invocarGuardarServidor2(panel);

        assertTrue(servidor.isSoloOrigen());
    }

    private void invocarEliminarNodoArbol(ServersConfigPanel panel) throws Exception {
        Method eliminarNodoArbol = ServersConfigPanel.class.getDeclaredMethod("eliminarNodoArbol");
        eliminarNodoArbol.setAccessible(true);
        eliminarNodoArbol.invoke(panel);
    }

    private void invocarGuardarServidor2(ServersConfigPanel panel) throws Exception {
        Method guardarServidor2 = ServersConfigPanel.class.getDeclaredMethod("guardarServidor2");
        guardarServidor2.setAccessible(true);
        guardarServidor2.invoke(panel);
    }

    private JCheckBox getCheckBox(ServersConfigPanel panel, String fieldName) throws Exception {
        Field field = ServersConfigPanel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (JCheckBox) field.get(panel);
    }
}
