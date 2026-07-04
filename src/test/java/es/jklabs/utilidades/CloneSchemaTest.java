package es.jklabs.utilidades;

import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class CloneSchemaTest {

    @SuppressWarnings("unchecked")
    private static List<String> getArgs(CloneSchema cloneSchema, String methodName) throws Exception {
        Method method = CloneSchema.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (List<String>) method.invoke(cloneSchema);
    }

    private static String getMensajeErrorUsuario(CloneSchema cloneSchema, String error) throws Exception {
        Method method = CloneSchema.class.getDeclaredMethod("getMensajeErrorUsuario", String.class);
        method.setAccessible(true);
        return (String) method.invoke(cloneSchema, error);
    }

    private static CloneSchema crearCloneSchema() {
        MysqlCliente mysqlCliente = new MysqlCliente();
        mysqlCliente.setPath("/opt/mysql/bin");
        return new CloneSchema(null, mysqlCliente, crearServidor(), "schema_01", "schema_02");
    }

    private static Servidor crearServidor() {
        Servidor servidor = new Servidor(1);
        ServidorBBDD servidorBBDD = new ServidorBBDD();
        servidor.setIp("origen.local");
        servidor.setPuerto(3306);
        servidorBBDD.setUsuario("usuario_origen");
        servidorBBDD.setPassword(UtilidadesEncryptacion.encrypt("password_origen"));
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
    public void construyeDumpSinDatabasesParaNoArrastrarNombreDeEsquemaOrigen() throws Exception {
        CloneSchema cloneSchema = crearCloneSchema();

        List<String> args = getArgs(cloneSchema, "getMysqlDumpArgs");

        assertTrue(args.get(0).endsWith(UtilidadesFichero.SEPARADOR + getMysqlDumpCommand()));
        assertEquals("-h", args.get(1));
        assertEquals("origen.local", args.get(2));
        assertEquals("-P", args.get(3));
        assertEquals("3306", args.get(4));
        assertEquals("-uusuario_origen", args.get(5));
        assertEquals("-ppassword_origen", args.get(6));
        assertFalse(args.contains("--databases"));
        assertTrue(args.contains("schema_01"));
    }

    @Test
    public void creaEsquemaDestinoEnElMismoServidor() throws Exception {
        CloneSchema cloneSchema = crearCloneSchema();

        List<String> args = getArgs(cloneSchema, "getCrearEsquemaArgs");

        assertTrue(args.get(0).endsWith(UtilidadesFichero.SEPARADOR + getMysqlCommand()));
        assertEquals("-e", args.get(7));
        assertEquals("CREATE DATABASE IF NOT EXISTS `schema_02`", args.get(8));
    }

    @Test
    public void restauraBackupSeleccionandoElEsquemaDestino() throws Exception {
        CloneSchema cloneSchema = crearCloneSchema();

        List<String> args = getArgs(cloneSchema, "getMysqlArgs");

        assertTrue(args.get(0).endsWith(UtilidadesFichero.SEPARADOR + getMysqlCommand()));
        assertEquals("schema_02", args.get(7));
    }

    @Test
    public void muestraMensajeAmigableCuandoElUsuarioNoTienePermisosParaClonar() throws Exception {
        CloneSchema cloneSchema = crearCloneSchema();
        String error = "ERROR 1044 (42000) at line 1: Access denied for user 'sql7815950'@'%' to database 'schema_02'";

        String mensaje = getMensajeErrorUsuario(cloneSchema, error);

        assertEquals("fallo.clonar.esquema.permisos", mensaje);
    }
}
