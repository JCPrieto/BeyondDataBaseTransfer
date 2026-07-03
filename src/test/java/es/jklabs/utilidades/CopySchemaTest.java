package es.jklabs.utilidades;

import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class CopySchemaTest {

    private static boolean isEsquemaValido(String esquema) throws Exception {
        Method method = CopySchema.class.getDeclaredMethod("isEsquemaValido", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(null, esquema);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getArgs(CopySchema copySchema, String methodName) throws Exception {
        Method method = CopySchema.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (List<String>) method.invoke(copySchema);
    }

    private static CopySchema crearCopySchema(String esquema, boolean limpiarEsquema) {
        MysqlCliente mysqlCliente = new MysqlCliente();
        mysqlCliente.setPath("/opt/mysql/bin");
        return new CopySchema(null, mysqlCliente, crearServidor("origen", 3306), crearServidor("destino", 3307),
                esquema, limpiarEsquema);
    }

    private static Servidor crearServidor(String tipo, int puerto) {
        Servidor servidor = new Servidor(1);
        ServidorBBDD servidorBBDD = new ServidorBBDD();
        servidor.setIp(tipo + ".local");
        servidor.setPuerto(puerto);
        servidorBBDD.setUsuario("usuario_" + tipo);
        servidorBBDD.setPassword(UtilidadesEncryptacion.encrypt("password_" + tipo));
        servidor.setServidorBBDD(servidorBBDD);
        return servidor;
    }

    private static String getMysqlCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQL_EXE : Constantes.MYSQL;
    }

    private static String getMysqlDumpCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQLDUMP_EXE : Constantes.MYSQLDUMP;
    }

    @Test
    public void validaNombreDeEsquemaAntesDeEjecutarProcesos() throws Exception {
        assertTrue(isEsquemaValido("schema_01"));
        assertTrue(isEsquemaValido("schema$01"));
        assertFalse(isEsquemaValido("schema 01"));
        assertFalse(isEsquemaValido("schema-01"));
        assertFalse(isEsquemaValido("../schema"));
        assertFalse(isEsquemaValido(""));
    }

    @Test
    public void construyeArgumentosMysqlDumpSinConcatenarOpcionesConValores() throws Exception {
        CopySchema copySchema = crearCopySchema("schema_01", true);

        List<String> args = getArgs(copySchema, "getMysqlDumpArgs");

        assertTrue(args.get(0).endsWith(UtilidadesFichero.SEPARADOR + getMysqlDumpCommand()));
        assertEquals("-h", args.get(1));
        assertEquals("origen.local", args.get(2));
        assertEquals("-P", args.get(3));
        assertEquals("3306", args.get(4));
        assertEquals("-uusuario_origen", args.get(5));
        assertEquals("-ppassword_origen", args.get(6));
        assertTrue(args.contains("--databases"));
        assertTrue(args.contains("schema_01"));
        assertTrue(args.contains("--add-drop-database"));
    }

    @Test
    public void construyeArgumentosMysqlParaRestaurarSinShellIntermedio() throws Exception {
        CopySchema copySchema = crearCopySchema("schema_01", false);

        List<String> args = getArgs(copySchema, "getMysqlArgs");

        assertTrue(args.get(0).endsWith(UtilidadesFichero.SEPARADOR + getMysqlCommand()));
        assertEquals("-h", args.get(1));
        assertEquals("destino.local", args.get(2));
        assertEquals("-P", args.get(3));
        assertEquals("3307", args.get(4));
        assertEquals("-uusuario_destino", args.get(5));
        assertEquals("-ppassword_destino", args.get(6));
        assertEquals("schema_01", args.get(7));
    }
}
