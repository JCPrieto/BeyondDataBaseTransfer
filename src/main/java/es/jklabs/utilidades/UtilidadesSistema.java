package es.jklabs.utilidades;

public class UtilidadesSistema {

    private UtilidadesSistema() {

    }


    public static boolean isWindows() {
        String sistemaOperativo = System.getProperty("os.name", "generic").toLowerCase();
        return sistemaOperativo.contains("win");
    }

    static String getTmpDir() {
        if (isWindows()) {
            return System.getProperty("java.io.tmpdir");
        } else {
            return System.getProperty("java.io.tmpdir") + UtilidadesFichero.SEPARADOR;
        }
    }
}
