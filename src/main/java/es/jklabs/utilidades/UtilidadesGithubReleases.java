package es.jklabs.utilidades;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.jklabs.gui.MainUI;
import es.jklabs.gui.utilidades.Growls;
import es.jklabs.json.github.GitHubAsset;
import es.jklabs.json.github.GitHubRelease;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.*;
import java.util.List;

public class UtilidadesGithubReleases {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private UtilidadesGithubReleases() {

    }

    public static boolean existeNuevaVersion() throws IOException, InterruptedException {
        GitHubRelease release = getLatestRelease();
        if (release == null || release.getTagName() == null) {
            return false;
        }
        return diferenteVersion(normalizarVersion(release.getTagName()));
    }

    public static void descargaNuevaVersion(MainUI ventana) throws InterruptedException {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retorno = fc.showSaveDialog(ventana);
        if (retorno == JFileChooser.APPROVE_OPTION) {
            File directorio = fc.getSelectedFile();
            try {
                GitHubRelease release = getLatestRelease();
                if (release == null || release.getTagName() == null) {
                    Logger.info("No se pudo obtener la ultima release de GitHub.");
                    return;
                }
                String version = normalizarVersion(release.getTagName());
                GitHubAsset asset = getAssetCompatible(release, getNombreApp(version));
                if (asset == null || asset.getBrowserDownloadUrl() == null) {
                    throw new IOException("No se ha encontrado un asset compatible.");
                }
                Path destino = Paths.get(directorio.getPath(), asset.getName());
                descargarAsset(asset.getBrowserDownloadUrl(), destino);
                Growls.mostrarInfo(null, "nueva.version.descargada");
            } catch (AccessDeniedException e) {
                Growls.mostrarError(null, "path.sin.permiso.escritura", e);
                descargaNuevaVersion(ventana);
            } catch (IOException e) {
                Logger.error("descargar.nueva.version", e);
            }
        }
    }

    private static GitHubRelease getLatestRelease() throws IOException {
        HttpURLConnection connection = abrirConexion(getLatestReleaseUrl());
        try {
            if (connection.getResponseCode() != HttpResponseCode.OK) {
                Logger.info("Respuesta no valida al consultar releases: " + connection.getResponseCode());
                return null;
            }
            try (InputStream input = connection.getInputStream()) {
                return MAPPER.readValue(input, GitHubRelease.class);
            }
        } finally {
            connection.disconnect();
        }
    }

    private static void descargarAsset(String url, Path destino) throws IOException {
        HttpURLConnection connection = abrirConexion(url);
        try {
            if (connection.getResponseCode() != HttpResponseCode.OK) {
                throw new IOException("Error al descargar el asset: " + connection.getResponseCode());
            }
            try (InputStream input = connection.getInputStream()) {
                Files.copy(input, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            connection.disconnect();
        }
    }

    private static HttpURLConnection abrirConexion(String url) throws IOException {
        URI uri = URI.create(url);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("User-Agent", Constantes.NOMBRE_APP);
        return connection;
    }

    private static String getLatestReleaseUrl() {
        return Constantes.GITHUB_API_BASE + "/repos/" + Constantes.GITHUB_OWNER + "/" + Constantes.GITHUB_REPO
                + "/releases/latest";
    }

    static GitHubAsset getAssetCompatible(GitHubRelease release, String nombreEsperado) {
        List<GitHubAsset> assets = release.getAssets();
        if (assets == null || assets.isEmpty()) {
            return null;
        }
        for (GitHubAsset asset : assets) {
            if (nombreEsperado.equals(asset.getName())) {
                return asset;
            }
        }
        for (GitHubAsset asset : assets) {
            if (asset.getName() != null && asset.getName().endsWith(".zip")) {
                return asset;
            }
        }
        return null;
    }

    static String getNombreApp(String version) {
        return Constantes.NOMBRE_APP + "-" + version + ".zip";
    }

    static String normalizarVersion(String tag) {
        String normalized = tag.trim();
        if (normalized.startsWith("v") || normalized.startsWith("V")) {
            normalized = normalized.substring(1);
        }
        int dash = normalized.indexOf('-');
        if (dash > 0) {
            normalized = normalized.substring(0, dash);
        }
        return normalized;
    }

    static boolean diferenteVersion(String serverVersion) {
        int[] sv = parseVersion(serverVersion);
        int[] av = parseVersion(Constantes.VERSION);
        return sv[0] > av[0] || sv[0] == av[0] && (sv[1] > av[1] || sv[1] == av[1] && sv[2] > av[2]);
    }

    static int[] parseVersion(String version) {
        int[] parts = new int[]{0, 0, 0};
        String[] tokens = version.split("\\.");
        for (int i = 0; i < parts.length && i < tokens.length; i++) {
            try {
                parts[i] = Integer.parseInt(tokens[i]);
            } catch (NumberFormatException e) {
                parts[i] = 0;
            }
        }
        return parts;
    }
}
