package es.jklabs.utilidades;

import es.jklabs.json.configuracion.Configuracion;
import es.jklabs.json.configuracion.server.Carpeta;
import es.jklabs.json.configuracion.server.Servidor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class UtilidadesConfiguracionTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void guardaConfiguracionEnDirectorioConfiguradoParaTests() throws Exception {
        String configDirProperty = System.getProperty(UtilidadesFichero.CONFIG_DIR_PROPERTY);
        Path configDir = temporaryFolder.newFolder("config").toPath();
        try {
            System.setProperty(UtilidadesFichero.CONFIG_DIR_PROPERTY, configDir.toString());

            UtilidadesConfiguracion.guardarConfiguracion(new Configuracion());

            assertTrue(Files.exists(configDir.resolve("config.json")));
            assertEquals(configDir.resolve("config.json"), UtilidadesConfiguracion.getConfigPath());
        } finally {
            if (configDirProperty == null) {
                System.clearProperty(UtilidadesFichero.CONFIG_DIR_PROPERTY);
            } else {
                System.setProperty(UtilidadesFichero.CONFIG_DIR_PROPERTY, configDirProperty);
            }
        }
    }

    @Test
    public void usaDirectorioDeLogsConfiguradoParaTests() throws Exception {
        String logsDirProperty = System.getProperty(UtilidadesFichero.LOGS_DIR_PROPERTY);
        Path logsDir = temporaryFolder.newFolder("logs").toPath();
        try {
            System.setProperty(UtilidadesFichero.LOGS_DIR_PROPERTY, logsDir.toString());

            UtilidadesFichero.createLogFolder();

            assertEquals(logsDir, UtilidadesFichero.getLogDir());
            assertTrue(Files.isDirectory(logsDir));
        } finally {
            if (logsDirProperty == null) {
                System.clearProperty(UtilidadesFichero.LOGS_DIR_PROPERTY);
            } else {
                System.setProperty(UtilidadesFichero.LOGS_DIR_PROPERTY, logsDirProperty);
            }
        }
    }

    @Test
    public void importaServidoresCreandoCopiasConIdsNuevosYManteniendoEstructura() throws Exception {
        Configuracion configuracion = new Configuracion();
        Carpeta destino = configuracion.getServerConfig().getRaiz();
        Carpeta origen = new Carpeta("Raiz importada", true, 1);
        Carpeta carpetaImportada = new Carpeta("Carpeta importada", false, 50);
        Carpeta subcarpetaImportada = new Carpeta("Subcarpeta importada", false, 51);
        Servidor servidorRaiz = new Servidor(90);
        Servidor servidorSubcarpeta = new Servidor(91);

        servidorRaiz.setNombre("Servidor raiz");
        servidorRaiz.setSoloOrigen(true);
        servidorSubcarpeta.setNombre("Servidor subcarpeta");
        origen.getServidores().add(servidorRaiz);
        subcarpetaImportada.getServidores().add(servidorSubcarpeta);
        carpetaImportada.getCarpetas().add(subcarpetaImportada);
        origen.getCarpetas().add(carpetaImportada);

        Method addServidores = UtilidadesConfiguracion.class.getDeclaredMethod(
                "addServidores", Configuracion.class, Carpeta.class, Carpeta.class);
        addServidores.setAccessible(true);
        addServidores.invoke(null, configuracion, destino, origen);

        assertEquals(1, destino.getServidores().size());
        assertEquals("Servidor raiz", destino.getServidores().getFirst().getNombre());
        assertTrue(destino.getServidores().getFirst().isSoloOrigen());
        assertNotEquals(90, destino.getServidores().getFirst().getId());
        assertEquals(1, destino.getCarpetas().size());

        Carpeta carpetaCopiada = destino.getCarpetas().getFirst();
        assertNotSame(carpetaImportada, carpetaCopiada);
        assertEquals("Carpeta importada", carpetaCopiada.getNombre());
        assertNotEquals(carpetaImportada.getId(), carpetaCopiada.getId());
        assertEquals(1, carpetaCopiada.getCarpetas().size());

        Carpeta subcarpetaCopiada = carpetaCopiada.getCarpetas().getFirst();
        assertNotSame(subcarpetaImportada, subcarpetaCopiada);
        assertEquals("Subcarpeta importada", subcarpetaCopiada.getNombre());
        assertNotEquals(subcarpetaImportada.getId(), subcarpetaCopiada.getId());
        assertEquals(1, subcarpetaCopiada.getServidores().size());
        assertEquals("Servidor subcarpeta", subcarpetaCopiada.getServidores().getFirst().getNombre());
        assertNotEquals(servidorSubcarpeta.getId(), subcarpetaCopiada.getServidores().getFirst().getId());
    }
}
