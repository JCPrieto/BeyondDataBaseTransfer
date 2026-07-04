package es.jklabs.utilidades;

import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestoreBackupTest {

    @SuppressWarnings("unchecked")
    private static List<String> getArgs(RestoreBackup restoreBackup, String methodName) throws Exception {
        Method method = RestoreBackup.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (List<String>) method.invoke(restoreBackup);
    }

    private static String getMensajeErrorUsuario(RestoreBackup restoreBackup, String error) throws Exception {
        Method method = RestoreBackup.class.getDeclaredMethod("getMensajeErrorUsuario", String.class);
        method.setAccessible(true);
        return (String) method.invoke(restoreBackup, error);
    }

    private static RestoreBackup crearRestoreBackup() {
        MysqlCliente mysqlCliente = new MysqlCliente();
        mysqlCliente.setPath("/opt/mysql/bin");
        return new RestoreBackup(null, mysqlCliente, crearServidor(), new File("schema_01.sql"));
    }

    private static Servidor crearServidor() {
        Servidor servidor = new Servidor(1);
        ServidorBBDD servidorBBDD = new ServidorBBDD();
        servidor.setIp("destino.local");
        servidor.setPuerto(3307);
        servidorBBDD.setUsuario("usuario_destino");
        servidorBBDD.setPassword(UtilidadesEncryptacion.encrypt("password_destino"));
        servidor.setServidorBBDD(servidorBBDD);
        return servidor;
    }

    private static String getMysqlCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQL_EXE : Constantes.MYSQL;
    }

    @Test
    public void construyeArgumentosMysqlParaRestaurarBackupSinSeleccionarEsquema() throws Exception {
        RestoreBackup restoreBackup = crearRestoreBackup();

        List<String> args = getArgs(restoreBackup, "getMysqlArgs");

        assertTrue(args.get(0).endsWith(UtilidadesFichero.SEPARADOR + getMysqlCommand()));
        assertEquals("-h", args.get(1));
        assertEquals("destino.local", args.get(2));
        assertEquals("-P", args.get(3));
        assertEquals("3307", args.get(4));
        assertEquals("-uusuario_destino", args.get(5));
        assertEquals("-ppassword_destino", args.get(6));
        assertEquals(7, args.size());
    }

    @Test
    public void muestraMensajeAmigableCuandoElUsuarioNoTienePermisosParaRestaurar() throws Exception {
        RestoreBackup restoreBackup = crearRestoreBackup();
        String error = "ERROR 1044 (42000) at line 22: Access denied for user 'sql7815950'@'%' to database 'jklabs'";

        String mensaje = getMensajeErrorUsuario(restoreBackup, error);

        assertEquals("fallo.restaurar.backup.permisos", mensaje);
    }
}
