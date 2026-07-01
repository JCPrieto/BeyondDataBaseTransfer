package es.jklabs.gui.configuracion.panel;

import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import es.jklabs.utilidades.UtilidadesEncryptacion;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ServersConfigPanelTest {

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
        assertSame(carpetaSistema, configuracion.getServerConfig().getRaiz().getCarpetas().get(0));
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

    private void invocarEliminarNodoArbol(ServersConfigPanel panel) throws Exception {
        Method eliminarNodoArbol = ServersConfigPanel.class.getDeclaredMethod("eliminarNodoArbol");
        eliminarNodoArbol.setAccessible(true);
        eliminarNodoArbol.invoke(panel);
    }
}
