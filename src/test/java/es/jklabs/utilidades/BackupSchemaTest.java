package es.jklabs.utilidades;

import es.jklabs.json.configuracion.mysql.MysqlCliente;
import es.jklabs.json.configuracion.server.Servidor;
import es.jklabs.json.configuracion.server.ServidorBBDD;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class BackupSchemaTest {

    @SuppressWarnings("unchecked")
    private static List<String> getArgs(BackupSchema backupSchema, String methodName) throws Exception {
        Method method = BackupSchema.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return (List<String>) method.invoke(backupSchema);
    }

    private static BackupSchema crearBackupSchema() {
        MysqlCliente mysqlCliente = new MysqlCliente();
        mysqlCliente.setPath("/opt/mysql/bin");
        return new BackupSchema(null, mysqlCliente, crearServidor(), "schema_01", new File("schema_01.sql"));
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

    private static String getMysqlDumpCommand() {
        return UtilidadesSistema.isWindows() ? Constantes.MYSQLDUMP_EXE : Constantes.MYSQLDUMP;
    }

    @Test
    public void construyeArgumentosMysqlDumpParaCrearBackupDeEsquema() throws Exception {
        BackupSchema backupSchema = crearBackupSchema();

        List<String> args = getArgs(backupSchema, "getMysqlDumpArgs");

        assertTrue(args.get(0).endsWith(UtilidadesFichero.SEPARADOR + getMysqlDumpCommand()));
        assertEquals("-h", args.get(1));
        assertEquals("origen.local", args.get(2));
        assertEquals("-P", args.get(3));
        assertEquals("3306", args.get(4));
        assertEquals("-uusuario_origen", args.get(5));
        assertEquals("-ppassword_origen", args.get(6));
        assertTrue(args.contains("--databases"));
        assertTrue(args.contains("schema_01"));
        assertFalse(args.contains("--events"));
    }
}
